/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import java.io.File
import java.util.Locale

class KmpCleanupSpec {
    private val fixture = kotlinFixture()

    @TempDir
    lateinit var kspDir: File

    @Test
    fun `It fulfils KmpCleanup`() {
        KmpCleanup fulfils KMockPluginContract.KmpCleanup::class
    }

    @Test
    fun `Given cleanup is called with a project, it ignores non ksp platforms`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val plugins: PluginContainer = mockk()
        val platforms: List<String> = fixture.listFixture(size = 3)

        every { project.tasks } returns tasks
        every { project.plugins } returns plugins
        every { tasks.findByName(any()) } returns null
        every { plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        // When
        KmpCleanup.cleanup(project, platforms)

        // Then
        platforms.forEach { platform ->
            verify(exactly = 1) {
                tasks.findByName("kspTestKotlin${platform.capitalize(Locale.getDefault())}")
            }
        }
    }

    @Test
    fun `Given cleanup is called with a project adds a cleanup hook`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val plugins: PluginContainer = mockk()
        val platforms: List<String> = fixture.listFixture(size = 1)
        val kspTask: Task = mockk()
        val interfaceFile = File(kspDir, "${fixture.fixture<String>()}KMockMultiInterfaceArtifacts.kt")
        interfaceFile.writeText(fixture.fixture())
        val path: String = fixture.fixture()

        every { project.tasks } returns tasks
        every { project.plugins } returns plugins
        every { tasks.findByName(any()) } returns kspTask
        every { plugins.hasPlugin(any<String>()) } returns false
        every { kspTask.project } returns project

        every { project.buildDir.absolutePath } returns path
        every { project.file(any()) } returns kspDir

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        invokeGradleAction(
            { probe -> kspTask.doLast(probe) },
            kspTask,
            kspTask
        )

        interfaceFile.exists() mustBe true

        // When
        KmpCleanup.cleanup(project, platforms)

        // Then
        verify(exactly = 1) {
            tasks.findByName("kspTestKotlin${platforms.first().capitalize(Locale.getDefault())}")
        }

        verify(exactly = 1) {
            project.file("$path/generated/ksp/${platforms.first()}Test")
        }

        interfaceFile.exists() mustBe false
    }

    @Test
    fun `Given cleanup is called with a project, it ignores non ksp platforms for Android`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val plugins: PluginContainer = mockk()
        val platforms: List<String> = listOf(
            "androidSomething"
        )

        every { project.tasks } returns tasks
        every { project.plugins } returns plugins
        every { tasks.findByName(any()) } returns null
        every { plugins.hasPlugin(any<String>()) } returns true

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        // When
        KmpCleanup.cleanup(project, platforms)

        // Then
        verify(exactly = 1) {
            tasks.findByName("kspDebugAndroidTestKotlinAndroid")
        }
        verify(exactly = 1) {
            tasks.findByName("kspDebugUnitTestKotlinAndroid")
        }
        verify(exactly = 1) {
            tasks.findByName("kspReleaseAndroidTestKotlinAndroid")
        }
        verify(exactly = 1) {
            tasks.findByName("kspReleaseUnitTestKotlinAndroid")
        }
    }

    @Test
    fun `Given cleanup is called with a project adds a cleanup hook for Android`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val plugins: PluginContainer = mockk()
        val platforms: List<String> = listOf(
            "androidSomething"
        )
        val kspTask: Task = mockk()
        val interfaceFile = File(kspDir, "${fixture.fixture<String>()}KMockMultiInterfaceArtifacts.kt")
        interfaceFile.writeText(fixture.fixture())
        val path: String = fixture.fixture()

        every { project.tasks } returns tasks
        every { project.plugins } returns plugins
        every { tasks.findByName(any()) } returnsMany listOf(kspTask, null)
        every { plugins.hasPlugin(any<String>()) } returns true
        every { kspTask.project } returns project

        every { project.buildDir.absolutePath } returns path
        every { project.file(any()) } returns kspDir

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        invokeGradleAction(
            { probe -> kspTask.doLast(probe) },
            kspTask,
            kspTask
        )

        interfaceFile.exists() mustBe true

        // When
        KmpCleanup.cleanup(project, platforms)

        // Then

        verify(exactly = 1) {
            tasks.findByName("kspDebugAndroidTestKotlinAndroid")
        }
        verify(exactly = 1) {
            tasks.findByName("kspDebugUnitTestKotlinAndroid")
        }
        verify(exactly = 1) {
            tasks.findByName("kspReleaseAndroidTestKotlinAndroid")
        }
        verify(exactly = 1) {
            tasks.findByName("kspReleaseUnitTestKotlinAndroid")
        }

        verify(exactly = 1) {
            project.file("$path/generated/ksp/android/androidDebugAndroidTest")
        }
        interfaceFile.exists() mustBe false
    }
}
