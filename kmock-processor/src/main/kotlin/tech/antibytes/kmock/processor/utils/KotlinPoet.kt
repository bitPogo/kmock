/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.tags.TypeAliasTag

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/utils.kt#L16
private fun TypeName.rawType(): ClassName {
    return findRawType() ?: throw IllegalArgumentException("Cannot get raw type from $this")
}

private fun TypeName.findRawType(): ClassName? {
    return when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        is LambdaTypeName -> {
            var count = parameters.size
            if (receiver != null) {
                count++
            }
            val functionSimpleName = if (count >= 23) {
                "FunctionN"
            } else {
                "Function$count"
            }
            ClassName("kotlin.jvm.functions", functionSimpleName)
        }
        else -> null
    }
}

private fun ClassName.withTypeArguments(arguments: List<TypeName>): TypeName {
    return if (arguments.isEmpty()) {
        this
    } else {
        this.parameterizedBy(arguments)
    }
}

private fun KSDeclaration.toClassNameInternal(): ClassName {
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

internal fun WildcardTypeName.toTypeVariableName(): TypeVariableName {
    val type = this.outTypes.first() as TypeVariableName

    return TypeVariableName(
        type.name,
        bounds = type.bounds,
    ).copy(
        reified = type.isReified,
        tags = type.tags,
        annotations = type.annotations,
        nullable = type.isNullable
    )
}

private fun extractAliasTypeResolver(
    declaration: KSTypeAlias,
    typeParameterResolver: TypeParameterResolver
): TypeParameterResolver {
    return if (declaration.typeParameters.isEmpty()) {
        typeParameterResolver
    } else {
        declaration.typeParameters.toTypeParameterResolver(typeParameterResolver)
    }
}

private fun KSTypeAlias.abbreviateType(
    typeParameterResolver: TypeParameterResolver,
    isNullable: Boolean,
    typeArguments: List<TypeName>
): TypeName {
    return this.type.resolve()
        .toTypeName(typeParameterResolver)
        .copy(nullable = isNullable)
        .rawType()
        .withTypeArguments(typeArguments)
}

private fun KSTypeAlias.parameterizedBy(
    abbreviateType: TypeName,
    typeArguments: List<TypeName>,
): TypeName {
    return this.toClassNameInternal()
        .withTypeArguments(typeArguments)
        .copy(tags = mapOf(TypeAliasTag::class to TypeAliasTag(abbreviateType)))
}

internal fun KSTypeReference.mapArgumentType(
    typeParameterResolver: TypeParameterResolver = TypeParameterResolver.EMPTY,
    mapping: Map<String, String>,
): TypeName {
    return resolve().mapArgumentType(
        typeParameterResolver = typeParameterResolver,
        mapping = mapping,
        typeArguments = element?.typeArguments.orEmpty(),
    )
}

private fun KSTypeArgument.mapArgumentType(
    typeParameterResolver: TypeParameterResolver = TypeParameterResolver.EMPTY,
    mapping: Map<String, String>,
): TypeName {
    val typeName = type?.mapArgumentType(
        typeParameterResolver = typeParameterResolver,
        mapping = mapping,
    ) ?: return STAR
    return when (variance) {
        Variance.COVARIANT -> WildcardTypeName.producerOf(typeName)
        Variance.CONTRAVARIANT -> WildcardTypeName.consumerOf(typeName)
        Variance.STAR -> STAR
        Variance.INVARIANT -> typeName
    }
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
                arguments.map { argument ->
                    argument.mapArgumentType(
                        typeParameterResolver = typeParameterResolver,
                        mapping = mapping,
                    )
                }
            )
        }
        is KSTypeAlias -> {
            val extraResolver = extractAliasTypeResolver(declaration, typeParameterResolver)

            val mappedArgs = arguments.map { argument ->
                argument.mapArgumentType(
                    typeParameterResolver = typeParameterResolver,
                    mapping = mapping,
                )
            }

            val abbreviatedType = declaration.abbreviateType(
                typeParameterResolver = extraResolver,
                isNullable = isMarkedNullable,
                typeArguments = mappedArgs
            )

            val aliasArgs = typeArguments.map { argument ->
                argument.mapArgumentType(
                    typeParameterResolver = typeParameterResolver,
                    mapping = mapping,
                )
            }

            declaration.parameterizedBy(abbreviatedType, aliasArgs)
        }
        else -> error("Unsupported type: $declaration")
    }

    return type.copy(nullable = isMarkedNullable)
}

