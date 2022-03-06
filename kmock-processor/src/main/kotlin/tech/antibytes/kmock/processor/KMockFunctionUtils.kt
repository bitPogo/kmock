/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName

internal class KMockFunctionUtils : ProcessorContract.FunctionUtils {
    private fun resolveBound(type: KSTypeParameter): List<KSTypeReference> = type.bounds.toList()

    override fun resolveGeneric(
        template: KSDeclaration,
        resolver: TypeParameterResolver
    ): Map<String, List<KSTypeReference>>? {
        return if (template.typeParameters.isEmpty()) {
            null
        } else {
            val generic: MutableMap<String, List<KSTypeReference>> = mutableMapOf()
            template.typeParameters.forEach { type ->
                generic[type.toTypeVariableName(resolver).toString()] = resolveBound(type)
            }

            generic
        }
    }

    override fun mapGeneric(
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): List<TypeVariableName> = generics.map { (type, bounds) ->
        TypeVariableName(
            type,
            bounds = bounds.map { ksReference -> ksReference.resolve().toTypeName(typeResolver) }
        )
    }
}
