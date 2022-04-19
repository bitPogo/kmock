/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.RELAXATION_NAME
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class AnnotationFilterSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils AnnotationFilter`() {
        AnnotationFilter(
            mockk(),
            fixture.listFixture<String>().toSet()
        ) fulfils ProcessorContract.AnnotationFilter::class
    }

    @Test
    fun `Given filterAnnotation is called with a empty map it does nothing`() {
        // Given
        val annotations: Map<String, String> = emptyMap()

        // When
        val actual = AnnotationFilter(
            mockk(),
            fixture.listFixture<String>().toSet()
        ).filterAnnotation(annotations)

        // Then
        actual mustBe emptyMap()
    }

    @Test
    fun `Given filterAnnotation is called with map, which contains invalid SourcesSets it filters them out and warns`() {
        // Given
        val annotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        // When
        val actual = AnnotationFilter(
            logger,
            fixture.listFixture<String>().toSet()
        ).filterAnnotation(annotations)

        // Then
        val keys = annotations.keys.toList()
        val sourceSets = annotations.values.toList()
        actual mustBe emptyMap()
        verify(exactly = 1) {
            logger.warn("${keys[0]} is not applicable since is SourceSet (${sourceSets[0]}) is not a know shared source.")
        }
        verify(exactly = 1) {
            logger.warn("${keys[1]} is not applicable since is SourceSet (${sourceSets[1]}) is not a know shared source.")
        }
        verify(exactly = 1) {
            logger.warn("${keys[2]} is not applicable since is SourceSet (${sourceSets[2]}) is not a know shared source.")
        }
    }

    @Test
    fun `Given filterAnnotation is called with map, which shadow build-in Annotations it filters them out and warns`() {
        // Given
        val annotations: Map<String, String> = mapOf(
            ANNOTATION_PLATFORM_NAME to fixture.fixture(),
            ANNOTATION_COMMON_NAME to fixture.fixture(),
            ANNOTATION_SHARED_NAME to fixture.fixture(),
            RELAXATION_NAME to fixture.fixture(),
        )
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        // When
        val actual = AnnotationFilter(
            logger,
            annotations.values.toSet()
        ).filterAnnotation(annotations)

        // Then
        val keys = annotations.keys.toList()
        actual mustBe emptyMap()
        verify(exactly = 1) {
            logger.warn("${keys[0]} is not applicable since is shadows a build-in annotation.")
        }
        verify(exactly = 1) {
            logger.warn("${keys[1]} is not applicable since is shadows a build-in annotation.")
        }
        verify(exactly = 1) {
            logger.warn("${keys[2]} is not applicable since is shadows a build-in annotation.")
        }
        verify(exactly = 1) {
            logger.warn("${keys[3]} is not applicable since is shadows a build-in annotation.")
        }
    }

    @Test
    fun `Given filterAnnotation is called with map, which contains valid SourcesSets it returns a map with them`() {
        // Given
        val annotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        // When
        val actual = AnnotationFilter(
            logger,
            annotations.values.toSet()
        ).filterAnnotation(annotations)

        // Then
        actual mustBe annotations
        verify(exactly = 0) {
            logger.warn(any())
        }
    }

    @Test
    fun `Given isApplicableAnnotation is called with a KSAnnotation it returns false if it takes less then 1 argument`() {
        // Given
        val annotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        val annotation: KSAnnotation = mockk()

        every {
            annotation.arguments
        } returns emptyList()

        // When
        val actual = AnnotationFilter(
            logger,
            annotations.values.toSet()
        ).isApplicableAnnotation(annotation)

        // Then
        actual mustBe false
    }

    @Test
    fun `Given isApplicableAnnotation is called with a KSAnnotation it returns false if it takes more then 1 argument`() {
        // Given
        val annotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        val annotation: KSAnnotation = mockk()

        every {
            annotation.arguments
        } returns listOf(mockk(), mockk())

        // When
        val actual = AnnotationFilter(
            logger,
            annotations.values.toSet()
        ).isApplicableAnnotation(annotation)

        // Then
        actual mustBe false
    }

    @Test
    fun `Given isApplicableAnnotation is called with a KSAnnotation it returns false if its argument is not an Array`() {
        // Given
        val annotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        val annotation: KSAnnotation = mockk()
        val argument: KSValueArgument = mockk()

        every {
            annotation.arguments
        } returns listOf(argument)

        every {
            argument.value
        } returns Any()

        // When
        val actual = AnnotationFilter(
            logger,
            annotations.values.toSet()
        ).isApplicableAnnotation(annotation)

        // Then
        actual mustBe false
    }

    @Test
    fun `Given isApplicableAnnotation is called with a KSAnnotation it returns false if its argument contains not an List of KSType`() {
        // Given
        val annotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        val annotation: KSAnnotation = mockk()
        val argument: KSValueArgument = mockk()

        every {
            annotation.arguments
        } returns listOf(argument)

        every {
            argument.value
        } returns listOf(Any(), Any())

        // When
        val actual = AnnotationFilter(
            logger,
            annotations.values.toSet()
        ).isApplicableAnnotation(annotation)

        // Then
        actual mustBe false
    }

    @Test
    fun `Given isApplicableAnnotation is called with a KSAnnotation it returns true if its argument contains an empty List`() {
        // Given
        val annotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        val annotation: KSAnnotation = mockk()
        val argument: KSValueArgument = mockk()

        every {
            annotation.arguments
        } returns listOf(argument)

        every {
            argument.value
        } returns listOf<Any?>()

        // When
        val actual = AnnotationFilter(
            logger,
            annotations.values.toSet()
        ).isApplicableAnnotation(annotation)

        // Then
        actual mustBe true
    }

    @Test
    fun `Given isApplicableAnnotation is called with a KSAnnotation it returns true if its argument contains an List of KSType`() {
        // Given
        val annotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk(relaxUnitFun = true)

        val annotation: KSAnnotation = mockk()
        val argument: KSValueArgument = mockk()

        every {
            annotation.arguments
        } returns listOf(argument)

        every {
            argument.value
        } returns listOf(mockk<KSType>(), mockk<KSType>())

        // When
        val actual = AnnotationFilter(
            logger,
            annotations.values.toSet()
        ).isApplicableAnnotation(annotation)

        // Then
        actual mustBe true
    }
}
