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
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.titleCase

internal class KMockMethodGenerator(
    private val allowedRecursiveTypes: Set<String>,
    private val uselessPrefixes: Set<String>,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val relaxerGenerator: ProcessorContract.RelaxerGenerator
) : ProcessorContract.MethodGenerator {
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

    data class TypeInfo(
        val typeName: TypeName,
        val isVarArg: Boolean
    )

    data class SpyTypeInfo(
        val typeName: String,
        val isVarArg: Boolean
    )

    private fun String.resolveActualName(
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): String {
        return if (this in generics) {
            determineGenericName(this, generics, typeResolver)
        } else {
            this
        }
    }

    private fun String.amendPlural(usePlural: Boolean): String {
        return if (usePlural) {
            "${this}s"
        } else {
            this
        }
    }

    private fun String.removePrefixes(prefixes: Iterable<String>): String {
        var cleaned = this

        prefixes.forEach { prefix ->
            cleaned = removePrefix(prefix)
        }

        return cleaned
    }

    private fun String.packageNameToVariableName(): String {
        val partialNames = split('.')

        return if (partialNames.size == 1) {
            this
        } else {
            partialNames
                .map { partialName -> partialName.titleCase() }
                .joinToString("")
        }
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
        arguments: Collection<TypeInfo>,
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): List<String> {
        return arguments.map { (suffix, usePlural) ->
            suffix
                .toString()
                .resolveActualName(generics, typeResolver)
                .removePrefixes(uselessPrefixes)
                .trimStart('.')
                .substringBefore('<') // Lambdas
                .packageNameToVariableName()
                .replace("?", "")
                .amendPlural(usePlural)
        }
    }

    private fun determineSuffixedProxyName(
        functionName: String,
        arguments: Collection<TypeInfo>,
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
        arguments: Collection<TypeInfo>,
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
    ): Map<String, TypeInfo> {
        return parameters.associate { parameter ->
            parameter.name!!.asString() to TypeInfo(
                typeName = parameter.type.toTypeName(parameterTypeResolver),
                isVarArg = parameter.isVararg,
            )
        }
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
        argumentTypes: Collection<TypeInfo>,
    ): List<Pair<TypeInfo, GenericDeclaration?>> {
        return argumentTypes.map { typeInfo ->
            val (typeName, declaration) = mapGenericProxyType(
                typeInfo.typeName,
                proxyGenericTypes
            )

            Pair(typeInfo.copy(typeName = typeName), declaration)
        }
    }

    private fun determineProxyArgumentTypes(
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        argumentTypes: Collection<TypeInfo>,
    ): List<Pair<TypeInfo, GenericDeclaration?>> {
        return if (proxyGenericTypes == null) {
            argumentTypes.map { typeInfo -> typeInfo to null }
        } else {
            resolveGenericArgumentTypes(proxyGenericTypes, argumentTypes)
        }
    }

    private fun mapProxyArgumentTypeNames(
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>
    ): List<TypeName> {
        return proxyArguments.map { argument ->
            if (!argument.first.isVarArg) {
                argument.first.typeName
            } else {
                specialArrays.getOrElse(argument.first.typeName) {
                    TypeVariableName(
                        "Array<out ${argument.first.typeName}>"
                    )
                }
            }
        }
    }

    private fun buildSideEffectSignature(
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>,
        proxyReturnType: TypeName,
        prefix: String
    ): TypeName {
        val argumentTypeName = mapProxyArgumentTypeNames(proxyArguments)

        return TypeVariableName(
            "$prefix(${argumentTypeName.joinToString(", ")}) -> $proxyReturnType"
        )
    }

    private fun determineSpyConstrains(
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>,
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
        spyArguments: Set<SpyTypeInfo>,
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>
    ) {
        val argumentNames = spyArguments.toList()
        proxyArguments.forEachIndexed { idx, (_, generic) ->
            if (generic != null && generic.types.size >= 2) {
                generic.types.forEach { type ->
                    val cast: String = if (argumentNames[idx].isVarArg) {
                        "Array<$type>"
                    } else {
                        type.toString()
                    }

                    body.append("\n@Suppress(\"UNCHECKED_CAST\")\n")
                        .append("${argumentNames[idx].typeName} as $cast\n")
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

    private fun extractSpyArgumentNames(
        spyArguments: Set<SpyTypeInfo>
    ): String {
        return spyArguments.joinToString(", ") { spyTypeInfo ->
            if (spyTypeInfo.isVarArg) {
                "*${spyTypeInfo.typeName}"
            } else {
                spyTypeInfo.typeName
            }
        }
    }

    private fun buildSpyBody(
        spyName: String,
        spyArguments: Set<SpyTypeInfo>,
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
    ): String {
        val body = StringBuilder(1)
        val spyArgumentNames = extractSpyArgumentNames(spyArguments)

        buildSpyArgumentCasts(body, spyArguments, proxyArguments)

        if (proxyReturnType.second?.castReturnType == true) {
            body.append("@Suppress(\"UNCHECKED_CAST\")\n")
        }

        body.append("__spyOn!!.$spyName($spyArgumentNames)")

        if (proxyReturnType.second?.castReturnType == true) {
            buildSpyReturnTypeCasts(body, proxyReturnType.second!!)
        }

        return body.toString()
    }

    private fun buildSpyBodyIfNotRecursive(
        spyName: String,
        spyArguments: Set<SpyTypeInfo>,
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
    ): String {
        val isRecursive = determineSpyConstrains(proxyArguments, proxyReturnType)

        return if (isRecursive) {
            "throw IllegalArgumentException(\n\"Recursive generics are not supported on function level spies (yet).\"\n)"
        } else {
            buildSpyBody(
                spyName = spyName,
                spyArguments = spyArguments,
                proxyArguments = proxyArguments,
                proxyReturnType = proxyReturnType
            )
        }
    }

    private fun buildFunctionSpyInvocation(
        spyName: String,
        spyArguments: Set<SpyTypeInfo>,
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
    ): String {
        val spyBody = buildSpyBodyIfNotRecursive(
            spyName = spyName,
            spyArguments = spyArguments,
            proxyArguments = proxyArguments,
            proxyReturnType = proxyReturnType
        )

        val spyArgumentNames = spyArguments.joinToString(", ") { spyTypeInfo -> spyTypeInfo.typeName }

        return if (spyArguments.isEmpty()) {
            "{ $spyBody }"
        } else {
            "{ $spyArgumentNames ->\n$spyBody\n}"
        }
    }

    private fun addSpy(
        relaxationDefinitions: StringBuilder,
        spyName: String,
        spyArguments: Set<SpyTypeInfo>,
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
    ) {
        val spyBody = buildFunctionSpyInvocation(
            spyName,
            spyArguments,
            proxyArguments,
            proxyReturnType
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
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
        relaxer: Relaxer?
    ) {
        if (proxyReturnType.first.toString() == "kotlin.Unit") {
            relaxationDefinitions.append("useUnitFunRelaxerIf(relaxUnitFun || relaxed)\n")
        } else {
            relaxerGenerator.addRelaxer(relaxationDefinitions, relaxer)
        }
    }

    private fun addRelaxation(
        enableSpy: Boolean,
        spyName: String,
        spyArguments: Set<SpyTypeInfo>,
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
        relaxer: Relaxer?
    ): String {
        val relaxationDefinitions = StringBuilder(3)

        relaxationDefinitions.append("{\n")
        if (enableSpy) {
            addSpy(
                relaxationDefinitions = relaxationDefinitions,
                spyName = spyName,
                spyArguments = spyArguments,
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
        qualifier: String,
        proxyName: String,
        proxyFactoryMethod: String,
        spyName: String,
        spyArguments: Set<SpyTypeInfo>,
        proxyArguments: List<Pair<TypeInfo, GenericDeclaration?>>,
        proxyReturnType: Pair<TypeName, GenericDeclaration?>,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): PropertySpec.Builder {
        val proxyId = "$qualifier#$proxyName"

        return proxySpec.initializer(
            "ProxyFactory.%L(%S, collector = verifier, freeze = freeze) %L",
            proxyFactoryMethod,
            proxyId,
            addRelaxation(
                enableSpy = enableSpy,
                spyName = spyName,
                spyArguments = spyArguments,
                proxyArguments = proxyArguments,
                proxyReturnType = proxyReturnType,
                relaxer = relaxer
            )
        )
    }

    private fun extractSpyNames(
        arguments: Map<String, TypeInfo>
    ): Set<SpyTypeInfo> {
        val spyInfo: MutableSet<SpyTypeInfo> = mutableSetOf()

        return arguments.mapTo(spyInfo) { (spyName, typeInfo) ->
            SpyTypeInfo(spyName, typeInfo.isVarArg)
        }
    }

    private fun buildProxy(
        qualifier: String,
        proxyName: String,
        spyName: String,
        generics: Map<String, List<KSTypeReference>>?,
        arguments: Map<String, TypeInfo>,
        returnType: KSType,
        typeResolver: TypeParameterResolver,
        suspending: Boolean,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec, TypeName> {
        val (proxyType, proxyFactoryMethod, sideEffectPrefix) = determineProxyType(suspending)
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

        val spyArguments = extractSpyNames(arguments)

        return Pair(
            PropertySpec.builder(
                proxyName,
                proxyType.parameterizedBy(proxyReturnType.first, sideEffect)
            ).let { proxySpec ->
                buildProxyInitializer(
                    proxySpec = proxySpec,
                    qualifier = qualifier,
                    proxyName = proxyName,
                    proxyFactoryMethod = proxyFactoryMethod,
                    spyName = spyName,
                    spyArguments = spyArguments,
                    proxyArguments = proxyArguments,
                    proxyReturnType = proxyReturnType,
                    enableSpy = enableSpy,
                    relaxer = relaxer
                )
            }.build(),
            proxyReturnType.first
        )
    }

    private fun buildMethod(
        functionName: String,
        proxyName: String,
        generics: Map<String, List<KSTypeReference>>?,
        isSuspending: Boolean,
        arguments: Map<String, TypeInfo>,
        rawReturnType: KSType,
        proxyReturnType: TypeName,
        typeResolver: TypeParameterResolver,
    ): FunSpec {
        val returnType = rawReturnType.toTypeName(typeResolver)

        val method = FunSpec
            .builder(functionName)
            .addModifiers(KModifier.OVERRIDE)

        if (generics != null) {
            method.typeVariables.addAll(
                this.genericResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        if (isSuspending) {
            method.addModifiers(KModifier.SUSPEND)
        }

        arguments.forEach { (argumentName, argumentType) ->
            if (argumentType.isVarArg) {
                method.addParameter(
                    argumentName,
                    argumentType.typeName,
                    KModifier.VARARG
                )
            } else {
                method.addParameter(
                    argumentName,
                    argumentType.typeName
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

        method.addCode(
            "return $proxyName.invoke(${arguments.keys.joinToString(", ")})$cast"
        )

        return method.build()
    }

    override fun buildMethodBundle(
        qualifier: String,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        existingProxies: Set<String>,
        enableSpy: Boolean,
        relaxer: Relaxer?
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
            enableSpy = enableSpy,
            relaxer = relaxer
        )

        val method = buildMethod(
            functionName = functionName,
            proxyName = proxyName,
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