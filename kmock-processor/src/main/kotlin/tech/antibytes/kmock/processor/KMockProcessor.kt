/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MultiInterfaceBinder
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.RelaxationAggregator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.SingleSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.Source
import tech.antibytes.kmock.processor.ProcessorContract.SourceFilter
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

/*
 * Notice -> No deep checking in order to not drain performance
 */
internal class KMockProcessor(
    private val logger: KSPLogger,
    private val isKmp: Boolean,
    private val codeGenerator: KmpCodeGenerator,
    private val interfaceGenerator: MultiInterfaceBinder,
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
    private var relaxer: Relaxer? = null

    private fun isFinalRound(): Boolean {
        return commonAggregated.extractedTemplates.isEmpty()
    }

    private fun <T : Source> mergeSources(
        rootSource: Aggregated<T>,
        dependentSource: Aggregated<T>,
        filteredTemplates: List<T>,
        additionalIllSources: List<KSAnnotated> = emptyList()
    ): Aggregated<T> {
        return Aggregated(
            illFormed = listOf(rootSource.illFormed, dependentSource.illFormed, additionalIllSources).flatten(),
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
        additionalIllSources: List<KSAnnotated>
    ): Aggregated<T> {
        return mergeSources(
            rootSource = rootSource,
            dependentSource = dependentSource,
            filteredTemplates = dependentSource.extractedTemplates,
            additionalIllSources = additionalIllSources
        )
    }

    private fun resolveCommonAggregated(
        singleCommonSources: Aggregated<TemplateSource>,
        multiCommonSources: Aggregated<TemplateMultiSource>
    ): Aggregated<TemplateSource> {
        return when {
            multiCommonSources.extractedTemplates.isEmpty() && commonMultiAggregated.extractedTemplates.isEmpty() -> {
                singleCommonSources
            }
            multiCommonSources.extractedTemplates.isNotEmpty() -> {
                commonAggregated = singleCommonSources
                commonMultiAggregated = multiCommonSources

                singleCommonSources
            }
            else -> {
                val commonAggregated = commonAggregated
                this.commonAggregated = EMPTY_AGGREGATED_SINGLE

                mergeSources(
                    rootSource = commonAggregated,
                    dependentSource = singleCommonSources,
                    additionalIllSources = commonMultiAggregated.illFormed
                )
            }
        }
    }

    private fun stubCommonSources(
        resolver: Resolver,
        relaxer: Relaxer?
    ): Aggregated<TemplateSource> {
        val singleCommonSources = singleSourceAggregator.extractCommonInterfaces(resolver)
        val multiCommonSources = multiSourceAggregator.extractCommonInterfaces(resolver)

        mockGenerator.writeCommonMocks(
            templateSources = singleCommonSources.extractedTemplates,
            templateMultiSources = commonMultiAggregated,
            dependencies = singleCommonSources.dependencies,
            relaxer = relaxer,
        )

        if (multiCommonSources.extractedTemplates.isNotEmpty()) {
            interfaceGenerator.bind(
                templateSources = multiCommonSources.extractedTemplates,
                dependencies = multiCommonSources.dependencies
            )
        }

        return resolveCommonAggregated(
            singleCommonSources = singleCommonSources,
            multiCommonSources = multiCommonSources,
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

        entryPointGenerator.generateShared(
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
        sharedAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ): Aggregated<TemplateSource> {
        val aggregated = singleSourceAggregator.extractPlatformInterfaces(resolver)
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

        return totalAggregated
    }

    private fun extractMetaSources(
        resolver: Resolver,
        relaxer: Relaxer?
    ): Pair<Aggregated<TemplateSource>, Aggregated<TemplateSource>> {
        return if (isKmp) {
            val commonAggregated = stubCommonSources(resolver, relaxer)
            val sharedAggregated = stubSharedSources(
                resolver = resolver,
                commonAggregated = commonAggregated,
                relaxer = relaxer
            )

            Pair(commonAggregated, sharedAggregated)
        } else {
            Pair(
                Aggregated(emptyList(), emptyList(), emptyList()),
                Aggregated(emptyList(), emptyList(), emptyList())
            )
        }
    }

    private fun writeFactories(
        commonAggregated: Aggregated<TemplateSource>,
        totalAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ) {
        factoryGenerator.writeFactories(
            totalAggregated.extractedTemplates,
            totalAggregated.dependencies,
            relaxer
        )

        entryPointGenerator.generateCommon(
            templateSources = commonAggregated.extractedTemplates,
            totalTemplates = totalAggregated.extractedTemplates
        )
    }

    private fun finalize(
        commonAggregated: Aggregated<TemplateSource>,
        totalAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ) {
        if (isFinalRound()) {
            writeFactories(
                commonAggregated = commonAggregated,
                totalAggregated = totalAggregated,
                relaxer = relaxer
            )

            codeGenerator.closeFiles()
        }
    }

    private fun extractRelaxer(resolver: Resolver): Relaxer? {
        val relaxer = relaxationAggregator.extractRelaxer(resolver) ?: this.relaxer
        this.relaxer = relaxer

        return relaxer
    }

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val relaxer = extractRelaxer(resolver)

        val (commonAggregated, sharedAggregated) = extractMetaSources(resolver, relaxer)

        val totalAggregated = stubPlatformSources(
            resolver = resolver,
            sharedAggregated = sharedAggregated,
            relaxer = relaxer
        )

        finalize(
            commonAggregated = commonAggregated,
            totalAggregated = totalAggregated,
            relaxer = relaxer
        )

        return totalAggregated.illFormed
    }

    private companion object {
        val EMPTY_AGGREGATED_SINGLE = Aggregated<TemplateSource>(emptyList(), emptyList(), emptyList())
        val EMPTY_AGGREGATED_MULTI = Aggregated<TemplateMultiSource>(emptyList(), emptyList(), emptyList())
    }
}
