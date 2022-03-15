/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockCLIProcessorSpec {
    @Test
    fun `It fulfils CommandLineProcessor`() {
        KMockCLIProcessor() fulfils CommandLineProcessor::class
    }

    @Test
    fun `It has a id`() {
        KMockCLIProcessor().pluginId mustBe "tech.antibytes.kmock.kmock-gradle"
    }

    @Test
    fun `It has cli options`() {
        val option = KMockCLIProcessor().pluginOptions.toList().first()

        option.optionName mustBe "enableOpenClasses"
        option.description mustBe "Enables/Disables classes to opened for testing"
        option.valueDescription mustBe "boolean"
        option.required mustBe true
        option.allowMultipleOccurrences mustBe false
    }

    @Test
    fun `Given processOption is called it fails due to a unknown cli option`() {
        // Given
        val option: AbstractCliOption = CliOption(
            optionName = "Something",
            description = "Enables/Disables classes to opened for testing",
            valueDescription = "boolean",
            required = true,
            allowMultipleOccurrences = false
        )
        val config: CompilerConfiguration = mockk()

        // Then
        val error = assertFailsWith<IllegalArgumentException> {
            // When
            KMockCLIProcessor().processOption(
                option,
                "something",
                config
            )
        }

        error.message mustBe "Unknown config option ${option.optionName}"
    }

    @Test
    fun `Given processOption is called it amends the option`() {
        // Given
        val option: AbstractCliOption = CliOption(
            optionName = "enableOpenClasses",
            description = "Enables/Disables classes to opened for testing",
            valueDescription = "boolean",
            required = true,
            allowMultipleOccurrences = false
        )
        val config: CompilerConfiguration = mockk()

        val key = slot<CompilerConfigurationKey<Boolean>>()
        every { config.put(capture(key), any()) } just Runs

        // When
        KMockCLIProcessor().processOption(
            option,
            "false",
            config
        )

        // Then
        key.captured.toString() mustBe "enableOpenClasses"
        verify(exactly = 1) { config.put(any(), false) }
    }
}
