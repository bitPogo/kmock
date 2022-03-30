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

internal object KMockBuildInMethodGenerator : BuildInMethodGenerator {
    private val buildIns = mapOf(
        "toString" to String::class,
        "equals" to Boolean::class,
        "hashCode" to Int::class
    )

    private val any = Any::class.asTypeName()
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

    private fun resolveArgumentType(methodName: String): TypeName? {
        return if (methodName == "equals") {
            any.copy(nullable = true)
        } else {
            null
        }
    }

    private fun resolveArgumentName(
        methodName: String
    ): String {
        return if (methodName == "equals") {
            "other"
        } else {
            ""
        }
    }

    private fun buildMethodSpyInvocation(
        spyName: String,
        spyArgumentName: String,
    ): String {
        val spyBody = "__spyOn!!.$spyName($spyArgumentName)"

        return if (spyArgumentName.isEmpty()) {
            "{ $spyBody }"
        } else {
            "{ $spyArgumentName ->\n$spyBody }"
        }
    }

    private fun buildRelaxer(
        methodName: String,
        argumentName: String
    ): String {
        val otherRelaxersStr = "unitFunRelaxer = null, relaxer = null"
        val argumentDelegation = if (argumentName.isEmpty()) {
            ""
        } else {
            "$argumentName -> "
        }
        val relaxerStr = "buildInRelaxer = { ${argumentDelegation}super.$methodName($argumentName) }"

        return "$otherRelaxersStr, $relaxerStr"
    }

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        qualifier: String,
        methodName: String,
        proxyName: String,
    ): PropertySpec.Builder {
        val argumentName = resolveArgumentName(methodName)
        val proxyId = "$qualifier#$proxyName"

        return proxySpec.initializer(
            "ProxyFactory.createSyncFunProxy(%S, spyOn = %L, collector = verifier, freeze = freeze, %L, ignorableForVerification = true)",
            proxyId,
            "if (spyOn != null) { ${
            buildMethodSpyInvocation(
                spyName = methodName,
                spyArgumentName = argumentName,
            )
            } } else { null }",
            buildRelaxer(methodName, argumentName)
        )
    }

    private fun buildSideEffectSignature(
        proxyArgument: TypeName?,
        proxyReturnType: TypeName,
    ): TypeName {
        return TypeVariableName(
            "(${proxyArgument?.toString() ?: ""}) -> $proxyReturnType"
        )
    }

    private fun buildProxy(
        qualifier: String,
        methodName: String,
        proxyName: String,
    ): PropertySpec {
        val proxyArgument = resolveArgumentType(methodName)
        val proxyReturnType = buildIns[methodName]!!.asTypeName()

        val sideEffect = buildSideEffectSignature(
            proxyArgument,
            proxyReturnType,
        )

        return PropertySpec.builder(
            proxyName,
            proxy.parameterizedBy(proxyReturnType, sideEffect)
        ).let { proxySpec ->
            buildProxyInitializer(
                proxySpec = proxySpec,
                methodName = methodName,
                qualifier = qualifier,
                proxyName = proxyName,
            )
        }.build()
    }

    private fun buildGenerics(amountOfGenerics: Int): String {
        return if (amountOfGenerics == 0) {
            ""
        } else {
            "<${List(amountOfGenerics) { "*" }.joinToString(", ")}>"
        }
    }

    private fun buildEqualsInvocation(
        mockName: String,
        method: FunSpec.Builder,
        methodName: String,
        proxyName: String,
        amountOfGenerics: Int
    ): FunSpec.Builder {
        return method.addCode(
            """
            | return if(other is $mockName${buildGenerics(amountOfGenerics)} && __spyOn != null) {
            |   super.$methodName(other)
            | } else {
            |   $proxyName.invoke(other)
            | }
            """.trimMargin()
        )
    }

    private fun buildBuildInFunction(
        method: FunSpec.Builder,
        proxyName: String,
    ): FunSpec.Builder = method.addCode("return $proxyName.invoke()")

    private fun buildMethodProxyBinding(
        mockName: String,
        method: FunSpec.Builder,
        methodName: String,
        proxyName: String,
        amountOfGenerics: Int
    ): FunSpec.Builder {
        return if (methodName == "equals") {
            buildEqualsInvocation(
                mockName = mockName,
                methodName = methodName,
                method = method,
                proxyName = proxyName,
                amountOfGenerics = amountOfGenerics,
            )
        } else {
            buildBuildInFunction(
                method = method,
                proxyName = proxyName,
            )
        }
    }

    private fun buildMethod(
        mockName: String,
        methodName: String,
        proxyName: String,
        amountOfGenerics: Int
    ): FunSpec {
        val method = FunSpec
            .builder(methodName)
            .addModifiers(KModifier.OVERRIDE)
            .returns(buildIns[methodName]!!)

        resolveArgumentType(methodName)?.also { argument ->
            method.addParameter("other", argument)
        }

        return buildMethodProxyBinding(
            mockName = mockName,
            method = method,
            methodName = methodName,
            proxyName = proxyName,
            amountOfGenerics = amountOfGenerics,
        ).build()
    }

    private fun buildBuildInMethodBundle(
        mockName: String,
        qualifier: String,
        methodName: String,
        existingProxies: Set<String>,
        amountOfGenerics: Int
    ): Pair<PropertySpec, FunSpec> {
        val proxyName = determineProxyName(methodName, existingProxies)
        val proxy = buildProxy(
            qualifier = qualifier,
            proxyName = proxyName,
            methodName = methodName,
        )

        val method = buildMethod(
            mockName = mockName,
            methodName = methodName,
            proxyName = proxyName,
            amountOfGenerics = amountOfGenerics,
        )

        return Pair(proxy, method)
    }

    override fun buildMethodBundles(
        mockName: String,
        qualifier: String,
        existingProxies: Set<String>,
        amountOfGenerics: Int
    ): Pair<List<PropertySpec>, List<FunSpec>> {
        val proxies: MutableList<PropertySpec> = mutableListOf()
        val methods: MutableList<FunSpec> = mutableListOf()

        buildIns.keys.forEach { methodName ->
            val (proxy, method) = buildBuildInMethodBundle(
                mockName = mockName,
                qualifier = qualifier,
                methodName = methodName,
                existingProxies = existingProxies,
                amountOfGenerics = amountOfGenerics,
            )

            proxies.add(proxy)
            methods.add(method)
        }

        return Pair(proxies, methods)
    }
}
