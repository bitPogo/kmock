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
    private fun String.mapTestTask(project: Project): Task? = project.tasks.findByName("${this}Test")

    private fun String.mapKspTask(project: Project): Task? = project.tasks.findByName(this)

    private fun List<Task>.amendAndroidTestTasks(project: Project): List<Task> {
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
                        ),
                    )
                }
        }
    }

    private fun List<Task>.amendAndroidKspTasks(project: Project): List<Task> {
        return if (!project.isAndroid()) {
            this
        } else {
            this.toMutableList()
                .also {
                    it.addAll(
                        listOf(
                            project.tasks.getByName("kspDebugUnitTestKotlinAndroid"),
                            project.tasks.getByName("kspReleaseUnitTestKotlinAndroid"),
                            project.tasks.getByName("kspDebugAndroidTestKotlinAndroid"),
                        ),
                    )
                }
        }
    }

    private fun Iterable<String>.toTestTasks(
        project: Project,
    ): List<Task> = this
        .filter { task -> !task.startsWith("android") }
        .mapNotNull { task -> task.mapTestTask(project) }
        .amendAndroidTestTasks(project)

    private fun Iterable<String>.toKspTasks(
        project: Project,
    ): List<Task> = this
        .filter { task -> "Android" !in task }
        .mapNotNull { task -> task.mapKspTask(project) }
        .amendAndroidKspTasks(project)

    private fun List<Task>.chainTasks() {
        this.forEachIndexed { idx, task ->
            if (idx != lastIndex) {
                this[idx + 1].mustRunAfter(task)
            }
        }
    }

    override fun chainTasks(
        project: Project,
        kspMapping: Map<String, String>,
    ) {
        project.afterEvaluate {
            kspMapping.keys.toTestTasks(this).chainTasks()
            kspMapping.values.toKspTasks(this).chainTasks()
        }
    }
}
