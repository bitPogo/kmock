/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.config.MainConfig
import java.util.Locale

object KMPSourceSetsConfigurator : KMockPluginContract.SourceSetConfigurator {
    private fun extendSourceSet(sourceSet: KotlinSourceSet, buildDir: String) {
        sourceSet.kotlin.srcDir(
            "$buildDir/generated/antibytes/${sourceSet.name}"
        )
    }

    private fun addSource(
        sourceSet: KotlinSourceSet,
        dependencies: DependencyHandler,
        buildDir: String
    ) {
        try {
            dependencies.add(
                "ksp${sourceSet.name.capitalize(Locale.ROOT)}",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        } catch (e: Throwable) {
            return
        }

        extendSourceSet(sourceSet, buildDir)
    }

    override fun configure(project: Project) {
        val dependencies = project.dependencies
        val buildDir = project.buildDir.absolutePath.trimEnd('/')
        project.extensions.configure<KotlinMultiplatformExtension>("kotlin") {
            for (source in sourceSets) {
                if (source.name.startsWith("android") || !source.name.endsWith("Test")) {
                    continue
                } else {
                    addSource(source, dependencies, buildDir)
                }
            }
        }
    }
}
