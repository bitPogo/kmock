/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.MagicStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockAggregatorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils Aggregator`() {
        KMockAggregator() fulfils ProcessorContract.Aggregator::class
    }

    @Test
    fun `Given extractInterfaces is called it retrieves all MagicStub Annotations`() {
        // Given
        val source: KSAnnotated = mockk()

        val annotation1: KSAnnotation = mockk()
        val annotation2: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation1)
            yield(annotation2)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        every {
            annotation1.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns fixture.fixture()

        every {
            annotation2.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns fixture.fixture()

        every { source.annotations } returns sourceAnnotations

        // Then
        assertFailsWith<NoSuchElementException> {
            // When
            KMockAggregator().extractInterfaces(
                annotated,
                mutableListOf()
            )
        }
    }

    @Test
    fun `Given extractInterfaces is called it filters all ill MagicStub Annotations`() {
        // Given
        val source: KSAnnotated = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        every { annotation.arguments } returns emptyList()

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MagicStub::class.qualifiedName!!

        every { source.annotations } returns sourceAnnotations

        val illegal = mutableListOf<KSAnnotated>()

        // When
        KMockAggregator().extractInterfaces(annotated, mutableListOf())

        // Then
        illegal mustBe listOf(source)
    }
}
