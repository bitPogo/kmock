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
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector

internal class KMockBuildInMethodGenerator(
    private val nameSelector: ProxyNameSelector,
    private val nonIntrusiveInvocationGenerator: NonIntrusiveInvocationGenerator
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

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
    ): PropertySpec.Builder {
        return proxySpec.initializer(
            "ProxyFactory.createSyncFunProxy(%S, collector = verifier, freeze = freeze, ignorableForVerification = true)",
            proxyInfo.proxyId
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
        proxyInfo: ProxyInfo,
        proxyArgument: MethodTypeInfo?,
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
                proxyInfo = proxyInfo,
            )
        }.build()
    }

    private fun buildMethod(
        mockName: String,
        proxyInfo: ProxyInfo,
        argument: MethodTypeInfo?,
        enableSpy: Boolean
    ): FunSpec {
        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE)
            .returns(buildIns[proxyInfo.templateName]!!)

        if (argument != null) {
            method.addParameter(argument.argumentName, argument.typeName)
        }

        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildBuildInNonIntrusiveInvocation(
            enableSpy = enableSpy,
            mockName = mockName,
            methodName = proxyInfo.templateName,
            argument = argument
        )

        val invocationArgument = argument?.argumentName ?: ""

        method.addCode("return ${proxyInfo.proxyName}.invoke($invocationArgument)$nonIntrusiveInvocation")

        return method.build()
    }

    private fun buildBuildInMethodBundle(
        mockName: String,
        qualifier: String,
        methodName: String,
        enableSpy: Boolean,
    ): Pair<PropertySpec, FunSpec> {
        val proxyInfo = nameSelector.selectBuildInMethodName(
            qualifier = qualifier,
            methodName = methodName,
        )
        val argument = resolveArgument(methodName)

        val proxy = buildProxy(
            proxyInfo = proxyInfo,
            proxyArgument = argument,
        )

        val method = buildMethod(
            mockName = mockName,
            proxyInfo = proxyInfo,
            argument = argument,
            enableSpy = enableSpy
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
