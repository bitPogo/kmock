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
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationFilter
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COMMON_INDICATOR
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource

internal class KMockMultiSourceAggregator(
    private val logger: KSPLogger,
    private val rootPackage: String,
    private val annotationFilter: AnnotationFilter,
    private val sourceSetValidator: SourceSetValidator,
    private val generics: GenericResolver,
    private val customAnnotations: Map<String, String>,
) : MultiSourceAggregator, BaseSourceAggregator(logger, customAnnotations, generics) {
    private fun resolveInterfaces(
        interfaceName: String,
        interfaces: List<KSDeclaration>,
        sourceIndicator: String,
        dependencies: List<KSFile>,
        templateCollector: MutableMap<String, TemplateMultiSource>,
    ) {
        val interfazes: MutableList<KSClassDeclaration> = mutableListOf()
        val generics: MutableList<Map<String, List<KSTypeReference>>?> = mutableListOf()

        interfaces.forEach { declaration ->
            val interfaze = safeCastInterface(declaration)
            generics.add(resolveGenerics(interfaze))
            interfazes.add(interfaze)
        }

        val qualifiedName = "$rootPackage.$interfaceName"

        templateCollector[qualifiedName + sourceIndicator] = TemplateMultiSource(
            indicator = sourceIndicator,
            templateName = interfaceName,
            packageName = rootPackage,
            dependencies = dependencies,
            templates = interfazes,
            generics = generics,
        )
    }

    private fun List<KSType>.extractDeclarations(): List<KSDeclaration> = this.map { type -> type.declaration }

    private fun resolveInterfaces(
        raw: Map<String, List<Triple<String, List<KSFile>, List<KSType>>>>,
        templateCollector: MutableMap<String, TemplateMultiSource>,
    ) {
        raw.forEach { (sourceIndicator, interfaces) ->
            interfaces.forEach { interfacesBundle ->
                resolveInterfaces(
                    interfaceName = interfacesBundle.first,
                    interfaces = interfacesBundle.third.extractDeclarations(),
                    sourceIndicator = sourceIndicator,
                    dependencies = interfacesBundle.second,
                    templateCollector = templateCollector,
                )
            }
        }
    }

    private fun determineSourceCategory(
        defaultIndicator: String,
        annotation: KSAnnotation
    ): String {
        return if (annotation.arguments.size == 3) {
            annotation.arguments.first().value as String
        } else {
            customAnnotations[resolveAnnotationName(annotation)] ?: defaultIndicator
        }
    }

    private fun determineMockName(annotation: KSAnnotation): String {
        return if (annotation.arguments.size == 3) {
            annotation.arguments[1].value
        } else {
            annotation.arguments.first().value
        } as String
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractInterfaces(
        defaultIndicator: String,
        annotated: Sequence<KSAnnotated>,
        condition: (String, KSAnnotation) -> Boolean,
    ): Aggregated<TemplateMultiSource> {
        val illAnnotated = mutableListOf<KSAnnotated>()
        val typeContainer = mutableMapOf<String, MutableList<Triple<String, List<KSFile>, List<KSType>>>>()
        val templateCollector: MutableMap<String, TemplateMultiSource> = mutableMapOf()
        val fileCollector: MutableList<KSFile> = mutableListOf()

        annotated.forEach { annotatedSymbol ->
            val annotation = findKMockAnnotation(annotatedSymbol.annotations, condition)

            if (annotation == null || annotation.arguments.isEmpty()) {
                illAnnotated.add(annotatedSymbol)
            } else {
                val sourceIndicator = determineSourceCategory(defaultIndicator, annotation)
                val interfaces = typeContainer.getOrElse(sourceIndicator) { mutableListOf() }

                interfaces.add(
                    Triple(
                        determineMockName(annotation),
                        listOf(annotatedSymbol.containingFile!!),
                        annotation.arguments.last().value as List<KSType>,
                    )
                )
                typeContainer[sourceIndicator] = interfaces
                fileCollector.add(annotatedSymbol.containingFile!!)
            }
        }

        resolveInterfaces(
            typeContainer,
            templateCollector
        )

        return Aggregated(
            illFormed = illAnnotated,
            extractedTemplates = templateCollector.values.toList(),
            totalDependencies = fileCollector
        )
    }

    private fun fetchCommonAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ANNOTATION_COMMON_MULTI_NAME,
            false
        )
    }

    override fun extractCommonInterfaces(
        resolver: Resolver
    ): Aggregated<TemplateMultiSource> {
        val annotated = fetchCommonAnnotated(resolver)

        return extractInterfaces(COMMON_INDICATOR, annotated) { annotationName, _ ->
            ANNOTATION_COMMON_MULTI_NAME == annotationName
        }
    }

    private fun fetchSharedAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        val shared = resolver.getSymbolsWithAnnotation(
            ANNOTATION_SHARED_MULTI_NAME,
            false
        )
        val customShared = fetchCustomShared(resolver)

        return sequenceOf(shared, *customShared).flatten()
    }

    private fun isSharedAnnotation(annotationName: String, annotation: KSAnnotation): Boolean {
        return (annotationName in customAnnotations.keys && annotationFilter.isApplicableMultiSourceAnnotation(annotation)) ||
            (ANNOTATION_SHARED_MULTI_NAME == annotationName && sourceSetValidator.isValidateSourceSet(annotation))
    }

    override fun extractSharedInterfaces(
        resolver: Resolver
    ): Aggregated<TemplateMultiSource> {
        val annotated = fetchSharedAnnotated(resolver)

        return extractInterfaces("", annotated, ::isSharedAnnotation)
    }

    private fun fetchPlatformAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ANNOTATION_PLATFORM_MULTI_NAME,
            false
        )
    }

    override fun extractPlatformInterfaces(
        resolver: Resolver
    ): Aggregated<TemplateMultiSource> {
        val annotated = fetchPlatformAnnotated(resolver)

        return extractInterfaces("", annotated) { annotationName, _ ->
            ANNOTATION_PLATFORM_MULTI_NAME == annotationName
        }
    }

    companion object : ProcessorContract.AggregatorFactory<MultiSourceAggregator> {
        override fun getInstance(
            logger: KSPLogger,
            rootPackage: String,
            sourceSetValidator: SourceSetValidator,
            annotationFilter: AnnotationFilter,
            generics: GenericResolver,
            customAnnotations: Map<String, String>,
            aliases: Map<String, String>,
        ): MultiSourceAggregator {
            val additionalAnnotations = annotationFilter.filterAnnotation(
                customAnnotations
            )

            return KMockMultiSourceAggregator(
                logger = logger,
                rootPackage = rootPackage,
                annotationFilter = annotationFilter,
                sourceSetValidator = sourceSetValidator,
                generics = generics,
                customAnnotations = additionalAnnotations,
            )
        }
    }
}
