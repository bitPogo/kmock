/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.aggregation

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.AggregatorFactory
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationFilter
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_NAME
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.SingleSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.utils.deriveSimpleName
import tech.antibytes.kmock.processor.utils.ensureNotNullClassName

internal class KMockSingleSourceAggregator(
    private val logger: KSPLogger,
    private val annotationFilter: AnnotationFilter,
    private val sourceSetValidator: SourceSetValidator,
    generics: GenericResolver,
    private val customAnnotations: Map<String, String>,
    private val aliases: Map<String, String>
) : SingleSourceAggregator, BaseSourceAggregator(logger, customAnnotations, generics) {
    private fun resolveInterface(
        declaration: KSDeclaration,
        sourceIndicator: String,
        dependencies: List<KSFile>,
        templateCollector: MutableMap<String, TemplateSource>,
    ) {
        val interfaze = safeCastInterface(declaration)
        val qualifiedName = ensureNotNullClassName(interfaze.qualifiedName?.asString())
        val packageName = interfaze.packageName.asString()

        templateCollector[qualifiedName + sourceIndicator] = TemplateSource(
            indicator = sourceIndicator,
            templateName = aliases[qualifiedName] ?: interfaze.deriveSimpleName(packageName),
            packageName = packageName,
            dependencies = dependencies,
            template = interfaze,
            generics = resolveGenerics(interfaze)
        )
    }

    private fun resolveInterfaces(
        raw: Map<String, Pair<List<KSFile>, MutableList<KSType>>>,
        templateCollector: MutableMap<String, TemplateSource>
    ) {
        raw.forEach { (sourceIndicator, interfaces) ->
            interfaces.second.forEach { interfaze ->
                resolveInterface(
                    declaration = interfaze.declaration,
                    sourceIndicator = sourceIndicator,
                    dependencies = interfaces.first,
                    templateCollector = templateCollector
                )
            }
        }
    }

    private fun determineSourceCategory(annotation: KSAnnotation): String {
        return if (annotation.arguments.size == 2) {
            annotation.arguments.first().value as String
        } else {
            customAnnotations[resolveAnnotationName(annotation)] ?: ""
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractInterfaces(
        annotated: Sequence<KSAnnotated>,
        condition: (String, KSAnnotation) -> Boolean,
    ): Aggregated<TemplateSource> {
        val illAnnotated = mutableListOf<KSAnnotated>()
        val typeContainer = mutableMapOf<String, Pair<List<KSFile>, MutableList<KSType>>>()
        val templateCollector: MutableMap<String, TemplateSource> = mutableMapOf()
        val fileCollector: MutableList<KSFile> = mutableListOf()

        annotated.forEach { annotatedSymbol ->
            val annotation = findKMockAnnotation(annotatedSymbol.annotations, condition)

            if (annotation == null || annotation.arguments.isEmpty()) {
                illAnnotated.add(annotatedSymbol)
            } else {
                val sourceIndicator = determineSourceCategory(annotation)
                val (_, interfaces) = typeContainer.getOrElse(sourceIndicator) { Pair(null, mutableListOf()) }

                interfaces.addAll(annotation.arguments.last().value as List<KSType>)
                typeContainer[sourceIndicator] = Pair(listOf(annotatedSymbol.containingFile!!), interfaces)
                fileCollector.add(annotatedSymbol.containingFile!!)
            }
        }

        resolveInterfaces(typeContainer, templateCollector)

        return Aggregated(
            illFormed = illAnnotated,
            extractedTemplates = templateCollector.values.toList(),
            totalDependencies = fileCollector
        )
    }

    private fun fetchCommonAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ANNOTATION_COMMON_NAME,
            false
        )
    }

    override fun extractCommonInterfaces(
        resolver: Resolver
    ): Aggregated<TemplateSource> {
        val annotated = fetchCommonAnnotated(resolver)

        return extractInterfaces(annotated) { annotationName, _ ->
            ANNOTATION_COMMON_NAME == annotationName
        }
    }

    private fun fetchSharedAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        val shared = resolver.getSymbolsWithAnnotation(
            ANNOTATION_SHARED_NAME,
            false
        )
        val customShared = fetchCustomShared(resolver)

        return sequenceOf(shared, *customShared).flatten()
    }

    private fun isSharedAnnotation(annotationName: String, annotation: KSAnnotation): Boolean {
        return (annotationName in customAnnotations.keys && annotationFilter.isApplicableSingleSourceAnnotation(annotation)) ||
            (ANNOTATION_SHARED_NAME == annotationName && sourceSetValidator.isValidateSourceSet(annotation))
    }

    override fun extractSharedInterfaces(
        resolver: Resolver
    ): Aggregated<TemplateSource> {
        val annotated = fetchSharedAnnotated(resolver)

        return extractInterfaces(annotated, ::isSharedAnnotation)
    }

    private fun fetchPlatformAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ANNOTATION_PLATFORM_NAME,
            false
        )
    }

    override fun extractPlatformInterfaces(
        resolver: Resolver
    ): Aggregated<TemplateSource> {
        val annotated = fetchPlatformAnnotated(resolver)

        return extractInterfaces(annotated) { annotationName, _ ->
            ANNOTATION_PLATFORM_NAME == annotationName
        }
    }

    companion object : AggregatorFactory<SingleSourceAggregator> {
        override fun getInstance(
            logger: KSPLogger,
            rootPackage: String,
            sourceSetValidator: SourceSetValidator,
            annotationFilter: AnnotationFilter,
            generics: GenericResolver,
            customAnnotations: Map<String, String>,
            aliases: Map<String, String>,
        ): SingleSourceAggregator {
            val additionalAnnotations = annotationFilter.filterAnnotation(
                customAnnotations
            )

            return KMockSingleSourceAggregator(
                logger = logger,
                annotationFilter = annotationFilter,
                sourceSetValidator = sourceSetValidator,
                generics = generics,
                customAnnotations = additionalAnnotations,
                aliases = aliases,
            )
        }
    }
}
