/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COMMON_INDICATOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NOOP_COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PROXY_FACTORY_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockGenerator(
    private val logger: KSPLogger,
    private val spyOn: Set<String>,
    private val useBuildInProxiesOn: Set<String>,
    private val codeGenerator: ProcessorContract.KmpCodeGenerator,
    private val genericsResolver: ProcessorContract.GenericResolver,
    private val propertyGenerator: ProcessorContract.PropertyGenerator,
    private val methodGenerator: ProcessorContract.MethodGenerator,
    private val buildInGenerator: ProcessorContract.BuildInMethodGenerator
) : ProcessorContract.MockGenerator {
    private val unused = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()

    private fun buildConstructor(superType: TypeName): FunSpec {
        val constructor = FunSpec.constructorBuilder()

        val collector = ParameterSpec.builder("verifier", COLLECTOR_NAME)
        collector.defaultValue("NoopCollector")
        constructor.addParameter(collector.build())

        val spy = ParameterSpec.builder(
            "spyOn",
            superType.copy(nullable = true),
        ).addAnnotation(unused).defaultValue("null")
        constructor.addParameter(spy.build())

        val freeze = ParameterSpec.builder("freeze", Boolean::class)
        freeze.defaultValue("true")
        constructor.addParameter(freeze.build())

        val relaxUnit = ParameterSpec.builder(
            "relaxUnitFun",
            Boolean::class
        ).addAnnotation(unused).defaultValue("false")
        constructor.addParameter(relaxUnit.build())

        val relaxed = ParameterSpec.builder(
            "relaxed",
            Boolean::class
        ).addAnnotation(unused).defaultValue("false")

        constructor.addParameter(relaxed.build())

        return constructor.build()
    }

    private fun buildClear(
        proxyNames: List<String>,
    ): FunSpec {
        val function = FunSpec
            .builder("_clearMock")

        proxyNames.forEach { name ->
            function.addStatement("$name.clear()")
        }

        return function.build()
    }

    private fun aggregateOverloading(template: KSClassDeclaration): Set<String> {
        val nameCollector: MutableList<String> = mutableListOf()
        val overloadedMethods: MutableSet<String> = mutableSetOf()

        template.getAllProperties().forEach { ksProperty ->
            val name = ksProperty.simpleName.asString()
            nameCollector.add(name)
        }

        template.getAllFunctions().forEach { ksFunction ->
            val name = ksFunction.simpleName.asString()

            if (name in nameCollector || "_$name" in nameCollector) {
                overloadedMethods.add("_$name")
            } else {
                nameCollector.add(name)
            }
        }

        return overloadedMethods
    }

    private fun constructQualifier(
        mockName: String,
        template: KSClassDeclaration,
    ): String = "${template.packageName.asString()}.$mockName"

    private fun KSDeclaration.isPublicOpen(): Boolean {
        return this.isPublic() && this.isOpen()
    }

    private fun buildMock(
        mockName: String,
        template: KSClassDeclaration,
        generics: Map<String, List<KSTypeReference>>?,
        relaxer: Relaxer?
    ): TypeSpec {
        val mock = TypeSpec.classBuilder(mockName)
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val templateName = template.qualifiedName!!.asString()
        val qualifier = constructQualifier(mockName, template)

        val superType = genericsResolver.resolveMockClassType(template, typeResolver)
        val proxyNameCollector: MutableList<String> = mutableListOf()

        val enableSpy = templateName in spyOn

        mock.addSuperinterface(superType)
        mock.addModifiers(KModifier.INTERNAL)

        if (generics != null) {
            mock.typeVariables.addAll(
                genericsResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        val overloadedMethods = aggregateOverloading(template)

        mock.primaryConstructor(
            buildConstructor(superType)
        )

        if (enableSpy) {
            mock.addProperty(
                PropertySpec.builder(
                    "__spyOn",
                    superType.copy(nullable = true),
                    KModifier.PRIVATE,
                ).initializer("spyOn").build()
            )
        }

        template.getAllProperties().forEach { ksProperty ->
            if (ksProperty.isPublicOpen()) {
                val (proxy, property) = propertyGenerator.buildPropertyBundle(
                    qualifier = qualifier,
                    ksProperty = ksProperty,
                    typeResolver = typeResolver,
                    enableSpy = enableSpy,
                    relaxer = relaxer,
                )

                proxyNameCollector.add(proxy.name)
                mock.addProperty(property)
                mock.addProperty(proxy)
            }
        }

        template.getAllFunctions().forEach { ksFunction ->
            val name = ksFunction.simpleName.asString()

            if (ksFunction.isPublicOpen() && name != "equals" && name != "toString" && name != "hashCode") {
                val (proxy, function) = methodGenerator.buildMethodBundle(
                    qualifier = qualifier,
                    ksFunction = ksFunction,
                    typeResolver = typeResolver,
                    existingProxies = overloadedMethods,
                    enableSpy = enableSpy,
                    relaxer = relaxer,
                )

                proxyNameCollector.add(proxy.name)
                mock.addFunction(function)
                mock.addProperty(proxy)
            }
        }

        if (templateName in useBuildInProxiesOn) {
            val (proxies, functions) = buildInGenerator.buildMethodBundles(
                mockName = mockName,
                qualifier = qualifier,
                existingProxies = overloadedMethods,
                enableSpy = enableSpy
            )

            mock.addFunctions(functions)
            proxies.forEach { proxy ->
                proxyNameCollector.add(proxy.name)
                mock.addProperty(proxy)
            }
        }

        mock.addFunction(buildClear(proxyNameCollector))

        return mock.build()
    }

    private fun writeMock(
        template: KSClassDeclaration,
        alias: String?,
        generics: Map<String, List<KSTypeReference>>?,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        val templateName = alias ?: template.simpleName.asString()
        val mockName = "${templateName}Mock"
        val file = FileSpec.builder(
            template.packageName.asString(),
            mockName
        )

        val implementation = buildMock(
            mockName = mockName,
            template = template,
            generics = generics,
            relaxer = relaxer
        )

        file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)
        file.addImport(PROXY_FACTORY_NAME.packageName, PROXY_FACTORY_NAME.simpleName)
        file.addImport(NOOP_COLLECTOR_NAME.packageName, NOOP_COLLECTOR_NAME.simpleName)

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
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        templateSources.forEach { template ->
            codeGenerator.setOneTimeSourceSet(COMMON_INDICATOR)

            writeMock(
                template = template.template,
                alias = template.alias,
                generics = template.generics,
                dependencies = dependencies,
                relaxer = relaxer
            )
        }
    }

    override fun writeSharedMocks(
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        templateSources.forEach { template ->
            codeGenerator.setOneTimeSourceSet(template.indicator)

            writeMock(
                template = template.template,
                alias = template.alias,
                generics = template.generics,
                dependencies = dependencies,
                relaxer = relaxer
            )
        }
    }

    override fun writePlatformMocks(
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        templateSources.forEach { template ->
            writeMock(
                template = template.template,
                alias = template.alias,
                generics = template.generics,
                dependencies = dependencies,
                relaxer = relaxer
            )
        }
    }
}
