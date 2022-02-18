/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

abstract class KMockCleanTask : KMockPluginContract.CleanUpTask, DefaultTask() {
    private fun guardInputs() {
        if (indicator.orNull.isNullOrEmpty()) {
            throw StopExecutionException("Missing CleanUp Indicator!")
        }

        if (target.orNull.isNullOrEmpty()) {
            throw StopExecutionException("Missing CleanUp Target!")
        }
    }

    @TaskAction
    override fun cleanUp() {
        guardInputs()

        val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/${target.get()}").toList()

        files.forEach { file ->
            val indicator = file.bufferedReader().readLine()

            if (indicator == "// ${this.indicator.get()}") {
                file.delete()
            }
        }
    }
}
