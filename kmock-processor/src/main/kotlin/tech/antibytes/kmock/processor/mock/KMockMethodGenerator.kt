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
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.MethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockMethodGenerator(
    private val allowedRecursiveTypes: Set<String>,
    private val nameSelector: ProxyNameSelector,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val relaxerGenerator: ProcessorContract.RelaxerGenerator
) : MethodGenerator {
    private val any = Any::class.asTypeName()
    private val unused = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build()
    private val asyncProxy = AsyncFunProxy::class.asClassName()
    private val syncProxy = SyncFunProxy::class.asClassName()

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

    private data class ProxyArgumentTypeInfo(
        val typeInfo: MethodTypeInfo,
        val generic: GenericDeclaration?
    )

    private data class ProxyReturnTypeInfo(
        val typeName: TypeName,
        val generic: GenericDeclaration?
    )

    private data class SpyArguments(
        val declaredArguments: String,
        val invokedArguments: String,
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
    ): ProxyReturnTypeInfo {
        val isNullable = typeName.isNullable
        val generic = proxyGenericTypes[typeName.toString().trimEnd('?')]

        val actualTypeName = resolveGenericProxyType(
            typeName,
            generic
        )

        return ProxyReturnTypeInfo(
            typeName = actualTypeName.copy(nullable = actualTypeName.isNullable || isNullable),
            generic = generic
        )
    }

    private fun determineProxyReturnType(
        returnType: KSType,
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        typeResolver: TypeParameterResolver,
    ): ProxyReturnTypeInfo {
        val typeName = returnType.toTypeName(typeResolver)

        return if (proxyGenericTypes == null) {
            ProxyReturnTypeInfo(
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
    ): List<ProxyArgumentTypeInfo> {
        return argumentTypes.map { typeInfo ->
            val (typeName, declaration) = mapGenericProxyType(
                typeInfo.typeName,
                proxyGenericTypes
            )

            ProxyArgumentTypeInfo(
                typeInfo = typeInfo.copy(typeName = typeName),
                generic = declaration
            )
        }
    }

    private fun determineProxyArgumentTypes(
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        argumentTypes: Array<MethodTypeInfo>,
    ): List<ProxyArgumentTypeInfo> {
        return if (proxyGenericTypes == null) {
            argumentTypes.map { typeInfo ->
                ProxyArgumentTypeInfo(
                    typeInfo = typeInfo,
                    generic = null,
                )
            }
        } else {
            resolveGenericArgumentTypes(proxyGenericTypes, argumentTypes)
        }
    }

    private fun mapProxyArgumentTypeNames(
        proxyArguments: List<ProxyArgumentTypeInfo>
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
        proxyArguments: List<ProxyArgumentTypeInfo>,
        proxyReturnType: TypeName,
        prefix: String
    ): TypeName {
        val argumentTypeName = mapProxyArgumentTypeNames(proxyArguments)

        return TypeVariableName(
            "$prefix(${argumentTypeName.joinToString(", ")}) -> $proxyReturnType"
        )
    }

    private fun determineSpyConstrains(
        proxyArguments: List<ProxyArgumentTypeInfo>,
        proxyReturnType: ProxyReturnTypeInfo,
    ): Boolean {
        var isRecursive = proxyReturnType.generic?.recursive ?: false

        proxyArguments.forEach { (_, genericDeclaration) ->
            isRecursive = isRecursive || genericDeclaration?.recursive ?: false
        }

        return isRecursive
    }

    private fun buildSpyArgumentCasts(
        body: StringBuilder,
        proxyArguments: List<ProxyArgumentTypeInfo>
    ) {
        proxyArguments.forEach { (typeInfo, generics) ->
            if (generics != null && generics.types.size >= 2) {
                generics.types.forEach { type ->
                    val cast: String = if (typeInfo.isVarArg) {
                        "Array<$type>"
                    } else {
                        type.toString()
                    }

                    body.append("\n@Suppress(\"UNCHECKED_CAST\")\n")
                        .append("${typeInfo.argumentName} as $cast\n")
                }
            }
        }
    }

    private fun buildSpyReturnTypeCasts(
        body: StringBuilder,
        returnTypes: GenericDeclaration
    ) {
        returnTypes.types.forEach { type ->
            val typeStr = type.toString()

            if (typeStr.substringBeforeLast('<') in allowedRecursiveTypes) {
                body.append(" as $typeStr")
            }
        }
    }

    private fun buildSpyBody(
        spyTargetName: String,
        spyArguments: SpyArguments,
        proxyArguments: List<ProxyArgumentTypeInfo>,
        proxyReturnType: ProxyReturnTypeInfo,
    ): String {
        val body = StringBuilder(1)

        buildSpyArgumentCasts(
            body = body,
            proxyArguments = proxyArguments
        )

        if (proxyReturnType.generic?.castReturnType == true) {
            body.append("@Suppress(\"UNCHECKED_CAST\")\n")
        }

        body.append("__spyOn!!.$spyTargetName(${spyArguments.invokedArguments})")

        if (proxyReturnType.generic?.castReturnType == true) {
            buildSpyReturnTypeCasts(body, proxyReturnType.generic)
        }

        return body.toString()
    }

    private fun buildSpyBodyIfNotRecursive(
        spyTargetName: String,
        spyArguments: SpyArguments,
        proxyArguments: List<ProxyArgumentTypeInfo>,
        proxyReturnType: ProxyReturnTypeInfo,
    ): String {
        val isRecursive = determineSpyConstrains(proxyArguments, proxyReturnType)

        return if (isRecursive) {
            "throw IllegalArgumentException(\n\"Recursive generics are not supported on function level spies (yet).\"\n)"
        } else {
            buildSpyBody(
                spyTargetName = spyTargetName,
                spyArguments = spyArguments,
                proxyArguments = proxyArguments,
                proxyReturnType = proxyReturnType
            )
        }
    }

    private fun determineSpyInvocationArgument(
        methodInfo: MethodTypeInfo
    ): String {
        return if (methodInfo.isVarArg) {
            "*${methodInfo.argumentName}"
        } else {
            methodInfo.argumentName
        }
    }

    private fun extractSpyArgumentNames(
        proxyArguments: List<ProxyArgumentTypeInfo>,
    ): SpyArguments {
        val declaredArguments = StringBuilder()
        val invokedArguments = StringBuilder()

        proxyArguments.forEach { (methodInfo, _) ->
            declaredArguments.append(methodInfo.argumentName)
            declaredArguments.append(", ")
            invokedArguments.append(determineSpyInvocationArgument(methodInfo))
            invokedArguments.append(", ")
        }

        return SpyArguments(
            declaredArguments = declaredArguments.toString().trimEnd(',', ' '),
            invokedArguments = invokedArguments.toString().trimEnd(',', ' ')
        )
    }

    private fun buildFunctionSpyInvocation(
        spyTargetName: String,
        proxyArguments: List<ProxyArgumentTypeInfo>,
        proxyReturnType: ProxyReturnTypeInfo,
    ): String {
        val spyArguments = extractSpyArgumentNames(proxyArguments)

        val spyBody = buildSpyBodyIfNotRecursive(
            spyTargetName = spyTargetName,
            spyArguments = spyArguments,
            proxyArguments = proxyArguments,
            proxyReturnType = proxyReturnType
        )

        return if (spyArguments.declaredArguments.isEmpty()) {
            "{ $spyBody }"
        } else {
            "{ ${spyArguments.declaredArguments} ->\n$spyBody\n}"
        }
    }

    private fun addSpy(
        relaxationDefinitions: StringBuilder,
        spyTargetName: String,
        proxyArguments: List<ProxyArgumentTypeInfo>,
        proxyReturnType: ProxyReturnTypeInfo,
    ) {
        val spyBody = buildFunctionSpyInvocation(
            spyTargetName = spyTargetName,
            proxyArguments = proxyArguments,
            proxyReturnType = proxyReturnType
        )

        relaxationDefinitions.append(
            """useSpyIf(
            |    spyTarget = __spyOn,
            |    spyOn = $spyBody
            |)
            """.trimMargin() + "\n"
        )
    }

    private fun addFunRelaxer(
        relaxationDefinitions: StringBuilder,
        proxyReturnType: ProxyReturnTypeInfo,
        relaxer: Relaxer?
    ) {
        if (proxyReturnType.typeName.toString() == "kotlin.Unit") {
            relaxationDefinitions.append("useUnitFunRelaxerIf(relaxUnitFun || relaxed)\n")
        } else {
            relaxerGenerator.addRelaxer(relaxationDefinitions, relaxer)
        }
    }

    private fun addRelaxation(
        enableSpy: Boolean,
        spyTargetName: String,
        proxyArguments: List<ProxyArgumentTypeInfo>,
        proxyReturnType: ProxyReturnTypeInfo,
        relaxer: Relaxer?
    ): String {
        val relaxationDefinitions = StringBuilder(3)

        relaxationDefinitions.append("{\n")
        if (enableSpy) {
            addSpy(
                relaxationDefinitions = relaxationDefinitions,
                spyTargetName = spyTargetName,
                proxyArguments = proxyArguments,
                proxyReturnType = proxyReturnType
            )
        }

        addFunRelaxer(
            relaxationDefinitions = relaxationDefinitions,
            proxyReturnType = proxyReturnType,
            relaxer = relaxer
        )
        relaxationDefinitions.append("}")

        return if (relaxationDefinitions.length == 3) {
            ""
        } else {
            relaxationDefinitions.toString()
        }
    }

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
        proxyFactoryMethod: String,
        proxyArguments: List<ProxyArgumentTypeInfo>,
        proxyReturnType: ProxyReturnTypeInfo,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): PropertySpec.Builder {
        return proxySpec.initializer(
            "ProxyFactory.%L(%S, collector = verifier, freeze = freeze) %L",
            proxyFactoryMethod,
            proxyInfo.proxyId,
            addRelaxation(
                enableSpy = enableSpy,
                spyTargetName = proxyInfo.templateName,
                proxyArguments = proxyArguments,
                proxyReturnType = proxyReturnType,
                relaxer = relaxer
            )
        )
    }

    private fun buildProxy(
        proxyInfo: ProxyInfo,
        arguments: Array<MethodTypeInfo>,
        suspending: Boolean,
        generics: Map<String, List<KSTypeReference>>?,
        returnType: KSType,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec, TypeName> {
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

        return Pair(
            PropertySpec.builder(
                proxyInfo.proxyName,
                proxyType.parameterizedBy(proxyReturnType.typeName, sideEffect)
            ).let { proxySpec ->
                buildProxyInitializer(
                    proxySpec = proxySpec,
                    proxyInfo = proxyInfo,
                    proxyFactoryMethod = proxyFactoryMethod,
                    proxyArguments = proxyArguments,
                    proxyReturnType = proxyReturnType,
                    enableSpy = enableSpy,
                    relaxer = relaxer
                )
            }.build(),
            proxyReturnType.typeName
        )
    }

    private fun buildMethod(
        proxyInfo: ProxyInfo,
        generics: Map<String, List<KSTypeReference>>?,
        isSuspending: Boolean,
        arguments: Array<MethodTypeInfo>,
        rawReturnType: KSType,
        proxyReturnType: TypeName,
        typeResolver: TypeParameterResolver,
    ): FunSpec {
        val returnType = rawReturnType.toTypeName(typeResolver)

        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE)

        if (generics != null) {
            method.typeVariables.addAll(
                this.genericResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        if (isSuspending) {
            method.addModifiers(KModifier.SUSPEND)
        }

        arguments.forEach { argument ->
            if (argument.isVarArg) {
                method.addParameter(
                    argument.argumentName,
                    argument.typeName,
                    KModifier.VARARG
                )
            } else {
                method.addParameter(
                    argument.argumentName,
                    argument.typeName
                )
            }
        }

        method.returns(returnType)

        val cast = if (returnType != proxyReturnType) {
            method.addAnnotation(unused)
            " as $returnType"
        } else {
            ""
        }

        val invocation = arguments.joinToString(", ") { argument -> argument.argumentName }

        method.addCode(
            "return ${proxyInfo.proxyName}.invoke($invocation)$cast"
        )

        return method.build()
    }

    override fun buildMethodBundle(
        qualifier: String,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec, FunSpec> {
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

        val (proxy, proxyReturnType) = buildProxy(
            proxyInfo = proxyInfo,
            arguments = arguments,
            generics = generics,
            suspending = isSuspending,
            returnType = returnType,
            enableSpy = enableSpy,
            typeResolver = parameterTypeResolver,
            relaxer = relaxer
        )

        val method = buildMethod(
            proxyInfo = proxyInfo,
            generics = generics,
            isSuspending = isSuspending,
            arguments = arguments,
            rawReturnType = returnType,
            proxyReturnType = proxyReturnType,
            typeResolver = parameterTypeResolver
        )

        return Pair(proxy, method)
    }
}
