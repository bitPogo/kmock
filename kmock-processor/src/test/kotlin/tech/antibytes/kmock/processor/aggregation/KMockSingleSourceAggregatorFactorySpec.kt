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
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.mapFixture
import tech.antibytes.kmock.processor.ProcessorContract.AggregatorFactory
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationFilter
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.SingleSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.util.test.fulfils

class KMockSingleSourceAggregatorFactorySpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils AggregatorFactory`() {
        KMockSingleSourceAggregator fulfils AggregatorFactory::class
    }

    @Test
    fun `Given getInstance is called it returns a SourceAggregator`() {
        // Given
        val logger: KSPLogger = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val generics: GenericResolver = mockk()

        val customAnnotations: Map<String, String> = fixture.mapFixture()
        val aliases: Map<String, String> = fixture.mapFixture()

        every { annotationFilter.filterAnnotation(any()) } returns customAnnotations

        // When
        val actual = KMockSingleSourceAggregator.getInstance(
            logger = logger,
            rootPackage = fixture.fixture(),
            sourceSetValidator = sourceSetValidator,
            annotationFilter = annotationFilter,
            generics = generics,
            customAnnotations = customAnnotations,
            aliases = aliases
        )

        // Then
        actual fulfils SingleSourceAggregator::class

        verify(exactly = 1) {
            annotationFilter.filterAnnotation(customAnnotations)
        }
    }
}
