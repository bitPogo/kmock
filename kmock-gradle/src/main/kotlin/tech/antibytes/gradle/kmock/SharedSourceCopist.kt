/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Project
import org.gradle.api.file.FileTreeElement
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopExecutionException
import java.util.Locale

object SharedSourceCopist : KMockPluginContract.SharedSourceCopist {
    private fun guardInputs(source: String, target: String, indicator: String) {
        if (source.isEmpty()) {
            throw StopExecutionException("Cannot copy form invalid SourceDefinition!")
        }

        if (target.isEmpty()) {
            throw StopExecutionException("Cannot copy to invalid SourceDefinition!")
        }

        if (indicator.isEmpty()) {
            throw StopExecutionException("Cannot copy with invalid Indicator!")
        }
    }

    private fun filterFiles(element: FileTreeElement, indicator: String): Boolean {
        return if (element.file.isFile) {
            val fileIndicator = element.file.bufferedReader().readLine()
            fileIndicator != "// $indicator"
        } else {
            false
        }
    }

    private fun setUpTask(
        task: Copy,
        buildDir: String,
        source: String,
        target: String,
        indicator: String
    ) {
        val capitalTarget = target.capitalize(Locale.ROOT)

        task.description = "Extract $capitalTarget Sources"
        task.group = "Code Generation"

        task.from("$buildDir/generated/ksp/$source/${source}Test")
            .into("$buildDir/generated/ksp/$target/${target}Test")
            .include("**/*.kt")
            .exclude { element: FileTreeElement -> filterFiles(element, indicator) }
    }

    override fun copySharedSource(
        project: Project,
        source: String,
        target: String,
        indicator: String
    ): Copy {
        guardInputs(source, target, indicator)

        val buildDir = project.buildDir.absolutePath.trimEnd('/')
        val task = project.tasks.create("moveTo${target.capitalize(Locale.ROOT)}", Copy::class.java)

        setUpTask(
            task,
            buildDir,
            source.substringBeforeLast("Test"),
            target.substringBeforeLast("Test"),
            indicator
        )

        return task
    }
}
