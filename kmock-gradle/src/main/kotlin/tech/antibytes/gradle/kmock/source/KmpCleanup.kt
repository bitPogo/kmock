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
import java.util.Locale

internal object KmpCleanup : KMockPluginContract.KmpCleanup {
    private val androidKspTasks = mapOf(
        "kspDebugAndroidTestKotlinAndroid" to "androidDebugAndroid",
        "kspDebugUnitTestKotlinAndroid" to "androidDebugUnit",
        "kspReleaseAndroidTestKotlinAndroid" to "androidReleaseAndroid",
        "kspReleaseUnitTestKotlinAndroid" to "androidReleaseUnit",
    )

    private fun Task?.addPurgeHook(platform: String) {
        this?.doLast {
            project.file("${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp/${platform}Test")
                .walkBottomUp()
                .toList()
                .forEach { file ->
                    if (file.absolutePath.endsWith("KMockMultiInterfaceArtifacts.kt")) {
                        file.delete()
                    }
                }
        }
    }

    private fun Project.addAndroidPurgeHook() {
        if (isAndroid()) {
            androidKspTasks.forEach { (taskName, sourceFolder) ->
                tasks.findByName(taskName)?.addPurgeHook("android/$sourceFolder")
            }
        }
    }

    override fun cleanup(project: Project, platforms: List<String>) {
        project.afterEvaluate {
            platforms.forEach { platform ->
                if (!platform.startsWith("android")) {
                    project.tasks.findByName(
                        "kspTestKotlin${platform.capitalize(Locale.getDefault())}"
                    ).addPurgeHook(platform)
                }
            }

            project.addAndroidPurgeHook()
        }
    }
}
