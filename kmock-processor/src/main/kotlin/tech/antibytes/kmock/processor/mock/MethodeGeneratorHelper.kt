/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSType
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
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MethodArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyBundle
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.utils.toSecuredTypeName

internal class MethodeGeneratorHelper(
    private val genericResolver: GenericResolver,
) : ProcessorContract.MethodeGeneratorHelper {
    override fun determineArguments(
        inherited: Boolean,
        arguments: List<KSValueParameter>,
        typeParameterResolver: TypeParameterResolver
    ): Array<MethodTypeInfo> {
        return arguments.map { parameter ->
            val argumentName = parameter.name!!.asString()

            parameter.type.modifiers
            MethodTypeInfo(
                argumentName = argumentName,
                typeName = parameter.type.toSecuredTypeName(
                    inheritedVarargArg = parameter.isVararg && inherited,
                    typeParameterResolver = typeParameterResolver
                ),
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

    private fun resolveProxyGenerics(
        generics: Map<String, List<KSTypeReference>>?,
        typeResolver: TypeParameterResolver
    ): Map<String, GenericDeclaration>? {
        return if (generics == null) {
            null
        } else {
            genericResolver.mapProxyGenerics(generics, typeResolver)
        }
    }

    private fun resolveGenericProxyType(
        typeName: TypeName,
        generic: GenericDeclaration?
    ): TypeName {
        return when {
            generic == null -> typeName
            generic.types.size > 1 -> any.copy(nullable = generic.nullable)
            else -> generic.types.first()
        }
    }

    private fun mapGenericProxyType(
        typeName: TypeName,
        classScopeGenerics: Map<String, List<TypeName>>?,
        proxyGenericTypes: Map<String, GenericDeclaration>,
    ): MethodReturnTypeInfo {
        val isNullable = typeName.isNullable
        val generic = proxyGenericTypes[typeName.toString().trimEnd('?')]

        val actualTypeName = resolveGenericProxyType(
            typeName = typeName,
            generic = generic
        )

        return MethodReturnTypeInfo(
            typeName = typeName,
            actualTypeName = actualTypeName.copy(nullable = actualTypeName.isNullable || isNullable),
            generic = generic,
            classScope = classScopeGenerics
        )
    }

    private fun determineProxyReturnType(
        returnType: KSType,
        classScopeGenerics: Map<String, List<TypeName>>?,
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        typeResolver: TypeParameterResolver,
    ): MethodReturnTypeInfo {
        val typeName = returnType.toTypeName(typeResolver)

        return if (proxyGenericTypes == null) {
            MethodReturnTypeInfo(
                typeName = typeName,
                actualTypeName = typeName,
                generic = null,
                classScope = classScopeGenerics
            )
        } else {
            mapGenericProxyType(
                typeName = typeName,
                classScopeGenerics = classScopeGenerics,
                proxyGenericTypes = proxyGenericTypes,
            )
        }
    }

    private fun resolveGenericArgumentTypes(
        proxyGenericTypes: Map<String, GenericDeclaration>,
        classScopeGenerics: Map<String, List<TypeName>>?,
        argumentTypes: Array<MethodTypeInfo>,
    ): List<MethodArgumentTypeInfo> {
        return argumentTypes.map { typeInfo ->
            val (_, actualTypeName, declaration, _) = mapGenericProxyType(
                typeName = typeInfo.typeName,
                classScopeGenerics = classScopeGenerics,
                proxyGenericTypes = proxyGenericTypes,
            )

            MethodArgumentTypeInfo(
                typeInfo = typeInfo.copy(typeName = actualTypeName),
                generic = declaration
            )
        }
    }

    private fun determineProxyArgumentTypes(
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        classScopeGenerics: Map<String, List<TypeName>>?,
        argumentTypes: Array<MethodTypeInfo>,
    ): List<MethodArgumentTypeInfo> {
        return if (proxyGenericTypes == null) {
            argumentTypes.map { typeInfo ->
                MethodArgumentTypeInfo(
                    typeInfo = typeInfo,
                    generic = null,
                )
            }
        } else {
            resolveGenericArgumentTypes(
                proxyGenericTypes = proxyGenericTypes,
                classScopeGenerics = classScopeGenerics,
                argumentTypes = argumentTypes,
            )
        }
    }

    private fun mapProxyArgumentTypeNames(
        proxyArguments: List<MethodArgumentTypeInfo>
    ): List<TypeName> {
        return proxyArguments.map { argument ->
            if (!argument.typeInfo.isVarArg) {
                argument.typeInfo.typeName
            } else {
                specialArrays.getOrElse(argument.typeInfo.typeName) {
                    TypeVariableName(
                        "Array<out ${argument.typeInfo.typeName}>"
                    )
                }
            }
        }
    }

    private fun buildSideEffectSignature(
        proxyArguments: List<MethodArgumentTypeInfo>,
        proxyReturnType: TypeName,
        prefix: String
    ): TypeName {
        val argumentTypeName = mapProxyArgumentTypeNames(proxyArguments)

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
        generics: Map<String, List<KSTypeReference>>?,
        returnType: KSType,
        typeResolver: TypeParameterResolver,
    ): ProxyBundle {
        val (proxyType, proxyFactoryMethod, sideEffectPrefix) = determineProxyType(suspending)
        val proxyGenericTypes = resolveProxyGenerics(
            generics = generics,
            typeResolver = typeResolver
        )

        val proxyArguments = determineProxyArgumentTypes(
            proxyGenericTypes = proxyGenericTypes,
            classScopeGenerics = classScopeGenerics,
            argumentTypes = arguments,
        )

        val proxyReturnType = determineProxyReturnType(
            returnType = returnType,
            classScopeGenerics = classScopeGenerics,
            proxyGenericTypes = proxyGenericTypes,
            typeResolver = typeResolver
        )

        val sideEffect = buildSideEffectSignature(
            proxyArguments = proxyArguments,
            proxyReturnType = proxyReturnType.actualTypeName,
            prefix = sideEffectPrefix
        )

        return ProxyBundle(
            PropertySpec.builder(
                proxyInfo.proxyName,
                proxyType.parameterizedBy(proxyReturnType.actualTypeName, sideEffect)
            ).let { proxySpec ->
                buildProxyInitializer(
                    proxySpec = proxySpec,
                    proxyInfo = proxyInfo,
                    proxyFactoryMethod = proxyFactoryMethod,
                )
            }.build(),
            proxyReturnType
        )
    }

    private companion object {
        private val any = Any::class.asTypeName()
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
