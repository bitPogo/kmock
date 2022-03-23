/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

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

internal class KMockFactoryEntryPointGenerator(
    private val enableSpies: Boolean,
    private val utils: ProcessorContract.MockFactoryGeneratorUtil,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val codeGenerator: ProcessorContract.KmpCodeGenerator,
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

    private fun buildGenericMockFactory(
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

    private fun buildGenericSpyFactory(
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
    ): List<Pair<FunSpec, FunSpec?>> {
        return templateSources.map { template ->
            val spyFactory = if (enableSpies) {
                buildGenericSpyFactory(template)
            } else {
                null
            }

            Pair(
                buildGenericMockFactory(template),
                spyFactory
            )
        }
    }

    private fun generateGenericEntryPoints(
        file: FileSpec.Builder,
        generics: List<TemplateSource>,
    ) {
        val genericFactories = buildGenericFactories(generics)

        genericFactories.forEach { factories ->
            val (mockFactory, spyFactory) = factories

            file.addFunction(mockFactory)

            if (spyFactory != null) {
                file.addFunction(spyFactory)
            }
        }
    }

    override fun generateCommon(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>
    ) {
        if (options.isKmp && templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            val file = FileSpec.builder(
                options.rootPackage,
                "MockFactory"
            )
            val (_, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)

            file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)

            file.addFunction(buildMockFactory())

            if (enableSpies) {
                file.addFunction(buildSpyFactory())
            }

            generateGenericEntryPoints(
                file,
                generics
            )

            codeGenerator.setOneTimeSourceSet("commonTest")
            file.build().writeTo(
                codeGenerator = codeGenerator,
                aggregating = false,
            )
        }
    }

    private fun generateShared(
        buckets: Map<String, List<TemplateSource>>,
        options: ProcessorContract.Options,
    ) {
        buckets.forEach { (indicator, templateSources) ->
            val (_, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)

            if (generics.isNotEmpty()) {
                val file = FileSpec.builder(
                    options.rootPackage,
                    "MockFactory"
                )

                file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)

                generateGenericEntryPoints(
                    file,
                    generics
                )

                codeGenerator.setOneTimeSourceSet(indicator)
                file.build().writeTo(
                    codeGenerator = codeGenerator,
                    aggregating = false,
                )
            }
        }
    }

    override fun generateShared(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>
    ) {
        if (options.isKmp && templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            val buckets: MutableMap<String, List<TemplateSource>> = mutableMapOf()

            templateSources.forEach { template ->
                val indicator = template.indicator
                val bucket = buckets.getOrElse(indicator) { mutableListOf() }.toMutableList()
                bucket.add(template)

                buckets[indicator] = bucket
            }

            generateShared(buckets, options)
        }
    }
}
