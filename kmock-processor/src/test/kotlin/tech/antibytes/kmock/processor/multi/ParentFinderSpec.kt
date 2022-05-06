/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import com.google.devtools.ksp.symbol.KSFile
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs

class ParentFinderSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils ParentFinder`() {
        KMockParentFinder fulfils ProcessorContract.ParentFinder::class
    }

    @Test
    fun `Given find is called with a TemplateSource and TemplateMultiSource and a KSFile, it returns a empty list and the given KSFile if no Parent with a matching packageName was found`() {
        // Given
        val indicator: String = fixture.fixture()
        val templateName: String = fixture.fixture()

        val templateSource = TemplateSource(
            indicator = indicator,
            packageName = fixture.fixture(),
            templateName = templateName,
            template = mockk(),
            generics = emptyMap()
        )

        val templateMultiSource = TemplateMultiSource(
            indicator = indicator,
            packageName = fixture.fixture(),
            templateName = templateName,
            templates = mockk(),
            generics = emptyList()
        )

        val singleDependency: KSFile = mockk()
        val multiDependency: KSFile = mockk()

        // When
        val (parents, dependency) = KMockParentFinder.find(
            templateSource = templateSource,
            templateMultiSources = Aggregated(
                illFormed = emptyList(),
                extractedTemplates = listOf(templateMultiSource),
                dependencies = listOf(multiDependency)
            ),
            dependency = singleDependency
        )

        // Then
        parents mustBe emptyList()
        dependency sameAs singleDependency
    }

    @Test
    fun `Given find is called with a TemplateSource and TemplateMultiSource and a KSFile, it returns a empty list and the given KSFile if no Parent with a matching indicator was found`() {
        // Given
        val packageName: String = fixture.fixture()
        val templateName: String = fixture.fixture()

        val templateSource = TemplateSource(
            indicator = fixture.fixture(),
            packageName = packageName,
            templateName = templateName,
            template = mockk(),
            generics = emptyMap()
        )

        val templateMultiSource = TemplateMultiSource(
            indicator = fixture.fixture(),
            packageName = packageName,
            templateName = templateName,
            templates = mockk(),
            generics = emptyList()
        )

        val singleDependency: KSFile = mockk()
        val multiDependency: KSFile = mockk()

        // When
        val (parents, dependency) = KMockParentFinder.find(
            templateSource = templateSource,
            templateMultiSources = Aggregated(
                illFormed = emptyList(),
                extractedTemplates = listOf(templateMultiSource),
                dependencies = listOf(multiDependency)
            ),
            dependency = singleDependency
        )

        // Then
        parents mustBe emptyList()
        dependency sameAs singleDependency
    }

    @Test
    fun `Given find is called with a TemplateSource and TemplateMultiSource and a KSFile, it returns a empty list and the given KSFile if no Parent with a matching templateName was found`() {
        // Given
        val packageName: String = fixture.fixture()
        val indicator: String = fixture.fixture()

        val templateSource = TemplateSource(
            indicator = indicator,
            packageName = packageName,
            templateName = fixture.fixture(),
            template = mockk(),
            generics = emptyMap()
        )

        val templateMultiSource = TemplateMultiSource(
            indicator = indicator,
            packageName = packageName,
            templateName = fixture.fixture(),
            templates = mockk(),
            generics = emptyList()
        )

        val singleDependency: KSFile = mockk()
        val multiDependency: KSFile = mockk()

        // When
        val (parents, dependency) = KMockParentFinder.find(
            templateSource = templateSource,
            templateMultiSources = Aggregated(
                illFormed = emptyList(),
                extractedTemplates = listOf(templateMultiSource),
                dependencies = listOf(multiDependency)
            ),
            dependency = singleDependency
        )

        // Then
        parents mustBe emptyList()
        dependency sameAs singleDependency
    }

    @Test
    fun `Given find is called with a TemplateSource and TemplateMultiSource and a KSFile, it returns a TemplateMultiSource Interfaces and its KSFile`() {
        // Given
        val packageName: String = fixture.fixture()
        val indicator: String = fixture.fixture()
        val templateName: String = fixture.fixture()

        val templateSource = TemplateSource(
            indicator = indicator,
            packageName = packageName,
            templateName = templateName,
            template = mockk(),
            generics = emptyMap()
        )

        val templateMultiSource = TemplateMultiSource(
            indicator = indicator,
            packageName = packageName,
            templateName = templateName,
            templates = mockk(),
            generics = emptyList()
        )

        val singleDependency: KSFile = mockk()
        val multiDependency: KSFile = mockk()

        // When
        val (parents, dependency) = KMockParentFinder.find(
            templateSource = templateSource,
            templateMultiSources = Aggregated(
                illFormed = emptyList(),
                extractedTemplates = listOf(templateMultiSource),
                dependencies = listOf(multiDependency)
            ),
            dependency = singleDependency
        )

        // Then
        parents mustBe templateMultiSource.templates
        dependency sameAs multiDependency
    }
}
