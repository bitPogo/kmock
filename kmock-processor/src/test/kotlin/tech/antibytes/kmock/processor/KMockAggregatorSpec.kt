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
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockAggregatorSpec {
    private val fixture = kotlinFixture { configuration ->
        configuration.addGenerator(
            String::class,
            StringAlphaGenerator,
            named("stringAlpha")
        )
    }

    @Test
    fun `It fulfils Aggregator`() {
        KMockAggregator(mockk()) fulfils ProcessorContract.Aggregator::class
    }

    @Test
    fun `Given extractInterfaces is called it retrieves all Stub Annotations`() {
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
    fun `Given extractInterfaces is called it filters all ill Stub Annotations`() {
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
        } returns if (fixture.random.access { it.nextBoolean() }) {
            Mock::class.qualifiedName!!
        } else {
            if (fixture.random.access { it.nextBoolean() }) {
                MockCommon::class.qualifiedName!!
            } else {
                MockShared::class.qualifiedName!!
            }
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
        } returns if (fixture.random.access { it.nextBoolean() }) {
            Mock::class.qualifiedName!!
        } else {
            MockCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
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
    fun `Given extractInterfaces is called it filters all non class types and reports an error for Shared Sources`() {
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
        } returns MockShared::class.qualifiedName!!

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns fixture.fixture<String>()
        every { arguments[1].value } returns values
        every { arguments.size } returns 2
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

        val selector = fixture.random.access { it.nextInt(0, selection.lastIndex) }

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

        val className: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns if (fixture.random.access { it.nextBoolean() }) {
            Mock::class.qualifiedName!!
        } else {
            MockCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
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
        verify(exactly = 1) { logger.error("Cannot stub non interface $packageName.$className.") }
    }

    @Test
    fun `Given extractInterfaces is called it filters all implementation class types and reports an error for Shared Sources`() {
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

        val selector = fixture.random.access { it.nextInt(0, selection.lastIndex) }

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

        val className: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns fixture.fixture<String>()
        every { arguments[1].value } returns values
        every { arguments.size } returns 2
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
        verify(exactly = 1) { logger.error("Cannot stub non interface $packageName.$className.") }
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

        val className: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns if (fixture.random.access { it.nextBoolean() }) {
            Mock::class.qualifiedName!!
        } else {
            MockCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
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
        interfaces mustBe listOf(ProcessorContract.InterfaceSource("", declaration))
    }

    @Test
    fun `Given extractInterfaces is called it returns all found interfaces for SharedSources`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSAnnotated = mockk()
        val file: KSFile = mockk()
        val marker = fixture.fixture<String>()

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

        val className: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns marker
        every { arguments[1].value } returns values
        every { arguments.size } returns 2
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
        interfaces mustBe listOf(ProcessorContract.InterfaceSource(marker, declaration))
    }

    @Test
    fun `Given extractInterfaces is called it returns all found interfaces for SharedSource while respecting doublets`() {
        // Given
        val logger: KSPLogger = mockk()
        val source0: KSAnnotated = mockk()
        val source1: KSAnnotated = mockk()
        val file: KSFile = mockk()

        val marker0 = fixture.fixture<String>()
        val marker1 = fixture.fixture<String>()

        val annotation0: KSAnnotation = mockk()
        val sourceAnnotations0: Sequence<KSAnnotation> = sequence {
            yield(annotation0)
        }
        val annotation1: KSAnnotation = mockk()
        val sourceAnnotations1: Sequence<KSAnnotation> = sequence {
            yield(annotation1)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source0)
            yield(source1)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments0: List<KSValueArgument> = mockk()
        val arguments1: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val className: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            annotation0.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every {
            annotation1.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { source0.annotations } returns sourceAnnotations0
        every { source1.annotations } returns sourceAnnotations1

        every { annotation0.arguments } returns arguments0
        every { arguments0.isEmpty() } returns false
        every { arguments0[0].value } returns marker0
        every { arguments0[1].value } returns values
        every { arguments0.size } returns 2

        every { annotation1.arguments } returns arguments1
        every { arguments1.isEmpty() } returns false
        every { arguments1[0].value } returns marker1
        every { arguments1[1].value } returns values
        every { arguments1.size } returns 2

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { source0.parent } returns file
        every { source1.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns className
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        // When
        val (_, interfaces, _) = KMockAggregator(logger).extractInterfaces(annotated)

        // Then
        interfaces mustBe listOf(
            ProcessorContract.InterfaceSource(marker0, declaration),
            ProcessorContract.InterfaceSource(marker1, declaration)
        )
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

        val className: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns if (fixture.random.access { it.nextBoolean() }) {
            Mock::class.qualifiedName!!
        } else {
            MockCommon::class.qualifiedName!!
        }

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
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

    @Test
    fun `Given extractInterfaces is called it returns the corresponding source files for Shared Sources`() {
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

        val className: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { source.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns fixture.fixture<String>()
        every { arguments[1].value } returns values
        every { arguments.size } returns 2
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

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it does nothing if no relaxer was defined`() {
        // Given
        val logger: KSPLogger = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}

        // When
        val relaxer = KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        relaxer mustBe null
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated function is not inlined`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        every { source.modifiers } returns emptySet()
        every { logger.error(any()) } just Runs

        every { source.packageName.asString() } returns fixture.fixture()
        every { source.simpleName.asString() } returns fixture.fixture()

        // When
        KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated function does not take exactly 1 parameter`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns emptyList()
        every { logger.error(any()) } just Runs

        every { source.packageName.asString() } returns fixture.fixture()
        every { source.simpleName.asString() } returns fixture.fixture()

        // When
        KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions parameter is not a string`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val parameter: KSValueParameter = mockk()

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { parameter.type.resolve().toString() } returns "Any"
        every { logger.error(any()) } just Runs

        every { source.packageName.asString() } returns fixture.fixture()
        every { source.simpleName.asString() } returns fixture.fixture()

        // When
        KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions typeParameter are not exactly 1`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val parameter: KSValueParameter = mockk()

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { parameter.type.resolve().toString() } returns "String"

        every { source.typeParameters } returns emptyList()
        every { source.returnType } returns mockk()

        every { logger.error(any()) } just Runs

        every { source.packageName.asString() } returns fixture.fixture()
        every { source.simpleName.asString() } returns fixture.fixture()

        // When
        KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions typeParameter has a bound`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        val bound: Sequence<KSTypeReference> = sequence {
            yield(mockk())
        }

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { parameter.type.resolve().toString() } returns "String"

        every { source.typeParameters } returns listOf(typeParameter)
        every { typeParameter.bounds } returns bound

        every { source.returnType } returns mockk()

        every { logger.error(any()) } just Runs

        every { source.packageName.asString() } returns fixture.fixture()
        every { source.simpleName.asString() } returns fixture.fixture()

        // When
        KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions typeParameter is not Refined`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { parameter.type.resolve().toString() } returns "String"

        every { source.returnType } returns mockk()

        every { source.typeParameters } returns listOf(typeParameter)
        every { typeParameter.bounds } returns sequence { }
        every { typeParameter.isReified } returns false

        every { logger.error(any()) } just Runs

        every { source.packageName.asString() } returns fixture.fixture()
        every { source.simpleName.asString() } returns fixture.fixture()

        // When
        KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions typeParameter does not match the ReturnType`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()
        val returnType: KSTypeReference = mockk()

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { source.returnType } returns returnType
        every { parameter.type.resolve().toString() } returns "String"

        every { source.typeParameters } returns listOf(typeParameter)
        every { typeParameter.bounds } returns sequence { }
        every { typeParameter.isReified } returns true

        every { source.packageName.asString() } returns fixture.fixture()
        every { source.simpleName.asString() } returns fixture.fixture()

        every { logger.error(any()) } just Runs

        // When
        KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it returns the FunctionName`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val functionName: String = fixture.fixture()
        val packageName: String = fixture.fixture()
        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { source.returnType.toString() } returns "Any"
        every { parameter.type.resolve().toString() } returns "String"

        every { source.typeParameters } returns listOf(typeParameter)
        every { typeParameter.bounds } returns sequence { }
        every { typeParameter.isReified } returns true
        every { typeParameter.toString() } returns "Any"

        every { source.packageName.asString() } returns packageName
        every { source.simpleName.asString() } returns functionName

        every { logger.error(any()) } just Runs

        // When
        val actual = KMockAggregator(logger).extractRelaxer(annotated)

        // Then
        verify(exactly = 0) { logger.error(any()) }

        actual mustBe ProcessorContract.Relaxer(packageName, functionName)
    }
}
