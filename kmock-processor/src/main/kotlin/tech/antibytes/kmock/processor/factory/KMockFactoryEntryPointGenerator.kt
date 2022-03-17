/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.titleCase

internal class KMockFactoryEntryPointGenerator(
    private val utils: ProcessorContract.MockFactoryGeneratorUtil,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryEntryPointGenerator {
    private fun buildMockFactory(): FunSpec {
        val type = TypeVariableName(KMOCK_FACTORY_TYPE_NAME)

        return utils.generateKmockSignature(
            type = type.copy(reified = true),
            generics = emptyList(),
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
            generics = emptyList(),
            hasDefault = true,
            modifier = KModifier.EXPECT
        ).build()
    }

    private fun buildMockGenericFactory(
        templateSource: TemplateSource,
    ): FunSpec {
        val generics = utils.resolveGenerics(templateSource)
        val type = genericResolver.resolveKMockFactoryType(
            KMOCK_FACTORY_TYPE_NAME,
            templateSource
        )

        return utils.generateKmockSignature(
            type = type.copy(reified = true),
            generics = generics,
            hasDefault = true,
            modifier = KModifier.EXPECT
        ).build()
    }

    private fun buildSpyGenericFactory(
        templateSource: TemplateSource,
    ): FunSpec {
        val generics = utils.resolveGenerics(templateSource)
        val spyType = genericResolver.resolveKMockFactoryType(
            KSPY_FACTORY_TYPE_NAME,
            templateSource
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return utils.generateKspySignature(
            spyType = spyType,
            mockType = mockType,
            generics = generics,
            hasDefault = true,
            modifier = KModifier.EXPECT
        ).build()
    }

    private fun buildGenericFactories(
        templateSources: List<TemplateSource>
    ): List<Pair<FunSpec, FunSpec>> {
        return templateSources.map { template ->
            Pair(
                buildMockGenericFactory(template),
                buildSpyGenericFactory(template)
            )
        }
    }

    private fun generateEntryPoint(
        indicator: String,
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>,
    ) {
        val infix = indicator.titleCase()

        val file = FileSpec.builder(
            options.rootPackage,
            "MockFactory${infix}Entry"
        )

        val (_, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)
        val genericFactories = buildGenericFactories(generics)

        file.addComment(indicator.uppercase())
        file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)

        file.addFunction(buildMockFactory())
        file.addFunction(buildSpyFactory())

        genericFactories.forEach { factories ->
            val (mockFactory, spyFactory) = factories

            file.addFunction(mockFactory)
            file.addFunction(spyFactory)
        }

        file.build().writeTo(
            codeGenerator = codeGenerator,
            aggregating = false,
        )
    }

    override fun generateCommon(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>
    ) {
        if (options.isKmp && templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            generateEntryPoint(
                ProcessorContract.Target.COMMON.value,
                options,
                templateSources
            )
        }
    }

    override fun generateShared(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>
    ) {
        if (options.isKmp && templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            val buckets: MutableMap<String, List<TemplateSource>> = mutableMapOf()

            templateSources.forEach { template ->
                val indicator = template.indicator.lowercase()
                val bucket = buckets.getOrElse(indicator) { mutableListOf() }.toMutableList()
                bucket.add(template)

                buckets[indicator] = bucket
            }

            buckets.forEach { (indicator, templates) ->
                generateEntryPoint(
                    indicator,
                    options,
                    templates
                )
            }
        }
    }
}
