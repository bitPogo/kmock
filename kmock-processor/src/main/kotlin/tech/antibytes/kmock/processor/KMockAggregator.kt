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
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toClassName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockAggregator(
    private val logger: KSPLogger
) : ProcessorContract.Aggregator {
    private fun findKMockAnnotation(annotations: Sequence<KSAnnotation>): KSAnnotation {
        val annotation = annotations.first { annotation ->
            val annotationString = annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
            ANNOTATION_NAME == annotationString ||
                ANNOTATION_COMMON_NAME == annotationString ||
                ANNOTATION_SHARED_NAME == annotationString
        }

        return annotation
    }

    private fun resolveInterface(
        interfaze: KSDeclaration,
        sourceIndicator: String,
        interfaceCollector: MutableMap<String, ProcessorContract.InterfaceSource>
    ) {
        when {
            interfaze !is KSClassDeclaration -> logger.error("Cannot stub non interfaces.")
            interfaze.classKind != ClassKind.INTERFACE -> logger.error("Cannot stub non interface ${interfaze.toClassName()}.")
            else -> interfaceCollector[interfaze.qualifiedName!!.asString()] = ProcessorContract.InterfaceSource(
                sourceIndicator,
                interfaze
            )
        }
    }

    private fun resolveInterfaces(
        raw: Map<String, MutableList<KSType>>,
        interfaceCollector: MutableMap<String, ProcessorContract.InterfaceSource>
    ) {
        raw.forEach { (sourceIndicator, interfaces) ->
            interfaces.forEach { value ->
                resolveInterface(value.declaration, sourceIndicator, interfaceCollector)
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
        val interfaceCollector: MutableMap<String, ProcessorContract.InterfaceSource> = mutableMapOf()
        val fileCollector: MutableList<KSFile> = mutableListOf()

        annotated.forEach { annotatedSymbol ->
            val stub = findKMockAnnotation(annotatedSymbol.annotations)

            if (stub.arguments.isEmpty()) {
                illAnnotated.add(annotatedSymbol)
            } else {
                val sourceIndicator = determineSourceCategory(stub)
                val interfaces = typeContainer.getOrElse(sourceIndicator) { mutableListOf() }

                interfaces.addAll(stub.arguments.last().value as List<KSType>)
                typeContainer[sourceIndicator] = interfaces
                fileCollector.add(annotatedSymbol.containingFile!!)
            }
        }

        resolveInterfaces(typeContainer, interfaceCollector)

        return ProcessorContract.Aggregated(
            illAnnotated,
            interfaceCollector.values.toList(),
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
            typeParameter.first().isReified &&
            typeParameter.first().toString() == returnType.toString()
    }

    private fun validateRelaxer(symbol: KSFunctionDeclaration) {
        val isValid = symbol.modifiers.contains(Modifier.INLINE) &&
            hasValidParameter(symbol.parameters) &&
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
