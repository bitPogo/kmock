/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import java.util.Locale
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

        invokeGradleAction(project) { probe ->
            project.afterEvaluate(probe)
        }

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

        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path
        every { project.file(any()) } returns kspDir

        invokeGradleAction(project) { probe ->
            project.afterEvaluate(probe)
        }

        invokeGradleAction(
            kspTask,
            kspTask,
        ) { probe ->
            kspTask.doLast(probe)
        }

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
}
