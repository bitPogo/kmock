/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.util.test.mustBe

class TemplateMultiSourceSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `Given hasGenerics is called, it returns false if no actual generics had been captured`() {
        // Given
        val source = TemplateMultiSource(
            indicator = fixture.fixture(),
            templateName = fixture.fixture(),
            packageName = fixture.fixture(),
            dependencies = emptyList(),
            templates = emptyList(),
            generics = listOf(null, null, null, null)
        )

        // When
        val actual = source.hasGenerics()

        // Then
        actual mustBe false
    }

    @Test
    fun `Given hasGenerics is called, it returns true if at least one actual generics had been captured`() {
        // Given
        val source = TemplateMultiSource(
            indicator = fixture.fixture(),
            templateName = fixture.fixture(),
            packageName = fixture.fixture(),
            dependencies = emptyList(),
            templates = emptyList(),
            generics = listOf(null, null, mockk(), null)
        )

        // When
        val actual = source.hasGenerics()

        // Then
        actual mustBe true
    }
}
