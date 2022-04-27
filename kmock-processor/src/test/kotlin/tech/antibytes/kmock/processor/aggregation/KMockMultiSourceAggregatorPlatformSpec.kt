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
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.MultiMock
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Aggregator
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockMultiSourceAggregatorPlatformSpec {
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
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ) fulfils MultiSourceAggregator::class
    }

    @Test
    fun `Given extractPlatformInterfaces is called it resolves the Annotated Thing as ill, if no KMockAnnotation was found`() {
        // Given
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
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractPlatformInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it filters all ill Annotations`() {
        // Given
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()

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
        } returns MultiMock::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        // When
        val (illegal, _, _) = KMockMultiSourceAggregator(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractPlatformInterfaces(resolver)

        // Then
        illegal mustBe listOf(symbol)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it filters all non class types and reports an error`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()

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
        } returns MultiMock::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments

        every { arguments.size } returns 2
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values
        every { type.declaration } returns declaration
        every { file.parent } returns null
        every { symbol.parent } returns file

        every { logger.error(any()) } just Runs

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockMultiSourceAggregator(
                logger,
                mockk(),
                mockk(),
                mockk(),
                emptyMap(),
            ).extractPlatformInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interfaces."
        verify(exactly = 1) { logger.error("Cannot stub non interfaces.") }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it filters all implementation class types and reports an error`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
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
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)
        val mockName: String = fixture.fixture(named("stringAlpha"))

        val simpleName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))
        val qualifiedName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMock::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 2
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns mockName
        every { arguments[1].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns selection[selector]

        every { declaration.parentDeclaration } returns null

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.packageName.asString() } returns packageName
        every { declaration.simpleName.asString() } returns simpleName
        every { declaration.qualifiedName!!.asString() } returns qualifiedName

        every { logger.error(any()) } just Runs

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockMultiSourceAggregator(
                logger,
                mockk(),
                mockk(),
                mockk(),
                emptyMap(),
            ).extractPlatformInterfaces(resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interface $packageName.$qualifiedName."
        verify(exactly = 1) { logger.error("Cannot stub non interface $packageName.$qualifiedName.") }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it returns all found interfaces`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()

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
        } returns MultiMock::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

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

        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            mockk(),
            mockk(),
            genericResolver,
            emptyMap(),
        ).extractPlatformInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockName,
                packageName = packageName,
                templates = listOf(declaration),
                generics = listOf(generics)
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it returns all found interfaces, while filtering douplets`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()

        val annotation: KSAnnotation = mockk()
        val sourceAnnotations: Sequence<KSAnnotation> = sequence {
            yield(annotation)
            yield(annotation)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbol)
            yield(symbol)
        }

        val type: KSType = mockk(relaxed = true)
        val declaration: KSClassDeclaration = mockk(relaxed = true)
        val arguments: List<KSValueArgument> = mockk()

        val values: List<KSType> = listOf(type)
        val mockName: String = fixture.fixture(named("stringAlpha"))
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
        } returns MultiMock::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

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

        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            mockk(),
            mockk(),
            genericResolver,
            emptyMap(),
        ).extractPlatformInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockName,
                packageName = packageName,
                templates = listOf(declaration),
                generics = listOf(generics)
            )
        )

        verify(exactly = 2) { genericResolver.extractGenerics(declaration, any()) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it returns all found interfaces while using the shortest package name`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()

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
        } returns MultiMock::class.qualifiedName!!

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

        every { genericResolver.extractGenerics(any(), any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            mockk(),
            mockk(),
            genericResolver,
            emptyMap(),
        ).extractPlatformInterfaces(resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockName,
                packageName = packageName3,
                templates = listOf(declaration1, declaration2, declaration3),
                generics = listOf(generics, generics, generics)
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration1, any()) }
        verify(exactly = 1) { genericResolver.extractGenerics(declaration2, any()) }
        verify(exactly = 1) { genericResolver.extractGenerics(declaration3, any()) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it returns the corresponding source files`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()

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
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMock::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

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

        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        // When
        val (_, _, sourceFiles) = KMockMultiSourceAggregator(
            logger,
            mockk(),
            mockk(),
            mockk(relaxed = true),
            emptyMap(),
        ).extractPlatformInterfaces(resolver)

        // Then
        sourceFiles mustBe listOf(file)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it returns the corresponding source files, while filter non related annotations`() {
        // Given
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
        val mockName: String = fixture.fixture(named("stringAlpha"))
        val packageName: String = fixture.fixture(named("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            notRelatedAnnotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returnsMany listOf(
            MockShared::class.qualifiedName!!,
            Mock::class.qualifiedName!!
        )

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMock::class.qualifiedName!!

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

        every { declaration.packageName.asString() } returns packageName

        every { logger.error(any()) } just Runs

        // When
        val (_, interfaces, sourceFiles) = KMockMultiSourceAggregator(
            logger,
            mockk(),
            mockk(),
            mockk(relaxed = true),
            emptyMap(),
        ).extractPlatformInterfaces(resolver)

        // Then
        sourceFiles mustBe listOf(file)
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockName,
                packageName = packageName,
                templates = listOf(declaration),
                generics = listOf(emptyMap())
            )
        )
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }
}
