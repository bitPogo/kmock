/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

class SourceFilterSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils SourceFilterSpec`() {
        SourceFilter(emptyMap(), mockk()) fulfils ProcessorContract.SourceFilter::class
    }

    @Test
    fun `Given filter is called with 2 Lists of Sources it filters the first by the 2nd according to the qualified name`() {
        // Given
        val source0_0: KSClassDeclaration = mockk()
        val source1_1: KSClassDeclaration = mockk()

        val source1_0: KSClassDeclaration = mockk()
        val source0_1: KSClassDeclaration = mockk()

        val sources0 = listOf(
            TemplateSource("", source0_0, null, null),
            TemplateSource("", source0_1, null, null)
        )

        val sources1 = listOf(
            TemplateSource("", source1_0, null, null),
            TemplateSource("", source1_1, null, null)
        )

        val sameSource: String = fixture.fixture()

        every { source0_0.qualifiedName!!.asString() } returns fixture.fixture()
        every { source0_1.qualifiedName!!.asString() } returns sameSource

        every { source1_0.qualifiedName!!.asString() } returns fixture.fixture()
        every { source1_1.qualifiedName!!.asString() } returns sameSource

        // When
        val actual = SourceFilter(emptyMap(), mockk()).filter(sources0, sources1)

        // Then
        actual mustBe listOf(
            sources0.first()
        )
    }

    @Test
    fun `Given filterSharedSources is called with aggregated SharedSource it returns them if they are already unique`() {
        // Given
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()

        val sources = listOf(
            TemplateSource(fixture.fixture(), source0, null, null),
            TemplateSource(fixture.fixture(), source1, null, null)
        )

        every { source0.qualifiedName!!.asString() } returns fixture.fixture()
        every { source1.qualifiedName!!.asString() } returns fixture.fixture()

        // When
        val actual = SourceFilter(emptyMap(), mockk()).filterSharedSources(sources)

        // Then
        actual mustBe sources
    }

    @Test
    fun `Given filterSharedSources is called with aggregated SharedSource it emits an error if the SharedSource was not declared and they are not unique`() {
        // Given
        val logger: KSPLogger = mockk(relaxUnitFun = true)
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()

        val marker0: String = fixture.fixture()
        val marker1: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(marker0, source0, null, null),
            TemplateSource(marker1, source1, null, null)
        )

        val interfaceName: String = fixture.fixture()

        every { source0.qualifiedName!!.asString() } returns interfaceName
        every { source1.qualifiedName!!.asString() } returns interfaceName

        // When
        SourceFilter(emptyMap(), logger).filterSharedSources(sources)

        // Then
        verify(exactly = 1) { logger.error("No SharedSource defined for $marker0.") }
        verify(exactly = 1) { logger.error("No SharedSource defined for $marker1.") }
    }

    @Test
    fun `Given filterSharedSources is called with aggregated SharedSource it uses the source with the highest precedence`() {
        // Given
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()
        val source2: KSClassDeclaration = mockk()

        val marker0: String = fixture.fixture()
        val marker1: String = fixture.fixture()
        val marker2: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(marker0, source0, null, null),
            TemplateSource(marker1, source1, null, null),
            TemplateSource(marker2, source2, null, null),
        )

        val interfaceName: String = fixture.fixture()

        val precedences = mapOf(
            marker0 to "1",
            marker1 to "2",
            marker2 to "0",
        )

        every { source0.qualifiedName!!.asString() } returns interfaceName
        every { source1.qualifiedName!!.asString() } returns interfaceName
        every { source2.qualifiedName!!.asString() } returns interfaceName

        // When
        val actual = SourceFilter(precedences, mockk()).filterSharedSources(sources)

        // Then
        actual mustBe listOf(sources[1])
    }
}
