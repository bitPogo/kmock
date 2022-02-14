/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import tech.antibytes.kmock.MagicStub

internal class KMockAggregator : ProcessorContract.Aggregator {
    private fun findKMockAnnotation(annotations: Sequence<KSAnnotation>): KSAnnotation {
        val annotation = annotations.first { annotation ->
            MagicStub::class.qualifiedName!! == annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        }

        return annotation
    }

    override fun extractInterfaces(
        annotated: Sequence<KSAnnotated>,
        validAnnotations: MutableList<KSAnnotation>
    ): List<KSAnnotated> {
        val illAnnotated = mutableListOf<KSAnnotated>()

        annotated.forEach { annotatedSymbol ->
            val magicStub = findKMockAnnotation(annotatedSymbol.annotations)

            if (magicStub.arguments.isEmpty()) {
                illAnnotated.add(annotatedSymbol)
            } else {
                validAnnotations.add(magicStub)
            }
        }

        return illAnnotated
    }
}
