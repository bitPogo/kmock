/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Source
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class SpyContainerSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils SpyContainer`() {
        SpyContainer(fixture.fixture(), emptySet()) fulfils ProcessorContract.SpyContainer::class
    }

    @Test
    fun `Given isSable is called it return false if nulled template nor the derivative TemplateName is not part of the enabled spies`() {
        // Given
        val enabledSpies = fixture.listFixture<String>(size = 3).toSet()

        // When
        val actual = SpyContainer(
            false,
            enabledSpies
        ).isSpyable(
            template = null,
            packageName = fixture.fixture(),
            templateName = fixture.fixture(),
        )

        // Then
        actual mustBe false
    }

    @Test
    fun `Given isSable is called it return false if template nor the derivative TemplateName is not part of the enabled spies`() {
        // Given
        val enabledSpies = fixture.listFixture<String>(size = 3).toSet()

        // When
        val actual = SpyContainer(
            false,
            enabledSpies
        ).isSpyable(
            template = mockk(relaxed = true),
            packageName = fixture.fixture(),
            templateName = fixture.fixture(),
        )

        // Then
        actual mustBe false
    }

    @Test
    fun `Given isSpyable is called it return true if template is part of the enabled spies`() {
        // Given
        val enabledSpies = fixture.listFixture<String>(size = 3)
        val template: KSClassDeclaration = mockk()

        every { template.qualifiedName!!.asString() } returns enabledSpies[2]

        // When
        val actual = SpyContainer(
            false,
            enabledSpies.toSet()
        ).isSpyable(
            template = template,
            packageName = fixture.fixture(),
            templateName = fixture.fixture(),
        )

        // Then
        actual mustBe true
    }

    @Test
    // Aliases & MultiMock
    fun `Given isSpyable is called it return true if the derivative TemplateName is part of the enabled spies`() {
        // Given
        val packageName: String = fixture.fixture()
        val templateName: String = fixture.fixture()
        val enabledSpies = fixture.listFixture<String>(size = 3).toMutableList().also {
            it.add("$packageName.$templateName")
        }

        // When
        val actual = SpyContainer(
            false,
            enabledSpies.toSet()
        ).isSpyable(
            template = mockk(relaxed = true),
            packageName = packageName,
            templateName = templateName,
        )

        // Then
        actual mustBe true
    }

    @Test
    fun `Given isSable is called it return true if template nor the derivative TemplateName is not part of the enabled spies, but spiesOnly is true`() {
        // Given
        val enabledSpies = fixture.listFixture<String>(size = 3).toSet()

        // When
        val actual = SpyContainer(
            true,
            enabledSpies
        ).isSpyable(
            template = mockk(relaxed = true),
            packageName = fixture.fixture(),
            templateName = fixture.fixture(),
        )

        // Then
        actual mustBe true
    }

    @Test
    fun `Given hasSpies is called it return false if Spies are not enabled`() {
        // When
        val actual = SpyContainer(false, emptySet()).hasSpies()

        // Then
        actual mustBe false
    }

    @Test
    fun `Given hasSpies is called it return true if Spies are not enabled but spies only is true`() {
        // When
        val actual = SpyContainer(true, emptySet()).hasSpies()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given hasSpies is called it return true if Spies are enabled`() {
        // Given
        val enabledSpies = fixture.listFixture<String>(size = 3).toSet()

        // When
        val actual = SpyContainer(true, enabledSpies).hasSpies()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given hasSpies is called it return true if Spies are enabled but spies only is false`() {
        // Given
        val enabledSpies = fixture.listFixture<String>(size = 3).toSet()

        // When
        val actual = SpyContainer(false, enabledSpies).hasSpies()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given hasSpies is called with a filter it return false if all found Spies are filtered out`() {
        // Given
        val packages = fixture.listFixture<String>(size = 3)
        val templateNames = fixture.listFixture<String>(size = 3)
        val filter: List<Source> = listOf(mockk(), mockk(), mockk())

        every { filter[0].packageName } returns packages[0]
        every { filter[1].packageName } returns packages[1]
        every { filter[2].packageName } returns packages[2]

        every { filter[0].templateName } returns templateNames[0]
        every { filter[1].templateName } returns templateNames[1]
        every { filter[2].templateName } returns templateNames[2]

        val enabledSpies = packages.mapIndexed { idx, packaqe ->
            "$packaqe.${templateNames[idx]}"
        }
        // When
        val actual = SpyContainer(false, enabledSpies.toSet()).hasSpies(filter)

        // Then
        actual mustBe false
    }

    @Test
    fun `Given hasSpies is called with a filter it return true if all found Spies are filtered out but spiesOnly is active`() {
        // Given
        val packages = fixture.listFixture<String>(size = 3)
        val templateNames = fixture.listFixture<String>(size = 3)
        val filter: List<Source> = listOf(mockk(), mockk(), mockk())

        every { filter[0].packageName } returns packages[0]
        every { filter[1].packageName } returns packages[1]
        every { filter[2].packageName } returns packages[2]

        every { filter[0].templateName } returns templateNames[0]
        every { filter[1].templateName } returns templateNames[1]
        every { filter[2].templateName } returns templateNames[2]

        val enabledSpies = packages.mapIndexed { idx, packaqe ->
            "$packaqe.${templateNames[idx]}"
        }
        // When
        val actual = SpyContainer(true, enabledSpies.toSet()).hasSpies(filter)

        // Then
        actual mustBe true
    }

    @Test
    fun `Given hasSpies is called with a filter it return true if not all Spies are filtered out`() {
        // Given
        val packages = fixture.listFixture<String>(size = 3)
        val templateNames = fixture.listFixture<String>(size = 3)
        val filter: List<Source> = listOf(mockk(), mockk())

        every { filter[0].packageName } returns packages[0]
        every { filter[1].packageName } returns packages[1]

        every { filter[0].templateName } returns templateNames[0]
        every { filter[1].templateName } returns templateNames[1]

        val enabledSpies = packages.mapIndexed { idx, packaqe ->
            "$packaqe.${templateNames[idx]}"
        }
        // When
        val actual = SpyContainer(true, enabledSpies.toSet()).hasSpies(filter)

        // Then
        actual mustBe true
    }
}
