/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KMock : Plugin<Project> {
    override fun apply(target: Project) {
        target.afterEvaluate {
            val sourceSet = extensions.findByType<KotlinMultiplatformExtension>() ?: throw RuntimeException()

            sourceSet.targets.all {
                println(this.name)
            }
        }
    }
}
