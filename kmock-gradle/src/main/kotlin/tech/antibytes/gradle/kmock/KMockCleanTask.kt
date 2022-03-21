/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

internal abstract class KMockCleanTask : KMockPluginContract.CleanUpTask, DefaultTask() {
    init {
        indicators.set(mutableSetOf())
    }

    private fun guardInputs() {
        if (!indicators.isPresent || indicators.get().isEmpty()) {
            throw StopExecutionException("Missing CleanUp Indicators!")
        }

        if (platform.orNull.isNullOrEmpty()) {
            throw StopExecutionException("Missing CleanUp Target Platform!")
        }

        if (target.orNull.isNullOrEmpty()) {
            throw StopExecutionException("Missing CleanUp Target!")
        }
    }

    @TaskAction
    override fun cleanUp() {
        guardInputs()

        val indicators: Set<String> = this.indicators.get()
        project.fileTree(
            "${project.buildDir.absolutePath}/generated/ksp/${platform.get()}/${target.get()}"
        ).asFileTree.forEach { file ->
            val indicator = file.nameWithoutExtension.substringAfterLast('@')

            if (indicator in indicators) {
                file.delete()
            }
        }
    }
}
