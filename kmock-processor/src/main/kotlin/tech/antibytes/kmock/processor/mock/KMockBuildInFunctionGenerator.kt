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
import tech.antibytes.kmock.processor.ProcessorContract.BuildInFunctionGenerator

internal object KMockBuildInFunctionGenerator : BuildInFunctionGenerator {
    private val buildIns = mapOf(
        "toString" to String::class,
        "equals" to Boolean::class,
        "hashCode" to Int::class
    )

    private val any = Any::class.asTypeName()

    private fun determineSuffixedProxyName(proxyName: String): String {
        return when (proxyName) {
            "_toString" -> "_toStringWithVoid"
            "_equals" -> "_equalsWithAny"
            else -> "_hashCodeWithVoid"
        }
    }

    private fun determineProxyName(
        functionName: String,
        existingProxies: Set<String>
    ): String {
        val proxyName = "_$functionName"

        return if (proxyName in existingProxies) {
            determineSuffixedProxyName(proxyName)
        } else {
            proxyName
        }
    }

    private fun resolveArgumentType(functionName: String): TypeName? {
        return if (functionName == "equals") {
            any.copy(nullable = true)
        } else {
            null
        }
    }

    private fun resolveArgumentName(
        functionName: String
    ): String {
        return if (functionName == "equals") {
            "other"
        } else {
            ""
        }
    }

    private fun buildFunctionSpyInvocation(
        spyName: String,
        spyArgumentName: String,
    ): String {
        val spyBody = "spyOn.$spyName($spyArgumentName)"

        return if (spyArgumentName.isEmpty()) {
            "{ $spyBody }"
        } else {
            "{ $spyArgumentName ->\n$spyBody }"
        }
    }

    private fun buildRelaxer(
        functionName: String,
        argumentName: String
    ): String {
        val otherRelaxersStr = "unitFunRelaxer = null, relaxer = null"
        val argumentDelegation = if (argumentName.isEmpty()) {
            ""
        } else {
            "$argumentName -> "
        }
        val relaxerStr = "buildInRelaxer = { ${argumentDelegation}super.$functionName($argumentName) }"

        return "$otherRelaxersStr, $relaxerStr"
    }

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        qualifier: String,
        functionName: String,
        proxyName: String,
    ): PropertySpec.Builder {
        val argumentName = resolveArgumentName(functionName)

        return proxySpec.initializer(
            "%L(%S, spyOn = %L, collector = verifier, freeze = freeze, %L, ignorableForVerification = true)",
            SyncFunProxy::class.simpleName,
            "$qualifier#$proxyName",
            "if (spyOn != null) { ${
            buildFunctionSpyInvocation(
                spyName = functionName,
                spyArgumentName = argumentName,
            )
            } } else { null }",
            buildRelaxer(functionName, argumentName)
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
        functionName: String,
        proxyName: String,
    ): PropertySpec {
        val proxyArgument = resolveArgumentType(functionName)
        val proxyReturnType = buildIns[functionName]!!.asTypeName()

        val sideEffect = buildSideEffectSignature(
            proxyArgument,
            proxyReturnType,
        )

        val proxyClass = SyncFunProxy::class.asClassName()

        return PropertySpec.builder(
            proxyName,
            proxyClass.parameterizedBy(proxyReturnType, sideEffect)
        ).let { proxySpec ->
            buildProxyInitializer(
                proxySpec = proxySpec,
                functionName = functionName,
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
        function: FunSpec.Builder,
        functionName: String,
        proxyName: String,
        amountOfGenerics: Int
    ): FunSpec.Builder {
        return function.addCode(
            """
            | return if(other is $mockName${buildGenerics(amountOfGenerics)} && __spyOn != null) {
            |   super.$functionName(other)
            | } else {
            |   $proxyName.invoke(other)
            | }
            """.trimMargin()
        )
    }

    private fun buildBuildInFunction(
        function: FunSpec.Builder,
        proxyName: String,
    ): FunSpec.Builder = function.addCode("return $proxyName.invoke()")

    private fun buildFunctionProxyBinding(
        mockName: String,
        function: FunSpec.Builder,
        functionName: String,
        proxyName: String,
        amountOfGenerics: Int
    ): FunSpec.Builder {
        return if (functionName == "equals") {
            buildEqualsInvocation(
                mockName = mockName,
                functionName = functionName,
                function = function,
                proxyName = proxyName,
                amountOfGenerics = amountOfGenerics,
            )
        } else {
            buildBuildInFunction(
                function = function,
                proxyName = proxyName,
            )
        }
    }

    private fun buildFunction(
        mockName: String,
        functionName: String,
        proxyName: String,
        amountOfGenerics: Int
    ): FunSpec {
        val function = FunSpec
            .builder(functionName)
            .addModifiers(KModifier.OVERRIDE)
            .returns(buildIns[functionName]!!)

        resolveArgumentType(functionName)?.also { argument ->
            function.addParameter("other", argument)
        }

        return buildFunctionProxyBinding(
            mockName = mockName,
            function = function,
            functionName = functionName,
            proxyName = proxyName,
            amountOfGenerics = amountOfGenerics,
        ).build()
    }

    private fun buildBuildInFunctionBundle(
        mockName: String,
        qualifier: String,
        functionName: String,
        existingProxies: Set<String>,
        amountOfGenerics: Int
    ): Pair<PropertySpec, FunSpec> {
        val proxyName = determineProxyName(functionName, existingProxies)
        val proxy = buildProxy(
            qualifier = qualifier,
            proxyName = proxyName,
            functionName = functionName,
        )

        val function = buildFunction(
            mockName = mockName,
            functionName = functionName,
            proxyName = proxyName,
            amountOfGenerics = amountOfGenerics,
        )

        return Pair(proxy, function)
    }

    override fun buildFunctionBundles(
        mockName: String,
        qualifier: String,
        existingProxies: Set<String>,
        amountOfGenerics: Int
    ): Pair<List<PropertySpec>, List<FunSpec>> {
        val proxies: MutableList<PropertySpec> = mutableListOf()
        val functions: MutableList<FunSpec> = mutableListOf()

        buildIns.keys.forEach { functionName ->
            val (proxy, function) = buildBuildInFunctionBundle(
                mockName = mockName,
                qualifier = qualifier,
                functionName = functionName,
                existingProxies = existingProxies,
                amountOfGenerics = amountOfGenerics,
            )

            proxies.add(proxy)
            functions.add(function)
        }

        return Pair(proxies, functions)
    }
}
