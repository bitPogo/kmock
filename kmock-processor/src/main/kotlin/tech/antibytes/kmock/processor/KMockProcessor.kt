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
import com.google.devtools.ksp.symbol.KSFile
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
    private var isFirstRound: Boolean = true
    private var commonAggregated: Aggregated<TemplateSource> = EMPTY_AGGREGATED_SINGLE
    private var commonMultiAggregated: Aggregated<TemplateMultiSource> = EMPTY_AGGREGATED_MULTI
    private var relaxer: Relaxer? = null

    private fun isFinalRound(): Boolean {
        return commonAggregated.extractedTemplates.isEmpty()
    }

    private fun List<KSFile>.merge(toAdd: List<KSFile>): List<KSFile> {
        return this.toMutableSet().also { it.addAll(toAdd) }.toList()
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
            totalDependencies = rootSource.totalDependencies.merge(dependentSource.totalDependencies)
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

    private fun interfaceBinderIsApplicable(
        templateSources: List<TemplateMultiSource>
    ): Boolean {
        return templateSources.isNotEmpty() &&
            commonMultiAggregated.extractedTemplates.isEmpty() // TODO - Test Concern see: https://github.com/tschuchortdev/kotlin-compile-testing/issues/263
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
            relaxer = relaxer,
        )

        if (interfaceBinderIsApplicable(multiCommonSources.extractedTemplates)) {
            interfaceGenerator.bind(
                templateSources = multiCommonSources.extractedTemplates,
                dependencies = multiCommonSources.totalDependencies,
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
            templateSources = filter.filterSharedSources(aggregated.extractedTemplates),
            filteredBy = commonAggregated.extractedTemplates
        )

        entryPointGenerator.generateShared(
            templateSources = filteredInterfaces,
            dependencies = aggregated.totalDependencies
        )

        mockGenerator.writeSharedMocks(
            templateSources = filteredInterfaces,
            relaxer = relaxer
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
            templateSources = aggregated.extractedTemplates,
            filteredBy = sharedAggregated.extractedTemplates
        )
        val totalAggregated = mergeSources(
            rootSource = sharedAggregated,
            dependentSource = aggregated,
            filteredTemplates = filteredInterfaces,
        )

        mockGenerator.writePlatformMocks(
            templateSources = filteredInterfaces,
            relaxer = relaxer,
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
            Pair(EMPTY_AGGREGATED_SINGLE, EMPTY_AGGREGATED_SINGLE)
        }
    }

    private fun writeFactories(
        commonAggregated: Aggregated<TemplateSource>,
        totalAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?
    ) {
        if (isFirstRound) {
            val totalDependencies = totalAggregated.totalDependencies.merge(commonMultiAggregated.totalDependencies)

            factoryGenerator.writeFactories(
                templateSources = totalAggregated.extractedTemplates,
                templateMultiSources = commonMultiAggregated.extractedTemplates,
                relaxer = relaxer,
                dependencies = totalDependencies
            )

            entryPointGenerator.generateCommon(
                templateSources = commonAggregated.extractedTemplates,
                templateMultiSources = commonMultiAggregated.extractedTemplates,
                totalTemplates = totalAggregated.extractedTemplates,
                totalMultiSources = commonMultiAggregated.extractedTemplates,
                dependencies = totalDependencies,
            )
        }
    }

    private fun finalize() {
        isFirstRound = false

        if (isFinalRound()) {
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

        writeFactories(
            commonAggregated = commonAggregated,
            totalAggregated = totalAggregated,
            relaxer = relaxer
        )

        finalize()
        return totalAggregated.illFormed
    }

    private companion object {
        val EMPTY_AGGREGATED_SINGLE = Aggregated<TemplateSource>(emptyList(), emptyList(), emptyList())
        val EMPTY_AGGREGATED_MULTI = Aggregated<TemplateMultiSource>(emptyList(), emptyList(), emptyList())
    }
}
