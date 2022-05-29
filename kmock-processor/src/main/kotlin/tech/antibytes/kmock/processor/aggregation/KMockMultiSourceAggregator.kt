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
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_KMOCK_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COMMON_INDICATOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PLATFORM_INDICATOR
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
    override fun extractKmockInterfaces(resolver: Resolver): ProcessorContract.AnnotationContainer {
        val annotated = resolver.getSymbolsWithAnnotation(
            ANNOTATION_KMOCK_MULTI_NAME,
            false
        )

        return resolveKmockAnnotation(annotated)
    }

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
            (annotation.arguments.first().value as String).ensureTestSourceSet()!!
        } else {
            customAnnotations[resolveAnnotationName(annotation)].ensureTestSourceSet() ?: defaultIndicator
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
    private fun resolveAnnotation(
        indicator: String,
        symbol: KSAnnotated,
        annotation: KSAnnotation?,
        illAnnotated: MutableList<KSAnnotated>,
        typeContainer: MutableMap<String, MutableList<Triple<String, List<KSFile>, List<KSType>>>>,
        fileCollector: MutableList<KSFile>,
    ) {
        if (annotation == null || annotation.arguments.isEmpty()) {
            illAnnotated.add(symbol)
        } else {
            val sourceIndicator = determineSourceCategory(indicator, annotation)
            val interfaces = typeContainer.getOrElse(sourceIndicator) { mutableListOf() }

            interfaces.add(
                Triple(
                    determineMockName(annotation),
                    listOf(symbol.containingFile!!),
                    annotation.arguments.last().value as List<KSType>,
                )
            )
            typeContainer[sourceIndicator] = interfaces
            fileCollector.add(symbol.containingFile!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractInterfaces(
        defaultIndicator: String,
        annotated: Sequence<KSAnnotated>,
        kmockAnnotated: Map<String, List<KSAnnotated>>,
        condition: (String, KSAnnotation) -> Boolean,
    ): Aggregated<TemplateMultiSource> {
        val illAnnotated = mutableListOf<KSAnnotated>()
        val typeContainer = mutableMapOf<String, MutableList<Triple<String, List<KSFile>, List<KSType>>>>()
        val templateCollector: MutableMap<String, TemplateMultiSource> = mutableMapOf()
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

        kmockAnnotated.forEach { (indicator, annotatedSymbols) ->
            annotatedSymbols.forEach { annotatedSymbol ->
                val annotation = findKMockAnnotation(annotatedSymbol.annotations) { annotationName, _ ->
                    annotationName == ANNOTATION_KMOCK_MULTI_NAME
                }

                resolveAnnotation(
                    indicator = indicator,
                    symbol = annotatedSymbol,
                    annotation = annotation,
                    illAnnotated = illAnnotated,
                    typeContainer = typeContainer,
                    fileCollector = fileCollector
                )
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
        kmockAnnotated: List<KSAnnotated>,
        resolver: Resolver
    ): Aggregated<TemplateMultiSource> {
        val annotated = fetchCommonAnnotated(resolver)

        return extractInterfaces(
            defaultIndicator = COMMON_INDICATOR,
            annotated = annotated,
            kmockAnnotated = mapOf(COMMON_INDICATOR to kmockAnnotated),
        ) { annotationName, _ ->
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
        kmockAnnotated: Map<String, List<KSAnnotated>>,
        resolver: Resolver,
    ): Aggregated<TemplateMultiSource> {
        val annotated = fetchSharedAnnotated(resolver)

        return extractInterfaces(
            defaultIndicator = "",
            annotated = annotated,
            kmockAnnotated = kmockAnnotated,
            condition = ::isSharedAnnotation
        )
    }

    private fun fetchPlatformAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ANNOTATION_PLATFORM_MULTI_NAME,
            false
        )
    }

    override fun extractPlatformInterfaces(
        kmockAnnotated: List<KSAnnotated>,
        resolver: Resolver,
    ): Aggregated<TemplateMultiSource> {
        val annotated = fetchPlatformAnnotated(resolver)

        return extractInterfaces(
            defaultIndicator = PLATFORM_INDICATOR,
            annotated = annotated,
            kmockAnnotated = mapOf(PLATFORM_INDICATOR to kmockAnnotated),
        ) { annotationName, _ ->
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
