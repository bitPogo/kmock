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
    private fun buildFunction(
        ksFunctionName: String,
        generics: Map<String, List<KSTypeReference>>?,
        typeResolver: TypeParameterResolver,
        isSuspending: Boolean,
        parameterNames: List<String>,
        parameterTypes: List<TypeName>,
        returnType: TypeName,
        mockeryName: String
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

        function.returns(returnType)
        function.addCode("return $mockeryName.invoke(${parameterNames.joinToString(", ")})")

        return function.build()
    }

    private fun guardMultiBoundaries(
        functionName: String,
        parameter: String,
        boundaries: Map<TypeVariableName, List<KSTypeReference>?>,
    ): String {
        var isMultiBoundary = false

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
        boundaries: Map<TypeVariableName, List<KSTypeReference>?>
    ): String {
        val parameter = parameterNames.joinToString(", ")

        return if (parameter.isEmpty()) {
            "{ $functionName() }"
        } else {
            "{ $parameter ->\n${guardMultiBoundaries(functionName, parameter, boundaries)} }"
        }
    }

    private fun determineFunctionInitializer(
        propertyMock: PropertySpec.Builder,
        mockery: String,
        qualifier: String,
        mockeryName: String,
        functionName: String,
        parameterNames: List<String>,
        boundaries: Map<TypeVariableName, List<KSTypeReference>?>,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec.Builder {
        return propertyMock.initializer(
            "%L(%S, spyOn = %L, collector = verifier, freeze = freeze, %L)",
            mockery,
            "$qualifier#$mockeryName",
            "if (spyOn != null) { ${
            buildFunctionSpyInvocation(
                functionName,
                parameterNames,
                boundaries
            )
            } } else { null }",
            relaxerGenerator.buildRelaxers(relaxer, true)
        )
    }

    private fun buildSyncFunMockery(
        qualifier: String,
        functionName: String,
        mockeryName: String,
        parameterNames: List<String>,
        parameterTypes: Map<TypeVariableName, List<KSTypeReference>?>,
        returnType: TypeName,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec {
        val lambda = TypeVariableName("(${parameterTypes.keys.joinToString(", ")}) -> $returnType")
        val property = PropertySpec.builder(
            mockeryName,
            KMockContract.SyncFunMockery::class
                .asClassName()
                .parameterizedBy(returnType, lambda),
        )

        return determineFunctionInitializer(
            property,
            "SyncFunMockery",
            qualifier,
            mockeryName,
            functionName,
            parameterNames,
            parameterTypes,
            relaxer
        ).build()
    }

    private fun buildASyncFunMockery(
        qualifier: String,
        functionName: String,
        mockeryName: String,
        parameterNames: List<String>,
        parameterTypes: Map<TypeVariableName, List<KSTypeReference>?>,
        returnType: TypeName,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec {
        val lambda = TypeVariableName("suspend (${parameterTypes.keys.joinToString(", ")}) -> $returnType")
        val property = PropertySpec.builder(
            mockeryName,
            KMockContract.AsyncFunMockery::class
                .asClassName()
                .parameterizedBy(returnType, lambda),
        )

        return determineFunctionInitializer(
            property,
            "AsyncFunMockery",
            qualifier,
            mockeryName,
            functionName,
            parameterNames,
            parameterTypes,
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

    private fun selectFunMockeryName(
        functionName: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        parameter: List<TypeName>,
        existingFunctions: List<String>
    ): String {
        val mockeryName = "_$functionName"

        return if (existingFunctions.contains(mockeryName)) {
            determineSuffixedFunctionName(
                mockeryName,
                parameter,
                generics,
                typeResolver
            )
        } else {
            mockeryName
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

    private fun determineMockeryParameter(
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
                        types.size > 1 -> "Any${isNullable(types as List<KSTypeReference>)}".also { typeListing = types }
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
        val mockeryParameter = determineMockeryParameter(parameter.second, generics, parameterTypeResolver)
        val mockeryName = selectFunMockeryName(
            functionName,
            generics ?: emptyMap(),
            parameterTypeResolver,
            parameter.second,
            functionNameCollector
        )
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)
        val returnType = ksFunction.returnType!!.toTypeName(parameterTypeResolver)

        val function = buildFunction(
            ksFunction.simpleName.asString(),
            generics,
            parameterTypeResolver,
            isSuspending,
            parameter.first,
            parameter.second,
            returnType,
            mockeryName
        )

        val mockery = if (isSuspending) {
            buildASyncFunMockery(
                qualifier,
                functionName,
                mockeryName,
                parameter.first,
                mockeryParameter,
                returnType,
                relaxer
            )
        } else {
            buildSyncFunMockery(
                qualifier,
                functionName,
                mockeryName,
                parameter.first,
                mockeryParameter,
                returnType,
                relaxer
            )
        }

        functionNameCollector.add(mockeryName)
        return Pair(mockery, function)
    }
}
