/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
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
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COMMON_INDICATOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NOOP_COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PROXY_FACTORY_NAME
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameCollector
import tech.antibytes.kmock.processor.ProcessorContract.PropertyGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.BuildInMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.Companion.MULTI_MOCK
import tech.antibytes.kmock.processor.ProcessorContract.Companion.multiMock
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.ParentFinder

internal class KMockGenerator(
    private val logger: KSPLogger,
    private val spyOn: Set<String>,
    private val useBuildInProxiesOn: Set<String>,
    private val codeGenerator: KmpCodeGenerator,
    private val genericsResolver: GenericResolver,
    private val nameCollector: ProxyNameCollector,
    private val parentFinder: ParentFinder,
    private val propertyGenerator: PropertyGenerator,
    private val methodGenerator: MethodGenerator,
    private val buildInGenerator: BuildInMethodGenerator
) : ProcessorContract.MockGenerator {
    private val unusedParameter = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
    private val unused = AnnotationSpec.builder(Suppress::class).addMember("%S", "unused").build()

    private fun resolveSpyType(superTypes: List<TypeName>): TypeName {
        return if (superTypes.size == 1) {
            superTypes.first()
        } else {
            multiMock
        }
    }

    private fun buildConstructor(superTypes: List<TypeName>): FunSpec {
        val constructor = FunSpec.constructorBuilder()

        val collector = ParameterSpec.builder("verifier", COLLECTOR_NAME)
        collector.defaultValue("NoopCollector")
        constructor.addParameter(collector.build())

        val spy = ParameterSpec.builder(
            "spyOn",
            resolveSpyType(superTypes).copy(nullable = true),
        ).addAnnotation(unusedParameter).defaultValue("null")
        constructor.addParameter(spy.build())

        val freeze = ParameterSpec.builder("freeze", Boolean::class)
        freeze.defaultValue("true")
        constructor.addParameter(freeze.build())

        val relaxUnit = ParameterSpec.builder(
            "relaxUnitFun",
            Boolean::class,
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

    private fun constructQualifier(
        mockName: String,
        template: KSClassDeclaration,
    ): String = "${template.packageName.asString()}.$mockName"

    private fun isNotBuildInMethod(
        name: String
    ): Boolean = name != "equals" && name != "toString" && name != "hashCode"

    private fun resolveSuperTypes(
        template: KSClassDeclaration,
        parents: List<KSClassDeclaration>,
        typeResolver: TypeParameterResolver,
    ): List<TypeName> {
        return if (parents.isEmpty()) {
            listOf(
                genericsResolver.resolveMockClassType(template, typeResolver)
            )
        } else {
            parents.map { parent ->
                val resolver = parent.typeParameters.toTypeParameterResolver()

                genericsResolver.resolveMockClassType(parent, resolver)
            }
        }
    }

    private fun buildMock(
        mockName: String,
        parents: List<KSClassDeclaration>,
        template: KSClassDeclaration,
        generics: Map<String, List<KSTypeReference>>?,
        relaxer: Relaxer?
    ): TypeSpec {
        val mock = TypeSpec.classBuilder(mockName)
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val templateName = template.qualifiedName!!.asString()
        val qualifier = constructQualifier(mockName, template)

        val superTypes = resolveSuperTypes(
            template = template,
            parents = parents,
            typeResolver = typeResolver,
        )
        val proxyNameCollector: MutableList<String> = mutableListOf()

        val enableSpy = templateName in spyOn

        mock.addSuperinterfaces(superTypes)
        mock.addModifiers(KModifier.INTERNAL)

        if (generics != null) {
            mock.typeVariables.addAll(
                genericsResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        if (superTypes.size > 1) {
            mock.typeVariables.add(
                TypeVariableName(MULTI_MOCK, bounds = superTypes)
            )
        }

        mock.primaryConstructor(
            buildConstructor(superTypes)
        )

        mock.addProperty(
            PropertySpec.builder(
                "relaxUnitFun",
                Boolean::class,
                KModifier.PRIVATE,
            ).initializer("relaxUnitFun").build()
        )

        mock.addProperty(
            PropertySpec.builder(
                "relaxed",
                Boolean::class,
                KModifier.PRIVATE,
            ).initializer("relaxed").build()
        )

        if (enableSpy) {
            mock.addProperty(
                PropertySpec.builder(
                    "__spyOn",
                    resolveSpyType(superTypes).copy(nullable = true),
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

                mock.addProperty(property)

                if (proxy != null) {
                    proxyNameCollector.add(proxy.name)
                    mock.addProperty(proxy)
                }
            }
        }

        template.getAllFunctions().forEach { ksFunction ->
            val name = ksFunction.simpleName.asString()
            val scope = ksFunction.determineScope()

            if (ksFunction.isPublicOpen() && (isNotBuildInMethod(name) || scope != null)) {
                val (proxy, method) = methodGenerator.buildMethodBundle(
                    methodScope = scope,
                    qualifier = qualifier,
                    ksFunction = ksFunction,
                    typeResolver = typeResolver,
                    enableSpy = enableSpy,
                    relaxer = relaxer,
                )

                mock.addFunction(method)

                if (proxy != null) {
                    proxyNameCollector.add(proxy.name)
                    mock.addProperty(proxy)
                }
            }
        }

        if (templateName in useBuildInProxiesOn) {
            val (proxies, functions) = buildInGenerator.buildMethodBundles(
                mockName = mockName,
                qualifier = qualifier,
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
        parents: List<KSClassDeclaration>,
        templateName: String,
        packageName: String,
        generics: Map<String, List<KSTypeReference>>?,
        dependency: KSFile,
        relaxer: Relaxer?
    ) {
        val mockName = "${templateName}Mock"
        val file = FileSpec.builder(
            packageName,
            mockName
        )

        nameCollector.collect(template)

        val implementation = buildMock(
            mockName = mockName,
            parents = parents,
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
            originatingKSFiles = listOf(dependency)
        )
    }

    override fun writeCommonMocks(
        templateSources: List<TemplateSource>,
        templateMultiSources: Aggregated<TemplateMultiSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        templateSources.forEachIndexed { idx, template ->
            codeGenerator.setOneTimeSourceSet(COMMON_INDICATOR)

            val (parents, dependency) = parentFinder.find(
                templateSource = template,
                templateMultiSources = templateMultiSources,
                dependency = dependencies[idx]
            )

            writeMock(
                template = template.template,
                parents = parents,
                templateName = template.templateName,
                packageName = template.packageName,
                generics = template.generics,
                dependency = dependency,
                relaxer = relaxer
            )
        }
    }

    override fun writeSharedMocks(
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        templateSources.forEachIndexed { idx, template ->
            codeGenerator.setOneTimeSourceSet(template.indicator)

            writeMock(
                template = template.template,
                parents = emptyList(),
                templateName = template.templateName,
                packageName = template.packageName,
                generics = template.generics,
                dependency = dependencies[idx],
                relaxer = relaxer
            )
        }
    }

    override fun writePlatformMocks(
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        templateSources.forEachIndexed { idx, template ->
            writeMock(
                template = template.template,
                parents = emptyList(),
                templateName = template.templateName,
                packageName = template.packageName,
                generics = template.generics,
                dependency = dependencies[idx],
                relaxer = relaxer
            )
        }
    }
}
