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
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.qualifier.qualifiedBy
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.KMockMulti
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.MultiMock
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Aggregator
import tech.antibytes.kmock.processor.ProcessorContract.MultiSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockMultiSourceAggregatorPlatformSpec {
    private val fixture = kotlinFixture {
        addGenerator(
            String::class,
            StringAlphaGenerator,
            qualifiedBy("stringAlpha")
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
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractPlatformInterfaces(emptyList(), resolver)

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
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
        ).extractPlatformInterfaces(emptyList(), resolver)

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
        val mockName: String = fixture.fixture(qualifiedBy("stringAlpha"))

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
                fixture.fixture(),
                mockk(),
                mockk(),
                mockk(),
                emptyMap(),
            ).extractPlatformInterfaces(emptyList(), resolver)
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

        val selector = fixture.random.nextInt(0, selection.lastIndex)

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
        val mockName: String = fixture.fixture(qualifiedBy("stringAlpha"))

        val simpleName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val qualifiedName: String = fixture.fixture(qualifiedBy("stringAlpha"))

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
                fixture.fixture(),
                mockk(),
                mockk(),
                mockk(),
                emptyMap(),
            ).extractPlatformInterfaces(emptyList(), resolver)
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
        val rootPackage: String = fixture.fixture()

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
        val mockName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))

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

        every { genericResolver.extractGenerics(any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            mockk(),
            genericResolver,
            emptyMap(),
        ).extractPlatformInterfaces(emptyList(), resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it returns all found interfaces while filtering doublets`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
        val file: KSFile = mockk()
        val rootPackage: String = fixture.fixture()

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
        val mockName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))

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

        every { genericResolver.extractGenerics(any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            mockk(),
            genericResolver,
            emptyMap(),
        ).extractPlatformInterfaces(emptyList(), resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 2) { genericResolver.extractGenerics(declaration) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractPlatformInterfaces is called it returns all found interfaces while using the rootPackage name`() {
        // Given
        val logger: KSPLogger = mockk()
        val symbol: KSAnnotated = mockk()
        val resolver: Resolver = mockk()
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
        val mockName: String = fixture.fixture(qualifiedBy("stringAlpha"))

        val className1: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName1 = "${fixture.fixture<String>(qualifiedBy("stringAlpha"))}.${fixture.fixture<String>(qualifiedBy("stringAlpha"))}.${fixture.fixture<String>(qualifiedBy("stringAlpha"))}"
        val className2: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName2 = "${fixture.fixture<String>(qualifiedBy("stringAlpha"))}.${fixture.fixture<String>(qualifiedBy("stringAlpha"))}"
        val className3: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName3 = fixture.fixture<String>(qualifiedBy("stringAlpha"))

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

        every { genericResolver.extractGenerics(any()) } returns generics

        // When
        val (_, interfaces, _) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            mockk(),
            genericResolver,
            emptyMap(),
        ).extractPlatformInterfaces(emptyList(), resolver)

        // Then
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration1, declaration2, declaration3),
                generics = listOf(generics, generics, generics),
                dependencies = listOf(file)
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration1) }
        verify(exactly = 1) { genericResolver.extractGenerics(declaration2) }
        verify(exactly = 1) { genericResolver.extractGenerics(declaration3) }
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
        val mockName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))

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
            fixture.fixture(),
            mockk(),
            mockk(),
            mockk(relaxed = true),
            emptyMap(),
        ).extractPlatformInterfaces(emptyList(), resolver)

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
        val mockName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))

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
            rootPackage,
            mockk(),
            mockk(),
            mockk(relaxed = true),
            emptyMap(),
        ).extractPlatformInterfaces(emptyList(), resolver)

        // Then
        sourceFiles mustBe listOf(file)
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockName,
                packageName = rootPackage,
                templates = listOf(declaration),
                generics = listOf(emptyMap()),
                dependencies = listOf(file)
            )
        )
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }

    @OptIn(KMockExperimental::class)
    @Test
    fun `Given extractCommonInterfaces is called it returns the corresponding source files, while merging kmock annotations`() {
        // Given
        val logger: KSPLogger = mockk()
        val rootPackage: String = fixture.fixture()
        val symbolFetch: KSAnnotated = mockk()
        val symbolGiven: KSAnnotated = mockk()
        val fileFetched: KSFile = mockk()
        val fileGiven: KSFile = mockk()
        val resolver: Resolver = mockk()

        val annotationFetched: KSAnnotation = mockk()
        val sourceAnnotationsFetched: Sequence<KSAnnotation> = sequence {
            yield(annotationFetched)
        }

        val annotationGiven: KSAnnotation = mockk()
        val sourceAnnotationsGiven: Sequence<KSAnnotation> = sequence {
            yield(annotationGiven)
        }

        val annotated: Sequence<KSAnnotated> = sequence {
            yield(symbolFetch)
        }

        val typeFetched: KSType = mockk(relaxed = true)
        val declarationFetched: KSClassDeclaration = mockk(relaxed = true)
        val argumentsFetched: List<KSValueArgument> = mockk()

        val valuesFetched: List<KSType> = listOf(typeFetched)

        val mockNameFetched: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageNameFetched: String = fixture.fixture(qualifiedBy("stringAlpha"))

        val typeGiven: KSType = mockk(relaxed = true)
        val declarationGiven: KSClassDeclaration = mockk(relaxed = true)
        val argumentsGiven: List<KSValueArgument> = mockk()

        val valuesGiven: List<KSType> = listOf(typeGiven)

        val mockNameGiven: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageNameGiven: String = fixture.fixture(qualifiedBy("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotationFetched.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MultiMock::class.qualifiedName!!

        every {
            annotationGiven.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns KMockMulti::class.qualifiedName!!

        every { symbolFetch.annotations } returns sourceAnnotationsFetched
        every { symbolGiven.annotations } returns sourceAnnotationsGiven

        every { annotationFetched.arguments } returns argumentsFetched
        every { argumentsFetched.size } returns 2
        every { argumentsFetched.isEmpty() } returns false
        every { argumentsFetched[0].value } returns mockNameFetched
        every { argumentsFetched[1].value } returns valuesFetched
        every { typeFetched.declaration } returns declarationFetched
        every { declarationFetched.classKind } returns ClassKind.INTERFACE

        every { annotationGiven.arguments } returns argumentsGiven
        every { argumentsGiven.size } returns 2
        every { argumentsGiven.isEmpty() } returns false
        every { argumentsGiven[0].value } returns mockNameGiven
        every { argumentsGiven[1].value } returns valuesGiven
        every { typeGiven.declaration } returns declarationGiven
        every { declarationGiven.classKind } returns ClassKind.INTERFACE

        every { fileFetched.parent } returns null
        every { symbolFetch.parent } returns fileFetched

        every { fileGiven.parent } returns null
        every { symbolGiven.parent } returns fileGiven

        every { declarationFetched.parentDeclaration } returns null
        every { declarationFetched.packageName.asString() } returns packageNameFetched

        every { declarationGiven.parentDeclaration } returns null
        every { declarationGiven.packageName.asString() } returns packageNameGiven

        every { logger.error(any()) } just Runs

        // When
        val (_, interfaces, sourceFiles) = KMockMultiSourceAggregator(
            logger,
            rootPackage,
            mockk(),
            mockk(),
            mockk(relaxed = true),
            emptyMap(),
        ).extractPlatformInterfaces(listOf(symbolGiven), resolver)

        // Then
        sourceFiles mustBe listOf(fileFetched, fileGiven)
        interfaces mustBe listOf(
            TemplateMultiSource(
                indicator = "",
                templateName = mockNameFetched,
                packageName = rootPackage,
                templates = listOf(declarationFetched),
                generics = listOf(emptyMap()),
                dependencies = listOf(fileFetched)
            ),
            TemplateMultiSource(
                indicator = "",
                templateName = mockNameGiven,
                packageName = rootPackage,
                templates = listOf(declarationGiven),
                generics = listOf(emptyMap()),
                dependencies = listOf(fileGiven)
            )
        )
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MultiMock::class.qualifiedName!!, false)
        }
    }
}
