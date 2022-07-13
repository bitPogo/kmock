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
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NULLABLE_ANY
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.mock.resolveGeneric
import tech.antibytes.kmock.processor.utils.extractParameter

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt#L1
// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/utils.kt#L16

/* Contains Workaround to overcome KSP misalignment on inherited types with function TypeParameter */
// see: https://github.com/google/ksp/issues/958
internal fun KSTypeReference.toProxyPairTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    rootTypeArguments: List<KSTypeArgument>,
    typeParameterResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> {
    return resolve().toProxyPairTypeName(
        typeParameterResolver = typeParameterResolver,
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
        typeArguments = element?.typeArguments.orEmpty(),
    )
}

internal fun KSTypeArgument.toProxyPairTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
    rootTypeArguments: List<KSTypeArgument>,
): Pair<TypeName, TypeName> {
    val (methodTypeName, proxyTypeName) = type?.toProxyPairTypeName(
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

private fun List<KSTypeArgument>.toProxyPairTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    rootTypeArguments: List<KSTypeArgument>,
    typeParameterResolver: TypeParameterResolver,
): Pair<List<TypeName>, List<TypeName>> {
    val methodTypes: MutableList<TypeName> = mutableListOf()
    val proxyTypes: MutableList<TypeName> = mutableListOf()

    this.forEach { argument ->
        val (methodType, proxyType) = argument.toProxyPairTypeName(
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
    val (methodType, proxyType) = this.toProxyPairTypeName(
        typeParameterResolver = extraResolver,
        inheritedVarargArg = inheritedVarargArg,
        generics = generics,
        rootTypeArguments = rootTypeArguments,
        typeArguments = emptyList(),
    )

    val (methodArgument, proxyArguments) = typeArguments.toProxyPairTypeName(
        typeParameterResolver = typeParameterResolver,
        inheritedVarargArg = inheritedVarargArg,
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

private fun List<TypeName>.resolveVararg(parent: KSClassDeclaration): TypeName {
    return when (val typeName = this.firstOrNull()) {
        null -> specialArrays["kotlin.${parent.simpleName.asString().trimEnd('?')}"]!!
        STAR -> NULLABLE_ANY
        is WildcardTypeName -> typeName.outTypes.first()
        else -> error("Cannot resolve vararg of ${parent.toClassName()}")
    }
}

private fun KSType.toProxyPairTypeName(
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
            val (methodArguments, proxyArguments) = typeArguments.toProxyPairTypeName(
                inheritedVarargArg = false,
                generics = generics,
                typeParameterResolver = typeParameterResolver,
                rootTypeArguments = rootTypeArguments,
            )

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
            val (resolvedType, mappedArgs, extraResolver) = declaration.resolveAlias(
                arguments = typeArguments,
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

            val (aliasMethodArgs, aliasProxyArgs) = typeArguments.toProxyPairTypeName(
                inheritedVarargArg = false,
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

internal fun KSTypeReference.toProxyPairTypeName(
    inheritedVarargArg: Boolean,
    generics: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> {
    val typeElements = extractParameter()
    val type = resolve()

    return type.toProxyPairTypeName(
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
