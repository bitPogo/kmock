/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.aggregation

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.AggregatorFactory
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationFilter
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_NAME
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.SourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockSourceAggregator(
    private val logger: KSPLogger,
    private val annotationFilter: AnnotationFilter,
    private val sourceSetValidator: SourceSetValidator,
    private val generics: GenericResolver,
    private val customAnnotations: Map<String, String>,
    private val aliases: Map<String, String>
) : SourceAggregator {
    private fun resolveAnnotationName(
        annotation: KSAnnotation
    ): String = annotation.annotationType.resolve().declaration.qualifiedName!!.asString()

    private fun findKMockAnnotation(
        annotations: Sequence<KSAnnotation>,
        condition: (String, KSAnnotation) -> Boolean
    ): KSAnnotation? {
        val annotation = annotations.firstOrNull { annotation ->
            condition(resolveAnnotationName(annotation), annotation)
        }

        return annotation
    }

    private fun resolveGenerics(template: KSDeclaration): Map<String, List<KSTypeReference>>? {
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        return generics.extractGenerics(
            template,
            typeResolver,
        )
    }

    private fun resolveInterface(
        interfaze: KSDeclaration,
        sourceIndicator: String,
        templateCollector: MutableMap<String, TemplateSource>
    ) {
        when {
            interfaze !is KSClassDeclaration -> logger.error("Cannot stub non interfaces.")
            interfaze.classKind != ClassKind.INTERFACE -> logger.error("Cannot stub non interface ${interfaze.toClassName()}.")
            else -> {
                val templateName = interfaze.qualifiedName!!.asString()
                templateCollector[templateName + sourceIndicator] = TemplateSource(
                    indicator = sourceIndicator,
                    templateName = aliases[templateName] ?: interfaze.simpleName.asString(),
                    packageName = interfaze.packageName.asString(),
                    template = interfaze,
                    generics = resolveGenerics(interfaze)
                )
            }
        }
    }

    private fun resolveInterfaces(
        raw: Map<String, MutableList<KSType>>,
        templateCollector: MutableMap<String, TemplateSource>
    ) {
        raw.forEach { (sourceIndicator, interfaces) ->
            interfaces.forEach { value ->
                resolveInterface(value.declaration, sourceIndicator, templateCollector)
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
        val typeContainer = mutableMapOf<String, MutableList<KSType>>()
        val templateCollector: MutableMap<String, TemplateSource> = mutableMapOf()
        val fileCollector: MutableList<KSFile> = mutableListOf()

        annotated.forEach { annotatedSymbol ->
            val annotation = findKMockAnnotation(annotatedSymbol.annotations, condition)

            if (annotation == null || annotation.arguments.isEmpty()) {
                illAnnotated.add(annotatedSymbol)
            } else {
                val sourceIndicator = determineSourceCategory(annotation)
                val interfaces = typeContainer.getOrElse(sourceIndicator) { mutableListOf() }

                interfaces.addAll(annotation.arguments.last().value as List<KSType>)
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
            ANNOTATION_COMMON_NAME,
            false
        )
    }

    override fun extractCommonInterfaces(
        resolver: Resolver
    ): Aggregated<TemplateSource>  {
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

        val customShared = customAnnotations.keys.map { annotation ->
            resolver.getSymbolsWithAnnotation(
                annotation,
                false
            )
        }.toTypedArray()

        return sequenceOf(shared, *customShared).flatten()
    }

    private fun isSharedAnnotation(annotationName: String, annotation: KSAnnotation): Boolean {
        return (annotationName in customAnnotations.keys && annotationFilter.isApplicableAnnotation(annotation)) ||
            (ANNOTATION_SHARED_NAME == annotationName && sourceSetValidator.isValidateSourceSet(annotation))
    }

    override fun extractSharedInterfaces(
        resolver: Resolver
    ): Aggregated<TemplateSource>  {
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
    ): Aggregated<TemplateSource>  {
        val annotated = fetchPlatformAnnotated(resolver)

        return extractInterfaces(annotated) { annotationName, _ ->
            ANNOTATION_PLATFORM_NAME == annotationName
        }
    }

    companion object : AggregatorFactory<SourceAggregator> {
        override fun getInstance(
            logger: KSPLogger,
            sourceSetValidator: SourceSetValidator,
            annotationFilter: AnnotationFilter,
            generics: GenericResolver,
            customAnnotations: Map<String, String>,
            aliases: Map<String, String>,
        ): SourceAggregator {
            val additionalAnnotations = annotationFilter.filterAnnotation(
                customAnnotations
            )

            return KMockSourceAggregator(
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
