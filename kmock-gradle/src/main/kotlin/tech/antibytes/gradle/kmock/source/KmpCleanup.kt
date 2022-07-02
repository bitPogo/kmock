/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.gradle.api.Task
import tech.antibytes.gradle.kmock.KMockPluginContract
import java.util.Locale

internal object KmpCleanup : KMockPluginContract.KmpCleanup {
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

    override fun cleanup(project: Project, platforms: List<String>) {
        project.afterEvaluate {
            platforms.forEach { platform ->
                if (!platform.startsWith("android")) {
                    project.tasks.findByName(
                        "kspTestKotlin${platform.capitalize(Locale.getDefault())}"
                    ).addPurgeHook(platform)
                }
            }
        }
    }
}
