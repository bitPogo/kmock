/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.KMockContract
import java.util.Locale

internal class KMockFunctionGenerator(
    private val utils: ProcessorContract.FunctionUtils,
    private val relaxerGenerator: ProcessorContract.RelaxerGenerator
) : ProcessorContract.FunctionGenerator {
    private data class ReturnType(
        val actualType: TypeName,
        val referenceType: TypeName,
        val isMultiBound: Boolean
    )

    private fun buildFunction(
        ksFunctionName: String,
        generics: Map<String, List<KSTypeReference>>?,
        typeResolver: TypeParameterResolver,
        isSuspending: Boolean,
        parameterNames: List<String>,
        parameterTypes: List<TypeName>,
        returnType: ReturnType,
        ProxyName: String
    ): FunSpec {
        val function = FunSpec
            .builder(ksFunctionName)
            .addModifiers(KModifier.OVERRIDE)

        if (generics != null) {
            function.typeVariables.addAll(utils.mapGeneric(generics, typeResolver))
        }

        if (isSuspending) {
            function.addModifiers(KModifier.SUSPEND)
        }

        parameterNames.forEachIndexed { idx, parameter ->
            function.addParameter(
                parameter,
                parameterTypes[idx]
            )
        }

        function.returns(returnType.referenceType)

        val cast = if (returnType.actualType != returnType.referenceType) {
            function.addAnnotation(
                AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build()
            )
            " as ${returnType.referenceType}"
        } else {
            ""
        }

        function.addCode(
            "return $ProxyName.invoke(${parameterNames.joinToString(", ")})$cast"
        )

        return function.build()
    }

    private fun guardMultiBoundaries(
        functionName: String,
        parameter: String,
        boundaries: Map<TypeVariableName, List<KSTypeReference>?>,
        returnType: ReturnType
    ): String {
        var isMultiBoundary = returnType.isMultiBound

        boundaries.values.forEach { types ->
            if (types is List) {
                isMultiBoundary = true
            }
        }

        return if (isMultiBoundary) {
            "throw IllegalArgumentException(\n\"Multi-Bound generics are not supported on function level spies (yet).\"\n)"
        } else {
            "$functionName($parameter)"
        }
    }

    private fun buildFunctionSpyInvocation(
        functionName: String,
        parameterNames: List<String>,
        boundaries: Map<TypeVariableName, List<KSTypeReference>?>,
        returnType: ReturnType
    ): String {
        val parameter = parameterNames.joinToString(", ")

        return if (parameter.isEmpty()) {
            "{ ${guardMultiBoundaries(functionName, "", boundaries, returnType)} }"
        } else {
            "{ $parameter ->\n${guardMultiBoundaries(functionName, parameter, boundaries, returnType)} }"
        }
    }

    private fun determineFunctionInitializer(
        propertyMock: PropertySpec.Builder,
        Proxy: String,
        qualifier: String,
        ProxyName: String,
        functionName: String,
        parameterNames: List<String>,
        boundaries: Map<TypeVariableName, List<KSTypeReference>?>,
        returnType: ReturnType,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec.Builder {
        return propertyMock.initializer(
            "%L(%S, spyOn = %L, collector = verifier, freeze = freeze, %L)",
            Proxy,
            "$qualifier#$ProxyName",
            "if (spyOn != null) { ${
            buildFunctionSpyInvocation(
                functionName,
                parameterNames,
                boundaries,
                returnType
            )
            } } else { null }",
            relaxerGenerator.buildRelaxers(
                relaxer,
                returnType.actualType.toString() == "kotlin.Unit"
            )
        )
    }

    private fun buildSyncFunProxy(
        qualifier: String,
        functionName: String,
        ProxyName: String,
        parameterNames: List<String>,
        parameterTypes: Map<TypeVariableName, List<KSTypeReference>?>,
        returnType: ReturnType,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec {
        val lambda = TypeVariableName(
            "(${parameterTypes.keys.joinToString(", ")}) -> ${returnType.actualType}"
        )
        val property = PropertySpec.builder(
            ProxyName,
            KMockContract.SyncFunProxy::class
                .asClassName()
                .parameterizedBy(returnType.actualType, lambda),
        )

        return determineFunctionInitializer(
            property,
            "SyncFunProxy",
            qualifier,
            ProxyName,
            functionName,
            parameterNames,
            parameterTypes,
            returnType,
            relaxer
        ).build()
    }

    private fun buildASyncFunProxy(
        qualifier: String,
        functionName: String,
        ProxyName: String,
        parameterNames: List<String>,
        parameterTypes: Map<TypeVariableName, List<KSTypeReference>?>,
        returnType: ReturnType,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec {
        val lambda = TypeVariableName(
            "suspend (${parameterTypes.keys.joinToString(", ")}) -> ${returnType.actualType}"
        )
        val property = PropertySpec.builder(
            ProxyName,
            KMockContract.AsyncFunProxy::class
                .asClassName()
                .parameterizedBy(returnType.actualType, lambda),
        )

        return determineFunctionInitializer(
            property,
            "AsyncFunProxy",
            qualifier,
            ProxyName,
            functionName,
            parameterNames,
            parameterTypes,
            returnType,
            relaxer
        ).build()
    }

    private fun String.titleCase(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(Locale.getDefault())
            } else {
                it.toString()
            }
        }
    }

    private fun determineGenericName(
        bounds: List<KSTypeReference>?,
        typeResolver: TypeParameterResolver
    ): String? {
        return if (bounds.isNullOrEmpty()) {
            null
        } else {
            bounds.joinToString("") { typeName ->
                typeName.toTypeName(typeResolver)
                    .toString()
                    .replace("?", "")
                    .replace("kotlin.", "")
            }
        }
    }

    private fun determineSuffixedFunctionName(
        functionName: String,
        parameter: List<TypeName>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        val titleCasedSuffixes: MutableList<String> = mutableListOf()
        parameter.forEach { suffix ->
            suffix
                .toString()
                .let { name ->
                    if (name in generics) {
                        determineGenericName(generics[name], typeResolver) ?: "Any"
                    } else {
                        name
                    }
                }
                .removePrefix("kotlin.")
                .substringBefore('<') // Lambdas
                .let { name ->
                    if (name.contains('.')) {
                        name.split('.')
                            .map { partialName -> partialName.titleCase() }
                            .joinToString("")
                    } else {
                        name
                    }
                }
                .also { suffixCased -> titleCasedSuffixes.add(suffixCased) }
        }

        return "${functionName}With${titleCasedSuffixes.joinToString("")}"
    }

    private fun selectFunProxyName(
        functionName: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        parameter: List<TypeName>,
        existingFunctions: List<String>
    ): String {
        val ProxyName = "_$functionName"

        return if (existingFunctions.contains(ProxyName)) {
            determineSuffixedFunctionName(
                ProxyName,
                parameter,
                generics,
                typeResolver
            )
        } else {
            ProxyName
        }
    }

    private fun determineParameter(
        parameters: List<KSValueParameter>,
        parameterTypeResolver: TypeParameterResolver
    ): Pair<List<String>, List<TypeName>> {
        val nameCollector: MutableList<String> = mutableListOf()
        val typeCollector: MutableList<TypeName> = mutableListOf()
        parameters.forEach { parameter ->
            nameCollector.add(parameter.name!!.asString())
            typeCollector.add(parameter.type.toTypeName(parameterTypeResolver))
        }

        return Pair(nameCollector, typeCollector)
    }

    private fun isNullable(bounds: List<KSTypeReference>): String {
        bounds.forEach { bound ->
            if (bound.resolve().nullability != Nullability.NULLABLE) {
                return ""
            }
        }

        return "?"
    }

    private fun determineProxyParameter(
        parameter: List<TypeName>,
        generics: Map<String, List<KSTypeReference>>?,
        typeResolver: TypeParameterResolver
    ): Map<TypeVariableName, List<KSTypeReference>?> {
        val typeMapping: MutableMap<TypeVariableName, List<KSTypeReference>?> = mutableMapOf()

        if (generics != null) {
            parameter.forEach { type ->
                var typeListing: List<KSTypeReference>? = null

                generics.getOrElse(type.toString()) { type }.let { types ->
                    @Suppress("UNCHECKED_CAST")
                    val actualType = when {
                        types !is List<*> || types.isEmpty() -> "Any?"
                        types.size > 1 -> "Any${isNullable(types as List<KSTypeReference>)}".also {
                            typeListing = types
                        }
                        else -> (types.first() as KSTypeReference).toTypeName(typeResolver).toString()
                    }

                    typeMapping[TypeVariableName(actualType)] = typeListing
                }
            }
        } else {
            parameter.forEach { type ->
                typeMapping[TypeVariableName(type.toString())] = null
            }
        }

        return typeMapping
    }

    private fun resolveGenericReturnValue(
        returnType: TypeName,
        generics: Map<String, List<KSTypeReference>>,
        parameterTypeResolver: TypeParameterResolver
    ): Pair<TypeName, Boolean> {
        val key = returnType.toString()
        return when {
            !generics.containsKey(key) -> Pair(returnType, false)
            generics[key]!!.isEmpty() -> Pair(TypeVariableName("Any?"), false)
            generics[key]!!.size > 1 -> Pair(TypeVariableName("Any${isNullable(generics[key]!!)}"), true)
            else -> Pair(generics[key]!!.first().toTypeName(parameterTypeResolver), false)
        }
    }

    private fun resolveReturnValue(
        returnValue: KSTypeReference,
        generics: Map<String, List<KSTypeReference>>?,
        parameterTypeResolver: TypeParameterResolver
    ): ReturnType {
        val typeName = returnValue.toTypeName(parameterTypeResolver)

        return if (generics == null) {
            ReturnType(typeName, typeName, false)
        } else {
            val (actualType, multiBound) = resolveGenericReturnValue(
                typeName,
                generics,
                parameterTypeResolver
            )

            ReturnType(actualType, typeName, multiBound)
        }
    }

    override fun buildFunctionBundle(
        qualifier: String,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        functionNameCollector: MutableList<String>,
        relaxer: ProcessorContract.Relaxer?
    ): Pair<PropertySpec, FunSpec> {
        val functionName = ksFunction.simpleName.asString()
        val parameterTypeResolver = ksFunction
            .typeParameters
            .toTypeParameterResolver(typeResolver)
        val generics = utils.resolveGeneric(ksFunction, parameterTypeResolver)
        val parameter = determineParameter(ksFunction.parameters, parameterTypeResolver)
        val proxyParameter = determineProxyParameter(parameter.second, generics, parameterTypeResolver)
        val proxyName = selectFunProxyName(
            functionName,
            generics ?: emptyMap(),
            parameterTypeResolver,
            parameter.second,
            functionNameCollector
        )
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)

        val returnType = resolveReturnValue(
            ksFunction.returnType!!,
            generics,
            parameterTypeResolver
        )

        val function = buildFunction(
            ksFunction.simpleName.asString(),
            generics,
            parameterTypeResolver,
            isSuspending,
            parameter.first,
            parameter.second,
            returnType,
            proxyName
        )

        val Proxy = if (isSuspending) {
            buildASyncFunProxy(
                qualifier,
                functionName,
                proxyName,
                parameter.first,
                proxyParameter,
                returnType,
                relaxer
            )
        } else {
            buildSyncFunProxy(
                qualifier,
                functionName,
                proxyName,
                parameter.first,
                proxyParameter,
                returnType,
                relaxer
            )
        }

        functionNameCollector.add(proxyName)
        return Pair(Proxy, function)
    }
}
