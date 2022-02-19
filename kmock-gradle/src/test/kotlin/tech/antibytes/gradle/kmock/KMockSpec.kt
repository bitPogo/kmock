/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer
import org.gradle.internal.impldep.org.testng.annotations.AfterTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.source.KMPSourceSetsConfigurator
import tech.antibytes.gradle.kmock.source.SingleSourceSetConfigurator
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.util.test.fulfils

class KMockSpec {
    @BeforeEach
    fun setUp() {
        mockkObject(SingleSourceSetConfigurator)
    }

    @AfterTest
    fun tearDown() {
        unmockkObject(SingleSourceSetConfigurator)
    }

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
        every { project.afterEvaluate(any<Action<Project>>()) } just Runs

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
        every { project.afterEvaluate(any<Action<Project>>()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 0) { plugins.apply("com.google.devtools.ksp") }
    }

    @Test
    fun `Given apply is called it delegates the call to the SingleSourceSetConfigurator if the Project is not KMP`() {
        // Given
        val kmock = KMock()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()

        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { plugins.apply(any()) } returns mockk()
        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 0) { plugins.apply("com.google.devtools.ksp") }
        verify(exactly = 1) { SingleSourceSetConfigurator.configure(project) }
    }

    @Test
    fun `Given apply is called it delegates the call to the KMPSourceSetsConfigurator if the Project is KMP`() {
        mockkObject(KMPSourceSetsConfigurator)
        // Given
        val kmock = KMock()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()

        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { plugins.apply(any()) } returns mockk()
        every { KMPSourceSetsConfigurator.configure(any()) } just Runs

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 0) { plugins.apply("com.google.devtools.ksp") }
        verify(exactly = 1) { KMPSourceSetsConfigurator.configure(project) }

        unmockkObject(KMPSourceSetsConfigurator)
    }
}
