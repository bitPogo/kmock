/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ASYNC_FUN_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PROP_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SYNC_FUN_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNIT_RELAXER
import tech.antibytes.kmock.processor.ProcessorContract.InterfaceSource
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockGenerator(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val generics: ProcessorContract.GenericResolver,
    private val propertyGenerator: ProcessorContract.PropertyGenerator,
    private val functionGenerator: ProcessorContract.FunctionGenerator,
    private val buildInGenerator: ProcessorContract.BuildInFunctionGenerator
) : ProcessorContract.MockGenerator {
    private fun resolveType(
        template: KSClassDeclaration,
        resolver: TypeParameterResolver
    ): TypeName {
        return if (template.typeParameters.isEmpty()) {
            template.toClassName()
        } else {
            template.toClassName()
                .parameterizedBy(
                    template.typeParameters.map { type -> type.toTypeVariableName(resolver) }
                )
        }
    }

    private fun buildConstructor(superType: TypeName): FunSpec {
        val constructor = FunSpec.constructorBuilder()

        val collector = ParameterSpec.builder("verifier", COLLECTOR_NAME)
        collector.defaultValue("Collector { _, _ -> Unit }")

        val spy = ParameterSpec.builder("spyOn", superType.copy(nullable = true))
        spy.defaultValue("null")

        val freeze = ParameterSpec.builder("freeze", Boolean::class)
        freeze.defaultValue("true")

        val relaxUnit = ParameterSpec.builder(
            "relaxUnitFun",
            Boolean::class
        ).addAnnotation(
            AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
        ).defaultValue("false")

        constructor.addParameter(collector.build())
        constructor.addParameter(spy.build())
        constructor.addParameter(freeze.build())
        constructor.addParameter(relaxUnit.build())

        val relaxed = ParameterSpec.builder(
            "relaxed",
            Boolean::class
        ).addAnnotation(
            AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
        ).defaultValue("false")

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

    private fun KSDeclaration.isPublicOpen(): Boolean {
        return this.isPublic() && this.isOpen()
    }

    private fun buildMock(
        className: String,
        template: KSClassDeclaration,
        relaxer: Relaxer?
    ): TypeSpec {
        val implementation = TypeSpec.classBuilder(className)
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val qualifier = template.qualifiedName!!.asString()
        val superType = resolveType(template, typeResolver)
        val proxyNameCollector: MutableList<String> = mutableListOf()

        implementation.addSuperinterface(superType)
        implementation.addModifiers(KModifier.INTERNAL)

        val generics = generics.extractGenerics(template, typeResolver)
        if (generics != null) {
            implementation.typeVariables.addAll(
                this.generics.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        val overloadedMethods = aggregateOverloading(template)

        implementation.primaryConstructor(
            buildConstructor(superType)
        )

        template.getAllProperties().forEach { ksProperty ->
            if (ksProperty.isPublicOpen()) {
                val (proxy, property) = propertyGenerator.buildPropertyBundle(
                    qualifier,
                    ksProperty,
                    typeResolver,
                    relaxer
                )

                proxyNameCollector.add(proxy.name)
                implementation.addProperty(property)
                implementation.addProperty(proxy)
            }
        }

        template.getAllFunctions().forEach { ksFunction ->
            val name = ksFunction.simpleName.asString()

            if (ksFunction.isPublicOpen() && name != "equals" && name != "toString" && name != "hashCode") {
                val (proxy, function) = functionGenerator.buildFunctionBundle(
                    qualifier,
                    ksFunction,
                    typeResolver,
                    overloadedMethods,
                    relaxer
                )

                proxyNameCollector.add(proxy.name)
                implementation.addFunction(function)
                implementation.addProperty(proxy)
            }
        }

        val (proxies, functions) = buildInGenerator.buildFunctionBundles(qualifier, overloadedMethods)

        implementation.addFunctions(functions)
        proxies.forEach { proxy ->
            proxyNameCollector.add(proxy.name)
            implementation.addProperty(proxy)
        }

        implementation.addFunction(buildClear(proxyNameCollector))

        return implementation.build()
    }

    private fun writeMock(
        template: KSClassDeclaration,
        dependencies: List<KSFile>,
        target: String,
        relaxer: Relaxer?
    ) {
        val className = "${template.simpleName.asString()}Mock"
        val file = FileSpec.builder(
            template.packageName.asString(),
            className
        )

        val implementation = buildMock(className, template, relaxer)

        if (target.isNotEmpty()) {
            file.addComment(target.uppercase())
        }

        file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)
        file.addImport(PROP_NAME.packageName, PROP_NAME.simpleName)
        file.addImport(SYNC_FUN_NAME.packageName, SYNC_FUN_NAME.simpleName)
        file.addImport(ASYNC_FUN_NAME.packageName, ASYNC_FUN_NAME.simpleName)
        file.addImport(UNIT_RELAXER.packageName, UNIT_RELAXER.simpleName)

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
        interfaces: List<InterfaceSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        interfaces.forEach { template ->
            writeMock(
                template.interfaze,
                dependencies,
                ProcessorContract.Target.COMMON.value,
                relaxer
            )
        }
    }

    override fun writeSharedMocks(
        interfaces: List<InterfaceSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        interfaces.forEach { template ->
            writeMock(
                template.interfaze,
                dependencies,
                template.marker,
                relaxer
            )
        }
    }

    override fun writePlatformMocks(
        interfaces: List<InterfaceSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        interfaces.forEach { template ->
            writeMock(
                template.interfaze,
                dependencies,
                ProcessorContract.Target.PLATFORM.value,
                relaxer
            )
        }
    }
}
