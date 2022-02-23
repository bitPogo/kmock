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
import tech.antibytes.kmock.MagicStub
import tech.antibytes.kmock.MagicStubCommon
import tech.antibytes.kmock.MagicStubRelaxer

/*
 * Notices -> No deep checking in order to no drain performance
 */
internal class KMockProcessor(
    private val stubGenerator: ProcessorContract.StubGenerator,
    private val aggregator: ProcessorContract.Aggregator
) : SymbolProcessor {
    private fun fetchPlatformAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            MagicStub::class.qualifiedName!!,
            false
        )
    }

    private fun fetchCommonAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            MagicStubCommon::class.qualifiedName!!,
            false
        )
    }

    private fun fetchRelaxerAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            MagicStubRelaxer::class.qualifiedName!!,
            false
        )
    }

    private fun stubPlatformSources(resolver: Resolver): List<KSAnnotated> {
        val annotated = fetchPlatformAnnotated(resolver)
        val aggregated = aggregator.extractInterfaces(annotated)

        stubGenerator.writePlatformStubs(aggregated.extractedInterfaces, aggregated.dependencies)

        return aggregated.illFormed
    }

    private fun stubCommonSources(resolver: Resolver): List<KSAnnotated> {
        val annotated = fetchCommonAnnotated(resolver)
        val aggregated = aggregator.extractInterfaces(annotated)

        stubGenerator.writeCommonStubs(aggregated.extractedInterfaces, aggregated.dependencies)

        return aggregated.illFormed
    }

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val relaxer = aggregator.extractRelaxer(fetchRelaxerAnnotated(resolver))

        val platformIll = stubPlatformSources(resolver)
        val commonIll = stubCommonSources(resolver)

        return platformIll.toMutableList().also {
            it.addAll(commonIll)
        }
    }
}
