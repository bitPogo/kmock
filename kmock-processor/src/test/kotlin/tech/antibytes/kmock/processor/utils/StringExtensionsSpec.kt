/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.mustBe

class StringExtensionsSpec {
    @Test
    fun `Given titleCase is called it does nothing if the String starts with an uppercase character`() {
        // Given
        val string = "Potato"

        // When
        val actual = string.titleCase()

        // Then
        actual mustBe string
    }

    @Test
    fun `Given titleCase is called it uppercases the first character if the String starts not with an uppercase character`() {
        // Given
        val string = "potato"

        // When
        val actual = string.titleCase()

        // Then
        actual mustBe "Potato"
    }

    @Test
    fun `Given ensureNotNullClassName is called it does nothing if the string is not null`() {
        // Given
        val string = "Potato"

        // When
        val actual = ensureNotNullClassName(string)

        // Then
        actual mustBe string
    }

    @Test
    fun `Given ensureNotNullClassName is called it fails if the string is null`() {
        // Given
        val string = null

        // When
        val error = assertFailsWith<IllegalStateException> {
            ensureNotNullClassName(string)
        }

        // Then
        error.message mustBe "Expected non null class name!"
    }
}
