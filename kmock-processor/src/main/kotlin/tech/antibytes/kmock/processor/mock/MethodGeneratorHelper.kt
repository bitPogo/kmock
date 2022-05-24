/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.CREATE_ASYNC_PROXY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.CREATE_SYNC_PROXY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PROXY_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.array
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MemberArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MemberReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyBundle
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.utils.toSecuredTypeName

internal class MethodGeneratorHelper(
    private val genericResolver: GenericResolver,
) : ProcessorContract.MethodGeneratorHelper {
    override fun determineArguments(
        inherited: Boolean,
        generics: Map<String, GenericDeclaration>?,
        arguments: List<KSValueParameter>,
        typeParameterResolver: TypeParameterResolver,
    ): Array<MemberArgumentTypeInfo> {
        return arguments.map { parameter ->
            val argumentName = parameter.name!!.asString()
            val (methodType, proxyType) = parameter.type.toSecuredTypeName(
                inheritedVarargArg = parameter.isVararg && inherited,
                generics = generics ?: emptyMap(),
                typeParameterResolver = typeParameterResolver
            )
            MemberArgumentTypeInfo(
                argumentName = argumentName,
                methodTypeName = methodType,
                proxyTypeName = proxyType,
                isVarArg = parameter.isVararg,
            )
        }.toTypedArray()
    }

    override fun resolveTypeParameter(
        parameter: List<KSTypeParameter>,
        typeParameterResolver: TypeParameterResolver
    ): List<TypeName> {
        var distribute = false
        val parameterTypes = parameter.map { type ->
            val parameterType = type.toTypeVariableName(typeParameterResolver)

            if (parameterType.bounds.size > 1) {
                distribute = true
            }

            parameterType
        }

        return if (distribute) {
            parameterTypes
        } else {
            emptyList()
        }
    }

    private fun determineProxyType(suspending: Boolean): Triple<ClassName, String, String> {
        return if (suspending) {
            Triple(asyncProxy, CREATE_ASYNC_PROXY, "suspend ")
        } else {
            Triple(syncProxy, CREATE_SYNC_PROXY, "")
        }
    }

    override fun resolveProxyGenerics(
        generics: Map<String, List<KSTypeReference>>?,
        typeResolver: TypeParameterResolver
    ): Map<String, GenericDeclaration>? {
        return if (generics == null) {
            null
        } else {
            genericResolver.mapProxyGenerics(generics, typeResolver)
        }
    }

    private fun mapGenericProxyType(
        methodTypeName: TypeName,
        proxyTypeName: TypeName,
        classScopeGenerics: Map<String, List<TypeName>>?,
        proxyGenericTypes: Map<String, GenericDeclaration>,
    ): MemberReturnTypeInfo {
        val generic = proxyGenericTypes[methodTypeName.toString().trimEnd('?')]

        return MemberReturnTypeInfo(
            methodTypeName = methodTypeName,
            proxyTypeName = proxyTypeName,
            generic = generic,
            classScope = classScopeGenerics
        )
    }

    private fun determineProxyReturnType(
        methodReturnType: TypeName,
        proxyReturnType: TypeName,
        classScopeGenerics: Map<String, List<TypeName>>?,
        proxyGenericTypes: Map<String, GenericDeclaration>?,
    ): MemberReturnTypeInfo {
        return if (proxyGenericTypes == null) {
            MemberReturnTypeInfo(
                methodTypeName = methodReturnType,
                proxyTypeName = proxyReturnType,
                generic = null,
                classScope = classScopeGenerics
            )
        } else {
            mapGenericProxyType(
                methodTypeName = methodReturnType,
                proxyTypeName = proxyReturnType,
                classScopeGenerics = classScopeGenerics,
                proxyGenericTypes = proxyGenericTypes,
            )
        }
    }

    private fun mapProxyArgumentTypeNames(
        arguments: Array<MemberArgumentTypeInfo>,
    ): List<TypeName> {
        return arguments.map { argument ->
            if (!argument.isVarArg) {
                argument.proxyTypeName
            } else {
                specialArrays.getOrElse(argument.proxyTypeName.toString()) {
                    TypeVariableName("$array<out ${argument.proxyTypeName}>")
                }
            }
        }
    }

    private fun buildSideEffectSignature(
        arguments: Array<MemberArgumentTypeInfo>,
        proxyReturnType: TypeName,
        prefix: String
    ): TypeVariableName {
        val argumentTypeName = mapProxyArgumentTypeNames(arguments)

        return TypeVariableName(
            "$prefix(${argumentTypeName.joinToString(", ")}) -> $proxyReturnType"
        )
    }

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
        proxyFactoryMethod: String,
    ): PropertySpec.Builder {
        return proxySpec.initializer(
            "$PROXY_FACTORY.%L(%S, $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT, $FREEZE_ARGUMENT = $FREEZE_ARGUMENT)",
            proxyFactoryMethod,
            proxyInfo.proxyId,
        )
    }

    override fun buildProxy(
        proxyInfo: ProxyInfo,
        arguments: Array<MemberArgumentTypeInfo>,
        suspending: Boolean,
        classScopeGenerics: Map<String, List<TypeName>>?,
        generics: Map<String, GenericDeclaration>?,
        methodReturnType: TypeName,
        proxyReturnType: TypeName,
        typeResolver: TypeParameterResolver,
    ): ProxyBundle {
        val (proxyType, proxyFactoryMethod, sideEffectPrefix) = determineProxyType(suspending)

        val returnTypeInfo = determineProxyReturnType(
            proxyReturnType = proxyReturnType,
            methodReturnType = methodReturnType,
            classScopeGenerics = classScopeGenerics,
            proxyGenericTypes = generics,
        )

        val sideEffect = buildSideEffectSignature(
            arguments = arguments,
            proxyReturnType = proxyReturnType,
            prefix = sideEffectPrefix
        )

        return ProxyBundle(
            proxy = PropertySpec.builder(
                proxyInfo.proxyName,
                proxyType.parameterizedBy(proxyReturnType, sideEffect)
            ).let { proxySpec ->
                buildProxyInitializer(
                    proxySpec = proxySpec,
                    proxyInfo = proxyInfo,
                    proxyFactoryMethod = proxyFactoryMethod,
                )
            }.build(),
            returnType = returnTypeInfo,
            sideEffect = sideEffect,
        )
    }

    private companion object {
        private val asyncProxy = KMockContract.AsyncFunProxy::class.asClassName()
        private val syncProxy = KMockContract.SyncFunProxy::class.asClassName()

        @OptIn(ExperimentalUnsignedTypes::class)
        private val specialArrays: Map<String, TypeName> = mapOf(
            Int::class.asTypeName().toString() to IntArray::class.asTypeName(),
            Byte::class.asTypeName().toString() to ByteArray::class.asTypeName(),
            Short::class.asTypeName().toString() to ShortArray::class.asTypeName(),
            Long::class.asTypeName().toString() to LongArray::class.asTypeName(),
            Float::class.asTypeName().toString() to FloatArray::class.asTypeName(),
            Double::class.asTypeName().toString() to DoubleArray::class.asTypeName(),
            Char::class.asTypeName().toString() to CharArray::class.asTypeName(),
            Boolean::class.asTypeName().toString() to BooleanArray::class.asTypeName(),
            UByte::class.asTypeName().toString() to UByteArray::class.asTypeName(),
            UShort::class.asTypeName().toString() to UShortArray::class.asTypeName(),
            UInt::class.asTypeName().toString() to UIntArray::class.asTypeName(),
            ULong::class.asTypeName().toString() to ULongArray::class.asTypeName(),
        )
    }
}
