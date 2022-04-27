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
import tech.antibytes.kmock.processor.ProcessorContract.SourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceFilter
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

/*
 * Notice -> No deep checking in order to not drain performance
 */
internal class KMockProcessor(
    private val isKmp: Boolean,
    private val codeGenerator: KmpCodeGenerator,
    private val mockGenerator: MockGenerator,
    private val factoryGenerator: MockFactoryGenerator,
    private val entryPointGenerator: MockFactoryEntryPointGenerator,
    private val sourceAggregator: SourceAggregator,
    private val relaxationAggregator: RelaxationAggregator,
    private val filter: SourceFilter,
) : SymbolProcessor {
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

    private fun stubCommonSources(
        resolver: Resolver,
        relaxer: Relaxer?
    ): Aggregated<TemplateSource> {
        val aggregated = sourceAggregator.extractCommonInterfaces(resolver)

        mockGenerator.writeCommonMocks(
            templateSources = aggregated.extractedTemplates,
            dependencies = aggregated.dependencies,
            relaxer = relaxer
        )

        return aggregated
    }

    private fun stubSharedSources(
        resolver: Resolver,
        commonAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ): Aggregated<TemplateSource> {
        val aggregated = sourceAggregator.extractSharedInterfaces(resolver)
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
        commonAggregated: Aggregated<TemplateSource>,
        sharedAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ): List<KSAnnotated> {
        val aggregated = sourceAggregator.extractPlatformInterfaces(resolver)
        val filteredInterfaces = filter.filter(
            templateSources = aggregated.extractedTemplates,
            filteredBy = sharedAggregated.extractedTemplates
        )
        val totalAggregated = mergeSources(sharedAggregated, aggregated, filteredInterfaces)

        mockGenerator.writePlatformMocks(
            filteredInterfaces,
            aggregated.dependencies,
            relaxer
        )

        factoryGenerator.writeFactories(
            totalAggregated.extractedTemplates,
            totalAggregated.dependencies,
            relaxer
        )

        entryPointGenerator.generateCommon(
            templateSources = commonAggregated.extractedTemplates,
            totalTemplates = totalAggregated.extractedTemplates
        )

        codeGenerator.closeFiles()
        return totalAggregated.illFormed
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
            Pair(NON_KMP_AGGREGATED_SINGLE, NON_KMP_AGGREGATED_SINGLE)
        }
    }

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val relaxer = relaxationAggregator.extractRelaxer(resolver)

        val (commonAggregated, sharedAggregated) = extractMetaSources(resolver, relaxer)

        return stubPlatformAndEntryPointSources(
            resolver = resolver,
            commonAggregated = commonAggregated,
            sharedAggregated = sharedAggregated,
            relaxer = relaxer
        )
    }

    private companion object {
        val NON_KMP_AGGREGATED_SINGLE = Aggregated<TemplateSource>(emptyList(), emptyList(), emptyList())
    }
}
