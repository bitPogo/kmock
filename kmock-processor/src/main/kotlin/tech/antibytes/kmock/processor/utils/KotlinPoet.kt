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
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.tags.TypeAliasTag
import tech.antibytes.kmock.processor.ProcessorContract.Companion.nullableAny
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.mock.resolveGeneric

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
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    val typeName = type?.toSecuredTypeName(
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        typeParameterResolver = typeParameterResolver,
        rootTypeArguments = rootTypeArguments,
    ) ?: return STAR to STAR
    return when (variance) {
        Variance.COVARIANT -> {
            WildcardTypeName.producerOf(typeName.first) to WildcardTypeName.producerOf(typeName.second)
        }
        Variance.CONTRAVARIANT -> {
            WildcardTypeName.consumerOf(typeName.first) to WildcardTypeName.producerOf(typeName.second)
        }
        Variance.STAR -> STAR to STAR
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
    val declaration = this.simpleName.getShortName().trimEnd('?')

    return inheritedVarargArg && (
        (declaration.endsWith("Array") && derived != resolved) ||
            ("kotlin.$declaration" in specialArrays)
        )
}

private fun TypeName.transferProperties(source: TypeName): TypeName {
    return this.copy(nullable = this.isNullable || source.isNullable, annotations = source.annotations)
}

private fun MutableList<TypeName>.resolveVararg(parent: KSClassDeclaration): TypeName {
    val typeName = this.firstOrNull()

    return when {
        typeName == null -> specialArrays["kotlin.${parent.simpleName.getShortName().trimEnd('?')}"]!!
        (typeName is WildcardTypeName && typeName.outTypes.first() is TypeVariableName) -> {
            typeName.outTypes.first()
        }
        typeName == STAR -> nullableAny
        (typeName is WildcardTypeName && typeName.outTypes.first() is ParameterizedTypeName) -> {
            typeName.outTypes.first()
        }
        (typeName is WildcardTypeName && typeName.outTypes.first() is ClassName) -> {
            typeName.outTypes.first()
        }
        else -> typeName
    }
}

private fun KSType.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    typeParameterResolver: TypeParameterResolver,
    generics: Map<String, GenericDeclaration>,
    typeArguments: List<KSTypeArgument>,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    require(!isError) {
        "Error type '$this' is not resolvable in the current round of processing."
    }

    var overrideNullability = false

    val (methodType, proxyType) = when (val declaration = this.declaration) {
        is KSClassDeclaration -> {
            val methodArguments: MutableList<TypeName> = mutableListOf()
            val proxyArguments: MutableList<TypeName> = mutableListOf()

            arguments.forEach { argument ->
                val (methodArgument, proxyArgument) = argument.toSecuredTypeName(
                    inheritedVarargArg = inheritedVarargArg,
                    generics = generics,
                    typeParameterResolver = typeParameterResolver,
                    rootTypeArguments = rootTypeArguments,
                )

                methodArguments.add(methodArgument)
                proxyArguments.add(proxyArgument)
            }

            if (declaration.isMisalignedVararg(inheritedVarargArg, methodArguments, rootTypeArguments)) {
                methodArguments.resolveVararg(declaration).also { resolved ->
                    overrideNullability = resolved.isNullable
                } to proxyArguments.resolveVararg(declaration)
            } else {
                declaration.toClassName().withTypeArguments(methodArguments) to
                    declaration.toClassName().withTypeArguments(proxyArguments)
            }
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
            val extraResolver = extractAliasTypeResolver(declaration, typeParameterResolver)
            val mappedMethodArguments: MutableList<TypeName> = mutableListOf()
            val mappedProxyArguments: MutableList<TypeName> = mutableListOf()
            val aliasMethodTypes: MutableList<TypeName> = mutableListOf()
            val aliasProxyTypes: MutableList<TypeName> = mutableListOf()

            arguments.forEach { argument ->
                val (methodArgument, proxyArgument) = argument.toSecuredTypeName(
                    inheritedVarargArg = inheritedVarargArg,
                    generics = generics,
                    typeParameterResolver = typeParameterResolver,
                    rootTypeArguments = rootTypeArguments,
                )

                mappedMethodArguments.add(methodArgument)
                mappedProxyArguments.add(proxyArgument)
            }

            val abbreviatedMethodType = declaration.abbreviateType(
                typeParameterResolver = extraResolver,
                isNullable = isMarkedNullable,
                typeArguments = mappedMethodArguments
            )

            val abbreviatedProxyType = declaration.abbreviateType(
                typeParameterResolver = extraResolver,
                isNullable = isMarkedNullable,
                typeArguments = mappedProxyArguments
            )

            typeArguments.forEach { argument ->
                val (methodArgument, proxyArgument) = argument.toSecuredTypeName(
                    inheritedVarargArg = inheritedVarargArg,
                    generics = generics,
                    typeParameterResolver = typeParameterResolver,
                    rootTypeArguments = rootTypeArguments,
                )

                aliasMethodTypes.add(methodArgument)
                aliasProxyTypes.add(proxyArgument)
            }

            declaration.parameterizedBy(abbreviatedMethodType, aliasMethodTypes) to
                declaration.parameterizedBy(abbreviatedProxyType, aliasProxyTypes)
        }
        else -> error("Unsupported type: $declaration")
    }

    return methodType.copy(nullable = (isMarkedNullable || overrideNullability)) to
        proxyType.copy(nullable = (proxyType.isNullable || isMarkedNullable || overrideNullability))
}

private fun KSTypeReference.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    val typeElements = element?.typeArguments.orEmpty()

    return resolve().toSecuredTypeName(
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        typeParameterResolver = typeParameterResolver,
        typeArguments = typeElements,
        rootTypeArguments = rootTypeArguments,
    )
}

internal fun KSTypeReference.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> {
    val typeElements = element?.typeArguments.orEmpty()
    val type = resolve()

    return type.toSecuredTypeName(
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        typeParameterResolver = typeParameterResolver,
        typeArguments = typeElements,
        rootTypeArguments = typeElements,
    )
}

@OptIn(ExperimentalUnsignedTypes::class)
private val specialArrays: Map<String, TypeName> = mapOf(
    IntArray::class.asTypeName().toString() to Int::class.asTypeName(),
    ByteArray::class.asTypeName().toString() to Byte::class.asTypeName(),
    ShortArray::class.asTypeName().toString() to Short::class.asTypeName(),
    LongArray::class.asTypeName().toString() to Long::class.asTypeName(),
    FloatArray::class.asTypeName().toString() to Float::class.asTypeName(),
    DoubleArray::class.asTypeName().toString() to Double::class.asTypeName(),
    CharArray::class.asTypeName().toString() to Char::class.asTypeName(),
    BooleanArray::class.asTypeName().toString() to Boolean::class.asTypeName(),
    UByteArray::class.asTypeName().toString() to UByte::class.asTypeName(),
    UShortArray::class.asTypeName().toString() to UShort::class.asTypeName(),
    UIntArray::class.asTypeName().toString() to UInt::class.asTypeName(),
    ULongArray::class.asTypeName().toString() to ULong::class.asTypeName(),
)
