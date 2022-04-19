/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.Relaxer
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class KMockAggregatorRelaxerSpec {
    private val fixture = kotlinFixture { configuration ->
        configuration.addGenerator(
            String::class,
            StringAlphaGenerator,
            named("stringAlpha")
        )
    }

    @Test
    fun `It fulfils Aggregator`() {
        KMockAggregator(
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ) fulfils ProcessorContract.Aggregator::class
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it does nothing if no relaxer was defined`() {
        // Given
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {}

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        // When
        val relaxer = KMockAggregator(
            logger, mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractRelaxer(resolver)

        // Then
        relaxer mustBe null
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(Relaxer::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated function does not take exactly 1 parameter`() {
        // Given
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }
        val functionName: String = fixture.fixture()
        val packageName: String = fixture.fixture()
        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns emptyList()
        every { source.returnType.toString() } returns "Any"
        every { parameter.type.resolve().toString() } returns "String"

        every { source.typeParameters } returns listOf(typeParameter)
        every { typeParameter.bounds } returns sequence { }
        every { typeParameter.isReified } returns true
        every { typeParameter.toString() } returns "String"

        every { source.packageName.asString() } returns packageName
        every { source.simpleName.asString() } returns functionName

        every { logger.error(any()) } just Runs

        // When
        KMockAggregator(
            logger,
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractRelaxer(resolver)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(Relaxer::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions parameter is not a string`() {
        // Given
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val functionName: String = fixture.fixture()
        val packageName: String = fixture.fixture()
        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { source.returnType.toString() } returns "Any"
        every { parameter.type.resolve().toString() } returns "Any"

        every { source.typeParameters } returns listOf(typeParameter)
        every { typeParameter.bounds } returns sequence { }
        every { typeParameter.isReified } returns true
        every { typeParameter.toString() } returns "Any"

        every { source.packageName.asString() } returns packageName
        every { source.simpleName.asString() } returns functionName

        every { logger.error(any()) } just Runs

        // When
        KMockAggregator(
            logger,
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractRelaxer(resolver)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(Relaxer::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions typeParameter are not exactly 1`() {
        // Given
        val logger: KSPLogger = mockk()
        val source: KSFunctionDeclaration = mockk()
        val resolver: Resolver = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val functionName: String = fixture.fixture()
        val packageName: String = fixture.fixture()
        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter, parameter)
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
        KMockAggregator(
            logger,
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractRelaxer(resolver)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(Relaxer::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions typeParameter has a bound`() {
        // Given
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val bound: Sequence<KSTypeReference> = sequence {
            yield(mockk())
        }

        val functionName: String = fixture.fixture()
        val packageName: String = fixture.fixture()
        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { source.returnType.toString() } returns "Any"
        every { parameter.type.resolve().toString() } returns "String"

        every { source.typeParameters } returns listOf(typeParameter)
        every { typeParameter.bounds } returns bound
        every { typeParameter.isReified } returns true
        every { typeParameter.toString() } returns "Any"

        every { source.packageName.asString() } returns packageName
        every { source.simpleName.asString() } returns functionName

        every { logger.error(any()) } just Runs

        // When
        KMockAggregator(
            logger,
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractRelaxer(resolver)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(Relaxer::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it fails if the annotated functions typeParameter does not match the ReturnType`() {
        // Given
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val functionName: String = fixture.fixture()
        val packageName: String = fixture.fixture()
        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { source.modifiers } returns setOf(Modifier.INLINE)
        every { source.parameters } returns listOf(parameter)
        every { source.returnType.toString() } returns "Any"
        every { parameter.type.resolve().toString() } returns "String"

        every { source.typeParameters } returns listOf(typeParameter)
        every { typeParameter.bounds } returns sequence { }
        every { typeParameter.isReified } returns true
        every { typeParameter.toString() } returns "String"

        every { source.packageName.asString() } returns packageName
        every { source.simpleName.asString() } returns functionName

        every { logger.error(any()) } just Runs

        // When
        KMockAggregator(
            logger,
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractRelaxer(resolver)

        // Then
        verify(exactly = 1) {
            logger.error("Invalid Relaxer!")
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(Relaxer::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractRelaxer is called with KSAnnotated it returns the FunctionName`() {
        // Given
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()
        val source: KSFunctionDeclaration = mockk()
        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source)
        }

        val functionName: String = fixture.fixture()
        val packageName: String = fixture.fixture()
        val parameter: KSValueParameter = mockk()
        val typeParameter: KSTypeParameter = mockk()

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

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
        val actual = KMockAggregator(
            logger,
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractRelaxer(resolver)

        // Then
        verify(exactly = 0) { logger.error(any()) }

        actual mustBe ProcessorContract.Relaxer(packageName, functionName)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(Relaxer::class.qualifiedName!!, false)
        }
    }
}
