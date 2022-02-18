/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils

class KMockSpec {
    @Test
    fun `It fulfils Plugin`() {
        KMock() fulfils Plugin::class
    }

    @Test
    fun `Given apply is called it applies the gradle Ksp Plugin if it does not exists`() {
        // Given
        val kmock = KMock()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()

        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.apply(any()) } returns mockk()

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 1) { plugins.apply("com.google.devtools.ksp") }
    }

    @Test
    fun `Given apply is called it does not applies the gradle Ksp Plugin if it already exists`() {
        // Given
        val kmock = KMock()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()

        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.apply(any()) } returns mockk()

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 0) { plugins.apply("com.google.devtools.ksp") }
    }
}
