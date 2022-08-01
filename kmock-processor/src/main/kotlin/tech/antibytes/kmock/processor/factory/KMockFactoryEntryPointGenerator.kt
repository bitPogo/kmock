/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.google.devtools.ksp.symbol.KSFile
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
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NOOP_COLLECTOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNUSED
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.SpyContainer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.multi.hasGenerics

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
            modifier = KModifier.EXPECT,
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
            modifier = KModifier.EXPECT,
        ).build()
    }

    private fun buildMockGenericFactory(
        templateSource: TemplateSource,
    ): FunSpec {
        val generics = utils.resolveGenerics(templateSource)
        val type = genericResolver.resolveKMockFactoryType(
            KMOCK_FACTORY_TYPE_NAME,
            templateSource,
        )

        return utils.generateKmockSignature(
            type = type.copy(reified = true),
            generics = generics,
            hasDefault = true,
            modifier = KModifier.EXPECT,
        ).build()
    }

    private fun buildSpyGenericFactory(
        templateSource: TemplateSource,
    ): FunSpec {
        val generics = utils.resolveGenerics(templateSource)
        val spyType = genericResolver.resolveKMockFactoryType(
            KSPY_FACTORY_TYPE_NAME,
            templateSource,
        )
        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return utils.generateKspySignature(
            spyType = spyType,
            mockType = mockType,
            generics = generics,
            hasDefault = true,
            modifier = KModifier.EXPECT,
        ).build()
    }

    private fun resolveGenericSpyFactory(
        source: TemplateSource,
    ): FunSpec? {
        return if (spyContainer.isSpyable(source.template, source.packageName, source.templateName)) {
            buildSpyGenericFactory(source)
        } else {
            null
        }
    }

    private fun buildGenericFactories(
        templateSources: List<TemplateSource>,
    ): List<Pair<FunSpec, FunSpec?>> {
        return templateSources.map { template ->
            Pair(
                buildMockGenericFactory(template),
                resolveGenericSpyFactory(template),
            )
        }
    }

    private fun FileSpec.Builder.generateGenericEntryPoints(generics: List<TemplateSource>) {
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
        boundaries: List<TypeName>,
    ): FunSpec {
        val spyType = TypeVariableName(KSPY_FACTORY_TYPE_NAME, bounds = boundaries)
        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME).copy(bounds = listOf(spyType))

        return utils.generateKspySignature(
            spyType = spyType,
            mockType = mockType,
            boundaries = boundaries,
            generics = emptyList(),
            hasDefault = true,
            modifier = KModifier.EXPECT,
        ).build()
    }

    private fun buildMultiInterfaceGenericKMockFactory(
        source: TemplateMultiSource,
        boundaries: List<TypeName>,
        generics: List<TypeVariableName>,
    ): FunSpec {
        val mock = utils.resolveMockType(source, generics)
        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME).copy(bounds = listOf(mock))

        return utils.generateKmockSignature(
            type = mockType,
            boundaries = boundaries,
            generics = emptyList(),
            hasDefault = true,
            modifier = KModifier.EXPECT,
        ).addTypeVariables(generics).build()
    }

    private fun buildMultiInterfaceGenericKSpyFactory(
        boundaries: List<TypeName>,
        generics: List<TypeVariableName>,
    ): FunSpec {
        val spyType = TypeVariableName(
            KSPY_FACTORY_TYPE_NAME,
            bounds = boundaries,
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return utils.generateKspySignature(
            mockType = mockType,
            spyType = spyType,
            boundaries = boundaries,
            generics = emptyList(),
            hasDefault = true,
            modifier = KModifier.EXPECT,
        ).addTypeVariables(generics).build()
    }

    private fun TemplateMultiSource.isSpyable(): Boolean {
        return spyContainer.isSpyable(null, this.packageName, this.templateName)
    }

    private fun resolveGenericMultiInterfaceFactories(
        templateSource: TemplateMultiSource,
        boundaries: List<TypeName>,
        generics: List<TypeVariableName>,
    ): FunSpec {
        return if (templateSource.isSpyable()) {
            buildMultiInterfaceGenericKSpyFactory(
                boundaries = boundaries,
                generics = generics,
            )
        } else {
            buildMultiInterfaceGenericKMockFactory(
                source = templateSource,
                boundaries = boundaries,
                generics = generics,
            )
        }
    }

    private fun buildMultiInterfaceFactory(
        templateSource: TemplateMultiSource,
    ): FunSpec? {
        val (types, generics) = genericResolver.remapTypes(templateSource.templates, templateSource.generics)

        return when {
            templateSource.isSpyable() && !templateSource.hasGenerics() -> {
                buildMultiInterfaceSpyFactory(types)
            }
            templateSource.hasGenerics() -> resolveGenericMultiInterfaceFactories(templateSource, types, generics)
            else -> null
        }
    }

    private fun FileSpec.Builder.generateMultiInterfaceEntryPoints(
        templateSources: List<TemplateMultiSource>,
    ) {
        templateSources.forEach { source ->
            val factory = buildMultiInterfaceFactory(source)

            if (factory != null) {
                this.addFunction(factory)
            }
        }
    }

    override fun generateCommon(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        totalTemplates: List<TemplateSource>,
        totalMultiSources: List<TemplateMultiSource>,
        dependencies: List<KSFile>,
    ) {
        if (isKmp && (totalTemplates.isNotEmpty() || totalMultiSources.isNotEmpty())) {
            val file = FileSpec.builder(
                rootPackage,
                FACTORY_FILE_NAME,
            )
            val (_, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)

            file.addAnnotation(UNUSED)
            file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)
            file.addImport(NOOP_COLLECTOR.packageName, NOOP_COLLECTOR.simpleName)

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
                originatingKSFiles = dependencies,
            )
        }
    }

    private fun generateShared(
        buckets: Map<String, Pair<List<TemplateSource>, List<TemplateMultiSource>>>,
        dependencies: List<KSFile>,
    ) {
        buckets.forEach { (indicator, generics) ->
            val (singleGenerics, multiGenerics) = generics

            if (singleGenerics.isNotEmpty() || multiGenerics.isNotEmpty()) {
                val file = FileSpec.builder(
                    rootPackage,
                    FACTORY_FILE_NAME,
                )
                file.addAnnotation(UNUSED)

                file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)
                file.addImport(NOOP_COLLECTOR.packageName, NOOP_COLLECTOR.simpleName)

                file.generateGenericEntryPoints(singleGenerics)
                file.generateMultiInterfaceEntryPoints(multiGenerics)

                codeGenerator.setOneTimeSourceSet(indicator)
                file.build().writeTo(
                    codeGenerator = codeGenerator,
                    aggregating = false,
                    originatingKSFiles = dependencies,
                )
            }
        }
    }

    private fun mergeSources(
        singleSources: Map<String, List<TemplateSource>>,
        multiSources: Map<String, List<TemplateMultiSource>>,
    ): Map<String, Pair<List<TemplateSource>, List<TemplateMultiSource>>> {
        val allKeys: Set<String> = singleSources.keys.toMutableSet().also { it.addAll(multiSources.keys) }

        return allKeys.associateWith { key ->
            Pair(
                singleSources[key] ?: emptyList(),
                multiSources[key] ?: emptyList(),
            )
        }
    }

    private fun needsEntryPoint(template: TemplateMultiSource): Boolean {
        return (
            template.hasGenerics() ||
                spyContainer.isSpyable(null, template.packageName, template.templateName)
            )
    }

    override fun generateShared(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        dependencies: List<KSFile>,
    ) {
        if (isKmp && (templateSources.isNotEmpty() || templateMultiSources.isNotEmpty())) {
            val bucketsSingle: MutableMap<String, List<TemplateSource>> = mutableMapOf()
            val bucketsMulti: MutableMap<String, List<TemplateMultiSource>> = mutableMapOf()

            templateSources.forEach { template ->
                if (template.generics != null) {
                    val indicator = template.indicator
                    val bucket = bucketsSingle.getOrElse(indicator) { mutableListOf() }.toMutableList()
                    bucket.add(template)

                    bucketsSingle[indicator] = bucket
                }
            }

            templateMultiSources.forEach { template ->
                if (needsEntryPoint(template)) {
                    val indicator = template.indicator
                    val bucket = bucketsMulti.getOrElse(indicator) { mutableListOf() }.toMutableList()
                    bucket.add(template)

                    bucketsMulti[indicator] = bucket
                }
            }

            generateShared(
                buckets = mergeSources(bucketsSingle, bucketsMulti),
                dependencies = dependencies,
            )
        }
    }
}
