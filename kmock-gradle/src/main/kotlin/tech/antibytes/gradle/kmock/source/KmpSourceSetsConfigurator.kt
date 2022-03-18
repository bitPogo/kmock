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
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.PRECEDENCE_PREFIX
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

    private fun addSource(
        sourceSetName: String,
        platformName: String,
        dependencies: Set<KotlinSourceSet>,
        kspCollector: MutableMap<String, String>,
        dependencyCollector: MutableMap<String, Set<String>>,
        metaDependencies: MutableMap<String, Set<String>>,
        dependencyHandler: DependencyHandler
    ) {
        val kspDependency = "kspTestKotlin${platformName.capitalize(Locale.ROOT)}"
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
            ksp.arg("$PRECEDENCE_PREFIX$sourceSet", precedence.toString())
        }
    }

    private fun flattenMetaDependencies(
        platformDependencies: Map<String, Set<String>>,
        metaDependencies: Map<String, Set<String>>,
        precedences: MutableMap<String, Int>
    ): MutableMap<String, Set<String>> {
        val flattenedDependencies: MutableMap<String, Set<String>> = mutableMapOf()

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

            flattenedDependencies[key] = dependencies
        }

        return flattenedDependencies
    }

    private fun mergeDependencies(
        platformDependencies: Map<String, Set<String>>,
        metaDependencies: Map<String, Set<String>>
    ): Map<String, Set<String>> {
        val dependencies: MutableMap<String, Set<String>> = platformDependencies.toMutableMap()

        metaDependencies.forEach { (key, values) ->
            if (key in platformDependencies) {
                dependencies[key] = dependencies[key]!!.toMutableSet().also { it.addAll(values) }
            } else {
                dependencies[key] = values
            }
        }

        return dependencies
    }

    override fun configure(
        project: Project
    ) {
        val dependencies = project.dependencies
        val buildDir = project.buildDir.absolutePath.trimEnd('/')
        val kspCollector: MutableMap<String, String> = mutableMapOf()
        val sourceDependencies: MutableMap<String, Set<String>> = mutableMapOf()
        val metaDependencies: MutableMap<String, Set<String>> = mutableMapOf()
        val precedences: MutableMap<String, Int> = mutableMapOf()

        project.extensions.configure<KotlinMultiplatformExtension>("kotlin") {
            for (sourceSet in sourceSets) {
                if (
                    sourceSet.name == "androidTest" || (sourceSet.name.endsWith("Test") && !sourceSet.name.startsWith("android"))
                ) {
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

        val allDependencies = mergeDependencies(
            sourceDependencies,
            flattenMetaDependencies(sourceDependencies, metaDependencies, precedences)
        )

        if (kspCollector.isNotEmpty()) {
            propagatePrecedences(project, precedences)

            project.afterEvaluate {
                KmpSetupConfigurator.wireSharedSourceTasks(
                    project,
                    kspCollector,
                    allDependencies
                )
            }
        }
    }
}
