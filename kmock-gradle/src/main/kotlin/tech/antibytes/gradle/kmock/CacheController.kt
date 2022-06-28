/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Project

internal object CacheController : KMockPluginContract.CacheController {
    private fun String.isAndroidKSPTask(): Boolean {
       return startsWith("ksp") && (
            endsWith("UnitTestKotlinAndroid") || endsWith("AndroidTestKotlinAndroid")
       )
    }

    private fun String.isKSPTask(): Boolean = startsWith("kspTestKotlin") || isAndroidKSPTask()

    override fun configure(project: Project) {
        val kspTasks = project.tasks.matching { task -> task.name.isKSPTask() }

        kspTasks.configureEach {
            outputs.cacheIf { false }
        }
    }
}
