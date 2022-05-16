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
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.utils.toSecuredTypeName

internal class KMockMethodGenerator(
    private val nameSelector: ProxyNameSelector,
    private val nonIntrusiveInvocationGenerator: NonIntrusiveInvocationGenerator,
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
        val returnType: MethodReturnTypeInfo
    )

    private fun determineArguments(
        inherited: Boolean,
        parameters: List<KSValueParameter>,
        typeParameterResolver: TypeParameterResolver
    ): Array<MethodTypeInfo> {
        return parameters.map { parameter ->
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
            typeName = actualTypeName.copy(nullable = actualTypeName.isNullable || isNullable),
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
            val (typeName, declaration, _) = mapGenericProxyType(
                typeName = typeInfo.typeName,
                classScopeGenerics = classScopeGenerics,
                proxyGenericTypes = proxyGenericTypes,
            )

            MethodArgumentTypeInfo(
                typeInfo = typeInfo.copy(typeName = typeName),
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

    private fun buildProxy(
        methodScope: TypeName?,
        proxyInfo: ProxyInfo,
        arguments: Array<MethodTypeInfo>,
        suspending: Boolean,
        classScopeGenerics: Map<String, List<TypeName>>?,
        generics: Map<String, List<KSTypeReference>>?,
        returnType: KSType,
        typeResolver: TypeParameterResolver,
    ): ProxyBundle? {
        return if (methodScope == null) {
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
                    )
                }.build(),
                proxyReturnType
            )
        } else {
            null
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
        proxyReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ) {
        val cast = if (returnType != proxyReturnType.typeName) {
            method.addAnnotation(unused)
            " as $returnType"
        } else {
            ""
        }

        val invocation = arguments.joinToString(", ") { argument -> argument.argumentName }
        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildMethodNonIntrusiveInvocation(
            enableSpy = enableSpy,
            methodName = proxyInfo.templateName,
            arguments = arguments,
            methodReturnType = proxyReturnType,
            relaxer = relaxer
        )

        method.addCode(
            "return %L.invoke(%L)%L%L",
            proxyInfo.proxyName,
            invocation,
            nonIntrusiveInvocation,
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
        proxyReturnType: MethodReturnTypeInfo?,
        typeResolver: TypeParameterResolver,
        relaxer: Relaxer?
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
                proxyReturnType = proxyReturnType!!,
                relaxer = relaxer
            )
        }

        return method.build()
    }

    override fun buildMethodBundle(
        methodScope: TypeName?,
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        inherited: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec?, FunSpec> {
        val methodName = ksFunction.simpleName.asString()
        val typeParameterResolver = ksFunction
            .typeParameters
            .toTypeParameterResolver(typeResolver)
        val generics = genericResolver.extractGenerics(ksFunction, typeParameterResolver)
        val arguments = determineArguments(
            inherited = inherited,
            parameters = ksFunction.parameters,
            typeParameterResolver = typeParameterResolver
        )

        val proxyInfo = nameSelector.selectMethodName(
            qualifier = qualifier,
            methodName = methodName,
            arguments = arguments,
            generics = generics ?: emptyMap(),
            typeResolver = typeParameterResolver,
        )
        val returnType = ksFunction.returnType!!.resolve()
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)

        val proxySignature = buildProxy(
            methodScope = methodScope,
            proxyInfo = proxyInfo,
            arguments = arguments,
            classScopeGenerics = classScopeGenerics,
            generics = generics,
            suspending = isSuspending,
            returnType = returnType,
            typeResolver = typeParameterResolver,
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
            typeResolver = typeParameterResolver,
            relaxer = relaxer
        )

        return Pair(proxySignature?.proxy, method)
    }
}
