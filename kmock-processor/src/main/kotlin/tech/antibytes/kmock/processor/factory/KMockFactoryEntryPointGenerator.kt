/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.titleCase

internal class KMockFactoryEntryPointGenerator(
    private val utils: ProcessorContract.MockFactoryGeneratorUtil,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryEntryPointGenerator {
    private fun buildMockFactory(): FunSpec {
        val type = TypeVariableName(KMOCK_FACTORY_TYPE_NAME)

        return utils.generateKmockSignature(
            type = type.copy(reified = true),
            hasDefault = true,
            modifier = KModifier.EXPECT
        ).build()
    }

    private fun buildSpyFactory(): FunSpec {
        val spyType = TypeVariableName(KSPY_FACTORY_TYPE_NAME)
        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME).copy(bounds = listOf(spyType))

        return utils.generateKspySignature(
            spyType = spyType,
            mockType = mockType,
            hasDefault = true,
            modifier = KModifier.EXPECT
        ).build()
    }

    private fun generate(
        indicator: String,
        options: ProcessorContract.Options,
        templateSources: List<ProcessorContract.TemplateSource>,
    ) {
        if (options.isKmp && templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            val infix = indicator.titleCase()

            val file = FileSpec.builder(
                options.rootPackage,
                "MockFactory${infix}Entry"
            )
            file.addComment(indicator.uppercase())
            file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)

            file.addFunction(buildMockFactory())
            file.addFunction(buildSpyFactory())

            file.build().writeTo(
                codeGenerator = codeGenerator,
                aggregating = false,
            )
        }
    }

    override fun generateCommon(
        options: ProcessorContract.Options,
        templateSources: List<ProcessorContract.TemplateSource>
    ) {
        generate(
            ProcessorContract.Target.COMMON.value,
            options,
            templateSources
        )
    }

    override fun generateShared(
        indicator: String,
        options: ProcessorContract.Options,
        templateSources: List<ProcessorContract.TemplateSource>
    ) {
        generate(
            indicator,
            options,
            templateSources
        )
    }
}
