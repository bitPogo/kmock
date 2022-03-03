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
import tech.antibytes.gradle.kmock.KMockPluginContract.SourceSetConfigurator
import tech.antibytes.gradle.kmock.config.MainConfig
import java.util.Locale

internal object KmpSourceSetsConfigurator : SourceSetConfigurator {
    private fun cleanSourceName(sourceName: String): String = sourceName.substringBeforeLast("Test")

    private fun addKspDependency(
        dependencies: DependencyHandler,
        dependency: String
    ) {
        dependencies.add(
            dependency,
            "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
        )
    }

    private fun extendSourceSet(
        platformName: String,
        sourceSet: KotlinSourceSet,
        buildDir: String
    ) {
        sourceSet.kotlin.srcDir(
            "$buildDir/generated/ksp/$platformName/${sourceSet.name}"
        )
    }

    private fun addSource(
        platformName: String,
        sourceCollector: MutableMap<String, String>,
        dependencies: DependencyHandler
    ) {
        val kspDependency = "kspTestKotlin${platformName.capitalize(Locale.ROOT)}"
        try {
            addKspDependency(dependencies, "ksp${platformName.capitalize(Locale.ROOT)}Test")
        } catch (e: Throwable) {
            return
        }

        if (!platformName.startsWith("android")) {
            sourceCollector[platformName] = kspDependency
        }
    }

    override fun configure(
        project: Project
    ) {
        val dependencies = project.dependencies
        val buildDir = project.buildDir.absolutePath.trimEnd('/')
        val sourceCollector: MutableMap<String, String> = mutableMapOf()

        project.extensions.configure<KotlinMultiplatformExtension>("kotlin") {
            for (sourceSet in sourceSets) {
                if (
                    sourceSet.name == "androidTest" || (sourceSet.name.endsWith("Test") && !sourceSet.name.startsWith("android"))
                ) {
                    val platformName = cleanSourceName(sourceSet.name)
                    extendSourceSet(platformName, sourceSet, buildDir)

                    addSource(platformName, sourceCollector, dependencies)
                }
            }
        }

        if (sourceCollector.isNotEmpty()) {
            project.afterEvaluate {
                KmpSetupConfigurator.wireSharedSourceTasks(project, sourceCollector)
            }
        }
    }
}
