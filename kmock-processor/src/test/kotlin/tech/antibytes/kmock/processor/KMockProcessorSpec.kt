/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils
import tech.antibytes.kmock.MagicStub
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.sameAs

class KMockProcessorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils SymbolProcessor`() {
        KMockProcessor(mockk(), mockk(), mockk()) fulfils SymbolProcessor::class
    }

    @Test
    fun `Given process is called it retrieves all MagicStub annotated sources`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        // When
        KMockProcessor(
            mockk(),
            mockk(),
            mockk(relaxed = true)
        ).process(resolver)

        // Then
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MagicStub::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given process it returns all aggregated illegal sources`() {
        // Given
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}
        val aggregator: ProcessorContract.Aggregator = mockk()

        val illegal: List<KSAnnotated> = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { aggregator.extractInterfaces(any(), any()) } returns illegal

        // When
        val actual = KMockProcessor(mockk(), mockk(), aggregator).process(resolver)

        // Then
        actual sameAs illegal
    }
}
