/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Plugin
import org.gradle.api.Project
import tech.antibytes.gradle.util.applyIfNotExists
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import tech.antibytes.gradle.util.isAndroidApplication
import tech.antibytes.gradle.util.isAndroidLibrary
import tech.antibytes.gradle.util.isKmp

class KMock : Plugin<Project> {
    private fun addKSP(project: Project) {
        project.applyIfNotExists("com.google.devtools.ksp")
    }

    override fun apply(target: Project) {
        addKSP(target)
    }
}
