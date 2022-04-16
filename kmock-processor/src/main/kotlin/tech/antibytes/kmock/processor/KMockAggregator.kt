/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_NAME
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockAggregator(
    private val logger: KSPLogger,
    private val knownSourceSets: Set<String>,
    private val generics: GenericResolver,
    private val aliases: Map<String, String>
) : ProcessorContract.Aggregator {
    private fun resolveAnnotationName(
        annotation: KSAnnotation
    ): String = annotation.annotationType.resolve().declaration.qualifiedName!!.asString()

    private fun validateSourceIndicator(
        annotation: KSAnnotation
    ): Boolean {
        return when (val actualSourceSet = annotation.arguments.firstOrNull()?.value) {
            !is String -> false
            !in knownSourceSets -> {
                logger.warn("$actualSourceSet is not a applicable sourceSet!")
                false
            }
            else -> true
        }
    }

    private fun findKMockAnnotation(annotations: Sequence<KSAnnotation>): KSAnnotation? {
        val annotation = annotations.firstOrNull { annotation ->
            val annotationName = resolveAnnotationName(annotation)
            ANNOTATION_PLATFORM_NAME == annotationName ||
                ANNOTATION_COMMON_NAME == annotationName ||
                (ANNOTATION_SHARED_NAME == annotationName && validateSourceIndicator(annotation))
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
                    template = interfaze,
                    alias = aliases[templateName],
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

    private fun determineSourceCategory(stub: KSAnnotation): String {
        return if (stub.arguments.size == 2) {
            stub.arguments.first().value as String
        } else {
            ""
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun extractInterfaces(
        annotated: Sequence<KSAnnotated>
    ): ProcessorContract.Aggregated {
        val illAnnotated = mutableListOf<KSAnnotated>()
        val typeContainer = mutableMapOf<String, MutableList<KSType>>()
        val templateCollector: MutableMap<String, TemplateSource> = mutableMapOf()
        val fileCollector: MutableList<KSFile> = mutableListOf()

        annotated.forEach { annotatedSymbol ->
            val stub = findKMockAnnotation(annotatedSymbol.annotations)

            if (stub == null || stub.arguments.isEmpty()) {
                illAnnotated.add(annotatedSymbol)
            } else {
                val sourceIndicator = determineSourceCategory(stub)
                val interfaces = typeContainer.getOrElse(sourceIndicator) { mutableListOf() }

                interfaces.addAll(stub.arguments.last().value as List<KSType>)
                typeContainer[sourceIndicator] = interfaces
                fileCollector.add(annotatedSymbol.containingFile!!)
            }
        }

        resolveInterfaces(typeContainer, templateCollector)

        return ProcessorContract.Aggregated(
            illAnnotated,
            templateCollector.values.toList(),
            fileCollector
        )
    }

    private fun hasValidParameter(parameter: List<KSValueParameter>): Boolean {
        return parameter.size == 1 &&
            parameter.first().type.resolve().toString() == "String"
    }

    private fun hasValidTypeParameter(
        typeParameter: List<KSTypeParameter>,
        returnType: KSTypeReference?
    ): Boolean {
        return typeParameter.size == 1 &&
            typeParameter.first().bounds.toList().isEmpty() &&
            typeParameter.first().toString() == returnType.toString()
    }

    private fun validateRelaxer(symbol: KSFunctionDeclaration) {
        val isValid = hasValidParameter(symbol.parameters) &&
            hasValidTypeParameter(symbol.typeParameters, symbol.returnType)

        if (!isValid) {
            logger.error("Invalid Relaxer!")
        }
    }

    override fun extractRelaxer(annotated: Sequence<KSAnnotated>): Relaxer? {
        val annotatedSymbol = annotated.firstOrNull()

        return if (annotatedSymbol is KSFunctionDeclaration) {
            validateRelaxer(annotatedSymbol)
            Relaxer(
                annotatedSymbol.packageName.asString(),
                annotatedSymbol.simpleName.asString()
            )
        } else {
            null
        }
    }
}
