/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockGenerator
import tech.antibytes.kmock.processor.ProcessorContract.RelaxationAggregator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.Source
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SingleSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceFilter
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.MultiInterfaceBinder

/*
 * Notice -> No deep checking in order to not drain performance
 */
internal class KMockProcessor(
    private val isKmp: Boolean,
    private val codeGenerator: KmpCodeGenerator,
    private val interfazeGenerator: MultiInterfaceBinder,
    private val mockGenerator: MockGenerator,
    private val factoryGenerator: MockFactoryGenerator,
    private val entryPointGenerator: MockFactoryEntryPointGenerator,
    private val multiSourceAggregator: MultiSourceAggregator,
    private val singleSourceAggregator: SingleSourceAggregator,
    private val relaxationAggregator: RelaxationAggregator,
    private val filter: SourceFilter,
) : SymbolProcessor {
    private var commonAggregated: Aggregated<TemplateSource> = EMPTY_AGGREGATED_SINGLE
    private var commonMultiAggregated: Aggregated<TemplateMultiSource> = EMPTY_AGGREGATED_MULTI

    private fun <T : Source> mergeSources(
        rootSource: Aggregated<T>,
        dependentSource: Aggregated<T>,
        filteredTemplates: List<T>
    ): Aggregated<T> {
        return Aggregated(
            illFormed = rootSource.illFormed.toMutableList().also {
                it.addAll(dependentSource.illFormed)
            },
            extractedTemplates = rootSource.extractedTemplates.toMutableList().also {
                it.addAll(filteredTemplates)
            },
            dependencies = rootSource.dependencies.toMutableList().also {
                it.addAll(dependentSource.dependencies)
            }
        )
    }

    private fun <T : Source> mergeSources(
        rootSource: Aggregated<T>,
        dependentSource: Aggregated<T>,
    ): Aggregated<T> {
        return mergeSources(
            rootSource = rootSource,
            dependentSource = dependentSource,
            filteredTemplates = dependentSource.extractedTemplates
        )
    }

    private fun resolveCommonAggregated(
        singleCommonSources: Aggregated<TemplateSource>,
        multiCommonSources: Aggregated<TemplateMultiSource>
    ): Aggregated<TemplateSource> {
        return if (multiCommonSources.extractedTemplates.isNotEmpty()) {
            commonAggregated = singleCommonSources
            commonMultiAggregated = multiCommonSources

            singleCommonSources
        } else {
            val commonAggregated = commonAggregated
            this.commonAggregated = EMPTY_AGGREGATED_SINGLE

            commonAggregated
        }
    }

    private fun stubCommonSources(
        resolver: Resolver,
        relaxer: Relaxer?
    ): Aggregated<TemplateSource> {
        val singleAggregated = singleSourceAggregator.extractCommonInterfaces(resolver)
        val multiAggregated = multiSourceAggregator.extractCommonInterfaces(resolver)

        mockGenerator.writeCommonMocks(
            templateSources = singleAggregated.extractedTemplates,
            templateMultiSources = commonMultiAggregated,
            dependencies = singleAggregated.dependencies,
            relaxer = relaxer
        )
        interfazeGenerator.bind(
            templateSources = multiAggregated.extractedTemplates,
            dependencies = multiAggregated.dependencies
        )

        return resolveCommonAggregated(
            singleCommonSources = singleAggregated,
            multiCommonSources = multiAggregated
        )
    }

    private fun stubSharedSources(
        resolver: Resolver,
        commonAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ): Aggregated<TemplateSource> {
        val aggregated = singleSourceAggregator.extractSharedInterfaces(resolver)
        val filteredInterfaces = filter.filter(
            filter.filterSharedSources(aggregated.extractedTemplates),
            commonAggregated.extractedTemplates
        )

        entryPointGenerator.generateShared(filteredInterfaces)

        mockGenerator.writeSharedMocks(
            templateSources = filteredInterfaces,
            dependencies = aggregated.dependencies,
            relaxer = relaxer
        )

        return mergeSources(commonAggregated, aggregated, filteredInterfaces)
    }

    private fun stubPlatformAndEntryPointSources(
        resolver: Resolver,
        sharedAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ): Aggregated<TemplateSource> {
        val aggregated = singleSourceAggregator.extractPlatformInterfaces(resolver)
        val filteredInterfaces = filter.filter(
            templateSources = aggregated.extractedTemplates,
            filteredBy = sharedAggregated.extractedTemplates
        )

        mockGenerator.writePlatformMocks(
            filteredInterfaces,
            aggregated.dependencies,
            relaxer
        )

        return mergeSources(sharedAggregated, aggregated, filteredInterfaces)
    }

    private fun extractMetaSources(
        resolver: Resolver,
        relaxer: Relaxer?
    ): Pair<Aggregated<TemplateSource>, Aggregated<TemplateSource>> {
        return if (isKmp) {
            val commonAggregated = stubCommonSources(resolver, relaxer)
            val sharedAggregated = stubSharedSources(resolver, commonAggregated, relaxer)

            Pair(commonAggregated, sharedAggregated)
        } else {
            Pair(EMPTY_AGGREGATED_SINGLE, EMPTY_AGGREGATED_SINGLE)
        }
    }

    private fun generateFactories(
        commonAggregated: Aggregated<TemplateSource>,
        sharedAggregated: Aggregated<TemplateSource>,
        platformAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ): List<KSAnnotated> {
        val totalAggregated = mergeSources(
            sharedAggregated,
            platformAggregated,
        )

        if (this.commonAggregated.extractedTemplates.isEmpty()) {
            factoryGenerator.writeFactories(
                templateSources = totalAggregated.extractedTemplates,
                templateMultiSources = commonMultiAggregated,
                dependencies = totalAggregated.dependencies,
                relaxer = relaxer
            )

            entryPointGenerator.generateCommon(
                templateSources = commonAggregated.extractedTemplates,
                templateMultiSources = commonMultiAggregated,
                totalTemplates = totalAggregated.extractedTemplates
            )
        }

        return totalAggregated.illFormed
    }

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val relaxer = relaxationAggregator.extractRelaxer(resolver)

        val (commonAggregated, sharedAggregated) = extractMetaSources(resolver, relaxer)
        val platformAggregated = stubPlatformAndEntryPointSources(
            resolver = resolver,
            sharedAggregated = sharedAggregated,
            relaxer = relaxer
        )

        val illAnnotated = generateFactories(
            commonAggregated = commonAggregated,
            sharedAggregated = sharedAggregated,
            platformAggregated = platformAggregated,
            relaxer = relaxer
        )

        codeGenerator.closeFiles()

        return illAnnotated
    }

    private companion object {
        val EMPTY_AGGREGATED_SINGLE = Aggregated<TemplateSource>(emptyList(), emptyList(), emptyList())
        val EMPTY_AGGREGATED_MULTI = Aggregated<TemplateMultiSource>(emptyList(), emptyList(), emptyList())
    }
}
