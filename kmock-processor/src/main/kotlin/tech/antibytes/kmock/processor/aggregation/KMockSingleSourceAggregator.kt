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
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationContainer
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.AggregatorFactory
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationFilter
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_KMOCK_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COMMON_INDICATOR
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
    override fun extractKmockInterfaces(resolver: Resolver): AnnotationContainer {
        val annotated = resolver.getSymbolsWithAnnotation(
            ANNOTATION_KMOCK_NAME,
            false
        )

        return resolveKmockAnnotation(annotated)
    }

    private fun resolveInterface(
        declaration: KSDeclaration,
        sourceIndicator: String,
        dependency: KSFile,
        templateCollector: MutableMap<String, TemplateSource>,
    ) {
        val interfaze = safeCastInterface(declaration)
        val qualifiedName = ensureNotNullClassName(interfaze.qualifiedName?.asString())
        val packageName = interfaze.packageName.asString()

        templateCollector[qualifiedName + sourceIndicator] = TemplateSource(
            indicator = sourceIndicator,
            templateName = aliases[qualifiedName] ?: interfaze.deriveSimpleName(packageName),
            packageName = packageName,
            dependencies = listOf(dependency),
            template = interfaze,
            generics = resolveGenerics(interfaze)
        )
    }

    private fun resolveInterfaces(
        raw: Map<String, Pair<List<KSFile>, MutableList<KSType>>>,
        templateCollector: MutableMap<String, TemplateSource>
    ) {
        raw.forEach { (sourceIndicator, interfaces) ->
            interfaces.second.forEachIndexed { idx, interfaze ->
                resolveInterface(
                    declaration = interfaze.declaration,
                    sourceIndicator = sourceIndicator,
                    dependency = interfaces.first[idx],
                    templateCollector = templateCollector
                )
            }
        }
    }

    private fun determineSourceCategory(
        defaultIndicator: String,
        annotation: KSAnnotation
    ): String {
        return if (annotation.arguments.size == 2) {
            (annotation.arguments.first().value as String).ensureTestSourceSet()!!
        } else {
            customAnnotations[resolveAnnotationName(annotation)].ensureTestSourceSet() ?: defaultIndicator
        }
    }

    fun MutableList<KSFile>.addTimes(value: KSFile, times: Int) {
        repeat(times) {
            this.add(value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun resolveAnnotation(
        indicator: String,
        symbol: KSAnnotated,
        annotation: KSAnnotation?,
        illAnnotated: MutableList<KSAnnotated>,
        typeContainer: MutableMap<String, Pair<MutableList<KSFile>, MutableList<KSType>>>,
        fileCollector: MutableList<KSFile>,
    ) {
        if (annotation == null || annotation.arguments.isEmpty()) {
            illAnnotated.add(symbol)
        } else {
            val sourceIndicator = determineSourceCategory(indicator, annotation)
            val (dependencies, interfaces) = typeContainer.getOrElse(sourceIndicator) {
                Pair(mutableListOf(), mutableListOf())
            }

            val extractedInterfaces: List<KSType> = annotation.arguments.last().value as List<KSType>
            interfaces.addAll(extractedInterfaces)

            dependencies.addTimes(symbol.containingFile!!, extractedInterfaces.size)

            typeContainer[sourceIndicator] = Pair(dependencies, interfaces)
            fileCollector.add(symbol.containingFile!!)
        }
    }

    private fun extractInterfaces(
        defaultIndicator: String,
        annotated: Sequence<KSAnnotated>,
        kmockAnnotated: List<KSAnnotated>,
        condition: (String, KSAnnotation) -> Boolean,
    ): Aggregated<TemplateSource> {
        val illAnnotated = mutableListOf<KSAnnotated>()
        val typeContainer = mutableMapOf<String, Pair<MutableList<KSFile>, MutableList<KSType>>>()
        val templateCollector: MutableMap<String, TemplateSource> = mutableMapOf()
        val fileCollector: MutableList<KSFile> = mutableListOf()

        annotated.forEach { annotatedSymbol ->
            val annotation = findKMockAnnotation(annotatedSymbol.annotations, condition)

            resolveAnnotation(
                indicator = defaultIndicator,
                symbol = annotatedSymbol,
                annotation = annotation,
                illAnnotated = illAnnotated,
                typeContainer = typeContainer,
                fileCollector = fileCollector
            )
        }

        kmockAnnotated.forEach { annotatedSymbol ->
            val annotation = findKMockAnnotation(annotatedSymbol.annotations) { annotationName, _ ->
                annotationName == ANNOTATION_KMOCK_NAME
            }

            resolveAnnotation(
                indicator = defaultIndicator,
                symbol = annotatedSymbol,
                annotation = annotation,
                illAnnotated = illAnnotated,
                typeContainer = typeContainer,
                fileCollector = fileCollector
            )
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
        kmockAnnotated: List<KSAnnotated>,
        resolver: Resolver,
    ): Aggregated<TemplateSource> {
        val annotated = fetchCommonAnnotated(resolver)

        return extractInterfaces(
            defaultIndicator = COMMON_INDICATOR,
            annotated = annotated,
            kmockAnnotated = kmockAnnotated,
        ) { annotationName, _ -> ANNOTATION_COMMON_NAME == annotationName }
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

        return extractInterfaces("", annotated, emptyList(), ::isSharedAnnotation)
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

        return extractInterfaces("", annotated, emptyList()) { annotationName, _ ->
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
