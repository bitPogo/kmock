/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.kotlinpoet

import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.tags.TypeAliasTag

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt#L1
// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/utils.kt#L16
internal fun TypeName.rawType(): ClassName {
    return findRawType() ?: throw IllegalArgumentException("Cannot get raw type from $this")
}

internal fun TypeName.findRawType(): ClassName? {
    return when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        is LambdaTypeName -> {
            var count = parameters.size
            if (receiver != null) {
                count++
            }
            val functionSimpleName = "Function$count"
            ClassName("kotlin", functionSimpleName)
        }
        else -> null
    }
}

internal fun ClassName.withTypeArguments(arguments: List<TypeName>): TypeName {
    return if (arguments.isEmpty()) {
        this
    } else {
        this.parameterizedBy(arguments)
    }
}

internal fun KSDeclaration.toClassNameInternal(): ClassName {
    require(!isLocal()) {
        "Local/anonymous classes are not supported!"
    }
    val pkgName = packageName.asString()
    val typesString = checkNotNull(qualifiedName).asString().removePrefix("$pkgName.")

    val simpleNames = typesString
        .split(".")
    return ClassName(pkgName, simpleNames)
}

internal fun TypeVariableName.copy(name: String): TypeVariableName {
    return TypeVariableName(
        name,
        bounds = this.bounds,
        variance = this.variance,
    ).copy(
        reified = this.isReified,
        tags = this.tags,
        nullable = this.isNullable,
        annotations = this.annotations,
    )
}

private fun extractAliasTypeResolver(
    declaration: KSTypeAlias,
    typeParameterResolver: TypeParameterResolver,
): TypeParameterResolver {
    return if (declaration.typeParameters.isEmpty()) {
        typeParameterResolver
    } else {
        declaration.typeParameters.toTypeParameterResolver(typeParameterResolver)
    }
}

private fun KSTypeAlias.mapAbbreviatedType(
    typeAliasTypeArguments: List<KSTypeArgument>,
    abbreviatedType: KSType,
): List<KSTypeArgument> {
    return abbreviatedType.arguments
        .map { typeArgument ->
            // Check if type argument is a reference to a typealias type parameter, and not an actual type.
            val typeAliasTypeParameterIndex = this.typeParameters.indexOfFirst { typeAliasTypeParameter ->
                typeAliasTypeParameter.name.asString() == typeArgument.type.toString()
            }
            if (typeAliasTypeParameterIndex >= 0) {
                typeAliasTypeArguments[typeAliasTypeParameterIndex]
            } else {
                typeArgument
            }
        }
}

internal fun KSTypeAlias.resolveAlias(
    arguments: List<KSTypeArgument>,
    typeParameterResolver: TypeParameterResolver,
): Triple<KSType, List<KSTypeArgument>, TypeParameterResolver> {
    var typeAlias: KSTypeAlias = this
    var resolvedArguments = arguments
    var resolvedType: KSType
    var mappedArgs: List<KSTypeArgument>
    var extraResolver: TypeParameterResolver = typeParameterResolver

    do {
        resolvedType = typeAlias.type.resolve()
        mappedArgs = typeAlias.mapAbbreviatedType(
            typeAliasTypeArguments = resolvedArguments,
            abbreviatedType = resolvedType,
        )
        extraResolver = extractAliasTypeResolver(this, extraResolver)

        if (resolvedType.declaration is KSTypeAlias) {
            typeAlias = resolvedType.declaration as KSTypeAlias
            resolvedArguments = mappedArgs
        }
    } while (resolvedType.declaration is KSTypeAlias)

    return Triple(resolvedType, mappedArgs, extraResolver)
}

internal fun KSTypeAlias.parameterizedBy(
    abbreviateType: TypeName,
    typeArguments: List<TypeName>,
): TypeName {
    return this.toClassNameInternal()
        .withTypeArguments(typeArguments)
        .copy(tags = mapOf(TypeAliasTag::class to TypeAliasTag(abbreviateType)))
}
