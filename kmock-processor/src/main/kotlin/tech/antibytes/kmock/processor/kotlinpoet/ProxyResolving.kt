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
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.mock.resolveGeneric
import tech.antibytes.kmock.processor.utils.extractParameter

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt#L1
// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/utils.kt#L16

/* Contains Workaround to overcome KSP misalignment on inherited types with function TypeParameter */
// see: https://github.com/google/ksp/issues/958
internal fun KSTypeReference.toProxyPairTypeName(
    generics: Map<String, GenericDeclaration>,
    rootTypeArguments: List<KSTypeArgument>,
    typeParameterResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> {
    return resolve().toProxyPairTypeName(
        typeParameterResolver = typeParameterResolver,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
        typeArguments = element?.typeArguments.orEmpty(),
    )
}

private fun KSTypeArgument.resolveVariance(
    type: KSTypeReference,
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    return when (variance) {
        Variance.COVARIANT -> {
            val (methodTypeName, proxyTypeName) = type.toProxyPairTypeName(
                generics = generics,
                rootTypeArguments = rootTypeArguments,
                typeParameterResolver = typeParameterResolver,
            )

            WildcardTypeName.producerOf(methodTypeName) to WildcardTypeName.producerOf(proxyTypeName)
        }
        Variance.CONTRAVARIANT -> {
            val (methodTypeName, proxyTypeName) = type.toProxyPairTypeName(
                generics = generics,
                rootTypeArguments = rootTypeArguments,
                typeParameterResolver = typeParameterResolver,
            )

            WildcardTypeName.consumerOf(methodTypeName) to WildcardTypeName.producerOf(proxyTypeName)
        }
        Variance.STAR -> STAR to STAR
        Variance.INVARIANT -> {
            val (methodTypeName, proxyTypeName) = type.toProxyPairTypeName(
                generics = generics,
                rootTypeArguments = rootTypeArguments,
                typeParameterResolver = typeParameterResolver,
            )

            methodTypeName to proxyTypeName
        }
    }
}

internal fun KSTypeArgument.toProxyPairTypeName(
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    val type = type ?: return STAR_PAIR

    return resolveVariance(
        type = type,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
        typeParameterResolver = typeParameterResolver,
    )
}

private fun List<KSTypeArgument>.toProxyPairTypeName(
    generics: Map<String, GenericDeclaration>,
    rootTypeArguments: List<KSTypeArgument>,
    typeParameterResolver: TypeParameterResolver,
): Pair<List<TypeName>, List<TypeName>> {
    val methodTypes: MutableList<TypeName> = mutableListOf()
    val proxyTypes: MutableList<TypeName> = mutableListOf()

    this.forEach { argument ->
        val (methodType, proxyType) = argument.toProxyPairTypeName(
            typeParameterResolver = typeParameterResolver,
            generics = generics,
            rootTypeArguments = rootTypeArguments,
        )

        methodTypes.add(methodType)
        proxyTypes.add(proxyType)
    }

    return methodTypes to proxyTypes
}

private fun KSType.abbreviateType(
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
    isNullable: Boolean,
    typeArguments: List<KSTypeArgument>,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    val (methodType, proxyType) = this.toProxyPairTypeName(
        typeParameterResolver = typeParameterResolver,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
        typeArguments = emptyList(),
    )

    val (methodArgument, proxyArguments) = typeArguments.toProxyPairTypeName(
        typeParameterResolver = typeParameterResolver,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
    )

    val parameterizedMethodType = methodType
        .rawType()
        .withTypeArguments(methodArgument)
        .copy(nullable = isNullable)

    val parameterizedProxyType = proxyType
        .rawType()
        .withTypeArguments(proxyArguments)
        .copy(nullable = isNullable)

    return parameterizedMethodType to parameterizedProxyType
}

private fun TypeName.transferProperties(source: TypeName): TypeName {
    return this.copy(nullable = this.isNullable || source.isNullable, annotations = source.annotations)
}

private fun KSType.toProxyPairTypeName(
    typeParameterResolver: TypeParameterResolver,
    generics: Map<String, GenericDeclaration>,
    typeArguments: List<KSTypeArgument>,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    require(!isError) {
        "Error type '$this' is not resolvable in the current round of processing."
    }

    val (methodType, proxyType) = when (val declaration = this.declaration) {
        is KSClassDeclaration -> {
            val (methodArguments, proxyArguments) = arguments.toProxyPairTypeName(
                generics = generics,
                typeParameterResolver = typeParameterResolver,
                rootTypeArguments = rootTypeArguments,
            )

            declaration.toClassName().withTypeArguments(methodArguments) to
                declaration.toClassName().withTypeArguments(proxyArguments)
        }
        is KSTypeParameter -> {
            val name = declaration.name.getShortName()
            val methodType = typeParameterResolver[name]

            methodType to if (name in generics) {
                generics[name]!!.resolveGeneric().transferProperties(methodType)
            } else {
                methodType
            }
        }
        is KSTypeAlias -> {
            val (resolvedType, mappedArgs, extraResolver) = declaration.resolveAlias(
                arguments = arguments,
                typeParameterResolver = typeParameterResolver,
            )

            val (abbreviatedMethodType, abbreviatedProxyType) = resolvedType.abbreviateType(
                generics = generics,
                typeParameterResolver = extraResolver,
                isNullable = isMarkedNullable,
                typeArguments = mappedArgs,
                rootTypeArguments = rootTypeArguments,
            )

            val (aliasMethodArgs, aliasProxyArgs) = typeArguments.toProxyPairTypeName(
                generics = generics,
                typeParameterResolver = typeParameterResolver,
                rootTypeArguments = rootTypeArguments,
            )
            val methodAlias = declaration.parameterizedBy(abbreviatedMethodType, aliasMethodArgs)

            val proxyAlias = declaration
                .parameterizedBy(abbreviatedProxyType, aliasProxyArgs)
                .transferProperties(methodAlias)

            methodAlias to proxyAlias
        }
        else -> error("Unsupported type: $declaration")
    }

    return methodType.copy(nullable = isMarkedNullable) to
        proxyType.copy(nullable = (proxyType.isNullable || isMarkedNullable))
}

internal fun KSTypeReference.toProxyPairTypeName(
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> {
    val typeElements = extractParameter()
    val type = resolve()

    return type.toProxyPairTypeName(
        generics = generics,
        typeParameterResolver = typeParameterResolver,
        typeArguments = typeElements,
        rootTypeArguments = typeElements,
    )
}

private val STAR_PAIR = STAR to STAR
