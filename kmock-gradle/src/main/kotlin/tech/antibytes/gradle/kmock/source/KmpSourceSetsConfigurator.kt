/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import com.google.devtools.ksp.gradle.KspExtension
import java.util.Locale
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.DEPENDENCIES
import tech.antibytes.gradle.kmock.KMockPluginContract.SourceSetConfigurator
import tech.antibytes.gradle.kmock.config.MainConfig

internal object KmpSourceSetsConfigurator : SourceSetConfigurator {
    private fun cleanSourceName(sourceName: String): String = sourceName.substringBeforeLast("Test")

    private fun addKspDependency(
        dependencies: DependencyHandler,
        dependency: String,
    ) {
        dependencies.add(
            dependency,
            "tech.antibytes.kmock:kmock-processor:${MainConfig.version}",
        )
    }

    private fun extendSourceSet(
        platformName: String,
        sourceSet: KotlinSourceSet,
        buildDir: String,
    ) {
        when {
            sourceSet.name == "androidAndroidTestDebug" -> {
                sourceSet.kotlin.srcDir(
                    "$buildDir/generated/ksp/android/androidDebugAndroidTest",
                )
            }
            sourceSet.name == "androidAndroidTestRelease" -> {
                sourceSet.kotlin.srcDir(
                    "$buildDir/generated/ksp/android/androidReleaseAndroidTest",
                )
            }
            platformName == "androidAndroid" -> {
                /* Do nothing*/
            }
            else -> {
                sourceSet.kotlin.srcDir(
                    "$buildDir/generated/ksp/$platformName/${sourceSet.name}",
                )
            }
        }
    }

    private fun collectDependencies(
        platformName: String,
        dependencies: Set<KotlinSourceSet>,
        dependencyCollector: MutableMap<String, Set<String>>,
    ) {
        dependencies.forEach { dependency ->
            val dependsOn = dependencyCollector.getOrElse(dependency.name) { mutableSetOf() }
                .toMutableSet()

            dependsOn.add(platformName)

            dependencyCollector[dependency.name] = dependsOn
        }
    }

    private fun resolveKspDependency(platformName: String): String {
        return if (platformName == "androidAndroid") {
            "kspAndroidTestKotlinAndroid"
        } else {
            "kspTestKotlin${platformName.capitalize(Locale.ROOT)}"
        }
    }

    private fun addSource(
        sourceSetName: String,
        platformName: String,
        dependencies: Set<KotlinSourceSet>,
        kspCollector: MutableMap<String, String>,
        dependencyCollector: MutableMap<String, Set<String>>,
        metaDependencies: MutableMap<String, Set<String>>,
        dependencyHandler: DependencyHandler,
    ) {
        if (sourceSetName != "androidAndroidTestDebug" && sourceSetName != "androidAndroidTestRelease") {
            val kspDependency = resolveKspDependency(platformName)
            try {
                addKspDependency(dependencyHandler, "ksp${platformName.capitalize(Locale.ROOT)}Test")
            } catch (e: Throwable) {
                collectDependencies(
                    sourceSetName,
                    dependencies,
                    metaDependencies,
                )
                return
            }

            collectDependencies(platformName, dependencies, dependencyCollector)
            kspCollector[platformName] = kspDependency
        }
    }

    private fun propagateDependencies(
        project: Project,
        dependencies: Map<String, Set<String>>,
    ) {
        val ksp: KspExtension = project.extensions.getByType(KspExtension::class.java)

        dependencies.forEach { (sourceSet, ancestors) ->
            ancestors.forEachIndexed { idx, ancestor ->
                ksp.arg("$DEPENDENCIES$sourceSet#$idx", ancestor)
            }
        }
    }

    private fun isAllowedSourceSet(sourceSetName: String): Boolean {
        return sourceSetName == "androidTest" ||
            sourceSetName == "androidAndroidTest" ||
            sourceSetName == "androidAndroidTestDebug" ||
            sourceSetName == "androidAndroidTestRelease" ||
            (sourceSetName.endsWith("Test") && !sourceSetName.startsWith("android"))
    }

    private fun Project.chainTests(): Boolean {
        return findProperty("kmock.noParallelTests")
            ?.toString()
            ?.toBoolean()
            ?: true
    }

    override fun configure(project: Project) {
        val dependencies = project.dependencies
        val buildDir = project.buildDir.absolutePath.trimEnd('/')
        val kspCollector: MutableMap<String, String> = mutableMapOf()
        val sourceDependencies: MutableMap<String, Set<String>> = mutableMapOf()
        val metaDependencies: MutableMap<String, Set<String>> = mutableMapOf()
        val platforms: MutableList<String> = mutableListOf()

        project.extensions.configure<KotlinMultiplatformExtension>("kotlin") {
            for (sourceSet in sourceSets) {
                if (isAllowedSourceSet(sourceSet.name)) {
                    val platformName = cleanSourceName(sourceSet.name)
                    platforms.add(platformName)

                    extendSourceSet(platformName, sourceSet, buildDir)

                    addSource(
                        sourceSetName = sourceSet.name,
                        platformName = platformName,
                        dependencies = sourceSet.dependsOn,
                        kspCollector = kspCollector,
                        dependencyCollector = sourceDependencies,
                        metaDependencies = metaDependencies,
                        dependencyHandler = dependencies,
                    )
                }
            }
        }

        val ancestors = DependencyGraph.resolveAncestors(
            sourceDependencies = sourceDependencies,
            metaDependencies = metaDependencies,
        )

        if (kspCollector.isNotEmpty()) {
            propagateDependencies(project, ancestors)
            KmpCleanup.cleanup(project, platforms)
            if (project.chainTests()) {
                KmpTestTaskChain.chainTasks(project, kspCollector)
            }
        }
    }
}
