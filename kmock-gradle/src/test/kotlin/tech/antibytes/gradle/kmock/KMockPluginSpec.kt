/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkObject
import io.mockk.verify
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.fixture.StringAlphaGenerator
import tech.antibytes.gradle.kmock.source.SingleSourceSetConfigurator
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.qualifier.qualifiedBy
import tech.antibytes.util.test.fulfils

class KMockPluginSpec {
    private val fixture = kotlinFixture {
        addGenerator(
            String::class,
            StringAlphaGenerator,
            qualifiedBy("stringAlpha"),
        )
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
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType(KspExtension::class.java) } returns mockk(relaxed = true)

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 1) { plugins.apply("com.google.devtools.ksp") }
    }

    @Test
    fun `Given apply is called it does not applies the gradle Ksp Plugin if it does not exists and KotlinJs is used`() {
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("org.jetbrains.kotlin.js") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType(KspExtension::class.java) } returns mockk(relaxed = true)

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("org.jetbrains.kotlin.js") }
        verify(exactly = 0) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 0) { plugins.apply("com.google.devtools.ksp") }
    }

    @Test
    fun `Given apply is called it does not applies the gradle Ksp Plugin if it already exists`() {
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("com.google.devtools.ksp") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType(KspExtension::class.java) } returns mockk(relaxed = true)

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 0) { plugins.apply("com.google.devtools.ksp") }

        unmockkObject(SingleSourceSetConfigurator)
    }

    @Test
    fun `Given apply is called it creates the KMockExtension`() {
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("com.google.devtools.ksp") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType(KspExtension::class.java) } returns mockk(relaxed = true)

        // When
        kmock.apply(project)

        verify(exactly = 1) { extensions.create("kmock", KMockExtension::class.java) }
    }
}
