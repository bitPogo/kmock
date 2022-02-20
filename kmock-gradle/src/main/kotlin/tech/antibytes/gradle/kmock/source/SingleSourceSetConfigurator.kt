/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.config.MainConfig
import tech.antibytes.gradle.util.isJs

internal object SingleSourceSetConfigurator : KMockPluginContract.SourceSetConfigurator {
    private fun extendJvmSourceSet(project: Project) {
        project.extensions.configure<KotlinJvmProjectExtension>("kotlin") {
            sourceSets.getByName("test") {
                kotlin.srcDir("${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp/test")
            }
        }
    }

    private fun extendJsSourceSet(project: Project) {
        project.extensions.configure<KotlinJsProjectExtension>("kotlin") {
            sourceSets.getByName("test") {
                kotlin.srcDir("${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp/test")
            }
        }
    }

    private fun addKsp(project: Project) {
        project.dependencies {
            add(
                "kspTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }
    }

    override fun configure(project: Project) {
        if (project.isJs()) {
            extendJsSourceSet(project)
        } else {
            extendJvmSourceSet(project)
        }

        addKsp(project)
    }
}
