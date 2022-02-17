/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import tech.antibytes.gradle.kmock.KMockPluginContract
import org.gradle.kotlin.dsl.dependencies
import tech.antibytes.gradle.kmock.config.MainConfig

object JvmSourceSetConfigurator : KMockPluginContract.SourceSetConfigurator {
    private fun extendSourceSet(project: Project) {
        project.extensions.configure<KotlinJvmProjectExtension>("kotlin") {
            sourceSets.getByName("test") {
                kotlin.srcDir("${project.buildDir.absolutePath.trimEnd('/')}/generated/antibytes/main")
            }
        }
    }

    private fun addKsp(project: Project) {
        project.dependencies {
            "kspTest"("tech.antibytes.kmock:kmock-processor:${MainConfig.version}")
        }
    }

    override fun configure(project: Project) {
        extendSourceSet(project)
        addKsp(project)
    }
}
