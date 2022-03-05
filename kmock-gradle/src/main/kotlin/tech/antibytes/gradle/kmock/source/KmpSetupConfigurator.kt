/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.getByName
import tech.antibytes.gradle.kmock.FactoryGenerator
import tech.antibytes.gradle.kmock.KMockCleanTask
import tech.antibytes.gradle.kmock.KMockExtension
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.SharedSourceCopist
import java.io.File
import java.util.Locale

internal object KmpSetupConfigurator : KMockPluginContract.KmpSetupConfigurator {
    enum class Precedence(val mapping: Pair<String, String>?) {
        NONE(null),
        JS(Pair("js", "jsTest")),
        JVM(Pair("jvm", "jvmTest")),
        ANDROID(Pair("android", "androidDebugUnitTest")),
    }

    private const val common = "commonTest"
    private const val indicatorCommon = "COMMON SOURCE"

    private const val androidDebug = "androidDebugUnitTest"
    private const val androidDebugKsp = "kspDebugUnitTestKotlinAndroid"

    private const val androidRelease = "androidReleaseUnitTest"
    private const val androidReleaseKsp = "kspReleaseUnitTestKotlinAndroid"

    private fun createCleanUpTask(
        project: Project,
        platformName: String,
        sourceSetName: String,
        indicators: List<String>,
        kspTask: String,
        moveTasks: Array<Copy>
    ): Task {
        val cleanUpTask: KMockCleanTask = project.tasks.create(
            "cleanDuplicates${sourceSetName.capitalize(Locale.ROOT)}",
            KMockCleanTask::class.java
        )

        cleanUpTask.target.set(sourceSetName)
        cleanUpTask.targetPlatform.set(platformName)
        cleanUpTask.indicators.addAll(indicators)
        cleanUpTask.description = "Removes Contradicting Sources"
        cleanUpTask.group = "Code Generation"

        return cleanUpTask.dependsOn(*moveTasks)
            .mustRunAfter(*moveTasks)
            .mustRunAfter(kspTask)
    }

    private fun setupAndroidCleanUpTasks(
        project: Project,
        indicators: List<String>,
        copyTasks: Array<Copy>
    ): Pair<Task, Task> {
        val cleanUpTaskDebug = createCleanUpTask(
            project = project,
            platformName = "android",
            indicators = indicators,
            sourceSetName = androidDebug,
            kspTask = androidDebugKsp,
            moveTasks = copyTasks
        )

        val cleanUpTaskRelease = createCleanUpTask(
            project = project,
            platformName = "android",
            indicators = indicators,
            sourceSetName = androidRelease,
            kspTask = androidReleaseKsp,
            moveTasks = copyTasks
        )

        return Pair(cleanUpTaskDebug, cleanUpTaskRelease)
    }

    private fun setCopyTaskDependencies(
        copy: Copy,
        kspTask: String
    ) {
        val dependency = if (kspTask.endsWith("Android")) {
            "kspDebugUnitTestKotlinAndroid"
        } else {
            kspTask
        }

        copy.dependsOn(dependency)
    }

    private fun selectReferenceSource(
        project: Project,
        dependencies: Set<String>,
        kspMapping: Map<String, String>,
        indicator: String,
        target: String
    ): Copy {
        var precedence = Precedence.NONE
        var kspTask = kspMapping[dependencies.first()]!!

        dependencies.forEach { source ->
            val current = when (source) {
                "android" -> Precedence.ANDROID
                "jvm" -> Precedence.JVM
                "js" -> Precedence.JS
                else -> Precedence.NONE
            }

            if (current.ordinal > precedence.ordinal) {
                precedence = current
                kspTask = kspMapping[source]!!
            }
        }

        return if (precedence.mapping != null) {
            SharedSourceCopist.copySharedSource(
                project = project,
                platform = precedence.mapping!!.first,
                source = precedence.mapping!!.second,
                target = target,
                indicator = indicator
            )
        } else {
            SharedSourceCopist.copySharedSource(
                project = project,
                platform = dependencies.first(),
                source = dependencies.first() + "Test",
                target = target,
                indicator = indicator
            )
        }.also { copy -> setCopyTaskDependencies(copy, kspTask) }
    }

