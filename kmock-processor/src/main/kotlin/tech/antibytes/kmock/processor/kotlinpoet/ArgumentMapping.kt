/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.kotlinpoet

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt#L1
// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/utils.kt#L16
private fun KSTypeArgument.resolveVariance(typeName: TypeName): TypeName {
    return when (variance) {
        Variance.COVARIANT -> WildcardTypeName.producerOf(typeName)
        Variance.CONTRAVARIANT -> WildcardTypeName.consumerOf(typeName)
        Variance.STAR -> STAR
        Variance.INVARIANT -> typeName
    }
}

private fun KSTypeArgument.mapArgumentType(
    typeParameterResolver: TypeParameterResolver,
    mapping: Map<String, String>,
): TypeName {
    val typeName = type?.mapArgumentType(
        typeParameterResolver = typeParameterResolver,
        mapping = mapping,
    ) ?: return STAR

    return resolveVariance(typeName)
}

private fun List<KSTypeArgument>.mapArgumentType(
    mapping: Map<String, String>,
    typeParameterResolver: TypeParameterResolver,
): List<TypeName> {
    return map { argument ->
        argument.mapArgumentType(
            typeParameterResolver = typeParameterResolver,
            mapping = mapping,
        )
    }
}

private fun KSType.abbreviateType(
    extraResolver: TypeParameterResolver,
    typeParameterResolver: TypeParameterResolver,
    isNullable: Boolean,
    typeArguments: List<KSTypeArgument>,
    mapping: Map<String, String>,
): TypeName {
    return this.mapArgumentType(extraResolver, mapping, emptyList())
        .rawType()
        .withTypeArguments(
            typeArguments.mapArgumentType(mapping, typeParameterResolver)
        )
        .copy(nullable = isNullable)
}

internal fun KSTypeReference.mapArgumentType(
    typeParameterResolver: TypeParameterResolver,
    mapping: Map<String, String>,
): TypeName {
    return resolve().mapArgumentType(
        typeParameterResolver = typeParameterResolver,
        mapping = mapping,
        typeArguments = element?.typeArguments.orEmpty(),
    )
}

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt#L60
internal fun KSType.mapArgumentType(
    typeParameterResolver: TypeParameterResolver,
    mapping: Map<String, String>,
    typeArguments: List<KSTypeArgument>,
): TypeName {
    require(!isError) {
        "Error type '$this' is not resolvable in the current round of processing."
    }

    val type = when (val declaration = this.declaration) {
        is KSTypeParameter -> {
            val parameterType = typeParameterResolver[declaration.name.getShortName()]

            if (parameterType.name in mapping) {
                parameterType.copy(mapping[parameterType.name]!!)
            } else {
                parameterType
            }
        }
        is KSClassDeclaration -> {
            declaration.toClassName().withTypeArguments(
                typeArguments.mapArgumentType(
                    typeParameterResolver = typeParameterResolver,
                    mapping = mapping,
                )
            )
        }
        is KSTypeAlias -> {
            val (resolvedType, mappedArgs, extraResolver) = declaration.resolveAlias(
                arguments = typeArguments,
                typeParameterResolver = typeParameterResolver,
            )

            val abbreviatedType = resolvedType.abbreviateType(
                extraResolver = extraResolver,
                typeParameterResolver = typeParameterResolver,
                isNullable = isMarkedNullable,
                typeArguments = mappedArgs,
                mapping = mapping,
            )

            val aliasArgs = typeArguments.mapArgumentType(
                mapping = mapping,
                typeParameterResolver = typeParameterResolver
            )

            declaration.parameterizedBy(abbreviatedType, aliasArgs)
        }
        else -> error("Unsupported type: $declaration")
    }

    return type.copy(nullable = isMarkedNullable)
}
