/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MethodArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.RelaxerGenerator
import tech.antibytes.kmock.processor.ProcessorContract.SpyGenerator

internal class KMockMethodGenerator(
    private val spyGenerator: SpyGenerator,
    private val nameSelector: ProxyNameSelector,
    private val relaxerGenerator: RelaxerGenerator,
    private val genericResolver: GenericResolver,
) : MethodGenerator {
    private val any = Any::class.asTypeName()
    private val unused = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build()
    private val varargs = arrayOf(KModifier.VARARG)
    private val noVarargs = arrayOf<KModifier>()
    private val asyncProxy = AsyncFunProxy::class.asClassName()
    private val syncProxy = SyncFunProxy::class.asClassName()
    private val scopedBody = "throw IllegalStateException(\n\"This action is not callable.\"\n)"

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

    private data class ProxyBundle(
        val proxy: PropertySpec,
        val returnType: TypeName
    )

    private fun determineArguments(
        parameters: List<KSValueParameter>,
        parameterTypeResolver: TypeParameterResolver
    ): Array<MethodTypeInfo> {
        return parameters.map { parameter ->
            val argumentName = parameter.name!!.asString()

            MethodTypeInfo(
                argumentName = argumentName,
                typeName = parameter.type.toTypeName(parameterTypeResolver),
                isVarArg = parameter.isVararg,
            )
        }.toTypedArray()
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
        proxyGenericTypes: Map<String, GenericDeclaration>,
    ): MethodReturnTypeInfo {
        val isNullable = typeName.isNullable
        val generic = proxyGenericTypes[typeName.toString().trimEnd('?')]

        val actualTypeName = resolveGenericProxyType(
            typeName,
            generic
        )

        return MethodReturnTypeInfo(
            typeName = actualTypeName.copy(nullable = actualTypeName.isNullable || isNullable),
            generic = generic
        )
    }

    private fun determineProxyReturnType(
        returnType: KSType,
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        typeResolver: TypeParameterResolver,
    ): MethodReturnTypeInfo {
        val typeName = returnType.toTypeName(typeResolver)

        return if (proxyGenericTypes == null) {
            MethodReturnTypeInfo(
                typeName = typeName,
                generic = null,
            )
        } else {
            mapGenericProxyType(typeName, proxyGenericTypes)
        }
    }

    private fun resolveGenericArgumentTypes(
        proxyGenericTypes: Map<String, GenericDeclaration>,
        argumentTypes: Array<MethodTypeInfo>,
    ): List<MethodArgumentTypeInfo> {
        return argumentTypes.map { typeInfo ->
            val (typeName, declaration) = mapGenericProxyType(
                typeInfo.typeName,
                proxyGenericTypes
            )

            MethodArgumentTypeInfo(
                typeInfo = typeInfo.copy(typeName = typeName),
                generic = declaration
            )
        }
    }

    private fun determineProxyArgumentTypes(
        proxyGenericTypes: Map<String, GenericDeclaration>?,
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
            resolveGenericArgumentTypes(proxyGenericTypes, argumentTypes)
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
        proxyReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ): PropertySpec.Builder {
        return proxySpec.initializer(
            "ProxyFactory.%L(%S, collector = verifier, freeze = freeze) %L",
            proxyFactoryMethod,
            proxyInfo.proxyId,
            relaxerGenerator.addMethodRelaxation(
                relaxer = relaxer,
                methodReturnType = proxyReturnType
            )
        )
    }

    private fun buildProxy(
        methodScope: TypeName?,
        proxyInfo: ProxyInfo,
        arguments: Array<MethodTypeInfo>,
        suspending: Boolean,
        generics: Map<String, List<KSTypeReference>>?,
        returnType: KSType,
        typeResolver: TypeParameterResolver,
        relaxer: Relaxer?
    ): ProxyBundle? {
        return if (methodScope == null) {
            val (proxyType, proxyFactoryMethod, sideEffectPrefix) = determineProxyType(suspending)
            val proxyGenericTypes = resolveProxyGenerics(
                generics = generics,
                typeResolver = typeResolver
            )

            val proxyArguments = determineProxyArgumentTypes(
                proxyGenericTypes = proxyGenericTypes,
                argumentTypes = arguments,
            )

            val proxyReturnType = determineProxyReturnType(
                returnType = returnType,
                proxyGenericTypes = proxyGenericTypes,
                typeResolver = typeResolver
            )

            val sideEffect = buildSideEffectSignature(
                proxyArguments = proxyArguments,
                proxyReturnType = proxyReturnType.typeName,
                prefix = sideEffectPrefix
            )

            ProxyBundle(
                PropertySpec.builder(
                    proxyInfo.proxyName,
                    proxyType.parameterizedBy(proxyReturnType.typeName, sideEffect)
                ).let { proxySpec ->
                    buildProxyInitializer(
                        proxySpec = proxySpec,
                        proxyInfo = proxyInfo,
                        proxyFactoryMethod = proxyFactoryMethod,
                        proxyReturnType = proxyReturnType,
                        relaxer = relaxer
                    )
                }.build(),
                proxyReturnType.typeName
            )
        } else {
            null
        }
    }

    private fun determineSpy(
        enableSpy: Boolean,
        proxyInfo: ProxyInfo,
        arguments: Array<MethodTypeInfo>,
    ): String {
        return if (enableSpy) {
            spyGenerator.buildMethodSpy(
                methodName = proxyInfo.templateName,
                arguments = arguments
            )
        } else {
            ""
        }
    }

    private fun FunSpec.Builder.addArguments(
        arguments: Array<MethodTypeInfo>
    ): FunSpec.Builder {
        arguments.forEach { argument ->
            val vararged = if (argument.isVarArg) {
                varargs
            } else {
                noVarargs
            }

            this.addParameter(
                argument.argumentName,
                argument.typeName,
                *vararged
            )
        }

        return this
    }

    private fun buildMethodBody(
        method: FunSpec.Builder,
        proxyInfo: ProxyInfo,
        enableSpy: Boolean,
        arguments: Array<MethodTypeInfo>,
        returnType: TypeName,
        proxyReturnType: TypeName?,
    ) {
        val cast = if (returnType != proxyReturnType) {
            method.addAnnotation(unused)
            " as $returnType"
        } else {
            ""
        }

        val invocation = arguments.joinToString(", ") { argument -> argument.argumentName }
        val spy = determineSpy(
            enableSpy = enableSpy,
            proxyInfo = proxyInfo,
            arguments = arguments,
        )

        method.addCode(
            "return %L.invoke(%L)%L%L",
            proxyInfo.proxyName,
            invocation,
            spy,
            cast
        )
    }

    private fun buildShallowMethodBody(method: FunSpec.Builder) {
        method.addCode(scopedBody)
    }

    private fun buildMethod(
        methodScope: TypeName?,
        proxyInfo: ProxyInfo,
        generics: Map<String, List<KSTypeReference>>?,
        isSuspending: Boolean,
        enableSpy: Boolean,
        arguments: Array<MethodTypeInfo>,
        rawReturnType: KSType,
        proxyReturnType: TypeName?,
        typeResolver: TypeParameterResolver,
    ): FunSpec {
        val returnType = rawReturnType.toTypeName(typeResolver)

        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE)
            .addArguments(arguments)
            .returns(returnType)

        if (generics != null) {
            method.typeVariables.addAll(
                this.genericResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        if (isSuspending) {
            method.addModifiers(KModifier.SUSPEND)
        }

        if (methodScope != null) {
            method.receiver(methodScope)
            buildShallowMethodBody(method)
        } else {
            buildMethodBody(
                method = method,
                proxyInfo = proxyInfo,
                enableSpy = enableSpy,
                arguments = arguments,
                returnType = returnType,
                proxyReturnType = proxyReturnType,
            )
        }

        return method.build()
    }

    override fun buildMethodBundle(
        methodScope: TypeName?,
        qualifier: String,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec?, FunSpec> {
        val methodName = ksFunction.simpleName.asString()
        val parameterTypeResolver = ksFunction
            .typeParameters
            .toTypeParameterResolver(typeResolver)
        val generics = genericResolver.extractGenerics(ksFunction, parameterTypeResolver)
        val arguments = determineArguments(ksFunction.parameters, parameterTypeResolver)

        val proxyInfo = nameSelector.selectMethodName(
            qualifier = qualifier,
            methodName = methodName,
            arguments = arguments,
            generics = generics ?: emptyMap(),
            typeResolver = parameterTypeResolver,
        )
        val returnType = ksFunction.returnType!!.resolve()
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)

        val proxySignature = buildProxy(
            methodScope = methodScope,
            proxyInfo = proxyInfo,
            arguments = arguments,
            generics = generics,
            suspending = isSuspending,
            returnType = returnType,
            typeResolver = parameterTypeResolver,
            relaxer = relaxer
        )

        val method = buildMethod(
            methodScope = methodScope,
            proxyInfo = proxyInfo,
            generics = generics,
            isSuspending = isSuspending,
            enableSpy = enableSpy,
            arguments = arguments,
            rawReturnType = returnType,
            proxyReturnType = proxySignature?.returnType,
            typeResolver = parameterTypeResolver
        )

        return Pair(proxySignature?.proxy, method)
    }
}
