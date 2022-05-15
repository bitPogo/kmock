/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MultiInterfaceBinder
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.RelaxationAggregator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.SingleSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.Source
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class KMockProcessorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils SymbolProcessor`() {
        KMockProcessor(
            mockk(),
            fixture.fixture(),
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
        ) fulfils SymbolProcessor::class
    }

    @Test
    fun `Given process is called it returns all aggregated and merges illegal sources`() {
        // Given
        val resolver: Resolver = mockk()
        val singleSourceAggregator: SingleSourceAggregator = mockk()
        val multiSourceAggregator: MultiSourceAggregator = mockk()
        val relaxationAggregator: RelaxationAggregator = mockk()

        val mockGenerator: MockGenerator = mockk()
        val factoryGenerator: MockFactoryGenerator = mockk()
        val entryPointGenerator: MockFactoryEntryPointGenerator = mockk()

        val illegalCommon: List<KSAnnotated> = listOf(mockk())
        val illegalShared: List<KSAnnotated> = listOf(mockk())
        val illegalPlatform: List<KSAnnotated> = listOf(mockk())

        every {
            singleSourceAggregator.extractCommonInterfaces(any())
        } returns Aggregated(illegalCommon, listOf(mockk()), listOf(mockk()))

        every {
            multiSourceAggregator.extractCommonInterfaces(any())
        } returns Aggregated(emptyList(), emptyList(), emptyList())

        every {
            singleSourceAggregator.extractSharedInterfaces(any())
        } returns Aggregated(illegalShared, listOf(mockk()), listOf(mockk()))

        every {
            multiSourceAggregator.extractSharedInterfaces(any())
        } returns Aggregated(emptyList(), emptyList(), emptyList())

        every {
            singleSourceAggregator.extractPlatformInterfaces(any())
        } returns Aggregated(illegalPlatform, listOf(mockk()), listOf(mockk()))

        every { relaxationAggregator.extractRelaxer(any()) } returns mockk()

        every { mockGenerator.writeCommonMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writeSharedMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writePlatformMocks(any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateCommon(any(), any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateShared(any(), any(), any()) } just Runs

        // When
        val actual = KMockProcessor(
            mockk(),
            false,
            true,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockGenerator,
            factoryGenerator,
            entryPointGenerator,
            multiSourceAggregator,
            singleSourceAggregator,
            relaxationAggregator,
            mockk(relaxed = true),
        ).process(resolver)

        // Then
        actual mustBe listOf(
            illegalCommon.first(),
            illegalShared.first(),
            illegalPlatform.first(),
        )
    }

    @Test
    fun `Given process is called it returns all aggregated and merges illegal sources in a multi interface setup`() {
        // Given
        val resolver: Resolver = mockk()
        val singleSourceAggregator: SingleSourceAggregator = mockk()
        val multiSourceAggregator: MultiSourceAggregator = mockk()
        val relaxationAggregator: RelaxationAggregator = mockk()

        val mockGenerator: MockGenerator = mockk()
        val factoryGenerator: MockFactoryGenerator = mockk()
        val entryPointGenerator: MockFactoryEntryPointGenerator = mockk()

        val filter: ProcessorContract.SourceFilter = mockk()

        val illegalCommonRound1: List<KSAnnotated> = listOf(mockk())
        val illegalCommonRound2: List<KSAnnotated> = listOf(mockk())
        val illegalMultiCommon: List<KSAnnotated> = listOf(mockk())
        val illegalSharedRound1: List<KSAnnotated> = listOf(mockk())
        val illegalSharedRound2: List<KSAnnotated> = listOf(mockk())
        val illegalMultiShared: List<KSAnnotated> = listOf(mockk())
        val illegalPlatform: List<KSAnnotated> = listOf(mockk())

        every {
            singleSourceAggregator.extractCommonInterfaces(any())
        } returnsMany listOf(
            Aggregated(illegalCommonRound1, listOf(mockk()), listOf(mockk())),
            Aggregated(illegalCommonRound2, listOf(mockk()), listOf(mockk()))
        )

        every {
            multiSourceAggregator.extractCommonInterfaces(any())
        } returnsMany listOf(
            Aggregated(illegalMultiCommon, listOf(mockk()), emptyList()),
            Aggregated(emptyList(), emptyList(), emptyList())
        )

        every {
            singleSourceAggregator.extractSharedInterfaces(any())
        } returnsMany listOf(
            Aggregated(illegalSharedRound1, listOf(mockk()), listOf(mockk())),
            Aggregated(illegalSharedRound2, listOf(mockk()), listOf(mockk()))
        )

        every {
            multiSourceAggregator.extractSharedInterfaces(any())
        } returnsMany listOf(
            Aggregated(illegalMultiShared, listOf(mockk()), emptyList()),
            Aggregated(emptyList(), emptyList(), emptyList())
        )

        every {
            singleSourceAggregator.extractPlatformInterfaces(any())
        } returns Aggregated(illegalPlatform, listOf(mockk()), listOf(mockk()))

        every { relaxationAggregator.extractRelaxer(any()) } returns mockk()

        // TemplateSource & TemplateMultiSource
        every { filter.filterByPrecedence<TemplateMultiSource>(any()) } returns mockk()

        every { filter.filter<TemplateMultiSource>(any(), any()) } returnsMany listOf(
            listOf(mockk()), // TemplateSource
            listOf(mockk()), // TemplateMultiSource
            listOf(mockk()), // TemplateSource
            emptyList(), // TemplateMultiSource
        )

        every { mockGenerator.writeCommonMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writeSharedMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writePlatformMocks(any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateCommon(any(), any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateShared(any(), any(), any()) } just Runs

        // When
        val processor = KMockProcessor(
            mockk(),
            false,
            true,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockGenerator,
            factoryGenerator,
            entryPointGenerator,
            multiSourceAggregator,
            singleSourceAggregator,
            relaxationAggregator,
            filter,
        )
        val actualRound1 = processor.process(resolver)
        val actualRound2 = processor.process(resolver)

        // Then
        actualRound1 mustBe listOf(
            illegalCommonRound1.first(),
            illegalSharedRound1.first(),
            illegalPlatform.first(),
            illegalMultiCommon.first(),
            illegalMultiShared.first(),
        )
        actualRound2 mustBe listOf(
            illegalCommonRound1.first(),
            illegalCommonRound2.first(),
            illegalMultiCommon.first(),
            illegalSharedRound1.first(),
            illegalSharedRound2.first(),
            illegalMultiShared.first(),
            illegalPlatform.first(),
            illegalMultiCommon.first(),
            illegalMultiShared.first(),
        )
    }

    @Test
    fun `Given process is called it delegates captured Stubs to the StubGenerator for KMP`() {
        // Given
        val resolver: Resolver = mockk()
        val multiSourceAggregator: MultiSourceAggregator = mockk()
        val singleSourceAggregator: SingleSourceAggregator = mockk()
        val relaxationAggregator: RelaxationAggregator = mockk()

        val codeGenerator: ProcessorContract.KmpCodeGenerator = mockk()
        val mockGenerator: MockGenerator = mockk()
        val factoryGenerator: MockFactoryGenerator = mockk()
        val entryPointGenerator: MockFactoryEntryPointGenerator = mockk()

        val filter: ProcessorContract.SourceFilter = mockk()
        val relaxer = Relaxer(fixture.fixture(), fixture.fixture())

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfacesCommon: List<TemplateSource> = listOf(mockk())
        val interfacesShared: List<TemplateSource> = listOf(mockk())
        val interfacesPlatform: List<TemplateSource> = listOf(mockk())
        val interfacesFiltered: List<TemplateSource> = listOf(mockk())

        val dependenciesCommon: List<KSFile> = listOf(mockk())
        val dependenciesShared: List<KSFile> = listOf(mockk())
        val dependenciesPlatform: List<KSFile> = listOf(mockk())
        val totalDependencies = listOf(dependenciesCommon, dependenciesShared, dependenciesPlatform,).flatten()

        every {
            singleSourceAggregator.extractCommonInterfaces(any())
        } returns Aggregated(illegal, interfacesCommon, dependenciesCommon)

        every {
            multiSourceAggregator.extractCommonInterfaces(any())
        } returns Aggregated(emptyList(), emptyList(), emptyList())

        every {
            singleSourceAggregator.extractSharedInterfaces(any())
        } returns Aggregated(illegal, interfacesShared, dependenciesShared)

        every {
            multiSourceAggregator.extractSharedInterfaces(any())
        } returns Aggregated(emptyList(), emptyList(), emptyList())

        every {
            singleSourceAggregator.extractPlatformInterfaces(any())
        } returns Aggregated(illegal, interfacesPlatform, dependenciesPlatform)

        every { filter.filter<Source>(any(), any()) } returnsMany listOf(
            interfacesFiltered, // Round1 TemplateSource Shared
            emptyList(), // Round1 TemplateMultiSource Shared
            interfacesFiltered, // Round1 TemplateSource Platform
        )

        every { filter.filterByPrecedence<Source>(any()) } returnsMany listOf(
            interfacesFiltered, // Round1 TemplateSource Shared
            emptyList(), // Round1 TemplateMultiSource Shared
            interfacesFiltered, // Round1 TemplateSource Platform
        )

        every { relaxationAggregator.extractRelaxer(any()) } returns relaxer

        every { codeGenerator.closeFiles() } just Runs

        every { mockGenerator.writeCommonMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writeSharedMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writePlatformMocks(any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateCommon(any(), any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateShared(any(), any(), any()) } just Runs

        // When
        KMockProcessor(
            mockk(),
            false,
            true,
            codeGenerator,
            mockk(relaxed = true),
            mockGenerator,
            factoryGenerator,
            entryPointGenerator,
            multiSourceAggregator,
            singleSourceAggregator,
            relaxationAggregator,
            filter,
        ).process(resolver)

        // Then
        verify(exactly = 1) {
            mockGenerator.writeCommonMocks(
                interfacesCommon,
                emptyList(),
                relaxer,
            )
        }

        verify(exactly = 1) { filter.filter(interfacesFiltered, interfacesCommon) }
        verify(exactly = 1) { filter.filterByPrecedence(interfacesShared) }

        verify(exactly = 1) { mockGenerator.writeSharedMocks(interfacesFiltered, emptyList(), relaxer) }

        verify(exactly = 1) {
            filter.filter(
                interfacesPlatform,
                listOf(
                    interfacesCommon,
                    interfacesFiltered,
                ).flatten()
            )
        }
        verify(exactly = 1) { mockGenerator.writePlatformMocks(interfacesFiltered, relaxer) }

        verify(exactly = 1) {
            factoryGenerator.writeFactories(
                listOf(
                    interfacesCommon,
                    interfacesFiltered,
                    interfacesFiltered,
                ).flatten(),
                emptyList(),
                relaxer,
                totalDependencies,
            )
        }

        verify(exactly = 1) {
            entryPointGenerator.generateCommon(
                interfacesCommon,
                emptyList(),
                listOf(
                    interfacesCommon,
                    interfacesFiltered,
                    interfacesFiltered,
                ).flatten(),
                emptyList(),
                totalDependencies,
            )
        }

        verify(exactly = 1) {
            entryPointGenerator.generateShared(
                interfacesFiltered,
                emptyList(),
                dependenciesShared
            )
        }

        verify(exactly = 1) { codeGenerator.closeFiles() }
    }

    @Test
    fun `Given process is called it delegates captured Stubs to the StubGenerator for KMP in a multi interface setup`() {
        // Given
        val resolver: Resolver = mockk()
        val interfaceBinder: MultiInterfaceBinder = mockk(relaxed = true)
        val multiSourceAggregator: MultiSourceAggregator = mockk()
        val singleSourceAggregator: SingleSourceAggregator = mockk()
        val relaxationAggregator: RelaxationAggregator = mockk()

        val codeGenerator: ProcessorContract.KmpCodeGenerator = mockk()
        val mockGenerator: MockGenerator = mockk()
        val factoryGenerator: MockFactoryGenerator = mockk()
        val entryPointGenerator: MockFactoryEntryPointGenerator = mockk()

        val filter: ProcessorContract.SourceFilter = mockk()
        val relaxer = Relaxer(fixture.fixture(), fixture.fixture())

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfacesCommonRound1: List<TemplateSource> = listOf(mockk())
        val interfacesCommonRound2: List<TemplateSource> = listOf(mockk())
        val multiInterfacesCommon: List<TemplateMultiSource> = listOf(mockk())
        val interfacesSharedRound1: List<TemplateSource> = listOf(mockk())
        val interfacesSharedRound2: List<TemplateSource> = listOf(mockk())
        val multiInterfacesShared: List<TemplateMultiSource> = listOf(mockk())
        val interfacesPlatform: List<TemplateSource> = listOf(mockk())
        val filteredSharedRound1: List<TemplateSource> = listOf(mockk())
        val filteredSharedRound2: List<TemplateSource> = listOf(mockk())
        val filteredSharedMulti: List<TemplateMultiSource> = listOf(mockk())
        val filteredPlatform: List<TemplateSource> = listOf(mockk())

        val dependenciesCommon: List<KSFile> = listOf(mockk())
        val dependenciesMultiCommon: List<KSFile> = listOf(mockk())
        val dependenciesShared: List<KSFile> = listOf(mockk())
        val dependenciesMultiShared: List<KSFile> = listOf(mockk())
        val dependenciesPlatform: List<KSFile> = listOf(mockk())
        val totalDependencies = listOf(
            dependenciesCommon,
            dependenciesShared,
            dependenciesPlatform,
            dependenciesMultiCommon,
            dependenciesMultiShared,
        ).flatten()

        every {
            singleSourceAggregator.extractCommonInterfaces(any())
        } returnsMany listOf(
            Aggregated(illegal, interfacesCommonRound1, dependenciesCommon),
            Aggregated(illegal, interfacesCommonRound2, dependenciesCommon),
        )

        every {
            multiSourceAggregator.extractCommonInterfaces(any())
        } returnsMany listOf(
            Aggregated(illegal, multiInterfacesCommon, dependenciesMultiCommon),
            Aggregated(emptyList(), emptyList(), emptyList())
        )

        every {
            singleSourceAggregator.extractSharedInterfaces(any())
        } returnsMany listOf(
            Aggregated(illegal, interfacesSharedRound1, dependenciesShared),
            Aggregated(illegal, interfacesSharedRound2, dependenciesShared),
        )

        every {
            multiSourceAggregator.extractSharedInterfaces(any())
        } returnsMany listOf(
            Aggregated(illegal, multiInterfacesShared, dependenciesMultiShared),
            Aggregated(emptyList(), emptyList(), emptyList())
        )

        every {
            singleSourceAggregator.extractPlatformInterfaces(any())
        } returns Aggregated(illegal, interfacesPlatform, dependenciesPlatform)

        every { filter.filter<Source>(any(), any()) } returnsMany listOf(
            filteredSharedRound1,
            filteredSharedMulti,
            filteredPlatform,
            filteredSharedRound2,
            emptyList(),
            filteredPlatform,
        )
        every { filter.filterByPrecedence<Source>(any()) } returnsMany listOf(
            filteredSharedRound1,
            filteredSharedMulti,
            filteredSharedRound2,
            filteredSharedMulti,
        )

        every { relaxationAggregator.extractRelaxer(any()) } returnsMany listOf(relaxer, null)

        every { codeGenerator.closeFiles() } just Runs

        every { mockGenerator.writeCommonMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writeSharedMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writePlatformMocks(any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateCommon(any(), any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateShared(any(), any(), any()) } just Runs

        // When
        val processor = KMockProcessor(
            mockk(),
            false,
            true,
            codeGenerator,
            interfaceBinder,
            mockGenerator,
            factoryGenerator,
            entryPointGenerator,
            multiSourceAggregator,
            singleSourceAggregator,
            relaxationAggregator,
            filter,
        )
        processor.process(resolver)
        processor.process(resolver)

        // Then
        verify(exactly = 1) {
            mockGenerator.writeCommonMocks(
                interfacesCommonRound1,
                emptyList(),
                relaxer
            )
        }

        verify(exactly = 1) {
            mockGenerator.writeCommonMocks(
                interfacesCommonRound2,
                multiInterfacesCommon,
                relaxer
            )
        }

        verify(exactly = 1) {
            interfaceBinder.bind(
                listOf(
                    multiInterfacesCommon,
                    filteredSharedMulti,
                ).flatten(),
                listOf(
                    dependenciesMultiCommon,
                    dependenciesMultiShared,
                ).flatten(),
            )
        }

        verify(exactly = 1) {
            interfaceBinder.bind(any(), any())
        }

        verify(exactly = 1) { filter.filter(filteredSharedRound1, interfacesCommonRound1) }
        verify(exactly = 2) { filter.filter(filteredSharedMulti, multiInterfacesCommon) }
        verify(exactly = 1) {
            filter.filter(
                filteredSharedRound2,
                listOf(interfacesCommonRound1, interfacesCommonRound2).flatten()
            )
        }
        verify(exactly = 1) {
            filter.filter(
                interfacesPlatform,
                listOf(
                    interfacesCommonRound1,
                    filteredSharedRound1,
                ).flatten()
            )
        }
        verify(exactly = 1) {
            filter.filter(
                interfacesPlatform,
                listOf(
                    interfacesCommonRound1,
                    interfacesCommonRound2,
                    filteredSharedRound1,
                    filteredSharedRound2,
                ).flatten()
            )
        }
        verify(exactly = 6) {
            filter.filter<Source>(
                any(),
                any(),
            )
        }

        verify(exactly = 1) { filter.filterByPrecedence(interfacesSharedRound1) }
        verify(exactly = 1) { filter.filterByPrecedence(interfacesSharedRound2) }
        verify(exactly = 1) { filter.filterByPrecedence(multiInterfacesShared) }
        verify(exactly = 1) { filter.filterByPrecedence(emptyList()) }
        verify(exactly = 4) { filter.filterByPrecedence<Source>(any()) }

        verify(exactly = 1) {
            mockGenerator.writeSharedMocks(
                filteredSharedRound1,
                emptyList(),
                relaxer
            )
        }
        verify(exactly = 1) {
            mockGenerator.writeSharedMocks(
                filteredSharedRound2,
                filteredSharedMulti,
                relaxer
            )
        }

        verify(exactly = 2) { mockGenerator.writePlatformMocks(filteredPlatform, relaxer) }

        verify(exactly = 1) {
            factoryGenerator.writeFactories(
                listOf(
                    interfacesCommonRound1,
                    filteredSharedRound1,
                    filteredPlatform,
                ).flatten(),
                listOf(
                    multiInterfacesCommon,
                    filteredSharedMulti,
                ).flatten(),
                relaxer,
                totalDependencies,
            )
        }

        verify(exactly = 1) {
            entryPointGenerator.generateCommon(
                templateSources = listOf(interfacesCommonRound1).flatten(),
                templateMultiSources = multiInterfacesCommon,
                totalTemplates = listOf(
                    interfacesCommonRound1,
                    filteredSharedRound1,
                    filteredPlatform,
                ).flatten(),
                totalMultiSources = listOf(
                    multiInterfacesCommon,
                    filteredSharedMulti,
                ).flatten(),
                dependencies = totalDependencies,
            )
        }

        verify(exactly = 1) {
            entryPointGenerator.generateShared(
                filteredSharedRound1,
                filteredSharedMulti,
                listOf(
                    dependenciesShared,
                    dependenciesMultiShared
                ).flatten()
            )
        }

        verify(exactly = 1) { codeGenerator.closeFiles() }
    }

    @Test
    fun `Given process is called it delegates it delegates only Platform sources`() {
        // Given
        val resolver: Resolver = mockk()
        val singleSourceAggregator: SingleSourceAggregator = mockk()
        val relaxationAggregator: RelaxationAggregator = mockk()

        val codeGenerator: ProcessorContract.KmpCodeGenerator = mockk()
        val mockGenerator: MockGenerator = mockk()
        val factoryGenerator: MockFactoryGenerator = mockk()
        val entryPointGenerator: MockFactoryEntryPointGenerator = mockk()

        val filter: ProcessorContract.SourceFilter = mockk()
        val relaxer = Relaxer(fixture.fixture(), fixture.fixture())

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfacesPlatform: List<TemplateSource> = listOf(mockk())
        val interfacesFiltered: List<TemplateSource> = listOf(mockk())

        val dependencies: List<KSFile> = listOf(mockk())

        every {
            singleSourceAggregator.extractPlatformInterfaces(any())
        } returns Aggregated(illegal, interfacesPlatform, dependencies)

        every { filter.filter<Source>(any(), any()) } returns interfacesFiltered
        every { filter.filterByPrecedence<Source>(any()) } returns interfacesFiltered

        every { relaxationAggregator.extractRelaxer(any()) } returns relaxer

        every { codeGenerator.closeFiles() } just Runs

        every { mockGenerator.writeCommonMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writeSharedMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writePlatformMocks(any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateCommon(any(), any(), any(), any(), any()) } just Runs
        every { entryPointGenerator.generateShared(any(), any(), any()) } just Runs

        // When
        KMockProcessor(
            mockk(),
            false,
            false,
            codeGenerator,
            mockk(),
            mockGenerator,
            factoryGenerator,
            entryPointGenerator,
            mockk(),
            singleSourceAggregator,
            relaxationAggregator,
            filter,
        ).process(resolver)

        // Then
        verify(exactly = 1) { singleSourceAggregator.extractPlatformInterfaces(any()) }

        verify(exactly = 0) { mockGenerator.writeCommonMocks(any(), any(), any()) }

        verify(exactly = 1) { filter.filter<Source>(any(), any()) }
        verify(exactly = 0) { filter.filterByPrecedence<Source>(any()) }

        verify(exactly = 0) { mockGenerator.writeSharedMocks(any(), any(), any()) }

        verify(exactly = 1) {
            filter.filter(
                interfacesPlatform,
                emptyList()
            )
        }
        verify(exactly = 1) { mockGenerator.writePlatformMocks(interfacesFiltered, relaxer) }

        verify(exactly = 1) {
            factoryGenerator.writeFactories(
                listOf(
                    interfacesFiltered.first(),
                ),
                emptyList(),
                relaxer,
                dependencies,
            )
        }

        verify(exactly = 0) {
            entryPointGenerator.generateCommon(emptyList(), emptyList(), any(), any(), any())
        }

        verify(exactly = 0) {
            entryPointGenerator.generateShared(emptyList(), emptyList(), any())
        }

        verify(exactly = 1) { codeGenerator.closeFiles() }
    }
}
