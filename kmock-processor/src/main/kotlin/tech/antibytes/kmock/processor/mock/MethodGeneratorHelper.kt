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
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.ReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
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
    ): Array<MethodTypeInfo> {
        return arguments.map { parameter ->
            val argumentName = parameter.name!!.asString()
            val (methodType, proxyType) = parameter.type.toSecuredTypeName(
                inheritedVarargArg = parameter.isVararg && inherited,
                generics = generics ?: emptyMap(),
                typeParameterResolver = typeParameterResolver
            )
            MethodTypeInfo(
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
            Triple(asyncProxy, "createAsyncFunProxy", "suspend ")
        } else {
            Triple(syncProxy, "createSyncFunProxy", "")
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
    ): ReturnTypeInfo {
        val generic = proxyGenericTypes[methodTypeName.toString().trimEnd('?')]

        return ReturnTypeInfo(
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
    ): ReturnTypeInfo {
        return if (proxyGenericTypes == null) {
            ReturnTypeInfo(
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

    private fun MethodTypeInfo.resolveVarargArray(): TypeName {
        val name = this.proxyTypeName.toString()
        println(name)
        return if (name.startsWith("out") || name == "*") {
            array.parameterizedBy(this.proxyTypeName)
        } else {
            TypeVariableName("Array<out ${this.proxyTypeName}>")
        }
    }

    private fun mapProxyArgumentTypeNames(
        arguments: Array<MethodTypeInfo>,
    ): List<TypeName> {
        return arguments.map { argument ->
            if (!argument.isVarArg) {
                argument.proxyTypeName
            } else {
                specialArrays.getOrElse(argument.proxyTypeName) {
                    argument.resolveVarargArray()
                }
            }
        }
    }

    private fun buildSideEffectSignature(
        arguments: Array<MethodTypeInfo>,
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
            "ProxyFactory.%L(%S, collector = verifier, freeze = freeze)",
            proxyFactoryMethod,
            proxyInfo.proxyId,
        )
    }

    override fun buildProxy(
        proxyInfo: ProxyInfo,
        arguments: Array<MethodTypeInfo>,
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
        private val array = Array::class.asClassName()
        private val asyncProxy = KMockContract.AsyncFunProxy::class.asClassName()
        private val syncProxy = KMockContract.SyncFunProxy::class.asClassName()

        @OptIn(ExperimentalUnsignedTypes::class)
        private val specialArrays: Map<TypeName, TypeName> = mapOf(
            Int::class.asTypeName() to IntArray::class.asTypeName(),
            Byte::class.asTypeName() to ByteArray::class.asTypeName(),
            Short::class.asTypeName() to ShortArray::class.asTypeName(),
            Long::class.asTypeName() to LongArray::class.asTypeName(),
            Float::class.asTypeName() to FloatArray::class.asTypeName(),
            Double::class.asTypeName() to DoubleArray::class.asTypeName(),
            Char::class.asTypeName() to CharArray::class.asTypeName(),
            Boolean::class.asTypeName() to BooleanArray::class.asTypeName(),
            UByte::class.asTypeName() to UByteArray::class.asTypeName(),
            UShort::class.asTypeName() to UShortArray::class.asTypeName(),
            UInt::class.asTypeName() to UIntArray::class.asTypeName(),
            ULong::class.asTypeName() to ULongArray::class.asTypeName(),
        )
    }
}
