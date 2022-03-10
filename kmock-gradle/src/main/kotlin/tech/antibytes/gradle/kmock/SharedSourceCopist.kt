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

internal object SharedSourceCopist : KMockPluginContract.SharedSourceCopist {
    private fun guardInputs(platform: String, source: String, target: String, indicator: String) {
        if (platform.isEmpty()) {
            throw StopExecutionException("Cannot copy from invalid Platform Definition!")
        }

        if (source.isEmpty()) {
            throw StopExecutionException("Cannot copy from invalid Source Definition!")
        }

        if (target.isEmpty()) {
            throw StopExecutionException("Cannot copy to invalid Target Definition!")
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
        platform: String,
        source: String,
        target: String,
        indicator: String,
        rename: Map<String, String>,
    ) {
        val capitalTarget = target.capitalize(Locale.ROOT)

        task.description = "Extract $capitalTarget Sources"
        task.group = "Code Generation"

        task.from("$buildDir/generated/ksp/$platform/${source}Test")
            .into("$buildDir/generated/ksp/$target/${target}Test")
            .include("**/*.kt")
            .exclude { element: FileTreeElement -> filterFiles(element, indicator) }
            .rename { fileName ->
                if (fileName is String && fileName in rename) {
                    rename[fileName]!!
                } else {
                    fileName
                }
            }
    }

    override fun copySharedSource(
        project: Project,
        platform: String,
        source: String,
        target: String,
        indicator: String,
        rename: Map<String, String>
    ): Copy {
        guardInputs(platform, source, target, indicator)

        val buildDir = project.buildDir.absolutePath.trimEnd('/')
        val task = project.tasks.create("moveTo${target.capitalize(Locale.ROOT)}", Copy::class.java)

        setUpTask(
            task = task,
            platform = platform,
            buildDir = buildDir,
            source = source.substringBeforeLast("Test"),
            target = target.substringBeforeLast("Test"),
            indicator = indicator,
            rename = rename,
        )

        return task
    }
}
