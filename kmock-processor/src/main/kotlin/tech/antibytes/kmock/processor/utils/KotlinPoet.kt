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
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.tags.TypeAliasTag
import tech.antibytes.kmock.processor.ProcessorContract.Companion.nullableAny
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.mock.resolveGeneric

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/utils.kt#L16
internal fun TypeName.rawType(): ClassName {
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

internal fun extractAliasTypeResolver(
    declaration: KSTypeAlias,
    typeParameterResolver: TypeParameterResolver
): TypeParameterResolver {
    return if (declaration.typeParameters.isEmpty()) {
        typeParameterResolver
    } else {
        declaration.typeParameters.toTypeParameterResolver(typeParameterResolver)
    }
}

private fun List<KSTypeArgument>.mapArgumentType(
    mapping: Map<String, String>,
    typeParameterResolver: TypeParameterResolver,
): List<TypeName> {
    return this.map { argument ->
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
        .copy(nullable = isNullable)
        .rawType()
        .withTypeArguments(
            typeArguments.mapArgumentType(mapping, typeParameterResolver)
        )
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

private fun resolveAlias(
    declaration: KSTypeAlias,
    arguments: List<KSTypeArgument>,
    typeParameterResolver: TypeParameterResolver,
): Triple<KSType, List<KSTypeArgument>, TypeParameterResolver> {
    var typeAlias: KSTypeAlias = declaration
    var resolvedArguments = arguments
    var resolvedType: KSType
    var mappedArgs: List<KSTypeArgument>
    var extraResolver: TypeParameterResolver

    do {
        resolvedType = typeAlias.type.resolve()
        mappedArgs = typeAlias.mapAbbreviatedType(
            typeAliasTypeArguments = resolvedArguments,
            abbreviatedType = resolvedType,
        )
        extraResolver = extractAliasTypeResolver(declaration, typeParameterResolver)

        if (resolvedType.declaration is KSTypeAlias) {
            typeAlias = resolvedType.declaration as KSTypeAlias
            resolvedArguments = mappedArgs
        }
    } while (resolvedType.declaration is KSTypeAlias)

    return Triple(resolvedType, mappedArgs, extraResolver)
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
            val (resolvedType, mappedArgs, extraResolver) = resolveAlias(
                declaration = declaration,
                arguments = arguments,
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

/* Workaround to overcome KSP misalignment on inherited types with function TypeParameter */
// see: https://github.com/google/ksp/issues/958
internal fun KSTypeReference.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    rootTypeArguments: List<KSTypeArgument>,
    typeParameterResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> {
    return resolve().toSecuredTypeName(
        typeParameterResolver = typeParameterResolver,
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
        typeArguments = element?.typeArguments.orEmpty(),
    )
}

private fun List<KSTypeArgument>.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    rootTypeArguments: List<KSTypeArgument>,
    typeParameterResolver: TypeParameterResolver,
): Pair<List<TypeName>, List<TypeName>> {
    val methodTypes: MutableList<TypeName> = mutableListOf()
    val proxyTypes: MutableList<TypeName> = mutableListOf()

    this.forEach { argument ->
        val (methodType, proxyType) = argument.toSecuredTypeName(
            typeParameterResolver = typeParameterResolver,
            inheritedVarargArg = inheritedVarargArg,
            generics = generics,
            rootTypeArguments = rootTypeArguments,
        )

        methodTypes.add(methodType)
        proxyTypes.add(proxyType)
    }

    return methodTypes to proxyTypes
}

private fun KSType.abbreviateType(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    extraResolver: TypeParameterResolver,
    typeParameterResolver: TypeParameterResolver,
    isNullable: Boolean,
    typeArguments: List<KSTypeArgument>,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    val (methodType, proxyType) = this.toSecuredTypeName(
        typeParameterResolver = extraResolver,
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
        typeArguments = emptyList(),
    )

    val (methodArgument, proxyArguments) = typeArguments.toSecuredTypeName(
        typeParameterResolver = typeParameterResolver,
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
    )

    val parameterizedMethodType = methodType.copy(nullable = isNullable)
        .rawType()
        .withTypeArguments(methodArgument)

    val parameterizedProxyType = proxyType.copy(nullable = isNullable)
        .rawType()
        .withTypeArguments(proxyArguments)

    return parameterizedMethodType to parameterizedProxyType
}

internal fun KSTypeArgument.toSecuredTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    val (methodTypeName, proxyTypeName) = type?.toSecuredTypeName(
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
        typeParameterResolver = typeParameterResolver,
    ) ?: return STAR to STAR

    return when (variance) {
        Variance.COVARIANT -> {
            WildcardTypeName.producerOf(methodTypeName) to WildcardTypeName.producerOf(proxyTypeName)
        }
        Variance.CONTRAVARIANT -> {
            WildcardTypeName.consumerOf(methodTypeName) to WildcardTypeName.producerOf(proxyTypeName)
        }
        Variance.STAR -> STAR to STAR
        Variance.INVARIANT -> methodTypeName to proxyTypeName
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
            val (resolvedType, mappedArgs, extraResolver) = resolveAlias(
                declaration = declaration,
                arguments = arguments,
                typeParameterResolver = typeParameterResolver,
            )

            val (abbreviatedMethodType, abbreviatedProxyType) = resolvedType.abbreviateType(
                inheritedVarargArg = inheritedVarargArg,
                generics = generics,
                extraResolver = extraResolver,
                typeParameterResolver = typeParameterResolver,
                isNullable = isMarkedNullable,
                typeArguments = mappedArgs,
                rootTypeArguments = rootTypeArguments,
            )

            val (aliasMethodArgs, aliasProxyArgs) = typeArguments.toSecuredTypeName(
                inheritedVarargArg = inheritedVarargArg,
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

    return methodType.copy(nullable = (isMarkedNullable || overrideNullability)) to
        proxyType.copy(nullable = (proxyType.isNullable || isMarkedNullable || overrideNullability))
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
