/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ASYNC_FUN_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PROP_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SYNC_FUN_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import java.util.Locale

internal class KMockGenerator(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockGenerator {
    private fun resolveBound(type: KSTypeParameter): KSTypeReference? {
        return if (type.bounds.toList().isEmpty()) {
            null
        } else {
            type.bounds.toList().first()
        }
    }

    private fun resolveGeneric(template: KSDeclaration): Map<String, KSTypeReference?>? {
        return if (template.typeParameters.isEmpty()) {
            null
        } else {
            val generic: MutableMap<String, KSTypeReference?> = mutableMapOf()
            template.typeParameters.forEach { type ->
                generic[type.toTypeVariableName().toString()] = resolveBound(type)
            }

            generic
        }
    }

    private fun mapGeneric(generics: Map<String, KSTypeReference?>): List<TypeVariableName> {
        return generics.map { (type, bound) ->
            val typeStr = if (bound != null) {
                "$type : ${bound.resolve()}"
            } else {
                type
            }

            TypeVariableName(typeStr)
        }
    }

    private fun resolveType(template: KSClassDeclaration): TypeName {
        return if (template.typeParameters.isEmpty()) {
            template.toClassName()
        } else {
            template.toClassName()
                .parameterizedBy(
                    template.typeParameters.map { type -> type.toTypeVariableName() }
                )
        }
    }

    private fun buildRelaxer(relaxer: Relaxer?): String {
        return if (relaxer == null) {
            ""
        } else {
            "relaxer = if(relaxed) { { mockId -> ${relaxer.functionName}(mockId) } } else { null }"
        }
    }

    private fun buildConstructor(
        superType: TypeName,
        relaxer: Relaxer?,
    ): FunSpec {
        val constructor = FunSpec.constructorBuilder()

        val collector = ParameterSpec.builder("verifier", COLLECTOR_NAME)
        collector.defaultValue("Collector { _, _ -> Unit }")

        val spy = ParameterSpec.builder("spyOn", superType.copy(nullable = true))
        spy.defaultValue("null")

        constructor.addParameter(collector.build())
        constructor.addParameter(spy.build())

        if (relaxer != null) {
            val relaxed = ParameterSpec.builder("relaxed", Boolean::class)
            relaxed.defaultValue("false")

            constructor.addParameter(relaxed.build())
        }

        return constructor.build()
    }

    private fun buildGetter(propertyName: String): FunSpec {
        return FunSpec
            .getterBuilder()
            .addStatement("return ${propertyName}Prop.onGet()")
            .build()
    }

    private fun buildSetter(
        propertyName: String,
        propertyType: TypeName
    ): FunSpec {
        return FunSpec
            .setterBuilder()
            .addParameter("value", propertyType)
            .addStatement("return ${propertyName}Prop.onSet(value)")
            .build()
    }

    private fun buildProperty(
        propertyName: String,
        propertyType: TypeName,
        isMutable: Boolean
    ): PropertySpec {
        val property = PropertySpec.builder(
            propertyName,
            propertyType,
            KModifier.OVERRIDE
        )

        if (isMutable) {
            property.mutable(true)
            property.setter(buildSetter(propertyName, propertyType))
        }

        return property
            .getter(buildGetter(propertyName))
            .build()
    }

    private fun determinePropertyInitializer(
        propertyMock: PropertySpec.Builder,
        qualifier: String,
        propertyName: String,
        isMutable: Boolean,
        relaxer: Relaxer?
    ): PropertySpec.Builder {
        return if (!isMutable) {
            propertyMock.initializer(
                "PropertyMockery(%S, spyOnGet = %L, collector = verifier, %L)",
                "$qualifier#${propertyName}Prop",
                "if (spyOn != null) { spyOn::$propertyName::get } else { null }",
                buildRelaxer(relaxer)
            )
        } else {
            propertyMock.initializer(
                "PropertyMockery(%S, spyOnGet = %L, spyOnSet = %L, collector = verifier, %L)",
                "$qualifier#${propertyName}Prop",
                "if (spyOn != null) { spyOn::$propertyName::get } else { null }",
                "if (spyOn != null) { spyOn::$propertyName::set } else { null }",
                buildRelaxer(relaxer)
            )
        }
    }

    private fun buildPropertyMockery(
        qualifier: String,
        propertyName: String,
        propertyType: TypeName,
        isMutable: Boolean,
        relaxer: Relaxer?
    ): PropertySpec {
        val property = PropertySpec.builder(
            "${propertyName}Prop",
            KMockContract.PropertyMockery::class
                .asClassName()
                .parameterizedBy(propertyType),
        )

        return determinePropertyInitializer(
            property,
            qualifier,
            propertyName,
            isMutable,
            relaxer
        ).build()
    }

    private fun buildPropertyBundle(
        qualifier: String,
        ksProperty: KSPropertyDeclaration,
        typeResolver: TypeParameterResolver,
        propertyNameCollector: MutableList<String> = mutableListOf(),
        relaxer: Relaxer?
    ): List<PropertySpec> {
        val propertyName = ksProperty.simpleName.asString()
        val propertyType = ksProperty.type.toTypeName(typeResolver)
        val isMutable = ksProperty.isMutable

        propertyNameCollector.add("${propertyName}Prop")
        return listOf(
            buildProperty(propertyName, propertyType, isMutable),
            buildPropertyMockery(qualifier, propertyName, propertyType, isMutable, relaxer)
        )
    }

    private fun buildFunction(
        ksFunctionName: String,
        generics: Map<String, KSTypeReference?>?,
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
            function.typeVariables.addAll(mapGeneric(generics))
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

    private fun buildFunctionSpyInvocation(
        functionName: String,
        parameterNames: List<String>
    ): String {
        val parameter = parameterNames.joinToString(" ,")

        return if (parameter.isEmpty()) {
            "{ $functionName() }"
        } else {
            "{ $parameter ->\n $functionName($parameter) }"
        }
    }

    private fun determineFunctionInitializer(
        propertyMock: PropertySpec.Builder,
        mockery: String,
        qualifier: String,
        mockeryName: String,
        functionName: String,
        parameterNames: List<String>,
        relaxer: Relaxer?
    ): PropertySpec.Builder {
        return propertyMock.initializer(
            "%L(%S, spyOn = %L, collector = verifier, %L)",
            mockery,
            "$qualifier#$mockeryName",
            "if (spyOn != null) { ${buildFunctionSpyInvocation(functionName, parameterNames)} } else { null }",
            buildRelaxer(relaxer)
        )
    }

    private fun buildSyncFunMockery(
        qualifier: String,
        functionName: String,
        mockeryName: String,
        parameterNames: List<String>,
        parameterTypes: List<TypeName>,
        returnType: TypeName,
        relaxer: Relaxer?
    ): PropertySpec {
        val lambda = TypeVariableName("(${parameterTypes.joinToString(", ")}) -> $returnType")
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
            relaxer
        ).build()
    }

    private fun buildASyncFunMockery(
        qualifier: String,
        functionName: String,
        mockeryName: String,
        parameterNames: List<String>,
        parameterTypes: List<TypeName>,
        returnType: TypeName,
        relaxer: Relaxer?
    ): PropertySpec {
        val lambda = TypeVariableName("suspend (${parameterTypes.joinToString(", ")}) -> $returnType")
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

    private fun determineSuffixedFunctionName(
        functionName: String,
        parameter: List<TypeName>,
        existingFunctions: List<String>
    ): String {
        val titleCasedSuffixes: MutableList<String> = mutableListOf()
        parameter.forEach { suffix ->
            val suffixCased = suffix.toString()
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

            val suffixedName: String = functionName + "With" + suffixCased

            if (!existingFunctions.contains(suffixedName + "Fun")) {
                return suffixedName
            } else {
                titleCasedSuffixes.add(suffixCased)
            }
        }

        val extensiveName = functionName + "With" + titleCasedSuffixes.joinToString("")

        if (existingFunctions.contains(extensiveName + "Fun")) {
            // Note: This should only happen with generic
            logger.error("The generator cannot differentiate between Functions - $extensiveName - Therefore please revisit your defining Interface and make your ParameterDefinitions more unique.")
        }

        return extensiveName
    }

    private fun selectFunMockeryName(
        functionName: String,
        parameter: List<TypeName>,
        existingFunctions: List<String>
    ): String {
        return if (existingFunctions.contains(functionName + "Fun")) {
            determineSuffixedFunctionName(functionName, parameter, existingFunctions)
        } else {
            functionName
        } + "Fun"
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

    private fun determineMockeryParameter(
        parameter: List<TypeName>,
        generics: Map<String, KSTypeReference?>?
    ): List<TypeVariableName> {
        return if (generics != null) {
            parameter.map { type ->
                generics.getOrElse(type.toString()) { type }.let { nonGeneric ->
                    val actualType: Any = if (nonGeneric is KSTypeReference) {
                        nonGeneric.resolve()
                    } else {
                        "Any?"
                    }

                    TypeVariableName(actualType.toString())
                }
            }
        } else {
            parameter.map { type -> TypeVariableName(type.toString()) }
        }
    }

    private fun buildFunctionBundle(
        qualifier: String,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        functionNameCollector: MutableList<String>,
        relaxer: Relaxer?
    ): Pair<PropertySpec, FunSpec> {
        val functionName = ksFunction.simpleName.asString()
        val generics = resolveGeneric(ksFunction)
        val parameterTypeResolver = ksFunction
            .typeParameters
            .toTypeParameterResolver(typeResolver)
        val parameter = determineParameter(ksFunction.parameters, parameterTypeResolver)
        val mockeryParameter = determineMockeryParameter(parameter.second, generics)
        val mockeryName = selectFunMockeryName(
            functionName,
            parameter.second,
            functionNameCollector
        )
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)
        val returnType = ksFunction.returnType!!.toTypeName(parameterTypeResolver)

        val function = buildFunction(
            ksFunction.simpleName.asString(),
            generics,
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

    private fun buildClear(
        propertyNames: List<String>,
        functionNames: List<String>
    ): FunSpec {
        val function = FunSpec
            .builder("clearMock")

        propertyNames.forEach { name ->
            function.addStatement("$name.clear()")
        }

        functionNames.forEach { name ->
            function.addStatement("$name.clear()")
        }

        return function.build()
    }

    private fun buildMock(
        className: String,
        template: KSClassDeclaration,
        relaxer: Relaxer?
    ): TypeSpec {
        val implementation = TypeSpec.classBuilder(className)
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val qualifier = template.qualifiedName!!.asString()

        val functionNameCollector: MutableList<String> = mutableListOf()
        val propertyNameCollector: MutableList<String> = mutableListOf()
        val superType = resolveType(template)

        implementation.addSuperinterface(superType)
        implementation.addModifiers(KModifier.INTERNAL)

        val generics = resolveGeneric(template)
        if (generics != null) {
            implementation.typeVariables.addAll(mapGeneric(generics))
        }

        implementation.primaryConstructor(
            buildConstructor(superType, relaxer,)
        )

        template.getAllProperties().forEach { ksProperty ->
            if (ksProperty.isAbstract()) {
                implementation.addProperties(
                    buildPropertyBundle(
                        qualifier,
                        ksProperty,
                        typeResolver,
                        propertyNameCollector,
                        relaxer
                    )
                )
            }
        }

        template.getAllFunctions().forEach { ksFunction ->
            if (ksFunction.isAbstract) {
                val (mockery, function) = buildFunctionBundle(
                    qualifier,
                    ksFunction,
                    typeResolver,
                    functionNameCollector,
                    relaxer
                )

                implementation.addFunction(function)
                implementation.addProperty(mockery)
            }
        }

        implementation.addFunction(buildClear(propertyNameCollector, functionNameCollector))

        return implementation.build()
    }

    private fun writeMock(
        template: KSClassDeclaration,
        dependencies: List<KSFile>,
        target: ProcessorContract.Target,
        relaxer: Relaxer?
    ) {
        val className = "${template.simpleName.asString()}Mock"
        val file = FileSpec.builder(
            template.packageName.asString(),
            className
        )

        val implementation = buildMock(className, template, relaxer)

        if (target.value.isNotEmpty()) {
            file.addComment(target.value)
        }

        file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)
        file.addImport(PROP_NAME.packageName, PROP_NAME.simpleName)
        file.addImport(SYNC_FUN_NAME.packageName, SYNC_FUN_NAME.simpleName)
        file.addImport(ASYNC_FUN_NAME.packageName, ASYNC_FUN_NAME.simpleName)

        if (relaxer != null) {
            file.addImport(relaxer.packageName, relaxer.functionName)
        }

        file.addType(implementation)
        file.build().writeTo(
            codeGenerator = codeGenerator,
            aggregating = true,
            originatingKSFiles = dependencies
        )
    }

    override fun writeCommonMocks(
        interfaces: List<KSClassDeclaration>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        interfaces.forEach { template ->
            writeMock(
                template,
                dependencies,
                ProcessorContract.Target.COMMON,
                relaxer
            )
        }
    }

    override fun writePlatformMocks(
        interfaces: List<KSClassDeclaration>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        interfaces.forEach { template ->
            writeMock(
                template,
                dependencies,
                ProcessorContract.Target.PLATFORM,
                relaxer
            )
        }
    }
}
