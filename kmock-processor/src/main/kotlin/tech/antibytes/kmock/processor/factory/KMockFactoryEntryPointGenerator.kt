/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COMMON_INDICATOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FACTORY_FILE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NOOP_COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNUSED
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.SpyContainer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockFactoryEntryPointGenerator(
    private val isKmp: Boolean,
    private val rootPackage: String,
    private val spyContainer: SpyContainer,
    private val spiesOnly: Boolean,
    private val utils: MockFactoryGeneratorUtil,
    private val genericResolver: GenericResolver,
    private val codeGenerator: KmpCodeGenerator,
) : MockFactoryEntryPointGenerator {
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

    private fun resolveGenericSpyFactory(
        source: TemplateSource
    ): FunSpec? {
        return if (spyContainer.isSpyable(source.template, source.packageName, source.templateName)) {
            buildSpyGenericFactory(source)
        } else {
            null
        }
    }

    private fun buildGenericFactories(
        templateSources: List<TemplateSource>
    ): List<Pair<FunSpec, FunSpec?>> {
        return templateSources.map { template ->
            Pair(
                buildMockGenericFactory(template),
                resolveGenericSpyFactory(template)
            )
        }
    }

    private fun FileSpec.Builder.generateGenericEntryPoints(generics: List<TemplateSource>,) {
        val genericFactories = buildGenericFactories(generics)

        genericFactories.forEach { factories ->
            val (mockFactory, spyFactory) = factories

            if (!spiesOnly) {
                this.addFunction(mockFactory)
            }

            if (spyFactory != null) {
                this.addFunction(spyFactory)
            }
        }
    }

    private fun buildMultiInterfaceSpyFactory(
        boundaries: List<TypeName>
    ): FunSpec {
        val spyType = TypeVariableName(KSPY_FACTORY_TYPE_NAME, bounds = boundaries)
        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME).copy(bounds = listOf(spyType))

        return utils.generateKspySignature(
            spyType = spyType,
            mockType = mockType,
            generics = emptyList(),
            hasDefault = true,
            modifier = KModifier.EXPECT
        ).build()
    }

    private fun buildMultiInterfaceSpyFactory(
        templateSource: TemplateMultiSource
    ): FunSpec? {
        return if (spyContainer.isSpyable(null, templateSource.packageName, templateSource.templateName)) {
            buildMultiInterfaceSpyFactory(utils.toTypeNames(templateSource.templates))
        } else {
            null
        }
    }

    private fun FileSpec.Builder.generateMultiInterfaceEntryPoints(
        templateSources: List<TemplateMultiSource>
    ) {
        templateSources.forEach { source ->
            val factory = buildMultiInterfaceSpyFactory(source)

            if (factory != null) {
                this.addFunction(factory)
            }
        }
    }

    override fun generateCommon(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        totalMultiSources: List<TemplateMultiSource>,
        totalTemplates: List<TemplateSource>,
    ) {
        if (isKmp && (totalTemplates.isNotEmpty() || totalMultiSources.isNotEmpty())) { // TODO: Solve multi Rounds in a better way
            val file = FileSpec.builder(
                rootPackage,
                FACTORY_FILE_NAME
            )
            val (_, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)

            file.addAnnotation(UNUSED)
            file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)
            file.addImport(NOOP_COLLECTOR_NAME.packageName, NOOP_COLLECTOR_NAME.simpleName)

            if (!spiesOnly) {
                file.addFunction(buildMockFactory())
            }

            if (spyContainer.hasSpies(totalMultiSources)) {
                file.addFunction(buildSpyFactory())
            }

            file.generateGenericEntryPoints(generics)
            file.generateMultiInterfaceEntryPoints(templateMultiSources)

            codeGenerator.setOneTimeSourceSet(COMMON_INDICATOR)
            file.build().writeTo(
                codeGenerator = codeGenerator,
                aggregating = false,
            )
        }
    }

    private fun generateShared(
        buckets: Map<String, List<TemplateSource>>,
    ) {
        buckets.forEach { (indicator, templateSources) ->
            val (_, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)

            if (generics.isNotEmpty()) {
                val file = FileSpec.builder(
                    rootPackage,
                    FACTORY_FILE_NAME
                )
                file.addAnnotation(UNUSED)

                file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)
                file.addImport(NOOP_COLLECTOR_NAME.packageName, NOOP_COLLECTOR_NAME.simpleName)

                file.generateGenericEntryPoints(generics)

                codeGenerator.setOneTimeSourceSet(indicator)
                file.build().writeTo(
                    codeGenerator = codeGenerator,
                    aggregating = false,
                )
            }
        }
    }

    override fun generateShared(
        templateSources: List<TemplateSource>
    ) {
        if (isKmp && templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            val buckets: MutableMap<String, List<TemplateSource>> = mutableMapOf()

            templateSources.forEach { template ->
                val indicator = template.indicator
                val bucket = buckets.getOrElse(indicator) { mutableListOf() }.toMutableList()
                bucket.add(template)

                buckets[indicator] = bucket
            }

            generateShared(buckets)
        }
    }
}
