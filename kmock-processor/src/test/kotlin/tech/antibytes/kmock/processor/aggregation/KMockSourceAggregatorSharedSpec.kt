/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.aggregation

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
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
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Aggregator
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationFilter
import tech.antibytes.kmock.processor.ProcessorContract.SingleSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockSourceAggregatorSharedSpec {
    private val fixture = kotlinFixture { configuration ->
        configuration.addGenerator(
            String::class,
            StringAlphaGenerator,
            named("stringAlpha")
        )
    }

    @Test
    fun `It fulfils Aggregator`() {
        KMockSingleSourceAggregator(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ) fulfils Aggregator::class
    }

    @Test
    fun `It fulfils SourceAggregator`() {
        KMockSingleSourceAggregator(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ) fulfils SingleSourceAggregator::class
    }

    @Test
    fun `Given extractSharedInterfaces is called it resolves the Annotated Thing as ill, if no KMockAnnotation or CustomAnnotation was found`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 3)
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()

        val annotation1: KSAnnotation = mockk()
        val annotation2: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation1)
            yield(annotation2)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation1.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns fixture.fixture()

        every {
            annotation2.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns fixture.fixture()

        every { symbol.annotations } returns sourceAnnotations

        // When
        val (illegal, _, _) = KMockSingleSourceAggregator(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            customAnnotations,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol, symbol, symbol, symbol)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }

        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it filters illegal shared sources`() {
        // Given
        val symbol: KSAnnotated = mockk()
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val arguments: List<KSValueArgument> = listOf(
            mockk(),
            mockk(relaxed = true)
        )

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { symbol.annotations } returns sourceAnnotations

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { annotation.arguments } returns arguments
        every { arguments[0].value } returns null

        every { sourceSetValidator.isValidateSourceSet(any()) } returns false

        // When
        val (illegal, _, _) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            sourceSetValidator,
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol)

        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it filters illegal custom shared sources`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 3)
        val symbol: KSAnnotated = mockk()
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val arguments: List<KSValueArgument> = listOf(
            mockk(),
            mockk(relaxed = true)
        )

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { symbol.annotations } returns sourceAnnotations

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { annotation.arguments } returns arguments
        every { arguments[0].value } returns null

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns false

        // When
        val (illegal, _, _) = KMockSingleSourceAggregator(
            logger,
            annotationFilter,
            sourceSetValidator,
            mockk(),
            customAnnotations,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol, symbol, symbol, symbol)

        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 4) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it filters all ill Shared Annotations`() {
        // Given
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { annotation.arguments } returns emptyList()

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        // When
        val (illegal, _, _) = KMockSingleSourceAggregator(
            mockk(),
            mockk(),
            sourceSetValidator,
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol)
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it filters all ill custom Shared Annotations`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 3)
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { annotation.arguments } returns emptyList()

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns true

        every { symbol.annotations } returns sourceAnnotations

        // When
        val (illegal, _, _) = KMockSingleSourceAggregator(
            mockk(),
            annotationFilter,
            sourceSetValidator,
            mockk(),
            customAnnotations,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol, symbol, symbol, symbol)
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 4) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it filters all non class types and reports an error for Shared Sources`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSTypeAlias = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val allowedSourceSets: Set<String> = setOf(fixture.fixture())

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns values
        every { arguments.size } returns 2
        every { type.declaration } returns declaration

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockSingleSourceAggregator(
                logger,
                mockk(),
                sourceSetValidator,
                mockk(),
                emptyMap(),
                emptyMap(),
            ).extractSharedInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interfaces."
        verify(exactly = 1) { logger.error("Cannot stub non interfaces.") }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it filters all non class types and reports an error for custom Shared Sources`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSTypeAlias = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments

        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { arguments.size } returns 1

        every { type.declaration } returns declaration
        every { file.parent } returns null
        every { symbol.parent } returns file

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns true

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockSingleSourceAggregator(
                logger,
                annotationFilter,
                sourceSetValidator,
                mockk(),
                customAnnotations,
                emptyMap(),
            ).extractSharedInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interfaces."
        verify(exactly = 1) { logger.error("Cannot stub non interfaces.") }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 4) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it filters all implementation class types and reports an error for Shared Sources`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

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
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))
        val allowedSourceSets: Set<String> = setOf(fixture.fixture())

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { type.declaration } returns declaration
        every { declaration.classKind } returns selection[selector]

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { logger.error(any()) } just Runs

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockSingleSourceAggregator(
                logger,
                mockk(),
                sourceSetValidator,
                mockk(),
                emptyMap(),
                emptyMap(),
            ).extractSharedInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interface $packageName.$simpleName."
        verify(exactly = 1) { logger.error("Cannot stub non interface $packageName.$simpleName.") }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it filters all implementation class types and reports an error for custom Shared Sources`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 3)
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

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
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { arguments.size } returns 1

        every { type.declaration } returns declaration
        every { declaration.classKind } returns selection[selector]

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns true

        every { logger.error(any()) } just Runs

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockSingleSourceAggregator(
                logger,
                annotationFilter,
                sourceSetValidator,
                mockk(),
                customAnnotations,
                emptyMap(),
            ).extractSharedInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interface $packageName.$simpleName."
        verify(exactly = 1) { logger.error("Cannot stub non interface $packageName.$simpleName.") }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 4) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns all found interfaces for SharedSources`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()
        val marker = fixture.fixture<String>()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        val genericResolver: ProcessorContract.GenericResolver = mockk()
        val generics: Map<String, List<KSTypeReference>>? = if (fixture.fixture()) {
            emptyMap()
        } else {
            null
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns marker
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            sourceSetValidator,
            genericResolver,
            emptyMap(),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = marker,
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = generics
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns all found interfaces for custom SharedSources`() {
        // Given
        val marker = fixture.fixture<String>()
        val customAnnotations: Map<String, String> = mapOf(
            fixture.fixture<String>() to marker
        )
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        val genericResolver: ProcessorContract.GenericResolver = mockk()
        val generics: Map<String, List<KSTypeReference>>? = if (fixture.fixture()) {
            emptyMap()
        } else {
            null
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { arguments.size } returns 1

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            annotationFilter,
            sourceSetValidator,
            genericResolver,
            customAnnotations,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = marker,
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = generics
            )
        )

        verify(exactly = 2) { genericResolver.extractGenerics(declaration, any()) }

        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 2) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns all found interfaces for SharedSource while respecting doublets`() {
        // Given
        val logger: KSPLogger = mockk()
        val resolver: Resolver = mockk()

        val source0: KSAnnotated = mockk()
        val source1: KSAnnotated = mockk()
        val source2: KSAnnotated = mockk()
        val source3: KSAnnotated = mockk()

        val file: KSFile = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

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
        val annotation2: KSAnnotation = mockk()
        val sourceAnnotations2: Sequence<KSAnnotation> = sequence {
            yield(annotation2)
        }
        val annotation3: KSAnnotation = mockk()
        val sourceAnnotations3: Sequence<KSAnnotation> = sequence {
            yield(annotation3)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source0)
            yield(source1)
            yield(source2)
            yield(source3)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments0: List<KSValueArgument> = mockk()
        val arguments1: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val genericResolver: ProcessorContract.GenericResolver = mockk()
        val generics: Map<String, List<KSTypeReference>>? = if (fixture.fixture()) {
            emptyMap()
        } else {
            null
        }

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation0.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every {
            annotation1.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every {
            annotation2.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every {
            annotation3.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { source0.annotations } returns sourceAnnotations0
        every { source1.annotations } returns sourceAnnotations1
        every { source2.annotations } returns sourceAnnotations2
        every { source3.annotations } returns sourceAnnotations3

        every { annotation0.arguments } returns arguments0
        every { arguments0.isEmpty() } returns false
        every { arguments0[0].value } returns marker0
        every { arguments0[1].value } returns values
        every { arguments0.size } returns 2

        every { annotation2.arguments } returns arguments0

        every { annotation1.arguments } returns arguments1
        every { arguments1.isEmpty() } returns false
        every { arguments1[0].value } returns marker1
        every { arguments1[1].value } returns values
        every { arguments1.size } returns 2

        every { annotation3.arguments } returns arguments1

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { source0.parent } returns file
        every { source1.parent } returns file
        every { source2.parent } returns file
        every { source3.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            sourceSetValidator,
            genericResolver,
            emptyMap(),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = marker0,
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = generics
            ),
            TemplateSource(
                indicator = marker1,
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = generics
            )
        )

        verify(exactly = 4) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation0)
        }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation1)
        }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation2)
        }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation3)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns all found interfaces for custom SharedSource while respecting doublets`() {
        // Given
        val marker0 = fixture.fixture<String>()
        val marker1 = fixture.fixture<String>()
        val customAnnotations: Map<String, String> = mapOf(
            fixture.fixture<String>() to marker0,
            fixture.fixture<String>() to marker1
        )
        val logger: KSPLogger = mockk()
        val source0: KSAnnotated = mockk()
        val source1: KSAnnotated = mockk()
        val source2: KSAnnotated = mockk()
        val source3: KSAnnotated = mockk()

        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation0: KSAnnotation = mockk()
        val sourceAnnotations0: Sequence<KSAnnotation> = sequence {
            yield(annotation0)
        }
        val annotation1: KSAnnotation = mockk()
        val sourceAnnotations1: Sequence<KSAnnotation> = sequence {
            yield(annotation1)
        }
        val annotation2: KSAnnotation = mockk()
        val sourceAnnotations2: Sequence<KSAnnotation> = sequence {
            yield(annotation2)
        }
        val annotation3: KSAnnotation = mockk()
        val sourceAnnotations3: Sequence<KSAnnotation> = sequence {
            yield(annotation3)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(source0)
            yield(source1)
            yield(source2)
            yield(source3)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments0: List<KSValueArgument> = mockk()
        val arguments1: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val genericResolver: ProcessorContract.GenericResolver = mockk()
        val generics: Map<String, List<KSTypeReference>>? = if (fixture.fixture()) {
            emptyMap()
        } else {
            null
        }

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation0.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.first()

        every {
            annotation2.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.first()

        every {
            annotation1.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.last()

        every {
            annotation3.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.last()

        every { source0.annotations } returns sourceAnnotations0
        every { source1.annotations } returns sourceAnnotations1
        every { source2.annotations } returns sourceAnnotations2
        every { source3.annotations } returns sourceAnnotations3

        every { annotation0.arguments } returns arguments0
        every { arguments0.isEmpty() } returns false
        every { arguments0[0].value } returns values
        every { arguments0.size } returns 1

        every { annotation2.arguments } returns arguments0

        every { annotation1.arguments } returns arguments1
        every { arguments1.isEmpty() } returns false
        every { arguments1[0].value } returns values
        every { arguments1.size } returns 1

        every { annotation3.arguments } returns arguments1

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { source0.parent } returns file
        every { source1.parent } returns file
        every { source2.parent } returns file
        every { source3.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            annotationFilter,
            sourceSetValidator,
            genericResolver,
            customAnnotations,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = marker0,
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = generics
            ),
            TemplateSource(
                indicator = marker1,
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = generics
            )
        )

        verify(exactly = 12) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 3) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation0)
        }
        verify(exactly = 3) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation1)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation0)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation1)
        }

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns the corresponding source files for Shared Sources`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))
        val allowedSourceSets: Set<String> = setOf(
            fixture.fixture()
        )

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        // When
        val (_, _, sourceFiles) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            sourceSetValidator,
            mockk(relaxed = true),
            emptyMap(),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        sourceFiles mustBe listOf(file)
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns the corresponding source files for custom Shared Sources`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 1)
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { arguments.size } returns 1

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns true

        // When
        val (_, _, sourceFiles) = KMockSingleSourceAggregator(
            logger,
            annotationFilter,
            sourceSetValidator,
            mockk(relaxed = true),
            customAnnotations,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        sourceFiles mustBe listOf(file, file)
        verify(exactly = 2) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given getSymbolsWithAnnotation is called it returns the corresponding source files, while filter non related annotations`() {
        // Given
        val sourceSetValidator: SourceSetValidator = mockk()
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val notRelatedSymbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()

        val annotation: KSAnnotation = mockk()
        val notRelatedAnnotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val notRelatedSource: Sequence<KSAnnotation> = sequence {
            yield(notRelatedAnnotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(notRelatedSymbol)
            yield(notRelatedSymbol)
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val packageName: String = fixture.fixture(named("stringAlpha"))
        val simpleName: String = fixture.fixture(named("stringAlpha"))

        val allowedSourceSets: Set<String> = setOf(
            fixture.fixture()
        )

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            notRelatedAnnotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returnsMany listOf(
            Mock::class.qualifiedName!!,
            MockCommon::class.qualifiedName!!
        )

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations
        every { notRelatedSymbol.annotations } returns notRelatedSource

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { declaration.parentDeclaration } returns null

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        // When
        val (_, interfaces, sourceFiles) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            sourceSetValidator,
            mockk(relaxed = true),
            emptyMap(),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = allowedSourceSets.first(),
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = emptyMap()
            ),
        )
        sourceFiles mustBe listOf(file)
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given getSymbolsWithAnnotation is called it returns the corresponding source files, while filter non related annotations for custom annotations`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 1)
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val notRelatedSymbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()

        val annotation: KSAnnotation = mockk()
        val notRelatedAnnotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val notRelatedSource: Sequence<KSAnnotation> = sequence {
            yield(notRelatedAnnotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(notRelatedSymbol)
            yield(notRelatedSymbol)
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val packageName: String = fixture.fixture(named("stringAlpha"))
        val simpleName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            notRelatedAnnotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returnsMany listOf(
            Mock::class.qualifiedName!!,
            MockCommon::class.qualifiedName!!
        )

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { symbol.annotations } returns sourceAnnotations
        every { notRelatedSymbol.annotations } returns notRelatedSource

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns true

        // When
        val (_, interfaces, sourceFiles) = KMockSingleSourceAggregator(
            logger,
            annotationFilter,
            sourceSetValidator,
            mockk(relaxed = true),
            customAnnotations,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = customAnnotations.values.first(),
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = emptyMap()
            ),
        )
        sourceFiles mustBe listOf(file, file)
        verify(exactly = 2) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns while mapping aliases`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))
        val alias: String = fixture.fixture(named("stringAlpha"))
        val allowedSourceSets: Set<String> = setOf(
            fixture.fixture()
        )

        val genericResolver: ProcessorContract.GenericResolver = mockk()
        val generics: Map<String, List<KSTypeReference>>? = if (fixture.fixture()) {
            emptyMap()
        } else {
            null
        }

        val mapping = mapOf("$packageName.$simpleName" to alias)

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false

        every { arguments.size } returns 2
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns values

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            sourceSetValidator,
            genericResolver,
            emptyMap(),
            mapping,
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = allowedSourceSets.first(),
                templateName = alias,
                packageName = packageName,
                template = declaration,
                generics = generics
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns while mapping aliases for custom`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 1)
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))
        val alias: String = fixture.fixture(named("stringAlpha"))

        val genericResolver: ProcessorContract.GenericResolver = mockk()
        val generics: Map<String, List<KSTypeReference>>? = if (fixture.fixture()) {
            emptyMap()
        } else {
            null
        }

        val mapping = mapOf("$packageName.$simpleName" to alias)

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.first()

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false

        every { arguments.size } returns 1
        every { arguments[0].value } returns values

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableSingleSourceAnnotation(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            annotationFilter,
            sourceSetValidator,
            genericResolver,
            customAnnotations,
            mapping,
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = customAnnotations.values.first(),
                templateName = alias,
                packageName = packageName,
                template = declaration,
                generics = generics
            )
        )

        verify(exactly = 2) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 2) {
            annotationFilter.isApplicableSingleSourceAnnotation(annotation)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }
}
