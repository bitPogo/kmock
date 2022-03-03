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
import tech.antibytes.gradle.util.isAndroid
import java.io.File
import java.util.Locale

internal object KmpSetupConfigurator : KMockPluginContract.KmpSetupConfigurator {
    private val precedences = listOf(
        "jvm",
        "js",
    )
    private val target = "commonTest"
    private val indicator = "COMMON SOURCE"

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
        cleanUpTask.indicators.add(indicator)
        cleanUpTask.description = "Removes Contradicting Sources"
        cleanUpTask.group = "Code Generation"

        return cleanUpTask.dependsOn(*moveTasks)
            .mustRunAfter(*moveTasks)
            .mustRunAfter(kspTask)
    }

    private fun selectReferenceSource(
        project: Project,
        sources: Map<String, String>
    ): Pair<Copy, String> {
        val actualSources = sources.keys

        precedences.forEach { precedence ->
            if (precedence in actualSources) {
                return Pair(
                    SharedSourceCopist.copySharedSource(
                        project = project,
                        platform = precedence,
                        source = precedence + "Test",
                        target = target,
                        indicator = indicator,
                    ),
                    precedence
                )
            }
        }

        return Pair(
            SharedSourceCopist.copySharedSource(
                project = project,
                platform = actualSources.first(),
                source = actualSources.first() + "Test",
                target = target,
                indicator = indicator
            ),
            actualSources.first()
        )
    }

    private fun useAndroid(project: Project): Pair<Copy, String> {
        val androidDebug = "androidDebugUnitTest"
        val androidDebugKsp = "kspDebugUnitTestKotlinAndroid"

        val androidRelease = "androidReleaseUnitTest"
        val androidReleaseKsp = "kspReleaseUnitTestKotlinAndroid"

        val copyToCommon = SharedSourceCopist.copySharedSource(
            project = project,
            platform = "android",
            source = androidDebug,
            target = target,
            indicator = indicator
        ).dependsOn(androidDebugKsp).mustRunAfter(androidDebugKsp) as Copy

        val cleanUpTaskDebug = createCleanUpTask(
            project = project,
            platformName = "android",
            sourceSetName = androidDebug,
            kspTask = androidDebugKsp,
            moveTasks = arrayOf(copyToCommon)
        )

        val cleanUpTaskRelease = createCleanUpTask(
            project = project,
            platformName = "android",
            sourceSetName = androidRelease,
            kspTask = androidReleaseKsp,
            moveTasks = arrayOf(copyToCommon)
        )

        project.tasks.getByName("compileDebugUnitTestKotlinAndroid").dependsOn(
            cleanUpTaskDebug,
            copyToCommon
        )

        project.tasks.getByName("compileReleaseUnitTestKotlinAndroid").dependsOn(
            cleanUpTaskRelease,
            copyToCommon
        ).mustRunAfter(copyToCommon)

        project.tasks.getByName(androidReleaseKsp).mustRunAfter(copyToCommon)

        return Pair(
            copyToCommon,
            "androidDebugUnitTest"
        )
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

    override fun wireSharedSourceTasks(
        project: Project,
        sourceCollector: Map<String, String>
    ) {
        val (copyToCommon, precedence) = if (project.isAndroid()) {
            useAndroid(project)
        } else {
            selectReferenceSource(project, sourceCollector)
        }

        copyToCommon.doLast { setUpKmpEntryPoint(this.project) }

        sourceCollector.forEach { (platform, kspTask) ->
            val cleanUpTask = createCleanUpTask(
                project,
                platform,
                "${platform}Test",
                kspTask,
                arrayOf(copyToCommon)
            )

            val compileTask = project.tasks.getByName(
                "compileTestKotlin${platform.capitalize(Locale.ROOT)}"
            ).dependsOn(
                cleanUpTask,
                copyToCommon
            )

            if (precedence != platform) {
                compileTask.mustRunAfter(copyToCommon)
                project.tasks.getByName(kspTask).mustRunAfter(copyToCommon)
            }
        }
    }
}
