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
    enum class Precedence(val mapping: Triple<String, String, String>?) {
        NONE(null),
        JS(Triple("js", "jsTest", "js")),
        JVM(Triple("jvm", "jvmTest", "jvm")),
        ANDROID(Triple("android", "androidDebugUnitTest", "androidDebugUnitTest")),
    }

    private val common = "commonTest"
    private val indicatorCommon = "COMMON SOURCE"

    private fun createCleanUpTask(
        project: Project,
        platformName: String,
        sourceSetName: String,
        kspTask: String,
        moveTasks: Array<Copy>
    ): Task {
        val cleanUpTask: KMockCleanTask = project.tasks.create(
            "cleanDuplicates${sourceSetName.capitalize(Locale.ROOT)}",
            KMockCleanTask::class.java
        )

        cleanUpTask.target.set(sourceSetName)
        cleanUpTask.targetPlatform.set(platformName)
        cleanUpTask.indicators.add(indicatorCommon)
        cleanUpTask.description = "Removes Contradicting Sources"
        cleanUpTask.group = "Code Generation"

        return cleanUpTask.dependsOn(*moveTasks)
            .mustRunAfter(*moveTasks)
            .mustRunAfter(kspTask)
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

        copy.dependsOn(dependency).mustRunAfter(dependency)
    }

    private fun selectReferenceSource(
        project: Project,
        sources: Map<String, String>
    ): Pair<Copy, String> {
        val actualSources = sources.keys
        var precedence = Precedence.NONE
        var kspTask = ""

        actualSources.forEach { source ->
            val current = when (source) {
                "android" -> Precedence.ANDROID
                "jvm" -> Precedence.JVM
                "js" -> Precedence.JS
                else -> Precedence.NONE
            }

            if (current.ordinal > precedence.ordinal) {
                precedence = current
                kspTask = sources[source]!!
            }
        }

        return if (precedence.mapping != null) {
            Pair(
                SharedSourceCopist.copySharedSource(
                    project = project,
                    platform = precedence.mapping!!.first,
                    source = precedence.mapping!!.second,
                    target = common,
                    indicator = indicatorCommon
                ).also { copy -> setCopyTaskDependencies(copy, kspTask) },
                precedence.mapping!!.third
            )
        } else {
            Pair(
                SharedSourceCopist.copySharedSource(
                    project = project,
                    platform = actualSources.first(),
                    source = actualSources.first() + "Test",
                    target = common,
                    indicator = indicatorCommon
                ).also { copy -> setCopyTaskDependencies(copy, sources.values.first()) },
                actualSources.first()
            )
        }
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
        copyTask: Copy,
    ) {
        val androidDebug = "androidDebugUnitTest"
        val androidDebugKsp = "kspDebugUnitTestKotlinAndroid"

        val androidRelease = "androidReleaseUnitTest"
        val androidReleaseKsp = "kspReleaseUnitTestKotlinAndroid"

        val cleanUpTaskDebug = createCleanUpTask(
            project = project,
            platformName = "android",
            sourceSetName = androidDebug,
            kspTask = androidDebugKsp,
            moveTasks = arrayOf(copyTask)
        )

        val cleanUpTaskRelease = createCleanUpTask(
            project = project,
            platformName = "android",
            sourceSetName = androidRelease,
            kspTask = androidReleaseKsp,
            moveTasks = arrayOf(copyTask)
        )

        project.tasks.getByName("compileDebugUnitTestKotlinAndroid").dependsOn(
            cleanUpTaskDebug,
            copyTask
        )

        project.tasks.getByName("compileReleaseUnitTestKotlinAndroid").dependsOn(
            cleanUpTaskRelease,
            copyTask
        ).mustRunAfter(copyTask)

        project.tasks.getByName(androidReleaseKsp).mustRunAfter(copyTask)
    }

    private fun setUpTaskChain(
        project: Project,
        precedence: String,
        platform: String,
        kspTask: String,
        copyTask: Copy,
    ) {
        val cleanUpTask = createCleanUpTask(
            project,
            platform,
            "${platform}Test",
            kspTask,
            arrayOf(copyTask)
        )

        val compileTask = project.tasks.getByName(
            "compileTestKotlin${platform.capitalize(Locale.ROOT)}"
        ).dependsOn(
            cleanUpTask,
            copyTask
        )

        if (precedence != platform) {
            compileTask.mustRunAfter(copyTask)
            project.tasks.getByName(kspTask).mustRunAfter(copyTask)
        }
    }

    private fun wireSharedSourceTasks(
        project: Project,
        indicators: Map<String, String>,
        sourceCollector: Map<String, String>
    ) {
        val (copyToCommon, precedence) = selectReferenceSource(project, sourceCollector)

        copyToCommon.doLast { setUpKmpEntryPoint(this.project) }

        sourceCollector.forEach { (platform, kspTask) ->
            if (platform == "android") {
                setUpAndroidTaskChain(
                    project,
                    copyToCommon
                )
            } else {
                setUpTaskChain(
                    project = project,
                    precedence = precedence,
                    platform = platform,
                    kspTask = kspTask,
                    copyTask = copyToCommon
                )
            }
        }
    }

    override fun wireSharedSourceTasks(
        project: Project,
        sourceCollector: Map<String, String>
    ) {
        val indicators: MutableMap<String, String> = (
            project.extensions
                .getByType(KMockExtension::class.java)
                .sharedSources
                .orNull ?: mutableMapOf()
            ).toMutableMap()

        indicators[common] = indicatorCommon

        wireSharedSourceTasks(project, indicators, sourceCollector)
    }
}
