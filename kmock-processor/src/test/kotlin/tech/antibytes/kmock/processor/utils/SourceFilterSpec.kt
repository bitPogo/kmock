/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

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

        val packageName0: String = fixture.fixture()
        val interfaceName0: String = fixture.fixture()

        val source1_0: KSClassDeclaration = mockk()
        val source0_1: KSClassDeclaration = mockk()

        val packageName1: String = fixture.fixture()
        val interfaceName1: String = fixture.fixture()

        val sources0 = listOf(
            TemplateSource(
                indicator = "",
                template = source0_0,
                templateName = packageName0,
                packageName = interfaceName0,
                generics = null
            ),
            TemplateSource(
                indicator = "",
                template = source0_1,
                templateName = packageName0,
                packageName = interfaceName1,
                generics = null
            )
        )

        val sources1 = listOf(
            TemplateSource(
                indicator = "",
                template = source1_0,
                templateName = packageName1,
                packageName = interfaceName0,
                generics = null
            ),
            TemplateSource(
                indicator = "",
                template = source1_1,
                templateName = packageName0,
                packageName = interfaceName1,
                generics = null
            )
        )

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
            TemplateSource(
                indicator = fixture.fixture(),
                template = source0,
                templateName = fixture.fixture(),
                packageName = fixture.fixture(),
                generics = null
            ),
            TemplateSource(
                indicator = fixture.fixture(),
                template = source1,
                templateName = fixture.fixture(),
                packageName = fixture.fixture(),
                generics = null
            )
        )

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

        val packageName: String = fixture.fixture()
        val interfaceName: String = fixture.fixture()

        val marker0: String = fixture.fixture()
        val marker1: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(
                indicator = marker0,
                template = source0,
                templateName = interfaceName,
                packageName = packageName,
                generics = null
            ),
            TemplateSource(
                indicator = marker1,
                template = source1,
                templateName = interfaceName,
                packageName = packageName,
                generics = null
            )
        )

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

        val packageName: String = fixture.fixture()
        val interfaceName: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(
                indicator = marker0,
                template = source0,
                templateName = interfaceName,
                packageName = packageName,
                generics = null
            ),
            TemplateSource(
                indicator = marker1,
                template = source1,
                templateName = interfaceName,
                packageName = packageName,
                generics = null
            ),
            TemplateSource(
                indicator = marker2,
                template = source2,
                templateName = interfaceName,
                packageName = packageName,
                generics = null
            )
        )

        val precedences = mapOf(
            marker0 to 1,
            marker1 to 2,
            marker2 to 0,
        )

        // When
        val actual = SourceFilter(precedences, mockk()).filterSharedSources(sources)

        // Then
        actual mustBe listOf(sources[1])
    }
}
