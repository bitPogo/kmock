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
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ksp.toClassName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_NAME

internal class KMockAggregator(
    private val logger: KSPLogger
) : ProcessorContract.Aggregator {
    private fun findKMockAnnotation(annotations: Sequence<KSAnnotation>): KSAnnotation {
        val annotation = annotations.first { annotation ->
            val annotationString = annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
            ANNOTATION_NAME == annotationString || ANNOTATION_COMMON_NAME == annotationString
        }

        return annotation
    }

    private fun resolveInterfaces(
        raw: List<KSType>,
        interfaceCollector: MutableMap<String, KSClassDeclaration>
    ) {
        raw.forEach { value ->
            val declaration = value.declaration
            when {
                declaration !is KSClassDeclaration -> logger.error("Cannot stub non interfaces.")
                declaration.classKind != ClassKind.INTERFACE -> logger.error("Cannot stub non interface ${declaration.toClassName()}.")
                else -> interfaceCollector[declaration.qualifiedName!!.asString()] = declaration
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun extractInterfaces(
        annotated: Sequence<KSAnnotated>
    ): ProcessorContract.Aggregated {
        val illAnnotated = mutableListOf<KSAnnotated>()
        val typeContainer = mutableListOf<KSType>()
        val interfaceCollector: MutableMap<String, KSClassDeclaration> = mutableMapOf()
        val fileCollector: MutableList<KSFile> = mutableListOf()

        annotated.forEach { annotatedSymbol ->
            val magicStub = findKMockAnnotation(annotatedSymbol.annotations)

            if (magicStub.arguments.isEmpty()) {
                illAnnotated.add(annotatedSymbol)
            } else {
                typeContainer.addAll(magicStub.arguments.first().value as List<KSType>)
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
}
