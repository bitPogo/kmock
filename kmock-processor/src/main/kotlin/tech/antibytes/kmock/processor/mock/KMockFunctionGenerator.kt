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
import tech.antibytes.kmock.processor.titleCase
import kotlin.reflect.KClass

internal class KMockFunctionGenerator(
    private val allowedRecursiveTypes: Set<String>,
    private val uselessPrefixes: Set<String>,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val relaxerGenerator: ProcessorContract.RelaxerGenerator
) : ProcessorContract.FunctionGenerator {
    private val any = Any::class.asTypeName()

    private fun String.removePrefixes(prefixes: Iterable<String>): String {
        var cleaned = this

        prefixes.forEach { prefix ->
            cleaned = removePrefix(prefix)
        }

        return cleaned
    }

    private fun resolveGenericName(
        boundaries: List<KSTypeReference>?,
        typeResolver: TypeParameterResolver
    ): String? {
        return if (boundaries.isNullOrEmpty()) {
            null
        } else {
            boundaries.joinToString("") { typeName ->
                typeName.toTypeName(typeResolver).toString()
            }
        }
    }

    private fun determineGenericName(
        name: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        var currentName = name

        do {
            currentName = resolveGenericName(generics[currentName], typeResolver) ?: "Any"
        } while (currentName in generics)

        return currentName
    }

    private fun resolveProxySuffixFromArguments(
        arguments: Collection<TypeName>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): List<String> {
        return arguments.map { suffix ->
            suffix
                .toString()
                .let { name ->
                    if (name in generics) {
                        determineGenericName(name, generics, typeResolver)
                    } else {
                        name
                    }
                }
                .removePrefixes(uselessPrefixes)
                .trimStart('.')
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
                .replace("?", "")
        }
    }

    private fun determineSuffixedProxyName(
        functionName: String,
        arguments: Collection<TypeName>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        val titleCasedSuffixes = if (arguments.isNotEmpty()) {
            resolveProxySuffixFromArguments(arguments, generics, typeResolver)
        } else {
            listOf("Void")
        }

        return "${functionName}With${titleCasedSuffixes.joinToString("")}"
    }

    private fun selectFunProxyName(
        functionName: String,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver,
        arguments: Collection<TypeName>,
        existingProxies: Set<String>
    ): String {
        val proxyName = "_$functionName"

        return if (proxyName in existingProxies) {
            determineSuffixedProxyName(
                proxyName,
                arguments,
                generics,
                typeResolver
            )
        } else {
            proxyName
        }
    }

    private fun determineParameter(
        parameters: List<KSValueParameter>,
        parameterTypeResolver: TypeParameterResolver
    ): Map<String, TypeName> {
        return parameters.associate { parameter ->
            parameter.name!!.asString() to parameter.type.toTypeName(parameterTypeResolver)
        }
    }

    private fun determineProxyType(suspending: Boolean): Pair<KClass<*>, String> {
        return if (suspending) {
            Pair(AsyncFunProxy::class, "suspend ")
        } else {
            Pair(SyncFunProxy::class, "")
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
    ): Pair<TypeName, GenericDeclaration?> {
        val isNullable = typeName.isNullable
        val generic = proxyGenericTypes[typeName.toString().trimEnd('?')]

        val actualTypeName = resolveGenericProxyType(
            typeName,
            generic
        )

        return actualTypeName.copy(nullable = actualTypeName.isNullable || isNullable) to generic
    }

    private fun determineProxyReturnType(
        returnType: KSType,
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        typeResolver: TypeParameterResolver,
    ): Pair<TypeName, GenericDeclaration?> {
        val typeName = returnType.toTypeName(typeResolver)

        return if (proxyGenericTypes == null) {
            typeName to null
        } else {
            mapGenericProxyType(typeName, proxyGenericTypes)
        }
    }

    private fun resolveGenericArgumentTypes(
        proxyGenericTypes: Map<String, GenericDeclaration>,
        argumentTypes: Collection<TypeName>,
    ): List<Pair<TypeName, GenericDeclaration?>> {
        return argumentTypes.map { typeName -> mapGenericProxyType(typeName, proxyGenericTypes) }
    }

    private fun determineProxyArgumentTypes(
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        argumentTypes: Collection<TypeName>,
    ): List<Pair<TypeName, GenericDeclaration?>> {
        return if (proxyGenericTypes == null) {
            argumentTypes.map { typeName -> typeName to null }
        } else {
            resolveGenericArgumentTypes(proxyGenericTypes, argumentTypes)
        }
    }

    private fun buildSideEffectSignature(
        proxyArguments: List<Pair<TypeName, GenericDeclaration?>>,
        proxyReturnType: TypeName,
        prefix: String
    ): TypeName {
        val argumentTypeName = proxyArguments.map { argument -> argument.first }

        return TypeVariableName(
            "$prefix(${argumentTypeName.joinToString(", ")}) -> $proxyReturnType"
        )
    }

    private fun determineSpyConstrains(
        proxyArguments: List<Pair<TypeName, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
    ): Boolean {
        var isRecursive = proxyReturnType.second?.recursive ?: false

        proxyArguments.forEach { (_, genericDeclaration) ->
            isRecursive = isRecursive || genericDeclaration?.recursive ?: false
        }

        return isRecursive
    }

    private fun buildSpyArgumentCasts(
        body: StringBuilder,
        spyArgumentNames: Set<String>,
        proxyArguments: List<Pair<TypeName, GenericDeclaration?>>
    ) {
        var idx = 0
        val argumentNames = spyArgumentNames.toList()
        proxyArguments.forEach { (_, generic) ->
            if (generic != null && generic.types.size >= 2) {
                generic.types.forEach { type ->

                    body.append("@Suppress(\"UNCHECKED_CAST\")\n")
                        .append("${argumentNames[idx]} as $type\n")
                }
            }

            idx += 1
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
        spyName: String,
        spyArgumentNames: Set<String>,
        spyArguments: String,
        proxyArguments: List<Pair<TypeName, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
    ): String {
        val isRecursive = determineSpyConstrains(proxyArguments, proxyReturnType)

        return if (isRecursive) {
            "throw IllegalArgumentException(\n\"Recursive generics are not supported on function level spies (yet).\"\n)"
        } else {
            val body = StringBuilder(1)

            buildSpyArgumentCasts(body, spyArgumentNames, proxyArguments)

            if (proxyReturnType.second?.castReturnType == true) {
                body.append("@Suppress(\"UNCHECKED_CAST\")\n")
            }

            body.append("$spyName($spyArguments)")

            if (proxyReturnType.second?.castReturnType == true) {
                buildSpyReturnTypeCasts(body, proxyReturnType.second!!)
            }

            body.toString()
        }
    }

    private fun buildFunctionSpyInvocation(
        spyName: String,
        spyArgumentNames: Set<String>,
        proxyArguments: List<Pair<TypeName, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
    ): String {
        val spyArguments = spyArgumentNames.joinToString(", ")
        val spyBody = buildSpyBody(
            spyName,
            spyArgumentNames,
            spyArguments,
            proxyArguments,
            proxyReturnType
        )

        return if (spyArgumentNames.isEmpty()) {
            "{ $spyBody }"
        } else {
            "{ $spyArguments ->\n$spyBody }"
        }
    }

    private fun buildProxyInitializer(
        proxySpec: PropertySpec.Builder,
        qualifier: String,
        proxyName: String,
        proxyType: ClassName,
        spyName: String,
        spyArgumentNames: Set<String>,
        proxyArguments: List<Pair<TypeName, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec.Builder {
        return proxySpec.delegate(
            """
            |lazy(mode = %T.PUBLICATION) {
            |   %L(%S, spyOn = %L, collector = verifier, freeze = freeze, %L)
            |}
            """.trimMargin(),
            LazyThreadSafetyMode::class.asTypeName(),
            proxyType.simpleName,
            "$qualifier#$proxyName",
            "if (spyOn != null) { ${
            buildFunctionSpyInvocation(
                spyName,
                spyArgumentNames,
                proxyArguments,
                proxyReturnType
            )
            } } else { null }",
            relaxerGenerator.buildRelaxers(
                relaxer,
                proxyReturnType.first.toString() == "kotlin.Unit"
            )
        )
    }

    private fun buildProxy(
        qualifier: String,
        proxyName: String,
        spyName: String,
        generics: Map<String, List<KSTypeReference>>?,
        arguments: Map<String, TypeName>,
        returnType: KSType,
        typeResolver: TypeParameterResolver,
        suspending: Boolean,
        relaxer: ProcessorContract.Relaxer?
    ): Pair<PropertySpec, TypeName> {
        val (proxyType, sideEffectPrefix) = determineProxyType(suspending)
        val proxyGenericTypes = resolveProxyGenerics(generics, typeResolver)

        val proxyArguments = determineProxyArgumentTypes(
            proxyGenericTypes,
            arguments.values,
        )

        val proxyReturnType = determineProxyReturnType(
            returnType,
            proxyGenericTypes,
            typeResolver
        )

        val sideEffect = buildSideEffectSignature(
            proxyArguments,
            proxyReturnType.first,
            sideEffectPrefix
        )

        val proxyClass = proxyType.asClassName()

        return Pair(
            PropertySpec.builder(
                proxyName,
                proxyClass.parameterizedBy(proxyReturnType.first, sideEffect)
            ).let { proxySpec ->
                buildProxyInitializer(
                    proxySpec = proxySpec,
                    qualifier = qualifier,
                    proxyName = proxyName,
                    proxyType = proxyClass,
                    spyName = spyName,
                    spyArgumentNames = arguments.keys,
                    proxyArguments = proxyArguments,
                    proxyReturnType = proxyReturnType,
                    relaxer = relaxer
                )
            }.build(),
            proxyReturnType.first
        )
    }

    private fun buildFunction(
        functionName: String,
        proxyName: String,
        generics: Map<String, List<KSTypeReference>>?,
        isSuspending: Boolean,
        arguments: Map<String, TypeName>,
        rawReturnType: KSType,
        proxyReturnType: TypeName,
        typeResolver: TypeParameterResolver,
    ): FunSpec {
        val returnType = rawReturnType.toTypeName(typeResolver)

        val function = FunSpec
            .builder(functionName)
            .addModifiers(KModifier.OVERRIDE)

        if (generics != null) {
            function.typeVariables.addAll(
                this.genericResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        if (isSuspending) {
            function.addModifiers(KModifier.SUSPEND)
        }

        arguments.forEach { (argumentName, argumentType) ->
            function.addParameter(
                argumentName,
                argumentType
            )
        }

        function.returns(returnType)

        val cast = if (returnType != proxyReturnType) {
            function.addAnnotation(
                AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build()
            )
            " as $returnType"
        } else {
            ""
        }

        function.addCode(
            "return $proxyName.invoke(${arguments.keys.joinToString(", ")})$cast"
        )

        return function.build()
    }

    override fun buildFunctionBundle(
        qualifier: String,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        existingProxies: Set<String>,
        relaxer: ProcessorContract.Relaxer?
    ): Pair<PropertySpec, FunSpec> {
        val functionName = ksFunction.simpleName.asString()
        val parameterTypeResolver = ksFunction
            .typeParameters
            .toTypeParameterResolver(typeResolver)
        val generics = genericResolver.extractGenerics(ksFunction, parameterTypeResolver)
        val arguments = determineParameter(ksFunction.parameters, parameterTypeResolver)
        val proxyName = selectFunProxyName(
            functionName = functionName,
            generics = generics ?: emptyMap(),
            typeResolver = parameterTypeResolver,
            arguments = arguments.values,
            existingProxies = existingProxies
        )
        val returnType = ksFunction.returnType!!.resolve()
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)

        val (proxy, proxyReturnType) = buildProxy(
            qualifier = qualifier,
            proxyName = proxyName,
            spyName = functionName,
            generics = generics,
            arguments = arguments,
            returnType = returnType,
            typeResolver = parameterTypeResolver,
            suspending = isSuspending,
            relaxer = relaxer
        )

        val function = buildFunction(
            functionName = functionName,
            proxyName = proxyName,
            generics = generics,
            isSuspending = isSuspending,
            arguments = arguments,
            rawReturnType = returnType,
            proxyReturnType = proxyReturnType,
            typeResolver = parameterTypeResolver
        )

        return Pair(proxy, function)
    }
}
