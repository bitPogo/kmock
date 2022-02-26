/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockFactoryGenerator(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryGenerator {
    private val boolean = TypeVariableName("Boolean")

    private fun buildRelaxedParameter(
        isKmp: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("relaxed", boolean)
        if (!isKmp) {
            parameter.defaultValue("false")
        }

        return parameter.build()
    }

    private fun buildVerifierParameter(
        isKmp: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("verifier", COLLECTOR_NAME)
        if (!isKmp) {
            parameter.defaultValue("Collector { _, _ -> Unit }")
        }
        return parameter.build()
    }

    private fun buildFreezeParameter(
        isKmp: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("freeze", boolean)
        if (!isKmp) {
            parameter.defaultValue("true")
        }
        return parameter.build()
    }

    private fun buildMockSelector(
        function: FunSpec.Builder,
        interfaces: List<KSClassDeclaration>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        function.beginControlFlow("return when (T::class)")

        interfaces.forEach { interfaze ->
            val qualifiedName = interfaze.qualifiedName!!.asString()
            val interfaceName = "${interfaze.packageName.asString()}.${interfaze.simpleName.asString()}"

            if (relaxer == null) {
                function.addStatement(
                    "%L::class -> %LMock(verifier = verifier, freeze = freeze) as T",
                    qualifiedName,
                    interfaceName,
                )

                function.addStatement(
                    "%LMock::class -> %LMock(verifier = verifier, freeze = freeze) as T",
                    interfaceName,
                    interfaceName,
                )
            } else {
                function.addStatement(
                    "%L::class -> %LMock(verifier = verifier, relaxed = relaxed, freeze = freeze) as T",
                    qualifiedName,
                    interfaceName,
                )
                function.addStatement(
                    "%LMock::class -> %LMock(verifier = verifier, relaxed = relaxed, freeze = freeze) as T",
                    interfaceName,
                    interfaceName,
                )
            }
        }
        function.addStatement("else -> throw RuntimeException(\"Unknown Interface \${T::class.simpleName}.\")")
        function.endControlFlow()

        return function
    }

    private fun buildMockFactory(
        isKmp: Boolean,
        interfaces: List<KSClassDeclaration>,
        relaxer: Relaxer?
    ): FunSpec {
        val factory = FunSpec.builder("kmock")
        val type = TypeVariableName("T")
        factory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(type.copy(reified = true))
            .returns(type)
            .addParameter(buildVerifierParameter(isKmp))
            .addParameter(buildRelaxedParameter(isKmp))
            .addParameter(buildFreezeParameter(isKmp))

        if (isKmp) {
            factory.addModifiers(KModifier.ACTUAL)
        }

        return buildMockSelector(factory, interfaces, relaxer).build()
    }

    private fun buildSpyParameter(): ParameterSpec {
        return ParameterSpec.builder("spyOn", TypeVariableName("T"))
            .build()
    }

    private fun buildSpySelector(
        function: FunSpec.Builder,
        interfaces: List<KSClassDeclaration>,
    ): FunSpec.Builder {
        function.beginControlFlow("return when (T::class)")

        interfaces.forEach { interfaze ->
            val qualifiedName = interfaze.qualifiedName!!.asString()
            val interfaceName = "${interfaze.packageName.asString()}.${interfaze.simpleName.asString()}"
            function.addStatement(
                "%L::class -> %LMock(verifier = verifier, spyOn = spyOn as %L, freeze = freeze) as T",
                qualifiedName,
                interfaceName,
                qualifiedName,
            )
            function.addStatement(
                "%LMock::class -> %LMock(verifier = verifier, spyOn = spyOn as %L, freeze = freeze) as T",
                interfaceName,
                interfaceName,
                qualifiedName,
            )
        }
        function.addStatement("else -> throw RuntimeException(\"Unknown Interface \${T::class.simpleName}.\")")
        function.endControlFlow()

        return function
    }

    private fun buildSpyFactory(
        isKmp: Boolean,
        interfaces: List<KSClassDeclaration>,
    ): FunSpec {
        val factory = FunSpec.builder("kspy")
        val type = TypeVariableName("T")
        factory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(type.copy(reified = true))
            .returns(type)
            .addParameter(buildSpyParameter())
            .addParameter(buildVerifierParameter(isKmp))
            .addParameter(buildFreezeParameter(isKmp))

        if (isKmp) {
            factory.addModifiers(KModifier.ACTUAL)
        }

        return buildSpySelector(factory, interfaces).build()
    }

    override fun writeFactories(
        options: ProcessorContract.Options,
        interfaces: List<KSClassDeclaration>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        if (interfaces.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            val file = FileSpec.builder(
                options.rootPackage,
                "MockFactory"
            )
            file.addImport(ProcessorContract.KMOCK_CONTRACT.packageName, ProcessorContract.KMOCK_CONTRACT.simpleName)

            file.addFunction(buildMockFactory(options.isKmp, interfaces, relaxer))
            file.addFunction(buildSpyFactory(options.isKmp, interfaces))

            file.build().writeTo(
                codeGenerator = codeGenerator,
                aggregating = false,
                originatingKSFiles = dependencies
            )
        }
    }
}
