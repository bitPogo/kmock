/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.config.MainConfig
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.util.test.fulfils

class SingleSourceSetConfiguratorSpec {
    private val fixture = kotlinFixture()

    @BeforeEach
    fun setUp() {
        mockkObject(AndroidSourceBinder)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(AndroidSourceBinder)
    }

    @Test
    fun `It fulfils SourceSetConfigurator`() {
        SingleSourceSetConfigurator fulfils KMockPluginContract.SourceSetConfigurator::class
    }

    @Test
    fun `Given configure is called with a Project it adds the output directory of the code generation to the consumer for Jvm`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val kotlin: KotlinJvmProjectExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val test: KotlinSourceSet = mockk()
        val path: String = fixture.fixture()

        every { project.dependencies } returns mockk(relaxed = true)
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin("org.jetbrains.kotlin.js") } returns false
        every { project.plugins.hasPlugin("com.android.library") } returns false
        every { project.plugins.hasPlugin("com.android.application") } returns false

        invokeGradleAction(
            { probe -> extensions.configure<KotlinJvmProjectExtension>("kotlin", probe) },
            kotlin,
        )

        every { kotlin.sourceSets } returns sources

        invokeGradleAction(
            { probe -> sources.getByName("test", probe) },
            test,
            test,
        )

        every { test.kotlin.srcDir(any<String>()) } returns mockk()

        // When
        SingleSourceSetConfigurator.configure(project)

        // Then
        verify(exactly = 1) { test.kotlin.srcDir("$path/generated/ksp/test/kotlin") }
    }

    @Test
    fun `Given configure is called with a Project it adds the output directory of the code generation to the consumer for Js`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val kotlin: KotlinJsProjectExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val test: KotlinSourceSet = mockk()
        val path: String = fixture.fixture()

        every { project.dependencies } returns mockk(relaxed = true)
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path

        every { project.plugins.hasPlugin("org.jetbrains.kotlin.js") } returns true
        every { project.plugins.hasPlugin("com.google.devtools.ksp") } returns true
        every { project.plugins.hasPlugin("com.android.library") } returns false
        every { project.plugins.hasPlugin("com.android.application") } returns false

        invokeGradleAction(
            { probe -> extensions.configure<KotlinJsProjectExtension>("kotlin", probe) },
            kotlin,
        )

        every { kotlin.sourceSets } returns sources

        invokeGradleAction(
            { probe -> sources.getByName("test", probe) },
            test,
            test,
        )

        every { test.kotlin.srcDir(any<String>()) } returns mockk()

        // When
        SingleSourceSetConfigurator.configure(project)

        // Then
        verify(exactly = 1) { test.kotlin.srcDir("$path/generated/ksp/js/test/kotlin") }
    }

    @Test
    fun `Given configure is called with a Project it adds the output directory of the code generation to the consumer for Js, while applying KSP`() {
        // Given
        val project: Project = mockk()
        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val kotlin: KotlinJsProjectExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val test: KotlinSourceSet = mockk()
        val path: String = fixture.fixture()

        every { project.dependencies } returns mockk(relaxed = true)
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path
        every { project.plugins } returns plugins

        every { plugins.hasPlugin("org.jetbrains.kotlin.js") } returns true
        every { plugins.hasPlugin("com.google.devtools.ksp") } returns false
        every { plugins.hasPlugin("com.android.library") } returns false
        every { plugins.hasPlugin("com.android.application") } returns false

        every { plugins.apply(any()) } returns mockk()

        invokeGradleAction(
            { probe -> extensions.configure<KotlinJsProjectExtension>("kotlin", probe) },
            kotlin,
        )

        every { kotlin.sourceSets } returns sources

        invokeGradleAction(
            { probe -> sources.getByName("test", probe) },
            test,
            test,
        )

        every { test.kotlin.srcDir(any<String>()) } returns mockk()

        // When
        SingleSourceSetConfigurator.configure(project)

        // Then
        verify(exactly = 1) { plugins.apply("com.google.devtools.ksp") }
        verify(exactly = 1) { test.kotlin.srcDir("$path/generated/ksp/js/test/kotlin") }
    }

    @Test
    fun `Given configure is called with a Project it adds the KSP dependencies`() {
        // Given
        val project: Project = mockk()
        val dependencies: DependencyHandler = mockk()

        every { project.buildDir.absolutePath } returns fixture.fixture<String>()
        every { project.extensions } returns mockk(relaxed = true)
        every { project.dependencies } returns dependencies

        every { dependencies.add(any<String>(), any<String>()) } returns mockk()

        every { project.plugins.hasPlugin("org.jetbrains.kotlin.js") } returns false
        every { project.plugins.hasPlugin("com.android.library") } returns false
        every { project.plugins.hasPlugin("com.android.application") } returns false

        // When
        SingleSourceSetConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            dependencies.add(
                "kspTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}",
            )
        }
    }

    @Test
    fun `Given configure is called with a Project it adds an additional KSP dependencies for an AndroidLibrary`() {
        // Given
        val project: Project = mockk()
        val dependencies: DependencyHandler = mockk()

        every { project.buildDir.absolutePath } returns fixture.fixture<String>()
        every { project.extensions } returns mockk(relaxed = true)
        every { project.dependencies } returns dependencies

        every { dependencies.add(any<String>(), any<String>()) } returns mockk()

        every { project.plugins.hasPlugin("org.jetbrains.kotlin.js") } returns false
        every { project.plugins.hasPlugin("com.android.library") } returns true
        every { project.plugins.hasPlugin("com.android.application") } returns false

        every { AndroidSourceBinder.bind(any()) } just Runs

        // When
        SingleSourceSetConfigurator.configure(project)

        // Then
        verify(exactly = 1) { AndroidSourceBinder.bind(project) }
        verify(atLeast = 1) {
            dependencies.add(
                "kspAndroidTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}",
            )
        }
    }

    @Test
    fun `Given configure is called with a Project it adds an additional KSP dependencies for an AndroidApplication`() {
        // Given
        val project: Project = mockk()
        val dependencies: DependencyHandler = mockk()

        every { project.extensions } returns mockk(relaxed = true)
        every { project.buildDir.absolutePath } returns fixture.fixture<String>()
        every { project.dependencies } returns dependencies

        every { dependencies.add(any<String>(), any<String>()) } returns mockk()

        every { project.plugins.hasPlugin("org.jetbrains.kotlin.js") } returns false
        every { project.plugins.hasPlugin("com.android.library") } returns false
        every { project.plugins.hasPlugin("com.android.application") } returns true

        every { AndroidSourceBinder.bind(any()) } just Runs

        // When
        SingleSourceSetConfigurator.configure(project)

        // Then
        verify(exactly = 1) { AndroidSourceBinder.bind(project) }
        verify(exactly = 1) {
            dependencies.add(
                "kspAndroidTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}",
            )
        }
    }
}
