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
import tech.antibytes.kmock.KMock
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Aggregator
import tech.antibytes.kmock.processor.ProcessorContract.SingleSourceAggregator
import tech.antibytes.kmock.processor.ProcessorContract.SourceSetValidator
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KMockSingleSourceAggregatorCommonSpec {
    private val fixture = kotlinFixture {
        addGenerator(
            String::class,
            StringAlphaGenerator,
            qualifiedBy("stringAlpha")
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
    fun `Given extractCommonInterfaces is called it resolves the Annotated Thing as ill, if no KMockAnnotation was found`() {
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
        val (illegal, _, _) = KMockSingleSourceAggregator(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractCommonInterfaces(emptyList(), resolver)

        // Then
        illegal mustBe listOf(symbol)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractCommonInterfaces is called it filters all ill Annotations`() {
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
        } returns MockCommon::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        // When
        val (illegal, _, _) = KMockSingleSourceAggregator(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            emptyMap(),
            emptyMap(),
        ).extractCommonInterfaces(emptyList(), resolver)

        // Then
        illegal mustBe listOf(symbol)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractCommonInterfaces is called it filters all non class types and reports an error`() {
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

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockCommon::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { file.parent } returns null
        every { symbol.parent } returns file

        every { logger.error(any()) } just Runs

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockSingleSourceAggregator(
                logger,
                mockk(),
                mockk(),
                mockk(),
                emptyMap(),
                emptyMap(),
            ).extractCommonInterfaces(emptyList(), resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interfaces."
        verify(exactly = 1) { logger.error("Cannot stub non interfaces.") }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractCommonInterfaces is called it filters all implementation class types and reports an error`() {
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

        val simpleName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockCommon::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns selection[selector]

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.parentDeclaration } returns null
        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        // When
        val error = assertFailsWith<IllegalArgumentException> {
            KMockSingleSourceAggregator(
                logger,
                mockk(),
                mockk(),
                mockk(),
                emptyMap(),
                emptyMap(),
            ).extractCommonInterfaces(emptyList(), resolver)
        }

        // Then
        error.message mustBe "Cannot stub non interface $packageName.$simpleName."
        verify(exactly = 1) { logger.error("Cannot stub non interface $packageName.$simpleName.") }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractCommonInterfaces is called it returns all found interfaces`() {
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

        val simpleName: String = fixture.fixture(qualifiedBy("stringAlpha"))
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
        } returns MockCommon::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.parentDeclaration } returns null
        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { genericResolver.extractGenerics(any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            mockk(),
            genericResolver,
            emptyMap(),
            emptyMap(),
        ).extractCommonInterfaces(emptyList(), resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = "commonTest",
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = generics,
                dependencies = listOf(file)
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractCommonInterfaces is called it returns all found interfaces while filtering douplets`() {
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

        val values: List<KSType> = listOf(type, type)

        val simpleName: String = fixture.fixture(qualifiedBy("stringAlpha"))
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
        } returns MockCommon::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.parentDeclaration } returns null
        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { genericResolver.extractGenerics(any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            mockk(),
            genericResolver,
            emptyMap(),
            emptyMap(),
        ).extractCommonInterfaces(emptyList(), resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = "commonTest",
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = generics,
                dependencies = listOf(file)
            )
        )

        verify(exactly = 2) { genericResolver.extractGenerics(declaration) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractCommonInterfaces is called it returns the corresponding source files`() {
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

        val simpleName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockCommon::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.parentDeclaration } returns null
        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        // When
        val (_, _, sourceFiles) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            mockk(),
            mockk(relaxed = true),
            emptyMap(),
            emptyMap(),
        ).extractCommonInterfaces(emptyList(), resolver)

        // Then
        sourceFiles mustBe listOf(file)
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractCommonInterfaces is called it returns the corresponding source files, while filter non related annotations`() {
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

        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val simpleName: String = fixture.fixture(qualifiedBy("stringAlpha"))

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
        } returns MockCommon::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations
        every { notRelatedSymbol.annotations } returns notRelatedSource

        every { annotation.arguments } returns arguments
        every { arguments.size } returns 1
        every { arguments.isEmpty() } returns false
        every { arguments[0].value } returns values
        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.parentDeclaration } returns null
        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        // When
        val (_, interfaces, sourceFiles) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            mockk(),
            mockk(relaxed = true),
            emptyMap(),
            emptyMap(),
        ).extractCommonInterfaces(emptyList(), resolver)

        // Then
        sourceFiles mustBe listOf(file)
        interfaces mustBe listOf(
            TemplateSource(
                indicator = "commonTest",
                templateName = simpleName,
                packageName = packageName,
                template = declaration,
                generics = emptyMap(),
                dependencies = listOf(file)
            )
        )
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @Test
    fun `Given extractCommonInterfaces is called it returns while mapping aliases`() {
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

        val simpleName: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val alias: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val packageName: String = fixture.fixture(qualifiedBy("stringAlpha"))

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
        } returns MockCommon::class.qualifiedName!!

        every { symbol.annotations } returns sourceAnnotations

        every { annotation.arguments } returns arguments
        every { arguments.isEmpty() } returns false

        every { arguments.size } returns 1
        every { arguments[0].value } returns values

        every { type.declaration } returns declaration
        every { declaration.classKind } returns ClassKind.INTERFACE

        every { file.parent } returns null
        every { symbol.parent } returns file

        every { declaration.parentDeclaration } returns null
        every { declaration.packageName.asString() } returns packageName
        every { declaration.qualifiedName!!.asString() } returns "$packageName.$simpleName"

        every { logger.error(any()) } just Runs

        every { sourceSetValidator.isValidateSourceSet(any()) } returns true

        every { genericResolver.extractGenerics(any()) } returns generics

        // When
        val (_, interfaces, _) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            sourceSetValidator,
            genericResolver,
            emptyMap(),
            mapping,
        ).extractCommonInterfaces(emptyList(), resolver)

        // Then
        interfaces mustBe listOf(
            TemplateSource(
                indicator = "commonTest",
                templateName = alias,
                packageName = packageName,
                template = declaration,
                generics = generics,
                dependencies = listOf(file)
            )
        )

        verify(exactly = 1) { genericResolver.extractGenerics(declaration) }
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }

    @OptIn(KMockExperimental::class)
    @Test
    fun `Given extractCommonInterfaces is called it returns the corresponding source files, while merging kmock annotations`() {
        // Given
        val logger: KSPLogger = mockk()
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

        val packageNameFetched: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val simpleNameFetched: String = fixture.fixture(qualifiedBy("stringAlpha"))

        val typeGiven: KSType = mockk(relaxed = true)
        val declarationGiven: KSClassDeclaration = mockk(relaxed = true)
        val argumentsGiven: List<KSValueArgument> = mockk()

        val valuesGiven: List<KSType> = listOf(typeGiven)

        val packageNameGiven: String = fixture.fixture(qualifiedBy("stringAlpha"))
        val simpleNameGiven: String = fixture.fixture(qualifiedBy("stringAlpha"))

        every {
            resolver.getSymbolsWithAnnotation(any(), any())
        } returns annotated

        every {
            annotationFetched.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns MockCommon::class.qualifiedName!!

        every {
            annotationGiven.annotationType.resolve().declaration.qualifiedName!!.asString()
        } returns KMock::class.qualifiedName!!

        every { symbolFetch.annotations } returns sourceAnnotationsFetched
        every { symbolGiven.annotations } returns sourceAnnotationsGiven

        every { annotationFetched.arguments } returns argumentsFetched
        every { argumentsFetched.size } returns 1
        every { argumentsFetched.isEmpty() } returns false
        every { argumentsFetched[0].value } returns valuesFetched
        every { typeFetched.declaration } returns declarationFetched
        every { declarationFetched.classKind } returns ClassKind.INTERFACE

        every { annotationGiven.arguments } returns argumentsGiven
        every { argumentsGiven.size } returns 1
        every { argumentsGiven.isEmpty() } returns false
        every { argumentsGiven[0].value } returns valuesGiven
        every { typeGiven.declaration } returns declarationGiven
        every { declarationGiven.classKind } returns ClassKind.INTERFACE

        every { fileFetched.parent } returns null
        every { symbolFetch.parent } returns fileFetched

        every { fileGiven.parent } returns null
        every { symbolGiven.parent } returns fileGiven

        every { declarationFetched.parentDeclaration } returns null
        every { declarationFetched.packageName.asString() } returns packageNameFetched
        every { declarationFetched.qualifiedName!!.asString() } returns "$packageNameFetched.$simpleNameFetched"

        every { declarationGiven.parentDeclaration } returns null
        every { declarationGiven.packageName.asString() } returns packageNameGiven
        every { declarationGiven.qualifiedName!!.asString() } returns "$packageNameGiven.$simpleNameGiven"

        every { logger.error(any()) } just Runs

        // When
        val (_, interfaces, sourceFiles) = KMockSingleSourceAggregator(
            logger,
            mockk(),
            mockk(),
            mockk(relaxed = true),
            emptyMap(),
            emptyMap(),
        ).extractCommonInterfaces(listOf(symbolGiven), resolver)

        // Then
        sourceFiles mustBe listOf(fileFetched, fileGiven)
        interfaces mustBe listOf(
            TemplateSource(
                indicator = "commonTest",
                templateName = simpleNameFetched,
                packageName = packageNameFetched,
                template = declarationFetched,
                generics = emptyMap(),
                dependencies = listOf(fileFetched)
            ),
            TemplateSource(
                indicator = "commonTest",
                templateName = simpleNameGiven,
                packageName = packageNameGiven,
                template = declarationGiven,
                generics = emptyMap(),
                dependencies = listOf(fileGiven)
            )
        )
        verify(exactly = 1) {
            resolver.getSymbolsWithAnnotation(MockCommon::class.qualifiedName!!, false)
        }
    }
}
