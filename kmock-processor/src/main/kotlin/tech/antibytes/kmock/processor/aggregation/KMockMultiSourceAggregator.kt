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
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource

internal class KMockMultiSourceAggregator(
    private val logger: KSPLogger,
    private val annotationFilter: AnnotationFilter,
    private val sourceSetValidator: SourceSetValidator,
    private val generics: GenericResolver,
    private val customAnnotations: Map<String, String>,
) : MultiSourceAggregator, BaseSourceAggregator(logger, customAnnotations, generics) {
    private fun isPackageIndicator(char: Char) = char == '.'

    private fun resolvePackageName(
        currentName: String?,
        nameCandidate: String
    ): String {
        val currentDepth = currentName?.count(::isPackageIndicator) ?: Int.MAX_VALUE
        val candidateDepth = nameCandidate.count(::isPackageIndicator)

        return if (currentDepth > candidateDepth) {
            nameCandidate
        } else {
            currentName!!
        }
    }

    private fun resolveInterfaces(
        interfaceName: String,
        interfaces: List<KSDeclaration>,
        sourceIndicator: String,
        templateCollector: MutableMap<String, TemplateMultiSource>
    ) {
        var packageName: String? = null
        val interfazes: MutableList<KSClassDeclaration> = mutableListOf()
        val generics: MutableList<Map<String, List<KSTypeReference>>?> = mutableListOf()

        interfaces.forEach { declaration ->
            val interfaze = safeCastInterface(declaration)
            packageName = resolvePackageName(
                currentName = packageName,
                nameCandidate = interfaze.packageName.asString()
            )

            generics.add(resolveGenerics(interfaze))
            interfazes.add(interfaze)
        }

        val qualifiedName = "$packageName.$interfaceName"

        templateCollector[qualifiedName + sourceIndicator] = TemplateMultiSource(
            indicator = sourceIndicator,
            templateName = interfaceName,
            packageName = packageName!!,
            templates = interfazes,
            generics = generics
        )
    }

    private fun List<KSType>.extractDeclarations(): List<KSDeclaration> = this.map { type -> type.declaration }

    private fun resolveInterfaces(
        raw: Map<String, List<Pair<String, List<KSType>>>>,
        templateCollector: MutableMap<String, TemplateMultiSource>
    ) {
        raw.forEach { (sourceIndicator, interfaces) ->
            interfaces.forEach { interfacesBundle ->
                resolveInterfaces(
                    interfaceName = interfacesBundle.first,
                    interfaces = interfacesBundle.second.extractDeclarations(),
                    sourceIndicator = sourceIndicator,
                    templateCollector = templateCollector
                )
            }
        }
    }

    private fun determineSourceCategory(annotation: KSAnnotation): String {
        return if (annotation.arguments.size == 3) {
            annotation.arguments.first().value as String
        } else {
            customAnnotations[resolveAnnotationName(annotation)] ?: ""
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
        annotated: Sequence<KSAnnotated>,
        condition: (String, KSAnnotation) -> Boolean,
    ): Aggregated<TemplateMultiSource> {
        val illAnnotated = mutableListOf<KSAnnotated>()
        val typeContainer = mutableMapOf<String, MutableList<Pair<String, List<KSType>>>>()
        val templateCollector: MutableMap<String, TemplateMultiSource> = mutableMapOf()
        val fileCollector: MutableList<KSFile> = mutableListOf()

        annotated.forEach { annotatedSymbol ->
            val annotation = findKMockAnnotation(annotatedSymbol.annotations, condition)

            if (annotation == null || annotation.arguments.isEmpty()) {
                illAnnotated.add(annotatedSymbol)
            } else {
                val sourceIndicator = determineSourceCategory(annotation)
                val interfaces = typeContainer.getOrElse(sourceIndicator) { mutableListOf() }

                interfaces.add(
                    Pair(
                        determineMockName(annotation),
                        annotation.arguments.last().value as List<KSType>
                    )
                )
                typeContainer[sourceIndicator] = interfaces
                fileCollector.add(annotatedSymbol.containingFile!!)
            }
        }

        resolveInterfaces(typeContainer, templateCollector)

        return Aggregated(
            illAnnotated,
            templateCollector.values.toList(),
            fileCollector
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

        return extractInterfaces(annotated) { annotationName, _ ->
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

        return extractInterfaces(annotated, ::isSharedAnnotation)
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

        return extractInterfaces(annotated) { annotationName, _ ->
            ANNOTATION_PLATFORM_MULTI_NAME == annotationName
        }
    }

    companion object : ProcessorContract.AggregatorFactory<MultiSourceAggregator> {
        override fun getInstance(
            logger: KSPLogger,
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
                annotationFilter = annotationFilter,
                sourceSetValidator = sourceSetValidator,
                generics = generics,
                customAnnotations = additionalAnnotations,
            )
        }
    }
}