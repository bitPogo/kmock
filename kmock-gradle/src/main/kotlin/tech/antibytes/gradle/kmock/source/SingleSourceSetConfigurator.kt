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
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KSP_PLUGIN
import tech.antibytes.gradle.kmock.config.MainConfig
import tech.antibytes.gradle.kmock.util.applyIfNotExists
import tech.antibytes.gradle.kmock.util.isAndroid
import tech.antibytes.gradle.kmock.util.isJs

internal object SingleSourceSetConfigurator : KMockPluginContract.SourceSetConfigurator {
    private fun extendJvmSourceSet(project: Project, buildDir: String) {
        project.extensions.configure<KotlinJvmProjectExtension>("kotlin") {
            sourceSets.getByName("test") {
                kotlin.srcDir("$buildDir/generated/ksp/test")
            }
        }
    }

    private fun extendAndroidSourceSet(project: Project) = AndroidSourceBinder.bind(project)

    private fun extendJsSourceSet(project: Project, buildDir: String) {
        project.extensions.configure<KotlinJsProjectExtension>("kotlin") {
            sourceSets.getByName("test") {
                kotlin.srcDir("$buildDir/generated/ksp/js/test")
            }
        }
    }

    private fun addKsp(project: Project) {
        project.dependencies {
            add(
                "kspTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )

            if (project.isAndroid()) {
                add(
                    "kspAndroidTest",
                    "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
                )
            }
        }
    }

    override fun configure(project: Project) {
        val buildDir = project.buildDir.absolutePath.trimEnd('/')

        when {
            project.isJs() -> {
                project.applyIfNotExists(KSP_PLUGIN)
                extendJsSourceSet(project, buildDir)
            }
            project.isAndroid() -> extendAndroidSourceSet(project)
            else -> extendJvmSourceSet(project, buildDir)
        }

        addKsp(project)
    }
}