/* Workaround to overcome KSP misalignment on inherited types with function TypeParameter */
// see: https://github.com/google/ksp/issues/958
// TODO - Remove the following methods when KSP fixes this
internal fun KSTypeArgument.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    typeParameterResolver: TypeParameterResolver,
    rootTypeArguments: List<KSTypeArgument>,
): TypeName {
    val typeName = type?.toSecuredTypeName(
        inheritedVarargArg = inheritedVarargArg,
        typeParameterResolver = typeParameterResolver,
        rootTypeArguments = rootTypeArguments,
    ) ?: return STAR
    return when (variance) {
        Variance.COVARIANT -> WildcardTypeName.producerOf(typeName)
        Variance.CONTRAVARIANT -> WildcardTypeName.consumerOf(typeName)
        Variance.STAR -> STAR
        Variance.INVARIANT -> typeName
    }
}

private fun KSClassDeclaration.isMisalignedVararg(
    inheritedVarargArg: Boolean,
    arguments: List<TypeName>,
    rootTypeArguments: List<KSTypeArgument>
): Boolean {
    val resolved = rootTypeArguments.firstOrNull()?.type?.resolve()?.declaration?.simpleName?.getShortName()
    val derived = arguments.firstOrNull()?.toString()

    return this.simpleName.getShortName() == "Array" && inheritedVarargArg && derived != resolved
}

private fun KSType.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    typeParameterResolver: TypeParameterResolver,
    typeArguments: List<KSTypeArgument>,
    rootTypeArguments: List<KSTypeArgument>,
): TypeName {
    require(!isError) {
        "Error type '$this' is not resolvable in the current round of processing."
    }

    var overrideNullability = false

    val type = when (val declaration = this.declaration) {
        is KSClassDeclaration -> {
            val arguments = arguments.map { argument ->
                argument.toSecuredTypeName(
                    inheritedVarargArg = inheritedVarargArg,
                    typeParameterResolver = typeParameterResolver,
                    rootTypeArguments = rootTypeArguments,
                )
            }

            if (declaration.isMisalignedVararg(inheritedVarargArg, arguments, rootTypeArguments)) {
                (arguments.first() as WildcardTypeName).toTypeVariableName().also { resolved ->
                    overrideNullability = resolved.isNullable
                }
            } else {
                declaration.toClassName().withTypeArguments(arguments)
            }
        }
        is KSTypeParameter -> {
            typeParameterResolver[declaration.name.getShortName()]
        }
        is KSTypeAlias -> {
            val extraResolver = extractAliasTypeResolver(declaration, typeParameterResolver)

            val mappedArgs = arguments.map { argument ->
                argument.toSecuredTypeName(
                    inheritedVarargArg = inheritedVarargArg,
                    typeParameterResolver = typeParameterResolver,
                    rootTypeArguments = rootTypeArguments,
                )
            }

            val abbreviatedType = declaration.abbreviateType(
                typeParameterResolver = extraResolver,
                isNullable = isMarkedNullable,
                typeArguments = mappedArgs
            )

            val aliasArgs = typeArguments.map { argument ->
                argument.toSecuredTypeName(
                    inheritedVarargArg = inheritedVarargArg,
                    typeParameterResolver = typeParameterResolver,
                    rootTypeArguments = rootTypeArguments,
                )
            }

            declaration.parameterizedBy(abbreviatedType, aliasArgs)
        }
        else -> error("Unsupported type: $declaration")
    }

    return type.copy(nullable = (isMarkedNullable || overrideNullability))
}

private fun KSTypeReference.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    typeParameterResolver: TypeParameterResolver,
    rootTypeArguments: List<KSTypeArgument>,
): TypeName {
    val typeElements = element?.typeArguments.orEmpty()

    return resolve().toSecuredTypeName(
        inheritedVarargArg = inheritedVarargArg,
        typeParameterResolver = typeParameterResolver,
        typeArguments = typeElements,
        rootTypeArguments = rootTypeArguments,
    )
}

internal fun KSTypeReference.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    typeParameterResolver: TypeParameterResolver,
): TypeName {
    val typeElements = element?.typeArguments.orEmpty()
    val type = resolve()

    return type.toSecuredTypeName(
        inheritedVarargArg = inheritedVarargArg,
        typeParameterResolver = typeParameterResolver,
        typeArguments = typeElements,
        rootTypeArguments = typeElements,
    )
}
