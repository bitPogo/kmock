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
import tech.antibytes.kmock.processor.titleCase

internal object KMockBuildInMethodGenerator : BuildInMethodGenerator {
    private val buildIns = mapOf(
        "toString" to String::class,
        "equals" to Boolean::class,
        "hashCode" to Int::class
    )

    private val any = Any::class.asTypeName().copy(nullable = true)
    private val proxy = SyncFunProxy::class.asClassName()

    private fun determineSuffixedProxyName(proxyName: String): String {
        return when (proxyName) {
            "_toString" -> "_toStringWithVoid"
            "_equals" -> "_equalsWithAny"
            else -> "_hashCodeWithVoid"
        }
    }

    private fun determineProxyName(
        methodName: String,
        existingProxies: Set<String>
    ): String {
        val proxyName = "_$methodName"

        return if (proxyName in existingProxies) {
            determineSuffixedProxyName(proxyName)
        } else {
            proxyName
        }
    }

    private fun resolveArgument(methodName: String): Pair<String, TypeName>? {
        return if (methodName == "equals") {
            Pair("other", any)
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
                |    spy = __spyOn,
                |    parent = $spyBody,
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
                |    spy = __spyOn,
                |    spyOn = $spyBody
                |)
                """.trimMargin() + "\n"
            )
        }
    }

    private fun addRelaxation(
        mockName: String,
        methodName: String,
        argument: Pair<String, TypeName>?,
        enableSpy: Boolean,
    ): String {
        val argumentName = argument?.first ?: ""
        val relaxerBody = buildRelaxationInvocation(
            parent = "super",
            methodName = methodName,
            argumentName = argumentName
        )

        val relaxationDefinitions = StringBuilder(3 + relaxerBody.length)

        relaxationDefinitions.append("{\n")

        relaxationDefinitions.append("use${methodName.titleCase()}Relaxer $relaxerBody\n")

        if (enableSpy) {
            addSpy(
                relaxationDefinitions = relaxationDefinitions,
                mockName = mockName,
                methodName = methodName,
                argumentName = argumentName
            )
        }

        relaxationDefinitions.append("}")

        return relaxationDefinitions.toString()
    }

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        qualifier: String,
        mockName: String,
        methodName: String,
        argument: Pair<String, TypeName>?,
        proxyName: String,
        enableSpy: Boolean
    ): PropertySpec.Builder {
        val proxyId = "$qualifier#$proxyName"

        return proxySpec.initializer(
            "ProxyFactory.createSyncFunProxy(%S, collector = verifier, freeze = freeze, ignorableForVerification = true) %L",
            proxyId,
            addRelaxation(
                mockName = mockName,
                methodName = methodName,
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
        qualifier: String,
        mockName: String,
        methodName: String,
        proxyName: String,
        proxyArgument: Pair<String, TypeName>?,
        enableSpy: Boolean
    ): PropertySpec {
        val proxyReturnType = buildIns[methodName]!!.asTypeName()

        val sideEffect = buildSideEffectSignature(
            proxyArgument?.second,
            proxyReturnType,
        )

        return PropertySpec.builder(
            proxyName,
            proxy.parameterizedBy(proxyReturnType, sideEffect)
        ).let { proxySpec ->
            buildProxyInitializer(
                proxySpec = proxySpec,
                mockName = mockName,
                methodName = methodName,
                argument = proxyArgument,
                qualifier = qualifier,
                proxyName = proxyName,
                enableSpy = enableSpy
            )
        }.build()
    }

    private fun buildMethod(
        methodName: String,
        argument: Pair<String, TypeName>?,
        proxyName: String,
    ): FunSpec {
        val method = FunSpec
            .builder(methodName)
            .addModifiers(KModifier.OVERRIDE)
            .returns(buildIns[methodName]!!)

        if (argument != null) {
            method.addParameter(argument.first, argument.second)
        }

        method.addCode("return $proxyName.invoke(${argument?.first ?: ""})")

        return method.build()
    }

    private fun buildBuildInMethodBundle(
        mockName: String,
        qualifier: String,
        methodName: String,
        existingProxies: Set<String>,
        enableSpy: Boolean
    ): Pair<PropertySpec, FunSpec> {
        val proxyName = determineProxyName(methodName, existingProxies)
        val argument = resolveArgument(methodName)
        val proxy = buildProxy(
            qualifier = qualifier,
            mockName = mockName,
            proxyName = proxyName,
            proxyArgument = argument,
            methodName = methodName,
            enableSpy = enableSpy
        )

        val method = buildMethod(
            methodName = methodName,
            proxyName = proxyName,
            argument = argument
        )

        return Pair(proxy, method)
    }

    override fun buildMethodBundles(
        mockName: String,
        qualifier: String,
        existingProxies: Set<String>,
        enableSpy: Boolean,
    ): Pair<List<PropertySpec>, List<FunSpec>> {
        val proxies: MutableList<PropertySpec> = mutableListOf()
        val methods: MutableList<FunSpec> = mutableListOf()

        buildIns.keys.forEach { methodName ->
            val (proxy, method) = buildBuildInMethodBundle(
                mockName = mockName,
                qualifier = qualifier,
                methodName = methodName,
                existingProxies = existingProxies,
                enableSpy = enableSpy,
            )

            proxies.add(proxy)
            methods.add(method)
        }

        return Pair(proxies, methods)
    }
}
