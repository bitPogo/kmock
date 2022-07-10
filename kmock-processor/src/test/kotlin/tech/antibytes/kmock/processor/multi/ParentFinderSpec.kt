/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

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
            generics = emptyMap(),
            dependencies = emptyList(),
        )

        val templateMultiSource = TemplateMultiSource(
            indicator = indicator,
            packageName = fixture.fixture(),
            templateName = templateName,
            templates = mockk(),
            generics = emptyList(),
            dependencies = emptyList(),
        )

        // When
        val parents = KMockParentFinder.find(
            templateSource = templateSource,
            templateMultiSources = listOf(templateMultiSource),
        )

        // Then
        parents mustBe null
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
            generics = emptyMap(),
            dependencies = emptyList(),
        )

        val templateMultiSource = TemplateMultiSource(
            indicator = fixture.fixture(),
            packageName = packageName,
            templateName = templateName,
            templates = mockk(),
            generics = emptyList(),
            dependencies = emptyList(),
        )

        // When
        val parents = KMockParentFinder.find(
            templateSource = templateSource,
            templateMultiSources = listOf(templateMultiSource),
        )

        // Then
        parents mustBe null
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
            generics = emptyMap(),
            dependencies = emptyList(),
        )

        val templateMultiSource = TemplateMultiSource(
            indicator = indicator,
            packageName = packageName,
            templateName = fixture.fixture(),
            templates = mockk(),
            generics = emptyList(),
            dependencies = emptyList(),
        )

        // When
        val parents = KMockParentFinder.find(
            templateSource = templateSource,
            templateMultiSources = listOf(templateMultiSource),
        )

        // Then
        parents mustBe null
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
            generics = emptyMap(),
            dependencies = emptyList(),
        )

        val templateMultiSource = TemplateMultiSource(
            indicator = indicator,
            packageName = packageName,
            templateName = templateName,
            templates = mockk(),
            generics = emptyList(),
            dependencies = emptyList(),
        )

        // When
        val parents = KMockParentFinder.find(
            templateSource = templateSource,
            templateMultiSources = listOf(templateMultiSource),
        )

        // Then
        parents mustBe templateMultiSource
    }
}
