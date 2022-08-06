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

private class Resolver(
    private val parent: TypeParameterResolver? = null,
    override val parametersMap: Map<String, TypeVariableName>
) : TypeParameterResolver {
    override fun get(index: String): TypeVariableName {
        return parametersMap.getOrElse(index) {
            parent?.get(index) ?: throw IllegalStateException(
                "Unknown type parameter $index, only ${parametersMap.keys} are known."
            )
        }
    }
}

// Fill the parametersMap. Need to do sequentially and allow for referencing previously defined params
private fun List<KSTypeParameter>.initializeTypeParameterResolver(): MutableMap<String, TypeVariableName> {
    return this.associate { parameter ->
        // Put the simple typevar in first, then it can be referenced in the full toTypeVariable()
        // replacement later that may add bounds referencing this.
        val id = parameter.name.getShortName()
        id to TypeVariableName(id)
    }.toMutableMap()
}

internal fun List<KSTypeParameter>.toTypeParameterResolver(
    parent: TypeParameterResolver? = null,
): TypeParameterResolver {
    val parametersMap = initializeTypeParameterResolver()

    val resolver = Resolver(parent, parametersMap)

    for (typeVar in this) {
        val id = typeVar.name.getShortName()
        // Now replace it with the full version.
        parametersMap[id] = typeVar.toTypeVariableName(resolver)
    }

    return resolver
}

internal fun KSTypeParameter.toTypeVariableName(
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
    typeParamResolver: TypeParameterResolver,
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
            decl.toClassName()
                .withTypeArguments(
                    typeArguments.map { it.toTypeNameX(typeParamResolver) }
                )
        }
        is KSTypeParameter -> typeParamResolver[decl.name.getShortName()]
        is KSTypeAlias -> {
            var typeAlias: KSTypeAlias = decl
            var arguments = typeArguments

            var resolvedType: KSType
            var mappedArgs: List<KSTypeArgument>
            var extraResolver: TypeParameterResolver = typeParamResolver
            while (true) {
                resolvedType = typeAlias.type.resolve()
                mappedArgs = typeAlias.mapAbbreviatedType(
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
                .withTypeArguments(arguments.map { it.toTypeNameX(extraResolver) })

            val aliasArgs = typeArguments.map { it.toTypeNameX(extraResolver) }

            decl.toClassNameInternal()
                .withTypeArguments(aliasArgs)
                .copy(tags = mapOf(TypeAliasTag::class to TypeAliasTag(abbreviatedType)))
        }
        else -> error("Unsupported type: $declaration")
    }

    return type.copy(nullable = isMarkedNullable)
}

private fun KSTypeArgument.toTypeNameX(
    typeParamResolver: TypeParameterResolver,
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
    typeParamResolver: TypeParameterResolver,
): TypeName {
    return resolve().toTypeNameX(
        typeParamResolver,
        element?.typeArguments ?: emptyList(),
    )
}
