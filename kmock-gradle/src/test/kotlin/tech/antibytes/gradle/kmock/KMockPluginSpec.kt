/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
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
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.source.KmpSourceSetsConfigurator
import tech.antibytes.gradle.kmock.source.SingleSourceSetConfigurator
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import java.io.File

class KMockPluginSpec {
    private val fixture = kotlinFixture()

    @BeforeEach
    fun setUp() {
        mockkObject(SingleSourceSetConfigurator)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(SingleSourceSetConfigurator)
    }

    @Test
    fun `It fulfils Plugin`() {
        KMockPlugin() fulfils Plugin::class
    }

    @Test
    fun `Given apply is called it applies the gradle Ksp Plugin if it does not exists`() {
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any<String>(), KMockExtension::class.java) } returns mockk()

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 1) { plugins.apply("com.google.devtools.ksp") }
    }

    @Test
    fun `Given apply is called it does not applies the gradle Ksp Plugin if it already exists`() {
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("com.google.devtools.ksp") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any<String>(), KMockExtension::class.java) } returns mockk()

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 0) { plugins.apply("com.google.devtools.ksp") }
    }

    @Test
    fun `Given apply is called it creates the KMockExtension`() {
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("com.google.devtools.ksp") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any<String>(), KMockExtension::class.java) } returns mockk()

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { extensions.create("kmock", KMockExtension::class.java) }
    }

    @Test
    fun `Given apply is called it delegates the call to the SingleSourceSetConfigurator if the Project is not Kmp`() {
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions

        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any<String>(), KMockExtension::class.java) } returns mockk()

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { SingleSourceSetConfigurator.configure(project) }
    }

    @Test
    fun `Given apply is called it delegates the call to the KmpSourceSetsConfigurator if the Project is Kmp`() {
        mockkObject(KmpSourceSetsConfigurator)
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.afterEvaluate(any<Action<Project>>()) } just Runs

        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any<String>(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType<KspExtension>(KspExtension::class.java) } returns mockk(relaxed = true)

        every { KmpSourceSetsConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { KmpSourceSetsConfigurator.configure(project) }

        unmockkObject(KmpSourceSetsConfigurator)
    }

    @Test
    fun `Given apply is called it delegates it sets up KSP and builds the Kmp Factories`() {
        mockkObject(KmpSourceSetsConfigurator)

        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val kmockExtension: KMockExtension = mockk()
        val kspExtension: KspExtension = mockk()
        val rootPackage: String = fixture.fixture()
        val buildDir = File(fixture.fixture<String>())

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.afterEvaluate(any<Action<Project>>()) } just Runs
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any<String>(), KMockExtension::class.java) } returns kmockExtension

        every { KmpSourceSetsConfigurator.configure(any()) } just Runs

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        every { extensions.getByType<KspExtension>(KspExtension::class.java) } returns kspExtension

        every { kmockExtension.rootPackage } returns rootPackage
        every { kspExtension.arg(any(), any()) } just Runs

        // When
        kmock.apply(project)

        // Then
        verify(exactly = 1) { kspExtension.arg("isKmp", "true") }

        unmockkObject(KmpSourceSetsConfigurator)
    }
}
