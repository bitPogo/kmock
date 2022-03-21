/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import tech.antibytes.gradle.kmock.KMockPluginContract

internal object KspSharedSourceCleaner : KMockPluginContract.KspSharedSourceCleaner {
    override fun cleanKspSources(project: Project, sourceSets: Set<String>) {
        sourceSets.forEach { sourceSet ->
            val platform = sourceSet.substring(0, sourceSet.length - 4)
            project.file("${project.buildDir.absolutePath}/generated/ksp/$platform").deleteRecursively()
        }
    }
}
