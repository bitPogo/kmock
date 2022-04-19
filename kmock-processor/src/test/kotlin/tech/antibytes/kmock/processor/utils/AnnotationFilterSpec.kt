/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
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
}
