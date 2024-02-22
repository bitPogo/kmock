/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.aggregation

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.RelaxationAggregator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockRelaxationAggregator(
    private val logger: KSPLogger,
) : RelaxationAggregator {
    private fun hasValidParameter(parameter: List<KSValueParameter>): Boolean {
        return parameter.size == 1 &&
            parameter.first().type.resolve().toString() == "String"
    }

    private fun hasValidTypeParameter(
        typeParameter: List<KSTypeParameter>,
        returnType: KSTypeReference?,
    ): Boolean {
        return typeParameter.size == 1 &&
            typeParameter.first().bounds.toList().isEmpty() &&
            typeParameter.first().toString() == returnType.toString()
    }

    private fun validateRelaxer(symbol: KSFunctionDeclaration) {
        val isValid = hasValidParameter(symbol.parameters) &&
            hasValidTypeParameter(symbol.typeParameters, symbol.returnType)

        if (!isValid) {
            logger.warn("Invalid Relaxer!")
        }
    }

    private fun fetchRelaxerAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ProcessorContract.RELAXATION_NAME,
            false,
        )
    }

    override fun extractRelaxer(resolver: Resolver): Relaxer? {
        val annotatedSymbol = fetchRelaxerAnnotated(resolver).firstOrNull()

        return if (annotatedSymbol is KSFunctionDeclaration) {
            validateRelaxer(annotatedSymbol)

            Relaxer(
                annotatedSymbol.packageName.asString(),
                annotatedSymbol.simpleName.asString(),
                annotatedSymbol.containingFile!!,
            )
        } else {
            null
        }
    }
}
