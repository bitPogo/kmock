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
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.Relaxer as RelaxerAnnotation

/*
 * Notices -> No deep checking in order to no drain performance
 */
internal class KMockProcessor(
    private val mockGenerator: ProcessorContract.MockGenerator,
    private val factoryGenerator: ProcessorContract.MockFactoryGenerator,
    private val aggregator: ProcessorContract.Aggregator,
    private val rootPackage: String,
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

    private fun fetchRelaxerAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            RelaxerAnnotation::class.qualifiedName!!,
            false
        )
    }

    private fun stubPlatformSources(
        resolver: Resolver,
        commonAggregated: Aggregated,
        relaxer: Relaxer?
    ): List<KSAnnotated> {
        val annotated = fetchPlatformAnnotated(resolver)
        val aggregated = aggregator.extractInterfaces(annotated)

        mockGenerator.writePlatformMocks(
            aggregated.extractedInterfaces,
            aggregated.dependencies,
            relaxer
        )

        factoryGenerator.writePlatformFactories(
            rootPackage,
            commonAggregated.extractedInterfaces.toMutableList().also { it.addAll(aggregated.extractedInterfaces) },
            commonAggregated.dependencies.toMutableList().also { it.addAll(aggregated.dependencies) },
            relaxer
        )

        return aggregated.illFormed
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

        factoryGenerator.writeCommonFactories(
            rootPackage,
            aggregated.extractedInterfaces,
            aggregated.dependencies,
            relaxer
        )

        return aggregated
    }

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val relaxer = aggregator.extractRelaxer(fetchRelaxerAnnotated(resolver))

        val commonAggregated = stubCommonSources(resolver, relaxer)
        val platformIll = stubPlatformSources(resolver, commonAggregated, relaxer)

        return platformIll.toMutableList().also {
            it.addAll(commonAggregated.illFormed)
        }
    }
}
