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
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.Relaxer
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
            mockk(),
            mockk(),
            ProcessorContract.Options(fixture.fixture(), fixture.fixture()),
            mockk(relaxed = true),
        ) fulfils SymbolProcessor::class
    }

    @Test
    fun `Given process is called it retrieves all Stub annotated sources`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { aggregator.extractInterfaces(any()) } returns mockk(relaxed = true)
        every { aggregator.extractRelaxer(any()) } returns mockk()

        // When
        KMockProcessor(
            mockk(relaxed = true),
            mockk(relaxed = true),
            aggregator,
            ProcessorContract.Options(fixture.fixture(), fixture.fixture()),
            mockk(relaxed = true),
        ).process(resolver)

        // Then
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(Mock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given process is called it retrieves all StubCommon annotated sources`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { aggregator.extractInterfaces(any()) } returns mockk(relaxed = true)
        every { aggregator.extractRelaxer(any()) } returns mockk()

        // When
        KMockProcessor(
            mockk(relaxed = true),
            mockk(relaxed = true),
            aggregator,
            ProcessorContract.Options(fixture.fixture(), fixture.fixture()),
            mockk(relaxed = true),
        ).process(resolver)

        // Then
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given process it returns all aggregated and merges illegal sources`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()

        val illegalCommon: List<KSAnnotated> = listOf(mockk())
        val illegalShared: List<KSAnnotated> = listOf(mockk())
        val illegalPlatform: List<KSAnnotated> = listOf(mockk())

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            aggregator.extractInterfaces(any())
        } returnsMany listOf(
            ProcessorContract.Aggregated(illegalCommon, listOf(mockk()), listOf(mockk())),
            ProcessorContract.Aggregated(illegalShared, listOf(mockk()), listOf(mockk())),
            ProcessorContract.Aggregated(illegalPlatform, listOf(mockk()), listOf(mockk()))
        )
        every { aggregator.extractRelaxer(any()) } returns mockk()

        // When
        val actual = KMockProcessor(
            mockk(relaxed = true),
            mockk(relaxed = true),
            aggregator,
            ProcessorContract.Options(fixture.fixture(), fixture.fixture()),
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
    fun `Given process it and resolves the Relaxer`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfaces: List<ProcessorContract.InterfaceSource> = listOf(mockk())
        val dependencies: List<KSFile> = listOf(mockk())

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            aggregator.extractInterfaces(any())
        } returns ProcessorContract.Aggregated(illegal, interfaces, dependencies)
        every { aggregator.extractRelaxer(any()) } returns mockk()

        // When
        KMockProcessor(
            mockk(relaxed = true),
            mockk(relaxed = true),
            aggregator,
            ProcessorContract.Options(fixture.fixture(), fixture.fixture()),
            mockk(relaxed = true),
        ).process(resolver)

        // Then
        verify(exactly = 1) { resolver.getSymbolsWithAnnotation(Relaxer::class.qualifiedName!!, false) }
        verify(exactly = 1) { aggregator.extractRelaxer(annotated) }
    }

    @Test
    fun `Given process it delegates captured Stubs to the StubGenerator`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()
        val generator: ProcessorContract.MockGenerator = mockk()
        val factoryGenerator: ProcessorContract.MockFactoryGenerator = mockk()
        val options = ProcessorContract.Options(fixture.fixture(), fixture.fixture())
        val filter: ProcessorContract.SourceFilter = mockk()
        val relaxer: ProcessorContract.Relaxer = ProcessorContract.Relaxer(fixture.fixture(), fixture.fixture())

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfacesCommon: List<ProcessorContract.InterfaceSource> = listOf(mockk())
        val interfacesShared: List<ProcessorContract.InterfaceSource> = listOf(mockk())
        val interfacesPlatform: List<ProcessorContract.InterfaceSource> = listOf(mockk())
        val interfacesFiltered: List<ProcessorContract.InterfaceSource> = listOf(mockk())

        val dependencies: List<KSFile> = listOf(mockk())

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            aggregator.extractInterfaces(any())
        } returnsMany listOf(
            ProcessorContract.Aggregated(illegal, interfacesCommon, dependencies),
            ProcessorContract.Aggregated(illegal, interfacesShared, dependencies),
            ProcessorContract.Aggregated(illegal, interfacesPlatform, dependencies),
        )

        every { filter.filter(any(), any()) } returns interfacesFiltered
        every { filter.filterSharedSources(any()) } returns interfacesFiltered

        every { aggregator.extractRelaxer(any()) } returns relaxer

        every { generator.writeCommonMocks(any(), any(), any()) } just Runs
        every { generator.writeSharedMocks(any(), any(), any()) } just Runs
        every { generator.writePlatformMocks(any(), any(), any()) } just Runs

        every { factoryGenerator.writeFactories(any(), any(), any(), any()) } just Runs

        // When
        KMockProcessor(
            generator,
            factoryGenerator,
            aggregator,
            options,
            filter,
        ).process(resolver)

        // Then
        verify(exactly = 1) { generator.writeCommonMocks(interfacesCommon, dependencies, relaxer) }

        verify(exactly = 1) { filter.filter(interfacesFiltered, interfacesCommon) }
        verify(exactly = 1) { filter.filterSharedSources(interfacesShared) }

        verify(exactly = 1) { generator.writeSharedMocks(interfacesFiltered, dependencies, relaxer) }

        verify(exactly = 1) {
            filter.filter(
                interfacesPlatform,
                listOf(
                    interfacesCommon.first(),
                    interfacesFiltered.first()
                )
            )
        }
        verify(exactly = 1) { generator.writePlatformMocks(interfacesFiltered, dependencies, relaxer) }

        verify(exactly = 1) {
            factoryGenerator.writeFactories(
                options,
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
    }
}
