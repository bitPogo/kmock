/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT

internal class KMockCommonEntryPointGenerator(
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryCommonEntryPointGenerator {
    private fun buildRelaxedParameter(): ParameterSpec {
        return ParameterSpec.builder("relaxed", Boolean::class)
            .defaultValue("false")
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
            )
            .build()
    }

    private fun buildUnitRelaxedParameter(): ParameterSpec {
        return ParameterSpec.builder("relaxUnitFun", Boolean::class)
            .defaultValue("false")
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
            )
            .build()
    }

    private fun buildVerifierParameter(): ParameterSpec {
        return ParameterSpec.builder("verifier", COLLECTOR_NAME)
            .defaultValue("Collector { _, _ -> Unit }")
            .build()
    }

    private fun buildFreezeParameter(): ParameterSpec {
        return ParameterSpec.builder("freeze", Boolean::class)
            .defaultValue("true")
            .build()
    }

    private fun buildMockFactory(): FunSpec {
        val factory = FunSpec.builder("kmock")
        val type = TypeVariableName("T")

        return factory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(type.copy(reified = true))
            .returns(type)
            .addParameter(buildVerifierParameter())
            .addParameter(buildRelaxedParameter())
            .addParameter(buildUnitRelaxedParameter())
            .addParameter(buildFreezeParameter())
            .addModifiers(KModifier.EXPECT)
            .build()
    }

    private fun buildSpyParameter(): ParameterSpec {
        return ParameterSpec.builder("spyOn", TypeVariableName("SpyOn"))
            .build()
    }

    private fun buildSpyFactory(): FunSpec {
        val factory = FunSpec.builder("kspy")
        val spy = TypeVariableName("SpyOn")
        val mock = TypeVariableName("Mock").copy(bounds = listOf(spy))

        return factory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(mock.copy(reified = true))
            .addTypeVariable(spy.copy(reified = true))
            .returns(mock)
            .addParameter(buildSpyParameter())
            .addParameter(buildVerifierParameter())
            .addParameter(buildFreezeParameter())
            .addModifiers(KModifier.EXPECT)
            .build()
    }

    override fun generate(
        options: ProcessorContract.Options,
        templateSources: List<ProcessorContract.TemplateSource>,
    ) {
        if (options.isKmp && templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            val file = FileSpec.builder(
                options.rootPackage,
                "MockFactoryCommonEntry"
            )
            file.addComment(ProcessorContract.Target.COMMON.value.uppercase())
            file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)

            file.addFunction(buildMockFactory())
            file.addFunction(buildSpyFactory())

            file.build().writeTo(
                codeGenerator = codeGenerator,
                aggregating = false,
            )
        }
    }
}
