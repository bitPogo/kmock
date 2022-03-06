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
    private val utils: ProcessorContract.FunctionUtils,
    private val propertyGenerator: ProcessorContract.PropertyGenerator,
    private val functionGenerator: ProcessorContract.FunctionGenerator
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

    private fun buildConstructor(
        superType: TypeName,
        relaxer: Relaxer?,
    ): FunSpec {
        val constructor = FunSpec.constructorBuilder()

        val collector = ParameterSpec.builder("verifier", COLLECTOR_NAME)
        collector.defaultValue("Collector { _, _ -> Unit }")

        val spy = ParameterSpec.builder("spyOn", superType.copy(nullable = true))
        spy.defaultValue("null")

        val freeze = ParameterSpec.builder("freeze", Boolean::class)
        freeze.defaultValue("true")

        val relaxUnit = ParameterSpec.builder("relaxUnitFun", Boolean::class)
        relaxUnit.addAnnotation(
            AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
        ).defaultValue("false")

        constructor.addParameter(collector.build())
        constructor.addParameter(spy.build())
        constructor.addParameter(freeze.build())
        constructor.addParameter(relaxUnit.build())

        if (relaxer != null) {
            val relaxed = ParameterSpec.builder("relaxed", Boolean::class)
            relaxed.defaultValue("false")

            constructor.addParameter(relaxed.build())
        }

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

    private fun buildMock(
        className: String,
        template: KSClassDeclaration,
        relaxer: Relaxer?
    ): TypeSpec {
        val implementation = TypeSpec.classBuilder(className)
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val qualifier = template.qualifiedName!!.asString()

        val proxyNameCollector: MutableList<String> = mutableListOf()
        val superType = resolveType(template, typeResolver)

        implementation.addSuperinterface(superType)
        implementation.addModifiers(KModifier.INTERNAL)

        val generics = utils.resolveGeneric(template, typeResolver)
        if (generics != null) {
            implementation.typeVariables.addAll(utils.mapGeneric(generics, typeResolver))
        }

        implementation.primaryConstructor(
            buildConstructor(superType, relaxer)
        )

        template.getAllProperties().forEach { ksProperty ->
            if (ksProperty.isAbstract()) {
                implementation.addProperties(
                    propertyGenerator.buildPropertyBundle(
                        qualifier,
                        ksProperty,
                        typeResolver,
                        proxyNameCollector,
                        relaxer
                    )
                )
            }
        }

        template.getAllFunctions().forEach { ksFunction ->
            if (ksFunction.isAbstract) {
                val (mockery, function) = functionGenerator.buildFunctionBundle(
                    qualifier,
                    ksFunction,
                    typeResolver,
                    proxyNameCollector,
                    relaxer
                )

                implementation.addFunction(function)
                implementation.addProperty(mockery)
            }
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
            file.addComment(target)
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
