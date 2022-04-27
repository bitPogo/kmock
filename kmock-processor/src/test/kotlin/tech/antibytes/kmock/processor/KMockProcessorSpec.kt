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
import tech.antibytes.kmock.processor.ProcessorContract.RelaxationAggregator
import tech.antibytes.kmock.processor.ProcessorContract.Source
import tech.antibytes.kmock.processor.ProcessorContract.SourceAggregator
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
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(relaxed = true),
        ) fulfils SymbolProcessor::class
    }

    @Test
    fun `Given process is called it returns all aggregated and merges illegal sources`() {
        // Given
        val resolver: Resolver = mockk()
        val sourceAggregator: SourceAggregator = mockk()
        val relaxationAggregator: RelaxationAggregator = mockk()

        val mockGenerator: MockGenerator = mockk()
        val factoryGenerator: MockFactoryGenerator = mockk()
        val entryPointGenerator: MockFactoryEntryPointGenerator = mockk()

        val illegalCommon: List<KSAnnotated> = listOf(mockk())
        val illegalShared: List<KSAnnotated> = listOf(mockk())
        val illegalPlatform: List<KSAnnotated> = listOf(mockk())

        every {
            sourceAggregator.extractCommonInterfaces(any())
        } returns Aggregated(illegalCommon, listOf(mockk()), listOf(mockk()))

        every {
            sourceAggregator.extractSharedInterfaces(any())
        } returns Aggregated(illegalShared, listOf(mockk()), listOf(mockk()))

        every {
            sourceAggregator.extractPlatformInterfaces(any())
        } returns Aggregated(illegalPlatform, listOf(mockk()), listOf(mockk()))

        every { relaxationAggregator.extractRelaxer(any()) } returns mockk()

        every { mockGenerator.writeCommonMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writeSharedMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writePlatformMocks(any(), any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any()) } just Runs
        every { entryPointGenerator.generateCommon(any(), any()) } just Runs
        every { entryPointGenerator.generateShared(any()) } just Runs

        // When
        val actual = KMockProcessor(
            true,
            mockk(relaxed = true),
            mockGenerator,
            factoryGenerator,
            entryPointGenerator,
            sourceAggregator,
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
    fun `Given process is called it delegates captured Stubs to the StubGenerator`() {
        // Given
        val resolver: Resolver = mockk()
        val sourceAggregator: SourceAggregator = mockk()
        val relaxationAggregator: RelaxationAggregator = mockk()

        val codeGenerator: ProcessorContract.KmpCodeGenerator = mockk()
        val mockGenerator: MockGenerator = mockk()
        val factoryGenerator: MockFactoryGenerator = mockk()
        val entryPointGenerator: MockFactoryEntryPointGenerator = mockk()

        val filter: ProcessorContract.SourceFilter = mockk()
        val relaxer: ProcessorContract.Relaxer = ProcessorContract.Relaxer(fixture.fixture(), fixture.fixture())

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfacesCommon: List<TemplateSource> = listOf(mockk())
        val interfacesShared: List<TemplateSource> = listOf(mockk())
        val interfacesPlatform: List<TemplateSource> = listOf(mockk())
        val interfacesFiltered: List<TemplateSource> = listOf(mockk())

        val dependencies: List<KSFile> = listOf(mockk())

        every {
            sourceAggregator.extractCommonInterfaces(any())
        } returns Aggregated(illegal, interfacesCommon, dependencies)

        every {
            sourceAggregator.extractSharedInterfaces(any())
        } returns Aggregated(illegal, interfacesShared, dependencies)

        every {
            sourceAggregator.extractPlatformInterfaces(any())
        } returns Aggregated(illegal, interfacesPlatform, dependencies)

        every { filter.filter<Source>(any(), any()) } returns interfacesFiltered
        every { filter.filterSharedSources<Source>(any()) } returns interfacesFiltered

        every { relaxationAggregator.extractRelaxer(any()) } returns relaxer

        every { codeGenerator.closeFiles() } just Runs

        every { mockGenerator.writeCommonMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writeSharedMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writePlatformMocks(any(), any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any()) } just Runs
        every { entryPointGenerator.generateCommon(any(), any()) } just Runs
        every { entryPointGenerator.generateShared(any()) } just Runs

        // When
        KMockProcessor(
            true,
            codeGenerator,
            mockGenerator,
            factoryGenerator,
            entryPointGenerator,
            sourceAggregator,
            relaxationAggregator,
            filter,
        ).process(resolver)

        // Then
        verify(exactly = 1) { mockGenerator.writeCommonMocks(interfacesCommon, dependencies, relaxer) }

        verify(exactly = 1) { filter.filter(interfacesFiltered, interfacesCommon) }
        verify(exactly = 1) { filter.filterSharedSources(interfacesShared) }

        verify(exactly = 1) { mockGenerator.writeSharedMocks(interfacesFiltered, dependencies, relaxer) }

        verify(exactly = 1) {
            filter.filter(
                interfacesPlatform,
                listOf(
                    interfacesCommon.first(),
                    interfacesFiltered.first()
                )
            )
        }
        verify(exactly = 1) { mockGenerator.writePlatformMocks(interfacesFiltered, dependencies, relaxer) }

        verify(exactly = 1) {
            factoryGenerator.writeFactories(
                listOf(
                    interfacesCommon.first(),
                    interfacesFiltered.first(),
                    interfacesFiltered.first(),
                ),
                dependencies.toMutableList().also {
                    it.addAll(dependencies)
                    it.addAll(dependencies)
                },
                relaxer
            )
        }

        verify(exactly = 1) {
            entryPointGenerator.generateCommon(
                interfacesCommon,
                listOf(
                    interfacesCommon.first(),
                    interfacesFiltered.first(),
                    interfacesFiltered.first(),
                )
            )
        }

        verify(exactly = 1) { entryPointGenerator.generateShared(interfacesFiltered) }

        verify(exactly = 1) { codeGenerator.closeFiles() }
    }

    @Test
    fun `Given process is called it delegates it delegates only Platform sources`() {
        // Given
        val resolver: Resolver = mockk()
        val sourceAggregator: SourceAggregator = mockk()
        val relaxationAggregator: RelaxationAggregator = mockk()

        val codeGenerator: ProcessorContract.KmpCodeGenerator = mockk()
        val mockGenerator: MockGenerator = mockk()
        val factoryGenerator: MockFactoryGenerator = mockk()
        val entryPointGenerator: MockFactoryEntryPointGenerator = mockk()

        val filter: ProcessorContract.SourceFilter = mockk()
        val relaxer: ProcessorContract.Relaxer = ProcessorContract.Relaxer(fixture.fixture(), fixture.fixture())

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfacesCommon: List<TemplateSource> = listOf(mockk())
        val interfacesPlatform: List<TemplateSource> = listOf(mockk())
        val interfacesFiltered: List<TemplateSource> = listOf(mockk())

        val dependencies: List<KSFile> = listOf(mockk())

        every {
            sourceAggregator.extractPlatformInterfaces(any())
        } returns Aggregated<TemplateSource>(illegal, interfacesPlatform, dependencies)

        every { filter.filter<Source>(any(), any()) } returns interfacesFiltered
        every { filter.filterSharedSources<Source>(any()) } returns interfacesFiltered

        every { relaxationAggregator.extractRelaxer(any()) } returns relaxer

        every { codeGenerator.closeFiles() } just Runs

        every { mockGenerator.writeCommonMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writeSharedMocks(any(), any(), any()) } just Runs
        every { mockGenerator.writePlatformMocks(any(), any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any()) } just Runs
        every { entryPointGenerator.generateCommon(any(), any()) } just Runs

        // When
        KMockProcessor(
            false,
            codeGenerator,
            mockGenerator,
            factoryGenerator,
            entryPointGenerator,
            sourceAggregator,
            relaxationAggregator,
            filter,
        ).process(resolver)

        // Then
        verify(exactly = 1) { sourceAggregator.extractPlatformInterfaces(any()) }

        verify(exactly = 0) { mockGenerator.writeCommonMocks(any(), any(), any()) }

        verify(exactly = 1) { filter.filter<Source>(any(), any()) }
        verify(exactly = 0) { filter.filterSharedSources<Source>(any()) }

        verify(exactly = 0) { mockGenerator.writeSharedMocks(any(), any(), any()) }

        verify(exactly = 1) {
            filter.filter(
                interfacesPlatform,
                emptyList()
            )
        }
        verify(exactly = 1) { mockGenerator.writePlatformMocks(interfacesFiltered, dependencies, relaxer) }

        verify(exactly = 1) {
            factoryGenerator.writeFactories(
                listOf(
                    interfacesFiltered.first(),
                ),
                dependencies,
                relaxer
            )
        }

        verify(exactly = 0) {
            entryPointGenerator.generateCommon(interfacesCommon, interfacesFiltered)
        }

        verify(exactly = 1) { codeGenerator.closeFiles() }
    }
}
