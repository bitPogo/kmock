/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.processor.ProcessorContract.BuildInMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.CREATE_SYNC_PROXY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.EQUALS
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.IGNORE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PROXY_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.MemberArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector

internal class KMockBuildInMethodGenerator(
    private val nameSelector: ProxyNameSelector,
    private val nonIntrusiveInvocationGenerator: NonIntrusiveInvocationGenerator,
) : BuildInMethodGenerator {
    private fun resolveArgument(methodName: String): MemberArgumentTypeInfo? {
        return if (methodName == EQUALS) {
            equalsPayload
        } else {
            null
        }
    }

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
    ): PropertySpec.Builder {
        return proxySpec.initializer(
            "${PROXY_FACTORY.simpleName}.$CREATE_SYNC_PROXY(%S, $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT, $FREEZE_ARGUMENT = $FREEZE_ARGUMENT, $IGNORE_ARGUMENT = true)",
            proxyInfo.proxyId,
        )
    }

    private fun buildProxy(
        proxyInfo: ProxyInfo,
    ): PropertySpec {
        val proxyReturnType = buildIns[proxyInfo.templateName]!!
        val sideEffect = sideEffects[proxyInfo.templateName]!!

        return PropertySpec.builder(
            proxyInfo.proxyName,
            proxy.parameterizedBy(proxyReturnType, sideEffect),
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
        argument: MemberArgumentTypeInfo?,
        enableSpy: Boolean,
    ): FunSpec {
        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
            .returns(buildIns[proxyInfo.templateName]!!)

        if (argument != null) {
            method.addParameter(argument.argumentName, argument.methodTypeName)
        }

        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildBuildInNonIntrusiveInvocation(
            enableSpy = enableSpy,
            mockName = mockName,
            methodName = proxyInfo.templateName,
            argument = argument,
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
    ): Triple<PropertySpec, FunSpec, LambdaTypeName> {
        val proxyInfo = nameSelector.selectBuildInMethodName(
            qualifier = qualifier,
            methodName = methodName,
        )
        val argument = resolveArgument(methodName)

        val proxy = buildProxy(
            proxyInfo = proxyInfo,
        )

        val method = buildMethod(
            mockName = mockName,
            proxyInfo = proxyInfo,
            argument = argument,
            enableSpy = enableSpy,
        )

        return Triple(proxy, method, sideEffects[proxyInfo.templateName]!!)
    }

    override fun buildMethodBundles(
        mockName: String,
        qualifier: String,
        enableSpy: Boolean,
    ): List<Triple<PropertySpec, FunSpec, LambdaTypeName>> {
        return buildIns.keys.map { methodName ->
            buildBuildInMethodBundle(
                mockName = mockName,
                qualifier = qualifier,
                methodName = methodName,
                enableSpy = enableSpy,
            )
        }
    }

    private companion object {
        private val any = Any::class.asClassName().copy(nullable = true)
        private val proxy = SyncFunProxy::class.asClassName()
        private val equalsPayload = MemberArgumentTypeInfo("other", any, any, false)

        private val buildIns = mapOf(
            "toString" to String::class.asClassName(),
            "equals" to Boolean::class.asClassName(),
            "hashCode" to Int::class.asClassName(),
        )
        private val sideEffects = mapOf(
            "toString" to LambdaTypeName.get(returnType = String::class.asClassName()),
            "equals" to LambdaTypeName.get(returnType = Boolean::class.asClassName(), parameters = arrayOf(any)),
            "hashCode" to LambdaTypeName.get(returnType = Int::class.asClassName()),
        )
    }
}
