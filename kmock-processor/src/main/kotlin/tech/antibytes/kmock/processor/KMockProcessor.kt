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
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.RELAXATION_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

/*
 * Notices -> No deep checking in order to no drain performance
 */
internal class KMockProcessor(
    private val codeGenerator: ProcessorContract.KmpCodeGenerator,
    private val mockGenerator: ProcessorContract.MockGenerator,
    private val factoryGenerator: ProcessorContract.MockFactoryGenerator,
    private val entryPointGenerator: ProcessorContract.MockFactoryEntryPointGenerator,
    private val aggregator: ProcessorContract.Aggregator,
    private val options: ProcessorContract.Options,
    private val filter: ProcessorContract.SourceFilter,
) : SymbolProcessor {
    private fun fetchPlatformAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ANNOTATION_PLATFORM_NAME,
            false
        )
    }

    private fun fetchCommonAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ANNOTATION_COMMON_NAME,
            false
        )
    }

    private fun fetchSharedAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            ANNOTATION_SHARED_NAME,
            false
        )
    }

    private fun fetchRelaxerAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            RELAXATION_NAME,
            false
        )
    }

    private fun mergeSources(
        rootSource: Aggregated,
        dependentSource: Aggregated,
        filteredTemplates: List<ProcessorContract.TemplateSource>
    ): Aggregated {
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
    ): Aggregated {
        val annotated = fetchCommonAnnotated(resolver)
        val aggregated = aggregator.extractInterfaces(annotated)

        mockGenerator.writeCommonMocks(
            aggregated.extractedTemplates,
            aggregated.dependencies,
            relaxer
        )

        entryPointGenerator.generateCommon(
            options,
            aggregated.extractedTemplates
        )

        return aggregated
    }

    private fun stubSharedSources(
        resolver: Resolver,
        commonAggregated: Aggregated,
        relaxer: Relaxer?
    ): Aggregated {
        val annotated = fetchSharedAnnotated(resolver)
        val aggregated = aggregator.extractInterfaces(annotated)
        val filteredInterfaces = filter.filter(
            filter.filterSharedSources(aggregated.extractedTemplates),
            commonAggregated.extractedTemplates
        )

        entryPointGenerator.generateShared(
            options,
            filteredInterfaces,
        )

        mockGenerator.writeSharedMocks(
            filteredInterfaces,
            aggregated.dependencies,
            relaxer
        )

        return mergeSources(commonAggregated, aggregated, filteredInterfaces)
    }

    private fun stubPlatformSources(
        resolver: Resolver,
        sharedAggregated: Aggregated,
        relaxer: Relaxer?
    ): List<KSAnnotated> {
        val annotated = fetchPlatformAnnotated(resolver)
        val aggregated = aggregator.extractInterfaces(annotated)
        val filteredInterfaces = filter.filter(
            aggregated.extractedTemplates,
            sharedAggregated.extractedTemplates
        )
        val totalAggregated = mergeSources(sharedAggregated, aggregated, filteredInterfaces)

        mockGenerator.writePlatformMocks(
            filteredInterfaces,
            aggregated.dependencies,
            relaxer
        )

        factoryGenerator.writeFactories(
            options,
            totalAggregated.extractedTemplates,
            totalAggregated.dependencies,
            relaxer
        )

        codeGenerator.closeFiles()
        return totalAggregated.illFormed
    }

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val relaxer = aggregator.extractRelaxer(fetchRelaxerAnnotated(resolver))

        val sharedAggregated = if (options.isKmp) {
            val commonAggregated = stubCommonSources(resolver, relaxer)
            stubSharedSources(resolver, commonAggregated, relaxer)
        } else {
            Aggregated(emptyList(), emptyList(), emptyList())
        }

        return stubPlatformSources(
            resolver,
            sharedAggregated,
            relaxer
        )
    }
}
