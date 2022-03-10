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
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.Relaxer as RelaxerAnnotation

/*
 * Notices -> No deep checking in order to no drain performance
 */
internal class KMockProcessor(
    private val mockGenerator: ProcessorContract.MockGenerator,
    private val factoryGenerator: ProcessorContract.MockFactoryGenerator,
    private val entryPointGenerator: ProcessorContract.MockFactoryCommonEntryPointGenerator,
    private val aggregator: ProcessorContract.Aggregator,
    private val options: ProcessorContract.Options,
    private val filter: ProcessorContract.SourceFilter,
) : SymbolProcessor {
    private fun fetchPlatformAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            Mock::class.qualifiedName!!,
            false
        )
    }

    private fun fetchCommonAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            MockCommon::class.qualifiedName!!,
            false
        )
    }

    private fun fetchSharedAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            MockShared::class.qualifiedName!!,
            false
        )
    }

    private fun fetchRelaxerAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            RelaxerAnnotation::class.qualifiedName!!,
            false
        )
    }

    private fun mergeSources(
        rootSource: Aggregated,
        dependentSource: Aggregated,
        filteredInterfaces: List<ProcessorContract.InterfaceSource>
    ): Aggregated {
        return Aggregated(
            illFormed = rootSource.illFormed.toMutableList().also {
                it.addAll(dependentSource.illFormed)
            },
            extractedInterfaces = rootSource.extractedInterfaces.toMutableList().also {
                it.addAll(filteredInterfaces)
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
            aggregated.extractedInterfaces,
            aggregated.dependencies,
            relaxer
        )

        entryPointGenerator.generate(
            options,
            aggregated.extractedInterfaces
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
            filter.filterSharedSources(aggregated.extractedInterfaces),
            commonAggregated.extractedInterfaces
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
            aggregated.extractedInterfaces,
            sharedAggregated.extractedInterfaces
        )
        val totalAggregated = mergeSources(sharedAggregated, aggregated, filteredInterfaces)

        mockGenerator.writePlatformMocks(
            filteredInterfaces,
            aggregated.dependencies,
            relaxer
        )

        factoryGenerator.writeFactories(
            options,
            totalAggregated.extractedInterfaces,
            totalAggregated.dependencies,
            relaxer
        )

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
