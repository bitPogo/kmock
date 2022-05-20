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
import tech.antibytes.kmock.MultiMockShared
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Aggregator
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationFilter
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockMultiSourceAggregatorSharedSpec {
    private val fixture = kotlinFixture { configuration ->
        configuration.addGenerator(
            String::class,
            StringAlphaGenerator,
            named("stringAlpha")
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
    fun `It fulfils MultiSourceAggregator`() {
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
        val (illegal, _, _) = KMockMultiSourceAggregator(
            mockk(),
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol, symbol, symbol, symbol)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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
        } returns MultiMockShared::class.qualifiedName!!

        every { annotation.arguments } returns arguments
        every { arguments[0].value } returns null

        every { sourceSetValidator.isValidateSourceSet(any()) } returns false

        // When
        val (illegal, _, _) = KMockMultiSourceAggregator(
            logger,
            fixture.fixture(),
            mockk(),
            sourceSetValidator,
            mockk(),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol)

        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns false

        // When
        val (illegal, _, _) = KMockMultiSourceAggregator(
            logger,
            fixture.fixture(),
            annotationFilter,
            sourceSetValidator,
            mockk(),
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol, symbol, symbol, symbol)

        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 4) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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
        } returns MultiMockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        // When
        val (illegal, _, _) = KMockMultiSourceAggregator(
            mockk(),
            fixture.fixture(),
            mockk(),
            sourceSetValidator,
            mockk(),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol)
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        every { symbol.annotations } returns sourceAnnotations

        // When
        val (illegal, _, _) = KMockMultiSourceAggregator(
            mockk(),
            fixture.fixture(),
            annotationFilter,
            sourceSetValidator,
            mockk(),
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol, symbol, symbol, symbol)
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 4) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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

        val mockName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns mockName
        every { arguments[2].value } returns values
        every { arguments.size } returns 3
        every { type.declaration } returns declaration

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockMultiSourceAggregator(
                logger,
                fixture.fixture(),
                mockk(),
                sourceSetValidator,
                mockk(),
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
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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

        val mockName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments

        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { type.declaration } returns declaration
        every { file.parent } returns null
        every { symbol.parent } returns file

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockMultiSourceAggregator(
                logger,
                fixture.fixture(),
                annotationFilter,
                sourceSetValidator,
                mockk(),
                customAnnotations,
            ).extractSharedInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interfaces."
        verify(exactly = 1) { logger.error("Cannot stub non interfaces.") }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 4) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))
        val allowedSourceSets: Set<String> = setOf(fixture.fixture())

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns mockName
        every { arguments[2].value } returns values
        every { arguments.size } returns 3

        every { type.declaration } returns declaration
        every { declaration.classKind } returns selection[selector]

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { logger.error(any()) } just Runs

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockMultiSourceAggregator(
                logger,
                fixture.fixture(),
                mockk(),
                sourceSetValidator,
                mockk(),
                emptyMap(),
            ).extractSharedInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interface $packageName.$qualifiedName."
        verify(exactly = 1) { logger.error("Cannot stub non interface $packageName.$qualifiedName.") }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
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
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { type.declaration } returns declaration
        every { declaration.classKind } returns selection[selector]

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        every { logger.error(any()) } just Runs

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockMultiSourceAggregator(
                logger,
                fixture.fixture(),
                annotationFilter,
                sourceSetValidator,
                mockk(),
                customAnnotations,
            ).extractSharedInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interface $packageName.$qualifiedName."
        verify(exactly = 1) { logger.error("Cannot stub non interface $packageName.$qualifiedName.") }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 4) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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
        val rootPackage: String = fixture.fixture()
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
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
        } returns MultiMockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns marker
        every { arguments[1].value } returns mockName
        every { arguments[2].value } returns values
        every { arguments.size } returns 3

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            sourceSetValidator,
            genericResolver,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "${marker}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns all found interfaces for SharedSources while respecting the full test source`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val rootPackage: String = fixture.fixture()
        val sourceSetValidator: SourceSetValidator = mockk()
        val marker = "${fixture.fixture<String>()}Test"

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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
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
        } returns MultiMockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns marker
        every { arguments[1].value } returns mockName
        every { arguments[2].value } returns values
        every { arguments.size } returns 3

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            sourceSetValidator,
            genericResolver,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = marker,
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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
        val rootPackage: String = fixture.fixture()
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
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
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            annotationFilter,
            sourceSetValidator,
            genericResolver,
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "${marker}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 2) { genericResolver.extractGenerics(declaration, any()) }

        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 2) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns all found interfaces for custom SharedSources while respecting the full source set`() {
        // Given
        val marker = "${fixture.fixture<String>()}Test"
        val customAnnotations: Map<String, String> = mapOf(
            fixture.fixture<String>() to marker
        )
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val rootPackage: String = fixture.fixture()
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
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
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            annotationFilter,
            sourceSetValidator,
            genericResolver,
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = marker,
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 2) { genericResolver.extractGenerics(declaration, any()) }

        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 2) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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
        val rootPackage: String = fixture.fixture()
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation0.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMockShared::class.qualifiedName!!

        every {
            annotation1.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMockShared::class.qualifiedName!!

        every {
            annotation2.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMockShared::class.qualifiedName!!

        every {
            annotation3.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMockShared::class.qualifiedName!!

        every { source0.annotations } returns sourceAnnotations0
        every { source1.annotations } returns sourceAnnotations1
        every { source2.annotations } returns sourceAnnotations2
        every { source3.annotations } returns sourceAnnotations3

        every { annotation0.arguments } returns arguments0
        every { arguments0.isEmpty() } returns false
        every { arguments0[0].value } returns marker0
        every { arguments0[1].value } returns mockName
        every { arguments0[2].value } returns values
        every { arguments0.size } returns 3

        every { annotation2.arguments } returns arguments0

        every { annotation1.arguments } returns arguments1
        every { arguments1.isEmpty() } returns false
        every { arguments1[0].value } returns marker1
        every { arguments1[1].value } returns mockName
        every { arguments1[2].value } returns values
        every { arguments1.size } returns 3

        every { annotation3.arguments } returns arguments1

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { source0.parent } returns file
        every { source1.parent } returns file
        every { source2.parent } returns file
        every { source3.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            sourceSetValidator,
            genericResolver,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "${marker0}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            ),
            TemplateMultiSource(
                indicator = "${marker1}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
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
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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
        val rootPackage: String = fixture.fixture()
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
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
        every { arguments0[0].value } returns mockName
        every { arguments0[1].value } returns values
        every { arguments0.size } returns 2

        every { annotation2.arguments } returns arguments0

        every { annotation1.arguments } returns arguments1
        every { arguments1.isEmpty() } returns false
        every { arguments1[0].value } returns mockName
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

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            annotationFilter,
            sourceSetValidator,
            genericResolver,
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "${marker0}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            ),
            TemplateMultiSource(
                indicator = "${marker1}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 12) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 3) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation0)
        }
        verify(exactly = 3) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation1)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation0)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation1)
        }

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns all found interfaces while using the rootPackage name`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()
        val file: KSFile = mockk()
        val rootPackage: String = fixture.fixture()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type1: KSType = mockk(relaxed = true)
        val type2: KSType = mockk(relaxed = true)
        val type3: KSType = mockk(relaxed = true)
        val declaration1: KSClassDeclaration = mockk(relaxed = true)
        val declaration2: KSClassDeclaration = mockk(relaxed = true)
        val declaration3: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type1, type2, type3)
        val mockName: String = fixture.fixture(named("stringAlpha"))

        val className1: String = fixture.fixture(named("stringAlpha"))
        val packageName1 = "${fixture.fixture<String>(named("stringAlpha"))}.${fixture.fixture<String>(named("stringAlpha"))}.${fixture.fixture<String>(named("stringAlpha"))}"
        val className2: String = fixture.fixture(named("stringAlpha"))
        val packageName2 = "${fixture.fixture<String>(named("stringAlpha"))}.${fixture.fixture<String>(named("stringAlpha"))}"
        val className3: String = fixture.fixture(named("stringAlpha"))
        val packageName3 = fixture.fixture<String>(named("stringAlpha"))

        val genericResolver: ProcessorContract.GenericResolver = mockk()
        val generics: Map<String, List<KSTypeReference>>? = if (fixture.fixture()) {
            emptyMap()
        } else {
            null
        }
        val allowedSourceSets: Set<String> = setOf(
            fixture.fixture()
        )

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { symbol.annotations } returns sourceAnnotations

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMockShared::class.qualifiedName!!

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 3
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns mockName
        every { arguments[2].value } returns values

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { type1.declaration } returns declaration1
        every { declaration1.classKind } returns ClassKind.INTERFACE
        every { declaration1.parentDeclaration } returns null
        every { declaration1.qualifiedName!!.asString() } returns className1
        every { declaration1.packageName.asString() } returns packageName1

        every { type2.declaration } returns declaration2
        every { declaration2.classKind } returns ClassKind.INTERFACE
        every { declaration2.parentDeclaration } returns null
        every { declaration2.qualifiedName!!.asString() } returns className2
        every { declaration2.packageName.asString() } returns packageName2

        every { type3.declaration } returns declaration3
        every { declaration3.classKind } returns ClassKind.INTERFACE
        every { declaration3.parentDeclaration } returns null
        every { declaration3.qualifiedName!!.asString() } returns className3
        every { declaration3.packageName.asString() } returns packageName3

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            sourceSetValidator,
            genericResolver,
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "${allowedSourceSets.first()}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration1, declaration2, declaration3),
                generics = listOf(generics, generics, generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 1) { sourceSetValidator.isValidateSourceSet(annotation) }
        verify(exactly = 1) { genericResolver.extractGenerics(declaration1, any()) }
        verify(exactly = 1) { genericResolver.extractGenerics(declaration2, any()) }
        verify(exactly = 1) { genericResolver.extractGenerics(declaration3, any()) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractSharedInterfaces is called it returns all found interfaces while using the rootPackage name for custom Annotations`() {
        // Given
        val customAnnotations: Map<String, String> = fixture.mapFixture(size = 1)
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val annotationFilter: AnnotationFilter = mockk()
        val sourceSetValidator: SourceSetValidator = mockk()
        val file: KSFile = mockk()
        val rootPackage: String = fixture.fixture()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
        }

        val type1: KSType = mockk(relaxed = true)
        val type2: KSType = mockk(relaxed = true)
        val type3: KSType = mockk(relaxed = true)
        val declaration1: KSClassDeclaration = mockk(relaxed = true)
        val declaration2: KSClassDeclaration = mockk(relaxed = true)
        val declaration3: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type1, type2, type3)
        val mockName: String = fixture.fixture(named("stringAlpha"))

        val className1: String = fixture.fixture(named("stringAlpha"))
        val packageName1 = "${fixture.fixture<String>(named("stringAlpha"))}.${fixture.fixture<String>(named("stringAlpha"))}.${fixture.fixture<String>(named("stringAlpha"))}"
        val className2: String = fixture.fixture(named("stringAlpha"))
        val packageName2 = "${fixture.fixture<String>(named("stringAlpha"))}.${fixture.fixture<String>(named("stringAlpha"))}"
        val className3: String = fixture.fixture(named("stringAlpha"))
        val packageName3 = fixture.fixture<String>(named("stringAlpha"))

        val genericResolver: ProcessorContract.GenericResolver = mockk()
        val generics: Map<String, List<KSTypeReference>>? = if (fixture.fixture()) {
            emptyMap()
        } else {
            null
        }

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every { symbol.annotations } returns sourceAnnotations

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns customAnnotations.keys.random()

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 2
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { type1.declaration } returns declaration1
        every { declaration1.classKind } returns ClassKind.INTERFACE
        every { declaration1.parentDeclaration } returns null
        every { declaration1.qualifiedName!!.asString() } returns className1
        every { declaration1.packageName.asString() } returns packageName1

        every { type2.declaration } returns declaration2
        every { declaration2.classKind } returns ClassKind.INTERFACE
        every { declaration2.parentDeclaration } returns null
        every { declaration2.qualifiedName!!.asString() } returns className2
        every { declaration2.packageName.asString() } returns packageName2

        every { type3.declaration } returns declaration3
        every { declaration3.classKind } returns ClassKind.INTERFACE
        every { declaration3.parentDeclaration } returns null
        every { declaration3.qualifiedName!!.asString() } returns className3
        every { declaration3.packageName.asString() } returns packageName3

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            annotationFilter,
            sourceSetValidator,
            genericResolver,
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "${customAnnotations.values.first()}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration1, declaration2, declaration3),
                generics = listOf(generics, generics, generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 2) { annotationFilter.isApplicableMultiSourceAnnotation(annotation) }
        verify(exactly = 2) { genericResolver.extractGenerics(declaration1, any()) }
        verify(exactly = 2) { genericResolver.extractGenerics(declaration2, any()) }
        verify(exactly = 2) { genericResolver.extractGenerics(declaration3, any()) }

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))
        val allowedSourceSets: Set<String> = setOf(
            fixture.fixture()
        )

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns mockName
        every { arguments[2].value } returns values
        every { arguments.size } returns 3

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        // When
        val (_, _, sourceFiles) = KMockMultiSourceAggregator(
            logger,
            fixture.fixture(),
            mockk(),
            sourceSetValidator,
            mockk(relaxed = true),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        sourceFiles mustBe listOf(file)
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
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
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values
        every { arguments.size } returns 2

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        // When
        val (_, _, sourceFiles) = KMockMultiSourceAggregator(
            logger,
            fixture.fixture(),
            annotationFilter,
            sourceSetValidator,
            mockk(relaxed = true),
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        sourceFiles mustBe listOf(file, file)
        verify(exactly = 2) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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
        val rootPackage: String = fixture.fixture()

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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

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
        } returns MultiMockShared::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations
        every { notRelatedSymbol.annotations } returns notRelatedSource

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns allowedSourceSets.first()
        every { arguments[1].value } returns mockName
        every { arguments[2].value } returns values
        every { arguments.size } returns 3

        every { declaration.parentDeclaration } returns null

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        // When
        val (_, interfaces, sourceFiles) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            sourceSetValidator,
            mockk(relaxed = true),
            emptyMap(),
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "${allowedSourceSets.first()}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(emptyMap()),
                dependencies = listOf(file)
            ),
        )
        sourceFiles mustBe listOf(file)
        verify(exactly = 1) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
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
        val rootPackage: String = fixture.fixture()

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

        val mockName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

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
        every { arguments.size } returns 2
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.qualifiedName!!.asString() } returns qualifiedName
        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { annotationFilter.isApplicableMultiSourceAnnotation(any()) } returns true

        // When
        val (_, interfaces, sourceFiles) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            annotationFilter,
            sourceSetValidator,
            mockk(relaxed = true),
            customAnnotations,
        ).extractSharedInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "${customAnnotations.values.first()}Test",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(emptyMap()),
                dependencies = listOf(file),
            ),
        )
        sourceFiles mustBe listOf(file, file)
        verify(exactly = 2) {
            annotationFilter.isApplicableMultiSourceAnnotation(annotation)
        }
        verify(exactly = 0) {
            sourceSetValidator.isValidateSourceSet(annotation)
        }

        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMockShared::class.qualifiedName!!, false)
        }
        customAnnotations.forEach { (annotation, _) ->
            verify(exactly = 1) {
                resolver.getSymbolsWithAnnotation(annotation, false)
            }
        }
    }
}
