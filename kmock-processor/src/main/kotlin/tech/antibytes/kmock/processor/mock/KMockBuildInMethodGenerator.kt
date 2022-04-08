/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.processor.ProcessorContract.BuildInMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.RelaxerGenerator

internal class KMockBuildInMethodGenerator(
    private val nameSelector: ProxyNameSelector,
    private val relaxerGenerator: RelaxerGenerator,
) : BuildInMethodGenerator {
    private val buildIns = mapOf(
        "toString" to String::class,
        "equals" to Boolean::class,
        "hashCode" to Int::class
    )

    private val any = Any::class.asTypeName().copy(nullable = true)
    private val proxy = SyncFunProxy::class.asClassName()

    private fun resolveArgument(methodName: String): MethodTypeInfo? {
        return if (methodName == "equals") {
            MethodTypeInfo("other", any, false)
        } else {
            null
        }
    }

    private fun buildRelaxationInvocation(
        parent: String,
        methodName: String,
        argumentName: String,
    ): String {
        val body = "$parent.$methodName($argumentName)"

        return if (argumentName.isEmpty()) {
            "{ $body }"
        } else {
            "{ $argumentName ->\n$body\n}"
        }
    }

    private fun addSpy(
        relaxationDefinitions: StringBuilder,
        mockName: String,
        methodName: String,
        argumentName: String
    ) {

        if (methodName == "equals") {
            val spyBody = buildRelaxationInvocation(
                parent = "super",
                methodName = methodName,
                argumentName = argumentName
            )

            relaxationDefinitions.append(
                """useSpyOnEqualsIf(
                |    spyTarget = __spyOn,
                |    equals = $spyBody,
                |    mockKlass = $mockName::class
                |)
                """.trimMargin() + "\n"
            )
        } else {
            val spyBody = buildRelaxationInvocation(
                parent = "__spyOn!!",
                methodName = methodName,
                argumentName = argumentName
            )

            relaxationDefinitions.append(
                """useSpyIf(
                |    spyTarget = __spyOn,
                |    spyOn = $spyBody
                |)
                """.trimMargin() + "\n"
            )
        }
    }

    private fun addRelaxation(
        mockName: String,
        methodName: String,
        argument: MethodTypeInfo?,
        enableSpy: Boolean,
    ): String {
        return relaxerGenerator.addBuildInRelaxation(
            methodName = methodName,
            argument = argument
        ) { relaxationDefinitions ->
            val argumentName = argument?.argumentName ?: ""

            if (enableSpy) {
                addSpy(
                    relaxationDefinitions = relaxationDefinitions,
                    mockName = mockName,
                    methodName = methodName,
                    argumentName = argumentName
                )
            }
        }
    }

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        mockName: String,
        argument: MethodTypeInfo?,
        proxyInfo: ProxyInfo,
        enableSpy: Boolean
    ): PropertySpec.Builder {
        return proxySpec.initializer(
            "ProxyFactory.createSyncFunProxy(%S, collector = verifier, freeze = freeze, ignorableForVerification = true) %L",
            proxyInfo.proxyId,
            addRelaxation(
                mockName = mockName,
                methodName = proxyInfo.templateName,
                argument = argument,
                enableSpy = enableSpy
            )
        )
    }

    private fun buildSideEffectSignature(
        proxyArgumentType: TypeName?,
        proxyReturnType: TypeName,
    ): TypeName {
        return TypeVariableName(
            "(${proxyArgumentType?.toString() ?: ""}) -> $proxyReturnType"
        )
    }

    private fun buildProxy(
        mockName: String,
        proxyInfo: ProxyInfo,
        proxyArgument: MethodTypeInfo?,
        enableSpy: Boolean
    ): PropertySpec {
        val proxyReturnType = buildIns[proxyInfo.templateName]!!.asTypeName()

        val sideEffect = buildSideEffectSignature(
            proxyArgument?.typeName,
            proxyReturnType,
        )

        return PropertySpec.builder(
            proxyInfo.proxyName,
            proxy.parameterizedBy(proxyReturnType, sideEffect)
        ).let { proxySpec ->
            buildProxyInitializer(
                proxySpec = proxySpec,
                mockName = mockName,
                argument = proxyArgument,
                proxyInfo = proxyInfo,
                enableSpy = enableSpy
            )
        }.build()
    }

    private fun buildMethod(
        proxyInfo: ProxyInfo,
        argument: MethodTypeInfo?,
    ): FunSpec {
        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE)
            .returns(buildIns[proxyInfo.templateName]!!)

        if (argument != null) {
            method.addParameter(argument.argumentName, argument.typeName)
        }

        method.addCode("return ${proxyInfo.proxyName}.invoke(${argument?.argumentName ?: ""})")

        return method.build()
    }

    private fun buildBuildInMethodBundle(
        mockName: String,
        qualifier: String,
        methodName: String,
        enableSpy: Boolean
    ): Pair<PropertySpec, FunSpec> {
        val proxyInfo = nameSelector.selectBuildInMethodName(
            qualifier = qualifier,
            methodName = methodName,
        )
        val argument = resolveArgument(methodName)

        val proxy = buildProxy(
            mockName = mockName,
            proxyInfo = proxyInfo,
            proxyArgument = argument,
            enableSpy = enableSpy
        )

        val method = buildMethod(
            proxyInfo = proxyInfo,
            argument = argument
        )

        return Pair(proxy, method)
    }

    override fun buildMethodBundles(
        mockName: String,
        qualifier: String,
        enableSpy: Boolean,
    ): Pair<List<PropertySpec>, List<FunSpec>> {
        val proxies: MutableList<PropertySpec> = mutableListOf()
        val methods: MutableList<FunSpec> = mutableListOf()

        buildIns.keys.forEach { methodName ->
            val (proxy, method) = buildBuildInMethodBundle(
                mockName = mockName,
                qualifier = qualifier,
                methodName = methodName,
                enableSpy = enableSpy,
            )

            proxies.add(proxy)
            methods.add(method)
        }

        return Pair(proxies, methods)
    }
}
