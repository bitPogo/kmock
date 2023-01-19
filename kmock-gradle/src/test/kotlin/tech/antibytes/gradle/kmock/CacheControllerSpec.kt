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
import io.mockk.slot
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskOutputs
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class CacheControllerSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils CacheController`() {
        CacheController fulfils KMockPluginContract.CacheController::class
    }

    @Test
    fun `Given configure is called it ignores non kspTasks`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val matcher = slot<Spec<Task>>()
        val kspCollection: TaskCollection<Task> = mockk()
        val task: Task = mockk()

        every { project.tasks } returns tasks
        every { tasks.matching(capture(matcher)) } returns kspCollection
        every { kspCollection.configureEach(any()) } returns mockk()

        // When
        CacheController.configure(project)

        // Then
        every { task.name } returns fixture.fixture()
        matcher.captured.isSatisfiedBy(task) mustBe false
    }

    @Test
    fun `Given configure is called it accepts kspTestTasks`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val matcher = slot<Spec<Task>>()
        val kspCollection: TaskCollection<Task> = mockk()
        val task: Task = mockk()

        every { project.tasks } returns tasks
        every { tasks.matching(capture(matcher)) } returns kspCollection
        every { kspCollection.configureEach(any()) } returns mockk()

        // When
        CacheController.configure(project)

        // Then
        every { task.name } returns "kspTestKotlin" + fixture.fixture()
        matcher.captured.isSatisfiedBy(task) mustBe true
    }

    @Test
    fun `Given configure is called it accepts kspAndroidUnitTestTasks`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val matcher = slot<Spec<Task>>()
        val kspCollection: TaskCollection<Task> = mockk()
        val task: Task = mockk()

        every { project.tasks } returns tasks
        every { tasks.matching(capture(matcher)) } returns kspCollection
        every { kspCollection.configureEach(any()) } returns mockk()

        // When
        CacheController.configure(project)

        // Then
        every { task.name } returns "ksp" + fixture.fixture() + "UnitTestKotlinAndroid"
        matcher.captured.isSatisfiedBy(task) mustBe true
    }

    @Test
    fun `Given configure is called it accepts kspAndroidAndroidTestTasks`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val matcher = slot<Spec<Task>>()
        val kspCollection: TaskCollection<Task> = mockk()
        val task: Task = mockk()

        every { project.tasks } returns tasks
        every { tasks.matching(capture(matcher)) } returns kspCollection
        every { kspCollection.configureEach(any()) } returns mockk()

        // When
        CacheController.configure(project)

        // Then
        every { task.name } returns "ksp" + fixture.fixture() + "AndroidTestKotlinAndroid"
        matcher.captured.isSatisfiedBy(task) mustBe true
    }

    @Test
    fun `Given configure is called it disables the cache for kspTestTasks`() {
        // Given
        val project: Project = mockk()
        val tasks: TaskContainer = mockk()
        val matcher = slot<Spec<Task>>()
        val kspCollection: TaskCollection<Task> = mockk()
        val kspTask: Task = mockk()
        val taskOutputs: TaskOutputs = mockk()
        val cacheable = slot<Spec<Task>>()

        every { project.tasks } returns tasks
        every { tasks.matching(capture(matcher)) } returns kspCollection

        invokeGradleAction(kspTask) { probe ->
            kspCollection.configureEach(probe)
        }

        every { kspTask.outputs } returns taskOutputs
        every { taskOutputs.cacheIf(capture(cacheable)) } just Runs

        // When
        CacheController.configure(project)

        // Then
        cacheable.captured.isSatisfiedBy(mockk()) mustBe false
    }
}
