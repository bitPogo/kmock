/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.gradle.api.Task
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.util.isAndroid

internal object KmpTestTaskChain : KMockPluginContract.KmpTestTaskChain {
    private fun String.mapTask(project: Project): Task = project.tasks.getByName("${this}Test")

    private fun List<Task>.amendAndroidTasks(project: Project): List<Task> {
        return if (!project.isAndroid()) {
            this
        } else {
            this.toMutableList()
                .also {
                    it.addAll(
                        listOf(
                            project.tasks.getByName("testDebugUnitTest"),
                            project.tasks.getByName("testReleaseUnitTest"),
                            project.tasks.getByName("connectedDebugAndroidTest"),
                        )
                    )
                }
        }
    }

    private fun List<String>.toTestTasks(
        project: Project
    ): List<Task> = this
        .filter { task -> !task.startsWith("android") }
        .map { task -> task.mapTask(project) }
        .amendAndroidTasks(project)

    private fun List<Task>.chainTasks() {
        this.forEachIndexed { idx, task ->
            if (idx != lastIndex) {
                this[idx + 1].mustRunAfter(task)
            }
        }
    }

    override fun chainTasks(project: Project, platforms: List<String>) {
        platforms
            .toTestTasks(project)
            .chainTasks()
    }
}
