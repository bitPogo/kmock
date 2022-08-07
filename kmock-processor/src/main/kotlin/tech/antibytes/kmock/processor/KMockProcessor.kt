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
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationContainer
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
    private var sharedAggregated: Aggregated<TemplateSource> = EMPTY_AGGREGATED_SINGLE
    private var platformAggregated: Aggregated<TemplateSource> = EMPTY_AGGREGATED_SINGLE
    private var commonMultiAggregated: Aggregated<TemplateMultiSource> = EMPTY_AGGREGATED_MULTI
    private var sharedMultiAggregated: Aggregated<TemplateMultiSource> = EMPTY_AGGREGATED_MULTI
    private var platformMultiAggregated: Aggregated<TemplateMultiSource> = EMPTY_AGGREGATED_MULTI
    private var totalMultiAggregated: Aggregated<TemplateMultiSource> = EMPTY_AGGREGATED_MULTI
    private var relaxer: Relaxer? = null

    private fun isFinalRound(): Boolean {
        return commonAggregated.extractedTemplates.isEmpty() &&
            sharedAggregated.extractedTemplates.isEmpty() &&
            platformAggregated.extractedTemplates.isEmpty()
    }

    private fun List<KSFile>.merge(toAdd: List<KSFile>): List<KSFile> {
        return this.toMutableSet().also { it.addAll(toAdd) }.toList()
    }

    private fun <T : Source> mergeSources(
        commonSource: Aggregated<T>,
        sharedSource: Aggregated<T>,
        platformSource: Aggregated<T>,
        additionalIllSources: List<KSAnnotated> = emptyList(),
    ): Aggregated<T> {
        return Aggregated(
            illFormed = listOf(
                commonSource.illFormed,
                sharedSource.illFormed,
                platformSource.illFormed,
                additionalIllSources,
            ).flatten(),
            extractedTemplates = listOf(
                commonSource.extractedTemplates,
                sharedSource.extractedTemplates,
                platformSource.extractedTemplates,
            ).flatten(),
            totalDependencies = listOf(
                commonSource.totalDependencies,
                sharedSource.totalDependencies,
                platformSource.totalDependencies,
            ).flatten(),
        )
    }

    private fun <T : Source> mergeSources(
        rootSource: Aggregated<T>,
        dependentSource: Aggregated<T>,
        additionalIllSources: List<KSAnnotated> = emptyList(),
    ): Aggregated<T> {
        return Aggregated(
            illFormed = listOf(rootSource.illFormed, dependentSource.illFormed, additionalIllSources).flatten(),
            extractedTemplates = listOf(rootSource.extractedTemplates, dependentSource.extractedTemplates).flatten(),
            totalDependencies = listOf(rootSource.totalDependencies, dependentSource.totalDependencies).flatten(),
        )
    }

    private fun resolveCommonAggregated(
        singleCommonSources: Aggregated<TemplateSource>,
        multiCommonSources: Aggregated<TemplateMultiSource>,
    ): Aggregated<TemplateSource> {
        return when {
            multiCommonSources.extractedTemplates.isEmpty() && totalMultiAggregated.extractedTemplates.isEmpty() -> {
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
                    additionalIllSources = commonMultiAggregated.illFormed,
                )
            }
        }
    }

    private fun interfaceBinderIsApplicable(): Boolean {
        return totalMultiAggregated.extractedTemplates.isNotEmpty() && isFirstRound
    }

    private fun stubCommonSources(
        resolver: Resolver,
        kmockSingleAnnotated: List<KSAnnotated>,
        kmockMultiAnnotated: List<KSAnnotated>,
        relaxer: Relaxer?,
    ): Aggregated<TemplateSource> {
        val singleCommonSources = singleSourceAggregator.extractCommonInterfaces(kmockSingleAnnotated, resolver)
        val multiCommonSources = multiSourceAggregator.extractCommonInterfaces(kmockMultiAnnotated, resolver)

        mockGenerator.writeCommonMocks(
            templateSources = singleCommonSources.extractedTemplates,
            templateMultiSources = this.commonMultiAggregated.extractedTemplates,
            relaxer = relaxer,
        )

        return resolveCommonAggregated(
            singleCommonSources = singleCommonSources,
            multiCommonSources = multiCommonSources,
        )
    }

    private fun resolveSharedAggregated(
        singleSharedSources: Aggregated<TemplateSource>,
        multiSharedSources: Aggregated<TemplateMultiSource>,
    ): Aggregated<TemplateSource> {
        return when {
            multiSharedSources.extractedTemplates.isEmpty() && totalMultiAggregated.extractedTemplates.isEmpty() -> {
                singleSharedSources
            }
            multiSharedSources.extractedTemplates.isNotEmpty() -> {
                sharedAggregated = singleSharedSources
                sharedMultiAggregated = multiSharedSources

                singleSharedSources
            }
            else -> {
                val sharedAggregated = sharedAggregated
                this.sharedAggregated = EMPTY_AGGREGATED_SINGLE

                mergeSources(
                    rootSource = sharedAggregated,
                    dependentSource = singleSharedSources,
                    additionalIllSources = sharedMultiAggregated.illFormed,
                )
            }
        }
    }

    private fun stubSharedSources(
        resolver: Resolver,
        kmockSingleAnnotated: Map<String, List<KSAnnotated>>,
        kmockMultiAnnotated: Map<String, List<KSAnnotated>>,
        commonAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?,
    ): Aggregated<TemplateSource> {
        val singleAggregated = singleSourceAggregator.extractSharedInterfaces(kmockSingleAnnotated, resolver)
        val multiAggregated = multiSourceAggregator.extractSharedInterfaces(kmockMultiAnnotated, resolver)

        val filteredSingleInterfaces = filter.filter(
            templateSources = filter.filterByDependencies(singleAggregated.extractedTemplates),
            filteredBy = commonAggregated.extractedTemplates,
        )

        val filteredMultiInterfaces = filter.filter(
            templateSources = filter.filterByDependencies(multiAggregated.extractedTemplates),
            filteredBy = commonMultiAggregated.extractedTemplates,
        )

        mockGenerator.writeSharedMocks(
            templateSources = filteredSingleInterfaces,
            templateMultiSources = sharedMultiAggregated.extractedTemplates,
            relaxer = relaxer,
        )

        return resolveSharedAggregated(
            singleAggregated.copy(extractedTemplates = filteredSingleInterfaces),
            multiAggregated.copy(extractedTemplates = filteredMultiInterfaces),
        )
    }

    private fun resolvePlatformAggregated(
        singlePlatformSources: Aggregated<TemplateSource>,
        multiPlatformSources: Aggregated<TemplateMultiSource>,
    ): Aggregated<TemplateSource> {
        return when {
            multiPlatformSources.extractedTemplates.isEmpty() && totalMultiAggregated.extractedTemplates.isEmpty() -> {
                singlePlatformSources
            }
            multiPlatformSources.extractedTemplates.isNotEmpty() -> {
                platformAggregated = singlePlatformSources
                platformMultiAggregated = multiPlatformSources

                singlePlatformSources
            }
            else -> {
                val platformAggregated = platformAggregated
                this.platformAggregated = EMPTY_AGGREGATED_SINGLE

                mergeSources(
                    rootSource = platformAggregated,
                    dependentSource = singlePlatformSources,
                    additionalIllSources = platformMultiAggregated.illFormed,
                )
            }
        }
    }

    private fun stubPlatformSources(
        resolver: Resolver,
        kmockSingleAnnotated: List<KSAnnotated>,
        kmockMultiAnnotated: List<KSAnnotated>,
        commonAggregated: Aggregated<TemplateSource>,
        sharedAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?,
    ): Aggregated<TemplateSource> {
        val singleAggregated = singleSourceAggregator.extractPlatformInterfaces(kmockSingleAnnotated, resolver)
        val multiAggregated = multiSourceAggregator.extractPlatformInterfaces(kmockMultiAnnotated, resolver)

        val filteredSingleInterfaces = filter.filter(
            templateSources = singleAggregated.extractedTemplates,
            filteredBy = listOf(
                commonAggregated.extractedTemplates,
                sharedAggregated.extractedTemplates,
            ).flatten(),
        )

        val filteredMultiInterfaces = filter.filter(
            templateSources = multiAggregated.extractedTemplates,
            filteredBy = listOf(
                commonMultiAggregated.extractedTemplates,
                sharedMultiAggregated.extractedTemplates,
            ).flatten(),
        )

        mockGenerator.writePlatformMocks(
            templateSources = filteredSingleInterfaces,
            templateMultiSources = platformMultiAggregated.extractedTemplates,
            relaxer = relaxer,
        )

        return resolvePlatformAggregated(
            singleAggregated.copy(extractedTemplates = filteredSingleInterfaces),
            multiAggregated.copy(extractedTemplates = filteredMultiInterfaces),
        )
    }

    private fun extractMetaSources(
        resolver: Resolver,
        kmockSingleAnnotated: AnnotationContainer,
        kmockMultiAnnotated: AnnotationContainer,
        relaxer: Relaxer?,
    ): Pair<Aggregated<TemplateSource>, Aggregated<TemplateSource>> {
        return if (isKmp) {
            val commonAggregated = stubCommonSources(
                resolver = resolver,
                kmockSingleAnnotated = kmockSingleAnnotated.common,
                kmockMultiAnnotated = kmockMultiAnnotated.common,
                relaxer = relaxer,
            )
            val sharedAggregated = stubSharedSources(
                resolver = resolver,
                kmockSingleAnnotated = kmockSingleAnnotated.shared,
                kmockMultiAnnotated = kmockMultiAnnotated.shared,
                commonAggregated = commonAggregated,
                relaxer = relaxer,
            )

            Pair(commonAggregated, sharedAggregated)
        } else {
            Pair(EMPTY_AGGREGATED_SINGLE, EMPTY_AGGREGATED_SINGLE)
        }
    }

    private fun writeFactories(
        commonAggregated: Aggregated<TemplateSource>,
        sharedAggregated: Aggregated<TemplateSource>,
        totalAggregated: Aggregated<TemplateSource>,
        relaxer: Relaxer?,
    ) {
        if (isFirstRound) {
            val totalDependencies = totalAggregated.totalDependencies.merge(totalMultiAggregated.totalDependencies)

            factoryGenerator.writeFactories(
                templateSources = totalAggregated.extractedTemplates,
                templateMultiSources = totalMultiAggregated.extractedTemplates,
                relaxer = relaxer,
                dependencies = totalDependencies,
            )

            if (isKmp) {
                entryPointGenerator.generateCommon(
                    templateSources = commonAggregated.extractedTemplates,
                    templateMultiSources = commonMultiAggregated.extractedTemplates,
                    totalTemplates = totalAggregated.extractedTemplates,
                    totalMultiSources = totalMultiAggregated.extractedTemplates,
                    dependencies = totalDependencies,
                )

                entryPointGenerator.generateShared(
                    templateSources = sharedAggregated.extractedTemplates,
                    templateMultiSources = sharedMultiAggregated.extractedTemplates,
                    dependencies = listOf(
                        sharedAggregated.totalDependencies,
                        sharedMultiAggregated.totalDependencies,
                    ).flatten(),
                )
            }
        }
    }

    private fun finalize() {
        isFirstRound = false

        if (isFinalRound()) {
            codeGenerator.closeFiles()
        }
    }

    private fun writeIntermediateInterfaces() {
        if (interfaceBinderIsApplicable()) {
            interfaceGenerator.bind(
                totalMultiAggregated.extractedTemplates,
                totalMultiAggregated.totalDependencies,
            )
        }
    }

    private fun extractRelaxer(resolver: Resolver): Relaxer? {
        val relaxer = relaxationAggregator.extractRelaxer(resolver) ?: this.relaxer
        this.relaxer = relaxer

        return relaxer
    }

    private fun extractTotal(
        commonAggregated: Aggregated<TemplateSource>,
        sharedAggregated: Aggregated<TemplateSource>,
        platformAggregated: Aggregated<TemplateSource>,
    ): Aggregated<TemplateSource> {
        totalMultiAggregated = mergeSources(
            commonSource = commonMultiAggregated,
            sharedSource = sharedMultiAggregated,
            platformSource = platformMultiAggregated,
        )

        return mergeSources(
            commonSource = commonAggregated,
            sharedSource = sharedAggregated,
            platformSource = platformAggregated,
            additionalIllSources = totalMultiAggregated.illFormed,
        )
    }

    private fun extractKmockAnnotated(resolver: Resolver): Pair<AnnotationContainer, AnnotationContainer> {
        return Pair(
            singleSourceAggregator.extractKmockInterfaces(resolver),
            multiSourceAggregator.extractKmockInterfaces(resolver),
        )
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val relaxer = extractRelaxer(resolver)
        val (kmockSingleAnnotated, kmockMultiAnnotated) = extractKmockAnnotated(resolver)

        val (commonAggregated, sharedAggregated) = extractMetaSources(
            resolver = resolver,
            kmockSingleAnnotated = kmockSingleAnnotated,
            kmockMultiAnnotated = kmockMultiAnnotated,
            relaxer = relaxer,
        )

        val platformAggregated = stubPlatformSources(
            resolver = resolver,
            kmockSingleAnnotated = kmockSingleAnnotated.platform,
            kmockMultiAnnotated = kmockMultiAnnotated.platform,
            commonAggregated = commonAggregated,
            sharedAggregated = sharedAggregated,
            relaxer = relaxer,
        )

        val totalAggregated = extractTotal(
            commonAggregated = commonAggregated,
            sharedAggregated = sharedAggregated,
            platformAggregated = platformAggregated,
        )

        writeIntermediateInterfaces()

        writeFactories(
            commonAggregated = commonAggregated,
            sharedAggregated = sharedAggregated,
            totalAggregated = totalAggregated,
            relaxer = relaxer,
        )

        finalize()
        return totalAggregated.illFormed
    }

    private companion object {
        val EMPTY_AGGREGATED_SINGLE = Aggregated<TemplateSource>(emptyList(), emptyList(), emptyList())
        val EMPTY_AGGREGATED_MULTI = Aggregated<TemplateMultiSource>(emptyList(), emptyList(), emptyList())
    }
}
