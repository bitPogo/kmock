/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class SourceSetValidatorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils SourceSet`() {
        SourceSetValidator(mockk(), mockk()) fulfils ProcessorContract.SourceSetValidator::class
    }

    @Test
    fun `Given isValidateSourceSet with a non string it returns false`() {
        // Given
        val any: Any = fixture.fixture()
        val logger: KSPLogger = mockk()

        every { logger.warn(any()) } just Runs

        // When
        val actual = SourceSetValidator(logger, mockk()).isValidateSourceSet(any)

        // Then
        actual mustBe false
        verify(exactly = 1) { logger.warn("Unexpected annotation payload!") }
    }

    @Test
    fun `Given isValidateSourceSet with a string which is not a valid source it returns false and logs a warning`() {
        // Given
        val logger: KSPLogger = mockk()
        val knownSourceSets: Set<String> = fixture.listFixture<String>().toSet()
        val invalidSourceSet: String = fixture.fixture()

        every { logger.warn(any()) } just Runs

        // When
        val actual = SourceSetValidator(logger, knownSourceSets).isValidateSourceSet(invalidSourceSet)

        // Then
        actual mustBe false

        verify(exactly = 1) {
            logger.warn("$invalidSourceSet is not a applicable sourceSet!")
        }
    }

    @Test
    fun `Given isValidateSourceSet with a string which is a valid source it returns true`() {
        // Given
        val logger: KSPLogger = mockk()
        val validSourceSet: String = fixture.fixture()
        val knownSourceSets: Set<String> = setOf(validSourceSet)

        every { logger.warn(any()) } just Runs

        // When
        val actual = SourceSetValidator(logger, knownSourceSets).isValidateSourceSet(validSourceSet)

        // Then
        actual mustBe true

        verify(exactly = 0) {
            logger.warn(any())
        }
    }

    @Test
    fun `Given isValidateSourceSet with a KSAnnotation which is a invalid source it returns false`() {
        // Given
        val logger: KSPLogger = mockk()
        val annotation: KSAnnotation = mockk()
        val validSourceSet: String = fixture.fixture()
        val knownSourceSets: Set<String> = setOf(validSourceSet)

        every { logger.warn(any()) } just Runs
        every { annotation.arguments } returns emptyList()

        // When
        val actual = SourceSetValidator(
            logger,
            knownSourceSets
        ).isValidateSourceSet(annotation)

        // Then
        actual mustBe false

        verify(exactly = 1) {
            logger.warn("Unexpected annotation payload!")
        }
    }

    @Test
    fun `Given isValidateSourceSet with a KSAnnotation which is a valid source it returns true`() {
        // Given
        val logger: KSPLogger = mockk()
        val annotation: KSAnnotation = mockk()
        val value: KSValueArgument = mockk()
        val validSourceSet: String = fixture.fixture()
        val knownSourceSets: Set<String> = setOf(validSourceSet)

        every { logger.warn(any()) } just Runs
        every { annotation.arguments } returns listOf(value)
        every { value.value } returns validSourceSet

        // When
        val actual = SourceSetValidator(
            logger,
            knownSourceSets
        ).isValidateSourceSet(annotation)

        // Then
        actual mustBe true

        verify(exactly = 0) {
            logger.warn(any())
        }
    }

    @Test
    fun `Given isValidateSourceSet with a KSAnnotation which is a valid source while referencing only the platform it returns true`() {
        // Given
        val logger: KSPLogger = mockk()
        val annotation: KSAnnotation = mockk()
        val value: KSValueArgument = mockk()
        val validSourceSet: String = fixture.fixture()
        val knownSourceSets: Set<String> = setOf("${validSourceSet}Test")

        every { logger.warn(any()) } just Runs
        every { annotation.arguments } returns listOf(value)
        every { value.value } returns validSourceSet

        // When
        val actual = SourceSetValidator(
            logger,
            knownSourceSets
        ).isValidateSourceSet(annotation)

        // Then
        actual mustBe true

        verify(exactly = 0) {
            logger.warn(any())
        }
    }
}
