/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.aggregation

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.qualifier.qualifiedBy
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.KMockMulti
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Aggregator
import tech.antibytes.kmock.processor.ProcessorContract.Companion.supportedPlatforms
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

@OptIn(KMockExperimental::class)
class KMockMultiSourceAggregatorAgnosticAnnotationSpec {
    private val fixture = kotlinFixture {
        addGenerator(
            String::class,
            StringAlphaGenerator,
            qualifiedBy("stringAlpha"),
        )
    }

    @Test
    fun `It fulfils Aggregator`() {
        KMockMultiSourceAggregator(
            mockk(),
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ) fulfils Aggregator::class
    }

    @Test
    fun `It fulfils SourceAggregator`() {
        KMockMultiSourceAggregator(
            mockk(),
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ) fulfils MultiSourceAggregator::class
    }

    @Test
    fun `Given extractKmockInterfaces is called it fails the Annotated if no file was found`() {
        // Given
        val resolver: Resolver = mockk()
        val logger: KSPLogger = mockk()
        val annotation1: KSAnnotated = mockk()
        val annotation2: KSAnnotated = mockk()

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(annotation1)
            yield(annotation2)
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { annotation1.parent } returns null
        every { annotation2.parent } returns null
        every { logger.error(any(), any()) } just Runs

        // When
        val actual = KMockMultiSourceAggregator(
            logger,
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractKmockInterfaces(resolver)

        // Then
        actual.common mustBe emptyList()
        actual.shared mustBe emptyMap()
        actual.platform mustBe emptyList()

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(KMockMulti::class.qualifiedName!!, false)
        }
        verify(exactly = 1) {
            logger.error("Unprocessable source.", annotation1)
        }
        verify(exactly = 1) {
            logger.error("Unprocessable source.", annotation2)
        }
    }

    @Test
    fun `Given extractKmockInterfaces is called it fails the Annotated if context is unknown`() {
        // Given
        val resolver: Resolver = mockk()
        val logger: KSPLogger = mockk()
        val annotation1: KSAnnotated = mockk()
        val annotation2: KSAnnotated = mockk()
        val context1: KSFile = mockk()
        val context2: KSFile = mockk()

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(annotation1)
            yield(annotation2)
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { annotation1.parent } returns null
        every {
            context1.filePath
        } returns "${fixture.fixture<String>()}/src/${fixture.fixture<String>()}/${fixture.fixture<String>()}"

        every { annotation2.parent } returns null
        every {
            context2.filePath
        } returns "${fixture.fixture<String>()}/src/${fixture.fixture<String>()}/${fixture.fixture<String>()}"
        every { logger.error(any(), any()) } just Runs

        // When
        val actual = KMockMultiSourceAggregator(
            logger,
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractKmockInterfaces(resolver)

        // Then
        actual.common mustBe emptyList()
        actual.shared mustBe emptyMap()
        actual.platform mustBe emptyList()

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(KMockMulti::class.qualifiedName!!, false)
        }
        verify(exactly = 1) {
            logger.error("Unprocessable source.", annotation1)
        }
        verify(exactly = 1) {
            logger.error("Unprocessable source.", annotation2)
        }
    }

    @Test
    fun `Given extractKmockInterfaces is called it resolves the Annotated for common sources`() {
        // Given
        val resolver: Resolver = mockk()
        val annotation1: KSAnnotated = mockk()
        val annotation2: KSAnnotated = mockk()
        val context1: KSFile = mockk()
        val context2: KSFile = mockk()

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(annotation1)
            yield(annotation2)
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { annotation1.parent } returns context1
        every {
            context1.filePath
        } returns "${fixture.fixture<String>()}/src/commonTest/${fixture.fixture<String>()}"

        every { annotation2.parent } returns context2
        every {
            context2.filePath
        } returns "${fixture.fixture<String>()}/src/commonTest/${fixture.fixture<String>()}"

        // When
        val actual = KMockMultiSourceAggregator(
            mockk(),
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractKmockInterfaces(resolver)

        // Then
        actual.common mustBe annotated.toList()
        actual.shared mustBe emptyMap()
        actual.platform mustBe emptyList()

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(KMockMulti::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractKmockInterfaces is called it resolves the Annotated for shared sources`() {
        // Given
        val resolver: Resolver = mockk()
        val annotation1: KSAnnotated = mockk()
        val annotation2: KSAnnotated = mockk()
        val context1: KSFile = mockk()
        val context2: KSFile = mockk()
        val source1: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val source2: String = fixture.fixture(qualifiedBy("stringAlpha"))

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(annotation1)
            yield(annotation2)
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { annotation1.parent } returns context1
        every {
            context1.filePath
        } returns "${fixture.fixture<String>()}/src/${source1}Test/${fixture.fixture<String>()}"

        every { annotation2.parent } returns context2
        every {
            context2.filePath
        } returns "${fixture.fixture<String>()}/src/${source2}Test/${fixture.fixture<String>()}"

        // When
        val actual = KMockMultiSourceAggregator(
            mockk(),
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractKmockInterfaces(resolver)

        // Then
        actual.common mustBe emptyList()
        actual.shared mustBe mapOf(
            "${source1}Test" to listOf(annotation1),
            "${source2}Test" to listOf(annotation2),
        )
        actual.platform mustBe emptyList()

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(KMockMulti::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractKmockInterfaces is called it resolves the Annotated for platform sources`() {
        // Given
        val resolver: Resolver = mockk()
        val platforms = supportedPlatforms.toList()
        val contexts: List<KSFile> = platforms.map { _ ->
            mockk()
        }
        val annotations: List<KSAnnotated> = platforms.map { _ ->
            mockk()
        }

        val annotated: Sequence<KSAnnotated> = sequenceOf(*annotations.toTypedArray())

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        annotations.forEachIndexed { idx, annotation ->
            every { annotation.parent } returns contexts[idx]
            every {
                contexts[idx].filePath
            } returns "${fixture.fixture<String>()}/src/${platforms[idx]}/${fixture.fixture<String>()}"
        }

        // When
        val actual = KMockMultiSourceAggregator(
            mockk(),
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractKmockInterfaces(resolver)

        // Then
        actual.common mustBe emptyList()
        actual.shared mustBe emptyMap()
        actual.platform mustBe annotations

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(KMockMulti::class.qualifiedName!!, false)
        }
    }
}
