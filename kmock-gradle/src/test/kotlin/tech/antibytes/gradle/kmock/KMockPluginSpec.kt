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
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.fixture.StringAlphaGenerator
import tech.antibytes.gradle.kmock.source.KmpSourceSetsConfigurator
import tech.antibytes.gradle.kmock.source.SingleSourceSetConfigurator
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import java.io.File

class KMockPluginSpec {
    private val fixture = kotlinFixture {
        it.addGenerator(
            String::class,
            StringAlphaGenerator,
            named("stringAlpha")
        )
    }

    @Test
    fun `It fulfils Plugin`() {
        KMockPlugin() fulfils Plugin::class
    }

    @Test
    fun `Given apply is called it applies the gradle Ksp Plugin if it does not exists`() {
        mockkObject(SingleSourceSetConfigurator)
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(named("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType(KspExtension::class.java) } returns mockk(relaxed = true)

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 1) { plugins.apply("com.google.devtools.ksp") }

        unmockkObject(SingleSourceSetConfigurator)
    }

    @Test
    fun `Given apply is called it does not applies the gradle Ksp Plugin if it already exists`() {
        mockkObject(SingleSourceSetConfigurator)
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(named("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("com.google.devtools.ksp") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType(KspExtension::class.java) } returns mockk(relaxed = true)

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { plugins.hasPlugin("com.google.devtools.ksp") }
        verify(exactly = 0) { plugins.apply("com.google.devtools.ksp") }

        unmockkObject(SingleSourceSetConfigurator)
    }

    @Test
    fun `Given apply is called it creates the KMockExtension`() {
        mockkObject(SingleSourceSetConfigurator)
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(named("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("com.google.devtools.ksp") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType(KspExtension::class.java) } returns mockk(relaxed = true)

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { extensions.create("kmock", KMockExtension::class.java) }

        unmockkObject(SingleSourceSetConfigurator)
    }

    @Test
    fun `Given apply is called it delegates the call to the SingleSourceSetConfigurator if the Project is not Kmp`() {
        mockkObject(SingleSourceSetConfigurator)
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(named("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns mockk()
        every { extensions.getByType(KspExtension::class.java) } returns mockk(relaxed = true)

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        // When
        kmock.apply(project)

        verify(exactly = 1) { SingleSourceSetConfigurator.configure(project) }

        unmockkObject(SingleSourceSetConfigurator)
    }

    @Test
    fun `Given apply is called it delegates it sets up KSP and builds the Single Source Factories`() {
        mockkObject(SingleSourceSetConfigurator)

        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val kmockExtension: KMockExtension = mockk()
        val kspExtension: KspExtension = mockk()
        val rootPackage: String = fixture.fixture()
        val buildDir = File(fixture.fixture<String>(named("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns kmockExtension

        every { SingleSourceSetConfigurator.configure(any()) } just Runs

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension

        every { kmockExtension.rootPackage } returns rootPackage
        every { kspExtension.arg(any(), any()) } just Runs

        // When
        kmock.apply(project)

        // Then
        verify(exactly = 1) { kspExtension.arg("kmock_isKmp", "false") }
        verify(exactly = 1) {
            kspExtension.arg("kmock_kspDir", "${buildDir.absolutePath.toString()}/generated/ksp")
        }

        unmockkObject(SingleSourceSetConfigurator)
    }

    @Test
    fun `Given apply is called it delegates the call to the KmpSourceSetsConfigurator if the Project is Kmp`() {
        mockkObject(KmpSourceSetsConfigurator)
        // Given
        val kmock = KMockPlugin()
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(named("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

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
        val buildDir = File(fixture.fixture<String>(named("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin(any<String>()) } returns true
        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { plugins.apply(any()) } returns mockk()

        every { extensions.create(any(), KMockExtension::class.java) } returns kmockExtension

        every { KmpSourceSetsConfigurator.configure(any()) } just Runs

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension

        every { kmockExtension.rootPackage } returns rootPackage
        every { kspExtension.arg(any(), any()) } just Runs

        // When
        kmock.apply(project)

        // Then
        verify(exactly = 1) { kspExtension.arg("kmock_isKmp", "true") }
        verify(exactly = 1) {
            kspExtension.arg("kmock_kspDir", "${buildDir.absolutePath.toString()}/generated/ksp")
        }

        unmockkObject(KmpSourceSetsConfigurator)
    }
}
