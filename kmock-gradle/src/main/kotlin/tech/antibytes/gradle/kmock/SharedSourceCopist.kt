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
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.INDICATOR_SEPARATOR
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
            val fileName = element.file.nameWithoutExtension

            !fileName.endsWith("$INDICATOR_SEPARATOR$indicator")
        } else {
            false
        }
    }

    private fun renameFile(fileName: String, indicator: String): String {
        val fileParts = fileName.split(INDICATOR_SEPARATOR, limit = 2)

        return if (fileParts.size != 2) {
            fileName
        } else {
            val actualIndicator = fileParts[1].substring(0, fileParts[1].length - 3)

            if (actualIndicator == indicator) {
                "${fileParts[0]}.kt"
            } else {
                fileName
            }
        }
    }

    private fun setUpTask(
        task: Copy,
        buildDir: String,
        platform: String,
        source: String,
        target: String,
        indicator: String,
    ) {
        val capitalTarget = target.capitalize(Locale.ROOT)

        task.description = "Extract $capitalTarget Sources for $platform"
        task.group = "Code Generation"

        task.from("$buildDir/generated/ksp/$platform/${source}Test")
            .into("$buildDir/generated/ksp/$target/${target}Test")
            .include("**/*.kt")
            .exclude { element: FileTreeElement -> filterFiles(element, indicator) }
            .rename { fileName ->
                if (fileName.contains(INDICATOR_SEPARATOR)) {
                    renameFile(fileName, indicator)
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
    ): Copy {
        guardInputs(platform, source, target, indicator)

        val buildDir = project.buildDir.absolutePath.trimEnd('/')
        val task = project.tasks.create(
            "moveTo${target.capitalize(Locale.ROOT)}For${platform.capitalize(Locale.ROOT)}",
            Copy::class.java
        )
        setUpTask(
            task = task,
            platform = platform,
            buildDir = buildDir,
            source = source.substring(0, source.length - 4),
            target = target.substring(0, target.length - 4),
            indicator = indicator,
        )

        return task
    }
}
