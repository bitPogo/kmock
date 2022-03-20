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
        moveTasks: Array<Copy>,
        allMoveTasks: Array<Copy> // just make Gradle stop complaining
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
            .mustRunAfter(*allMoveTasks)
            .mustRunAfter(kspTask)
    }

    private fun setupAndroidCleanUpTasks(
        project: Project,
        indicators: List<String>,
        moveTasksDebug: Array<Copy>,
        moveTasksRelease: Array<Copy>,
        allMoveTasks: Array<Copy>,
    ): Pair<Task, Task> {
        val cleanUpTaskDebug = createCleanUpTask(
            project = project,
            platform = "android",
            indicators = indicators,
            sourceSet = androidDebugTest,
            kspTask = androidDebugKsp,
            moveTasks = moveTasksDebug,
            allMoveTasks = allMoveTasks
        )

        val cleanUpTaskRelease = createCleanUpTask(
            project = project,
            platform = "android",
            indicators = indicators,
            sourceSet = androidReleaseTest,
            kspTask = androidReleaseKsp,
            moveTasks = moveTasksRelease,
            allMoveTasks = allMoveTasks
        )

        return Pair(cleanUpTaskDebug, cleanUpTaskRelease)
    }

    private fun createCopyTask(
        project: Project,
        platform: String,
        target: String,
        indicator: String,
        kspTask: String,
        allKspTasks: Array<String>,
    ): Copy {
        return SharedSourceCopist.copySharedSource(
            project = project,
            platform = platform,
            source = platform + "Test",
            target = target,
            indicator = indicator,
        ).also { copyTask ->
            copyTask.dependsOn(kspTask)
            copyTask.mustRunAfter(*allKspTasks)
        }
    }

    private fun chainCopyTask(
        project: Project,
        platform: String,
        target: String,
        indicator: String,
        kspTask: String,
        allKspTasks: Array<String>,
    ): Pair<Copy, Copy?> {
        return if (platform == "android") {
            val copyTaskDebug = createCopyTask(
                project = project,
                platform = androidDebug,
                target = target,
                indicator = indicator,
                kspTask = androidDebugKsp,
                allKspTasks = allKspTasks
            )

            val copyTaskRelease = createCopyTask(
                project = project,
                platform = androidRelease,
                target = target,
                indicator = indicator,
                kspTask = androidReleaseKsp,
                allKspTasks = allKspTasks
            )

            Pair(copyTaskDebug, copyTaskRelease)
        } else {
            val platformMoveTasks = createCopyTask(
                project = project,
                platform = platform,
                target = target,
                indicator = indicator,
                kspTask = kspTask,
                allKspTasks = allKspTasks
            )

            Pair(platformMoveTasks, null)
        }
    }

    // Note this only necessary due to the Android special case
    private fun appendMoveTasks(
        platform: String,
        moveTasks: Pair<Copy, Copy?>,
        copyTaskContainer: MutableMap<String, MutableSet<Copy>>,
        allMoveTasks: MutableSet<Copy>,
    ) {
        if (platform != "android") {
            val platformSpecificMoveTasks = copyTaskContainer.getOrElse(platform) { mutableSetOf() }
            platformSpecificMoveTasks.add(moveTasks.first)

            copyTaskContainer[platform] = platformSpecificMoveTasks
            allMoveTasks.addAll(platformSpecificMoveTasks)
        } else {
            val androidDebugMoveTasks = copyTaskContainer.getOrElse(androidDebug) { mutableSetOf() }
            androidDebugMoveTasks.add(moveTasks.first)

            copyTaskContainer[androidDebug] = androidDebugMoveTasks

            val androidReleaseMoveTasks = copyTaskContainer.getOrElse(androidRelease) { mutableSetOf() }
            androidReleaseMoveTasks.add(moveTasks.second!!)

            copyTaskContainer[androidRelease] = androidReleaseMoveTasks

            allMoveTasks.addAll(androidDebugMoveTasks)
            allMoveTasks.addAll(androidReleaseMoveTasks)
        }
    }

    private fun createMoveTasks(
        project: Project,
        indicators: Map<String, String>,
        kspMapping: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ): Pair<Map<String, MutableSet<Copy>>, Array<Copy>> {
        val copyTaskContainer: MutableMap<String, MutableSet<Copy>> = mutableMapOf()
        val allMoveTasks: MutableSet<Copy> = mutableSetOf()

        val kspTasks: MutableSet<String> = mutableSetOf()

        kspMapping.forEach { (platform, kspTask) ->
            if (platform == "android") {
                kspTasks.add(androidDebugKsp)
                kspTasks.add(androidReleaseKsp)
            } else {
                kspTasks.add(kspTask)
            }
        }

        val allKspTasks = kspTasks.toTypedArray()

        dependencies.forEach { (sharedSource, platforms) ->
            platforms.forEach { platform ->
                val moveTasks = chainCopyTask(
                    project = project,
                    platform = platform,
                    target = sharedSource,
                    indicator = indicators[sharedSource]!!,
                    kspTask = kspMapping[platform]!!,
                    allKspTasks = allKspTasks,
                )

                appendMoveTasks(
                    platform = platform,
                    moveTasks = moveTasks,
                    copyTaskContainer = copyTaskContainer,
                    allMoveTasks = allMoveTasks
                )
            }
        }

        return Pair(copyTaskContainer, allMoveTasks.toTypedArray())
    }

    private fun setUpAndroidTaskChain(
        project: Project,
        indicators: List<String>,
        moveTasksContainer: Pair<Map<String, Set<Copy>>, Array<Copy>>,
    ) {
        val moveDebugTasks = moveTasksContainer.first[androidDebug]!!.toTypedArray()
        val moveReleaseTasks = moveTasksContainer.first[androidRelease]!!.toTypedArray()

        val (cleanUpTaskDebug, cleanUpTaskRelease) = setupAndroidCleanUpTasks(
            project = project,
            indicators = indicators,
            moveTasksDebug = moveDebugTasks,
            moveTasksRelease = moveReleaseTasks,
            allMoveTasks = moveTasksContainer.second
        )

        project.tasks.getByName("compileDebugUnitTestKotlinAndroid")
            .dependsOn(cleanUpTaskDebug)
            .mustRunAfter(cleanUpTaskDebug)
            .dependsOn(*moveDebugTasks)
            .mustRunAfter(*moveTasksContainer.second)

        project.tasks.getByName("compileReleaseUnitTestKotlinAndroid")
            .dependsOn(cleanUpTaskRelease)
            .mustRunAfter(cleanUpTaskRelease)
            .dependsOn(*moveReleaseTasks)
            .mustRunAfter(*moveTasksContainer.second)
    }

    private fun setUpTaskChain(
        project: Project,
        indicators: List<String>,
        platform: String,
        kspTask: String,
        moveTasksContainer: Pair<Map<String, Set<Copy>>, Array<Copy>>,
    ) {
        val moveTasks = moveTasksContainer.first[platform]!!.toTypedArray()
        val cleanUpTask = createCleanUpTask(
            project = project,
            platform = platform,
            sourceSet = "${platform}Test",
            indicators = indicators,
            kspTask = kspTask,
            moveTasks = moveTasks,
            allMoveTasks = moveTasksContainer.second
        )

        project.tasks.getByName(
            "compileTestKotlin${platform.capitalize(Locale.ROOT)}"
        ).dependsOn(
            cleanUpTask
        ).mustRunAfter(
            cleanUpTask
        ).dependsOn(
            *moveTasks
        ).mustRunAfter(
            *moveTasksContainer.second
        )
    }

    private fun wireSharedSourceTasks(
        project: Project,
        indicatorMapping: Map<String, String>,
        kspMapping: Map<String, String>,
        dependencies: Map<String, Set<String>>
    ) {
        val moveTasks = createMoveTasks(
            project,
            indicatorMapping,
            kspMapping,
            dependencies
        )

        val indicators = indicatorMapping.values.toList()
        kspMapping.forEach { (platform, kspTask) ->
            if (platform == "android") {
                setUpAndroidTaskChain(
                    project = project,
                    indicators = indicators,
                    moveTasksContainer = moveTasks
                )
            } else {
                setUpTaskChain(
                    project = project,
                    indicators = indicators,
                    platform = platform,
                    kspTask = kspTask,
                    moveTasksContainer = moveTasks
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
