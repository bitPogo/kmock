/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.aggregation

import com.google.devtools.ksp.processing.KSPLogger
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract.Aggregator
import tech.antibytes.kmock.processor.ProcessorContract.AggregatorFactory
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationFilter
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fulfils

class KMockSourceAggregatorFactorySpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils AggregatorFactory`() {
        KMockSourceAggregator fulfils AggregatorFactory::class
    }

    @Test
    fun `Given getInstance is called it returns a Aggregator`() {
        // Given
        val logger: KSPLogger = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val generics: GenericResolver = mockk()

        val customAnnotations: Map<String, String> = fixture.mapFixture()
        val aliases: Map<String, String> = fixture.mapFixture()

        every { annotationFilter.filterAnnotation(any()) } returns customAnnotations

        // When
        val actual = KMockSourceAggregator.getInstance(
            logger = logger,
            sourceSetValidator = sourceSetValidator,
            annotationFilter = annotationFilter,
            generics = generics,
            customAnnotations = customAnnotations,
            aliases = aliases
        )

        // Then
        actual fulfils Aggregator::class

        verify(exactly = 1) {
            annotationFilter.filterAnnotation(customAnnotations)
        }
    }
}
