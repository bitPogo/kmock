/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSValueArgument
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.MagicStub
import tech.antibytes.kmock.MagicStubCommon
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockAggregatorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils Aggregator`() {
        KMockAggregator(mockk()) fulfils ProcessorContract.Aggregator::class
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
            KMockAggregator(mockk()).extractInterfaces(annotated)
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
        } returns if (fixture.random.nextBoolean()) {
            MagicStub::class.qualifiedName!!
        } else {
            MagicStubCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        // When
        val (illegal, _, _) = KMockAggregator(mockk()).extractInterfaces(annotated)

        // Then
        illegal mustBe listOf(source)
    }

    @Test
    fun `Given extractInterfaces is called it filters all non class types and reports an error`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSAnnotated = mockk()
        val file: KSFile = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSTypeAlias = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns if (fixture.random.nextBoolean()) {
            MagicStub::class.qualifiedName!!
        } else {
            MagicStubCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { file.parent } returns null
        every { source.parent } returns file

        every { logger.error(any()) } just Runs

        // When
        KMockAggregator(logger).extractInterfaces(annotated)

        // Then
        verify(exactly = 1) { logger.error("Cannot stub non interfaces.") }
    }

    @Test
    fun `Given extractInterfaces is called it filters all implementation class types and reports an error`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSAnnotated = mockk()
        val file: KSFile = mockk()

        val selection = listOf(
            ClassKind.CLASS,
            ClassKind.ENUM_CLASS,
            ClassKind.ENUM_ENTRY,
            ClassKind.OBJECT,
            ClassKind.ANNOTATION_CLASS
        )

        val selector = fixture.random.nextInt(0, selection.lastIndex)

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val className: String = fixture.fixture()
        val packageName: String = fixture.fixture()

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns if (fixture.random.nextBoolean()) {
            MagicStub::class.qualifiedName!!
        } else {
            MagicStubCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns selection[selector]

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { source.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns className
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        // When
        KMockAggregator(logger).extractInterfaces(annotated)

        // Then
        verify(exactly = 1) { logger.error("Cannot stub non interface `$packageName`.`$className`.") }
    }

    @Test
    fun `Given extractInterfaces is called it returns all found interfaces`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSAnnotated = mockk()
        val file: KSFile = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val className: String = fixture.fixture()
        val packageName: String = fixture.fixture()

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns if (fixture.random.nextBoolean()) {
            MagicStub::class.qualifiedName!!
        } else {
            MagicStubCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { source.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns className
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        // When
        val (_, interfaces, _) = KMockAggregator(logger).extractInterfaces(annotated)

        // Then
        interfaces mustBe listOf(declaration)
    }

    @Test
    fun `Given extractInterfaces is called it returns the corresponding source files`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSAnnotated = mockk()
        val file: KSFile = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val className: String = fixture.fixture()
        val packageName: String = fixture.fixture()

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns if (fixture.random.nextBoolean()) {
            MagicStub::class.qualifiedName!!
        } else {
            MagicStubCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { source.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns className
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        // When
        val (_, _, sourceFiles) = KMockAggregator(logger).extractInterfaces(annotated)

        // Then
        sourceFiles mustBe listOf(file)
    }
}
