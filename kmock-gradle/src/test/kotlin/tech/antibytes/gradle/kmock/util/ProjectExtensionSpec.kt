/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache License, Version 2.0
 */

package tech.antibytes.gradle.kmock.util

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer
import org.junit.jupiter.api.Test
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture

class ProjectExtensionSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `Given isKmp is called on a Project which has the Multiplatform plugin it returns true`() {
        // Given
        val project: Project = mockk()

        every { project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true

        // When
        val result = project.isKmp()

        // Then
        assertTrue(result)
    }

    @Test
    fun `Given isKmp is called on a Project which has not the Multiplatform plugin it returns false`() {
        // Given
        val project: Project = mockk()

        every { project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false

        // When
        val result = project.isKmp()

        // Then
        assertFalse(result)
    }

    @Test
    fun `Given applyIfNotExists is called with a PluginsNames it applies them if they had not been applied`() {
        // Given
        val pluginNames: Set<String> = fixture.listFixture<String>().toSet()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()

        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.apply(any()) } returns mockk()

        // When
        project.applyIfNotExists(*pluginNames.toTypedArray())

        // Then
        pluginNames.forEach { pluginName ->
            verify(exactly = 1) { plugins.hasPlugin(pluginName) }
            verify(exactly = 1) { plugins.apply(pluginName) }
        }
    }

    @Test
    fun `Given applyIfNotExists is called with a PluginsNames it will not apply them if they had been already applied`() {
        // Given
        val pluginNames: Set<String> = fixture.listFixture<String>().toSet()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()

        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.apply(any()) } returns mockk()

        // When
        project.applyIfNotExists(*pluginNames.toTypedArray())

        // Then
        pluginNames.forEach { pluginName ->
            verify(exactly = 1) { plugins.hasPlugin(pluginName) }
            verify(exactly = 0) { plugins.apply(pluginName) }
        }
    }

    @Test
    fun `Given isAndroid is called on a Project which has neither a AndroidLibrary nor AndroidApplication plugin applied it returns false`() {
        // Given
        val project: Project = mockk()

        every { project.plugins.hasPlugin("com.android.application") } returns false
        every { project.plugins.hasPlugin("com.android.library") } returns false

        // When
        val result = project.isAndroid()

        // Then
        assertFalse(result)
    }

    @Test
    fun `Given isAndroid is called on a Project which has a AndroidLibrary plugin applied it returns false`() {
        // Given
        val project: Project = mockk()

        every { project.plugins.hasPlugin("com.android.application") } returns false
        every { project.plugins.hasPlugin("com.android.library") } returns true

        // When
        val result = project.isAndroid()

        // Then
        assertTrue(result)
    }

    @Test
    fun `Given isAndroid is called on a Project which has a AndroidApplication plugin applied it returns false`() {
        // Given
        val project: Project = mockk()

        every { project.plugins.hasPlugin("com.android.application") } returns true
        every { project.plugins.hasPlugin("com.android.library") } returns false

        // When
        val result = project.isAndroid()

        // Then
        assertTrue(result)
    }

    @Test
    fun `Given isAndroid is called on a Project which has a AndroidLibrary and AndroidApplication plugin applied it returns true`() { // This should never be the case
        // Given
        val project: Project = mockk()

        every { project.plugins.hasPlugin("com.android.application") } returns true
        every { project.plugins.hasPlugin("com.android.library") } returns true

        // When
        val result = project.isAndroid()

        // Then
        assertTrue(result)
    }

    @Test
    fun `Given isJs is called on a Project which has no Kotlin Js Plugin applied it returns false`() {
        // Given
        val project: Project = mockk()

        every { project.plugins.hasPlugin("org.jetbrains.kotlin.js") } returns false

        // When
        val result = project.isJs()

        // Then
        assertFalse(result)
    }

    @Test
    fun `Given isJs is called on a Project which has a Kotlin Js Plugin applied it returns false`() {
        // Given
        val project: Project = mockk()

        every { project.plugins.hasPlugin("org.jetbrains.kotlin.js") } returns true

        // When
        val result = project.isJs()

        // Then
        assertTrue(result)
    }
}