    private fun setUpKmpEntryPoint(project: Project) {
        val extension: KMockExtension = project.extensions.getByType(KMockExtension::class.java)

        val kspCommon: File = project.file(
            "${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp/common/commonTest/kotlin"
        )

        FactoryGenerator.generate(
            kspCommon,
            extension.rootPackage
        )
    }

    private fun setUpAndroidTaskChain(
        project: Project,
        indicators: List<String>,
        copyTasks: Array<Copy>,
    ) {
        val (cleanUpTaskDebug, cleanUpTaskRelease) = setupAndroidCleanUpTasks(
            project,
            indicators,
            copyTasks
        )

        project.tasks.getByName("compileDebugUnitTestKotlinAndroid").dependsOn(
            cleanUpTaskDebug
        ).dependsOn(*copyTasks)

        project.tasks.getByName("compileReleaseUnitTestKotlinAndroid").dependsOn(
            cleanUpTaskRelease
        ).dependsOn(*copyTasks)

        copyTasks.forEach { copyTask ->
            copyTask.mustRunAfter(androidDebugKsp)
            copyTask.mustRunAfter(androidReleaseKsp)
        }
    }

    private fun setUpTaskChain(
        project: Project,
        indicators: List<String>,
        platform: String,
        kspTask: String,
        copyTasks: Array<Copy>,
    ) {
        val cleanUpTask = createCleanUpTask(
            project,
            platform,
            "${platform}Test",
            indicators,
            kspTask,
            copyTasks
        )

        val compileTask = project.tasks.getByName(
            "compileTestKotlin${platform.capitalize(Locale.ROOT)}"
        ).dependsOn(
            cleanUpTask
        ).dependsOn(
            *copyTasks
        )

        compileTask.mustRunAfter(*copyTasks)
        copyTasks.forEach { copyTask ->
            copyTask.mustRunAfter(kspTask)
        }
    }

    private fun createCopyTasks(
        project: Project,
        indicators: Map<String, String>,
        kspMapping: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ): Array<Copy> {
        return indicators
            .filter { (source, _) ->
                source in dependencies
            }
            .map { (source, indicator) ->
                selectReferenceSource(
                    project,
                    dependencies[source]!!,
                    kspMapping,
                    indicator,
                    source
                ).also { copySet ->
                    if (source == "commonTest") {
                        copySet.doLast { setUpKmpEntryPoint(this.project) }
                    }
                }
            }
            .toTypedArray()
    }

    private fun wireSharedSourceTasks(
        project: Project,
        indicators: Map<String, String>,
        kspMapping: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ) {
        val copyTasks = createCopyTasks(
            project,
            indicators,
            kspMapping,
            dependencies
        )

        val indicatorMarkers = indicators.values.toList()
        kspMapping.forEach { (platform, kspTask) ->
            if (platform == "android") {
                setUpAndroidTaskChain(
                    project,
                    indicatorMarkers,
                    copyTasks
                )
            } else {
                setUpTaskChain(
                    project,
                    indicatorMarkers,
                    platform,
                    kspTask,
                    copyTasks
                )
            }
        }
    }

    private fun adjustDependencies(
        indicators: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ): Map<String, Set<String>> {
        val cleanDependencies = dependencies.toMutableMap()

        dependencies.keys.forEach { sourceName ->
            if (sourceName !in indicators && sourceName != "commonTest") {
                cleanDependencies.remove(sourceName)
            }
        }

        return cleanDependencies
    }

    override fun wireSharedSourceTasks(
        project: Project,
        kspMapping: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ) {
        val indicators: MutableMap<String, String> = project.extensions
            .getByType(KMockExtension::class.java)
            .sharedSources
            .mapValues { (_, value) -> value.first }
            .toMutableMap()

        indicators[common] = indicatorCommon
        val cleanDependencies = adjustDependencies(
            indicators,
            dependencies
        )

        wireSharedSourceTasks(
            project,
            indicators,
            kspMapping,
            cleanDependencies
        )
    }
}
