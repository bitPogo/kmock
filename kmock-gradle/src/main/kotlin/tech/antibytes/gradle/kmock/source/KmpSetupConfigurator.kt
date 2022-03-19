/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import tech.antibytes.gradle.kmock.KMockCleanTask
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.SharedSourceCopist
import java.util.Locale

internal object KmpSetupConfigurator : KMockPluginContract.KmpSetupConfigurator {
    private const val common = "commonTest"

    private const val androidDebug = "androidDebugUnit"
    private const val androidDebugTest = "androidDebugUnitTest"
    private const val androidDebugKsp = "kspDebugUnitTestKotlinAndroid"

    private const val androidRelease = "androidReleaseUnit"
    private const val androidReleaseTest = "androidReleaseUnitTest"
    private const val androidReleaseKsp = "kspReleaseUnitTestKotlinAndroid"

    private fun createCleanUpTask(
        project: Project,
        platform: String,
        sourceSet: String,
        indicators: List<String>,
        kspTask: String,
        moveTasks: Array<Copy>
    ): Task {
        val cleanUpTask: KMockCleanTask = project.tasks.create(
            "cleanDuplicates${sourceSet.capitalize(Locale.ROOT)}",
            KMockCleanTask::class.java
        )

        cleanUpTask.target.set(sourceSet)
        cleanUpTask.platform.set(platform)
        cleanUpTask.indicators.addAll(indicators)
        cleanUpTask.description = "Removes Contradicting Sources for $platform"
        cleanUpTask.group = "Code Generation"

        return cleanUpTask.dependsOn(*moveTasks)
            .mustRunAfter(*moveTasks)
            .mustRunAfter(kspTask)
    }

    private fun setupAndroidCleanUpTasks(
        project: Project,
        indicators: List<String>,
        copyTasksDebug: Array<Copy>,
        copyTasksRelease: Array<Copy>
    ): Pair<Task, Task> {
        val cleanUpTaskDebug = createCleanUpTask(
            project = project,
            platform = androidDebug,
            indicators = indicators,
            sourceSet = androidDebugTest,
            kspTask = androidDebugKsp,
            moveTasks = copyTasksDebug
        )

        val cleanUpTaskRelease = createCleanUpTask(
            project = project,
            platform = androidRelease,
            indicators = indicators,
            sourceSet = androidReleaseTest,
            kspTask = androidReleaseKsp,
            moveTasks = copyTasksRelease
        )

        return Pair(cleanUpTaskDebug, cleanUpTaskRelease)
    }

    private fun createCopyTask(
        project: Project,
        platform: String,
        target: String,
        indicator: String,
        kspTask: String,
    ): Copy {
        return SharedSourceCopist.copySharedSource(
            project = project,
            platform = platform,
            source = platform + "Test",
            target = target,
            indicator = indicator,
        ).also { copyTask ->
            copyTask.dependsOn(kspTask)
            copyTask.mustRunAfter(kspTask)
        }
    }

    private fun chainCopyTask(
        project: Project,
        platform: String,
        target: String,
        indicator: String,
        kspTask: String,
    ): Pair<Copy, Copy?> {
        return if (platform == "android") {
            val copyTaskDebug = createCopyTask(
                project = project,
                platform = androidDebug,
                target = target,
                indicator = indicator,
                kspTask = androidDebugKsp,
            )

            val copyTaskRelease = createCopyTask(
                project = project,
                platform = androidRelease,
                target = target,
                indicator = indicator,
                kspTask = androidReleaseKsp,
            )

            Pair(copyTaskDebug, copyTaskRelease)
        } else {
            val platformCopyTasks = createCopyTask(
                project = project,
                platform = platform,
                target = target,
                indicator = indicator,
                kspTask = kspTask,
            )

            Pair(platformCopyTasks, null)
        }
    }

