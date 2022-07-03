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
import com.google.devtools.ksp.symbol.Variance.CONTRAVARIANT
import com.google.devtools.ksp.symbol.Variance.COVARIANT
import com.google.devtools.ksp.symbol.Variance.INVARIANT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.tags.TypeAliasTag

public fun List<KSTypeParameter>.toTypeParameterResolver(
    parent: TypeParameterResolver? = null,
    sourceTypeHint: String = "<unknown>",
): TypeParameterResolver {
    val parametersMap = LinkedHashMap<String, TypeVariableName>()
    val typeParamResolver = { id: String ->
        parametersMap[id]
            ?: parent?.get(id)
            ?: throw IllegalStateException(
                "No type argument found for $id! Analyzed $sourceTypeHint with known parameters " +
                    "${parametersMap.keys}",
            )
    }

    val resolver = object : TypeParameterResolver {
        override val parametersMap: Map<String, TypeVariableName> = parametersMap

        override operator fun get(index: String): TypeVariableName = typeParamResolver(index)
    }

    // Fill the parametersMap. Need to do sequentially and allow for referencing previously defined params
    for (typeVar in this) {
        // Put the simple typevar in first, then it can be referenced in the full toTypeVariable()
        // replacement later that may add bounds referencing this.
        val id = typeVar.name.getShortName()
        parametersMap[id] = TypeVariableName(id)
    }

    for (typeVar in this) {
        val id = typeVar.name.getShortName()
        // Now replace it with the full version.
        parametersMap[id] = typeVar.toTypeVariableName(resolver)
    }

    return resolver
}

public fun KSTypeParameter.toTypeVariableName(
    typeParamResolver: TypeParameterResolver = TypeParameterResolver.EMPTY,
): TypeVariableName {
    val typeVarName = name.getShortName()
    val typeVarBounds = bounds.map { it.toTypeNameX(typeParamResolver) }.toList()
    val typeVarVariance = when (variance) {
        COVARIANT -> KModifier.OUT
        CONTRAVARIANT -> KModifier.IN
        else -> null
    }
    return TypeVariableName(typeVarName, bounds = typeVarBounds, variance = typeVarVariance)
}

private fun KSType.toTypeNameX(
    typeParamResolver: TypeParameterResolver = TypeParameterResolver.EMPTY,
): TypeName = toTypeNameX(typeParamResolver, emptyList())

private fun KSType.toTypeNameX(
    typeParamResolver: TypeParameterResolver,
    typeArguments: List<KSTypeArgument>,
): TypeName {
    require(!isError) {
        "Error type '$this' is not resolvable in the current round of processing."
    }
    val type = when (val decl = declaration) {
        is KSClassDeclaration -> {
            decl.toClassName().withTypeArguments(arguments.map { it.toTypeNameX(typeParamResolver) })
        }
        is KSTypeParameter -> typeParamResolver[decl.name.getShortName()]
        is KSTypeAlias -> {
            var typeAlias: KSTypeAlias = decl
            var arguments = arguments

            var resolvedType: KSType
            var mappedArgs: List<KSTypeArgument>
            var extraResolver: TypeParameterResolver = typeParamResolver
            while (true) {
                resolvedType = typeAlias.type.resolve()
                mappedArgs = mapTypeArgumentsFromTypeAliasToAbbreviatedType(
                    typeAlias = typeAlias,
                    typeAliasTypeArguments = arguments,
                    abbreviatedType = resolvedType,
                )
                extraResolver = if (typeAlias.typeParameters.isEmpty()) {
                    extraResolver
                } else {
                    typeAlias.typeParameters.toTypeParameterResolver(extraResolver)
                }

                typeAlias = resolvedType.declaration as? KSTypeAlias ?: break
                arguments = mappedArgs
            }

            val abbreviatedType = resolvedType
                .toTypeNameX(extraResolver)
                .copy(nullable = isMarkedNullable)
                .rawType()
                .withTypeArguments(mappedArgs.map { it.toTypeNameX(extraResolver) })

            val aliasArgs = typeArguments.map { it.toTypeNameX(typeParamResolver) }

            decl.toClassNameInternal()
                .withTypeArguments(aliasArgs)
                .copy(tags = mapOf(TypeAliasTag::class to TypeAliasTag(abbreviatedType)))
        }
        else -> error("Unsupported type: $declaration")
    }

    return type.copy(nullable = isMarkedNullable)
}

private fun mapTypeArgumentsFromTypeAliasToAbbreviatedType(
    typeAlias: KSTypeAlias,
    typeAliasTypeArguments: List<KSTypeArgument>,
    abbreviatedType: KSType,
): List<KSTypeArgument> {
    return abbreviatedType.arguments
        .map { typeArgument ->
            // Check if type argument is a reference to a typealias type parameter, and not an actual type.
            val typeAliasTypeParameterIndex = typeAlias.typeParameters.indexOfFirst { typeAliasTypeParameter ->
                typeAliasTypeParameter.name.asString() == typeArgument.type.toString()
            }
            if (typeAliasTypeParameterIndex >= 0) {
                typeAliasTypeArguments[typeAliasTypeParameterIndex]
            } else {
                typeArgument
            }
        }
}

private fun KSTypeArgument.toTypeNameX(
    typeParamResolver: TypeParameterResolver = TypeParameterResolver.EMPTY,
): TypeName {
    val type = this.type ?: return STAR
    return when (variance) {
        COVARIANT -> WildcardTypeName.producerOf(type.toTypeNameX(typeParamResolver))
        CONTRAVARIANT -> WildcardTypeName.consumerOf(type.toTypeNameX(typeParamResolver))
        Variance.STAR -> STAR
        INVARIANT -> type.toTypeNameX(typeParamResolver)
    }
}

private fun KSTypeReference.toTypeNameX(
    typeParamResolver: TypeParameterResolver = TypeParameterResolver.EMPTY,
): TypeName {
    val resolved = resolve()
    return resolved.toTypeNameX(
        typeParamResolver,
        resolved.arguments,
    )
}
