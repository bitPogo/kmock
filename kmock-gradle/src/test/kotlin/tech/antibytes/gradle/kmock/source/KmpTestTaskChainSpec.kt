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
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.util.test.fulfils

class KmpTestTaskChainSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils KmpTestTaskChain`() {
        KmpTestTaskChain fulfils KMockPluginContract.KmpTestTaskChain::class
    }

    @Test
    fun `Given chainTasks is called it order the TestsTasks`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val plugins: PluginContainer = mockk()
        val platforms: List<String> = fixture.listFixture(size = 5)
        val testTasks: List<Task> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
        )
        every { project.tasks } returns tasks
        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns false
        every { tasks.getByName(any()) } returnsMany testTasks

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project,
        )

        // When
        KmpTestTaskChain.chainTasks(project, platforms)

        // Then
        platforms.forEach { taskName ->
            verify(exactly = 1) {
                tasks.getByName("${taskName}Test")
            }
        }

        verify(exactly = 1) {
            testTasks[1].mustRunAfter(testTasks[0])
        }
        verify(exactly = 1) {
            testTasks[2].mustRunAfter(testTasks[1])
        }
        verify(exactly = 1) {
            testTasks[3].mustRunAfter(testTasks[2])
        }
        verify(exactly = 1) {
            testTasks[4].mustRunAfter(testTasks[3])
        }
    }

    @Test
    fun `Given chainTasks is called it order the TestsTasks with an Android Library`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val plugins: PluginContainer = mockk()
        val platforms: List<String> = fixture.listFixture(size = 5)
        val testTasks: List<Task> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
        )
        every { project.tasks } returns tasks
        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("com.android.library") } returns true
        every { tasks.getByName(any()) } returnsMany testTasks

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project,
        )

        // When
        KmpTestTaskChain.chainTasks(project, platforms)

        // Then
        platforms.forEach { taskName ->
            verify(exactly = 1) {
                tasks.getByName("${taskName}Test")
            }
        }

        verify(exactly = 1) {
            tasks.getByName("testDebugUnitTest")
        }

        verify(exactly = 1) {
            tasks.getByName("testReleaseUnitTest")
        }

        verify(exactly = 1) {
            tasks.getByName("connectedDebugAndroidTest")
        }

        verify(exactly = 1) {
            testTasks[1].mustRunAfter(testTasks[0])
        }
        verify(exactly = 1) {
            testTasks[2].mustRunAfter(testTasks[1])
        }
        verify(exactly = 1) {
            testTasks[3].mustRunAfter(testTasks[2])
        }
        verify(exactly = 1) {
            testTasks[4].mustRunAfter(testTasks[3])
        }
        verify(exactly = 1) {
            testTasks[5].mustRunAfter(testTasks[4])
        }
        verify(exactly = 1) {
            testTasks[6].mustRunAfter(testTasks[5])
        }
        verify(exactly = 1) {
            testTasks[7].mustRunAfter(testTasks[6])
        }
    }

    @Test
    fun `Given chainTasks is called it order the TestsTasks with a Android Application`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val plugins: PluginContainer = mockk()
        val platforms: List<String> = fixture.listFixture(size = 5)
        val testTasks: List<Task> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
        )
        every { project.tasks } returns tasks
        every { project.plugins } returns plugins
        every { plugins.hasPlugin(any<String>()) } returns false
        every { plugins.hasPlugin("com.android.application") } returns true
        every { tasks.getByName(any()) } returnsMany testTasks

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project,
        )

        // When
        KmpTestTaskChain.chainTasks(project, platforms)

        // Then
        platforms.forEach { taskName ->
            verify(exactly = 1) {
                tasks.getByName("${taskName}Test")
            }
        }

        verify(exactly = 1) {
            tasks.getByName("testDebugUnitTest")
        }

        verify(exactly = 1) {
            tasks.getByName("testReleaseUnitTest")
        }

        verify(exactly = 1) {
            tasks.getByName("connectedDebugAndroidTest")
        }

        verify(exactly = 1) {
            testTasks[1].mustRunAfter(testTasks[0])
        }
        verify(exactly = 1) {
            testTasks[2].mustRunAfter(testTasks[1])
        }
        verify(exactly = 1) {
            testTasks[3].mustRunAfter(testTasks[2])
        }
        verify(exactly = 1) {
            testTasks[4].mustRunAfter(testTasks[3])
        }
        verify(exactly = 1) {
            testTasks[5].mustRunAfter(testTasks[4])
        }
        verify(exactly = 1) {
            testTasks[6].mustRunAfter(testTasks[5])
        }
        verify(exactly = 1) {
            testTasks[7].mustRunAfter(testTasks[6])
        }
    }
}
