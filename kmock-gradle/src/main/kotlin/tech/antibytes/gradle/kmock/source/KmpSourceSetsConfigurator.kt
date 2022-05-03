/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.PRECEDENCE
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
        when {
            sourceSet.name == "androidAndroidTestDebug" -> {
                sourceSet.kotlin.srcDir(
                    "$buildDir/generated/ksp/android/androidDebugAndroidTest"
                )
            }
            sourceSet.name == "androidAndroidTestRelease" -> {
                sourceSet.kotlin.srcDir(
                    "$buildDir/generated/ksp/android/androidReleaseAndroidTest"
                )
            }
            platformName == "androidAndroid" -> { /* Do nothing*/ }
            else -> {
                sourceSet.kotlin.srcDir(
                    "$buildDir/generated/ksp/$platformName/${sourceSet.name}"
                )
            }
        }
    }

    private fun collectDependencies(
        platformName: String,
        dependencies: Set<KotlinSourceSet>,
        dependencyCollector: MutableMap<String, Set<String>>
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
        dependencyHandler: DependencyHandler
    ) {
        if (sourceSetName != "androidAndroidTestDebug" && sourceSetName != "androidAndroidTestRelease") {
            val kspDependency = resolveKspDependency(platformName)
            try {
                addKspDependency(dependencyHandler, "ksp${platformName.capitalize(Locale.ROOT)}Test")
            } catch (e: Throwable) {
                collectDependencies(
                    sourceSetName,
                    dependencies,
                    metaDependencies
                )
                return
            }

            collectDependencies(platformName, dependencies, dependencyCollector)
            kspCollector[platformName] = kspDependency
        }
    }

    private fun setPrecedence(
        sourceSetName: String,
        precedences: MutableMap<String, Int>
    ) {
        if (sourceSetName in precedences) {
            precedences[sourceSetName] = precedences[sourceSetName]!! - 1
        } else {
            precedences[sourceSetName] = 0
        }
    }

    private fun propagatePrecedences(
        project: Project,
        precedences: Map<String, Int>
    ) {
        val ksp: KspExtension = project.extensions.getByType(KspExtension::class.java)

        precedences.forEach { (sourceSet, precedence) ->
            ksp.arg("$PRECEDENCE$sourceSet", precedence.toString())
        }
    }

    private fun extractPrecedences(
        platformDependencies: Map<String, Set<String>>,
        metaDependencies: Map<String, Set<String>>,
    ): Map<String, Int> {
        val precedences: MutableMap<String, Int> = mutableMapOf()

        metaDependencies.keys.forEach { key ->
            val dependencies = metaDependencies[key]!!.toMutableSet()
            var idx = 0

            while (idx < dependencies.size) {
                val sourceSet = dependencies.elementAt(idx)

                val inMeta = sourceSet in metaDependencies
                val inPlatform = sourceSet in platformDependencies

                when {
                    inMeta && inPlatform -> {
                        dependencies.addAll(metaDependencies[sourceSet]!!)
                        dependencies.addAll(platformDependencies[sourceSet]!!)

                        dependencies.remove(sourceSet)

                        setPrecedence(sourceSet, precedences)
                    }
                    inMeta -> {
                        dependencies.addAll(metaDependencies[sourceSet]!!)

                        dependencies.remove(sourceSet)

                        setPrecedence(sourceSet, precedences)
                    }
                    inPlatform -> {
                        dependencies.addAll(platformDependencies[sourceSet]!!)

                        dependencies.remove(sourceSet)

                        setPrecedence(sourceSet, precedences)
                    }
                    else -> idx++
                }
            }
        }

        return precedences
    }

    private fun isAllowedSourceSet(sourceSetName: String): Boolean {
        return sourceSetName == "androidTest" ||
            sourceSetName == "androidAndroidTest" ||
            sourceSetName == "androidAndroidTestDebug" ||
            sourceSetName == "androidAndroidTestRelease" ||
            (sourceSetName.endsWith("Test") && !sourceSetName.startsWith("android"))
    }

    override fun configure(project: Project) {
        val dependencies = project.dependencies
        val buildDir = project.buildDir.absolutePath.trimEnd('/')
        val kspCollector: MutableMap<String, String> = mutableMapOf()
        val sourceDependencies: MutableMap<String, Set<String>> = mutableMapOf()
        val metaDependencies: MutableMap<String, Set<String>> = mutableMapOf()

        project.extensions.configure<KotlinMultiplatformExtension>("kotlin") {
            for (sourceSet in sourceSets) {
                if (isAllowedSourceSet(sourceSet.name)) {
                    val platformName = cleanSourceName(sourceSet.name)
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

        val precedences = extractPrecedences(sourceDependencies, metaDependencies)

        if (kspCollector.isNotEmpty()) {
            propagatePrecedences(project, precedences)
        }
    }
}