    // Note this only necessary due to the Android special case
    private fun appendCopyTasks(
        platform: String,
        copyTasks: Pair<Copy, Copy?>,
        copyTaskContainer: MutableMap<String, MutableList<Copy>>,
    ) {
        if (platform != "android") {
            val platformSpecificCopyTasks = copyTaskContainer.getOrElse(platform) { mutableListOf() }
            platformSpecificCopyTasks.add(copyTasks.first)

            copyTaskContainer[platform] = platformSpecificCopyTasks
        } else {
            val androidDebugCopyTasks = copyTaskContainer.getOrElse(androidDebug) { mutableListOf() }
            androidDebugCopyTasks.add(copyTasks.first)

            copyTaskContainer[androidDebug] = androidDebugCopyTasks

            val androidReleaseCopyTasks = copyTaskContainer.getOrElse(androidRelease) { mutableListOf() }
            androidReleaseCopyTasks.add(copyTasks.second!!)

            copyTaskContainer[androidRelease] = androidReleaseCopyTasks
        }
    }

    private fun createCopyTasks(
        project: Project,
        indicators: Map<String, String>,
        kspMapping: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ): Map<String, MutableList<Copy>> {
        val copyTaskContainer: MutableMap<String, MutableList<Copy>> = mutableMapOf()

        dependencies.forEach { (sharedSource, platforms) ->
            platforms.forEach { platform ->
                val copyTasks = chainCopyTask(
                    project = project,
                    platform = platform,
                    target = sharedSource,
                    indicator = indicators[sharedSource]!!,
                    kspTask = kspMapping[platform]!!,
                )

                appendCopyTasks(
                    platform = platform,
                    copyTasks = copyTasks,
                    copyTaskContainer = copyTaskContainer,
                )
            }
        }

        return copyTaskContainer
    }

    private fun setUpAndroidTaskChain(
        project: Project,
        indicators: List<String>,
        copyTasksContainer: Map<String, List<Copy>>,
    ) {
        val copyDebugTasks = copyTasksContainer[androidDebug]!!.toTypedArray()
        val copyReleaseTasks = copyTasksContainer[androidRelease]!!.toTypedArray()

        val (cleanUpTaskDebug, cleanUpTaskRelease) = setupAndroidCleanUpTasks(
            project,
            indicators,
            copyDebugTasks,
            copyReleaseTasks
        )

        project.tasks.getByName("compileDebugUnitTestKotlinAndroid")
            .dependsOn(cleanUpTaskDebug)
            .mustRunAfter(cleanUpTaskDebug)
            .dependsOn(*copyDebugTasks)
            .mustRunAfter(*copyDebugTasks)

        project.tasks.getByName("compileReleaseUnitTestKotlinAndroid")
            .dependsOn(cleanUpTaskRelease)
            .mustRunAfter(cleanUpTaskRelease)
            .dependsOn(*copyReleaseTasks)
            .mustRunAfter(*copyReleaseTasks)
    }

    private fun setUpTaskChain(
        project: Project,
        indicators: List<String>,
        platform: String,
        kspTask: String,
        copyTasksContainer: Map<String, List<Copy>>,
    ) {
        val copyTasks = copyTasksContainer[platform]!!.toTypedArray()
        val cleanUpTask = createCleanUpTask(
            project,
            platform,
            "${platform}Test",
            indicators,
            kspTask,
            copyTasks
        )

        project.tasks.getByName(
            "compileTestKotlin${platform.capitalize(Locale.ROOT)}"
        ).dependsOn(
            cleanUpTask
        ).mustRunAfter(
            cleanUpTask
        ).dependsOn(
            *copyTasks
        ).mustRunAfter(
            *copyTasks
        )
    }

    private fun wireSharedSourceTasks(
        project: Project,
        indicatorMapping: Map<String, String>,
        kspMapping: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ) {
        val copyTasks = createCopyTasks(
            project,
            indicatorMapping,
            kspMapping,
            dependencies
        )

        val indicators = indicatorMapping.values.toList()
        kspMapping.forEach { (platform, kspTask) ->
            if (platform == "android") {
                setUpAndroidTaskChain(
                    project,
                    indicators,
                    copyTasks
                )
            } else {
                setUpTaskChain(
                    project,
                    indicators,
                    platform,
                    kspTask,
                    copyTasks
                )
            }
        }
    }

    override fun wireSharedSourceTasks(
        project: Project,
        kspMapping: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ) {
        val indicators: MutableMap<String, String> = dependencies.keys
            .associateWith { sourceSetName -> sourceSetName.toUpperCase(Locale.ROOT) }
            .toMutableMap()

        indicators[common] = common.toUpperCase(Locale.ROOT)

        wireSharedSourceTasks(
            project,
            indicators,
            kspMapping,
            dependencies
        )
    }
}
