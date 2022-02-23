/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.MagicStub
import tech.antibytes.kmock.MagicStubCommon
import tech.antibytes.kmock.MagicStubRelaxer
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class KMockProcessorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils SymbolProcessor`() {
        KMockProcessor(mockk(), mockk()) fulfils SymbolProcessor::class
    }

    @Test
    fun `Given process is called it retrieves all MagicStub annotated sources`() {
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
            aggregator
        ).process(resolver)

        // Then
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MagicStub::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given process is called it retrieves all MagicStubCommon annotated sources`() {
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
            aggregator
        ).process(resolver)

        // Then
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MagicStubCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given process it returns all aggregated and merges illegal sources`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()

        val illegalCommon: List<KSAnnotated> = listOf(mockk())
        val illegalPlatform: List<KSAnnotated> = listOf(mockk())

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            aggregator.extractInterfaces(any())
        } returnsMany listOf(
            ProcessorContract.Aggregated(illegalCommon, mockk(), mockk()),
            ProcessorContract.Aggregated(illegalPlatform, mockk(), mockk())
        )
        every { aggregator.extractRelaxer(any()) } returns mockk()

        // When
        val actual = KMockProcessor(mockk(relaxed = true), aggregator).process(resolver)

        // Then
        actual mustBe listOf(
            illegalCommon.first(),
            illegalPlatform.first()
        )
    }

    @Test
    fun `Given process it and resolves the Relaxer`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()
        val generator: ProcessorContract.StubGenerator = mockk()

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfaces: List<KSClassDeclaration> = mockk()
        val dependencies: List<KSFile> = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            aggregator.extractInterfaces(any())
        } returns ProcessorContract.Aggregated(illegal, interfaces, dependencies)
        every { aggregator.extractRelaxer(any()) } returns mockk()

        every { generator.writeCommonStubs(any(), any(), any()) } just Runs
        every { generator.writePlatformStubs(any(), any(), any()) } just Runs

        // When
        KMockProcessor(generator, aggregator).process(resolver)

        // Then
        verify(exactly = 1) { resolver.getSymbolsWithAnnotation(MagicStubRelaxer::class.qualifiedName!!, false) }
        verify(exactly = 1) { aggregator.extractRelaxer(annotated) }
    }

    @Test
    fun `Given process it delegates captured Stubs to the StubGenerator`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()
        val generator: ProcessorContract.StubGenerator = mockk()
        val relaxer: ProcessorContract.Relaxer = ProcessorContract.Relaxer(fixture.fixture(), fixture.fixture())

        val illegal: List<KSAnnotated> = listOf(mockk())

        val interfaces: List<KSClassDeclaration> = mockk()
        val dependencies: List<KSFile> = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            aggregator.extractInterfaces(any())
        } returns ProcessorContract.Aggregated(illegal, interfaces, dependencies)
        every { aggregator.extractRelaxer(any()) } returns relaxer

        every { generator.writeCommonStubs(any(), any(), any()) } just Runs
        every { generator.writePlatformStubs(any(), any(), any()) } just Runs

        // When
        KMockProcessor(generator, aggregator).process(resolver)

        // Then
        verify(exactly = 1) { generator.writeCommonStubs(interfaces, dependencies, relaxer) }
        verify(exactly = 1) { generator.writePlatformStubs(interfaces, dependencies, relaxer) }
    }
}
