/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import tech.antibytes.gradle.kmock.KMockCleanTask
import tech.antibytes.gradle.kmock.KMockPluginContract.SourceSetConfigurator
import tech.antibytes.gradle.kmock.SharedSourceCopist
import tech.antibytes.gradle.kmock.config.MainConfig
import tech.antibytes.gradle.util.isAndroid
import java.util.Locale

internal object KMPSourceSetsConfigurator : SourceSetConfigurator {
    private val precedences = listOf(
        "jvm",
        "js",
    )
    private val target = "commonTest"
    private val indicator = "COMMON SOURCE"

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

    private fun createCleanUpTask(
        project: Project,
        sourceSetName: String,
        kspTask: String,
        moveTask: Copy
    ): Task {
        val cleanUpTask = project.tasks.create(
            "cleanDuplicates${sourceSetName.capitalize(Locale.ROOT)}",
            KMockCleanTask::class.java
        )

        cleanUpTask.target.set(sourceSetName)
        cleanUpTask.indicator.set(indicator)
        cleanUpTask.description = "Removes Contradicting Sources"
        cleanUpTask.group = "Code Generation"

        return cleanUpTask.dependsOn(moveTask)
            .mustRunAfter(moveTask)
            .mustRunAfter(kspTask)
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

    private fun selectReferenceSource(
        project: Project,
        sources: Map<String, String>
    ): Pair<Copy, String> {
        val actualSources = sources.keys

        precedences.forEach { precedence ->
            if (precedence in actualSources) {
                return Pair(
                    SharedSourceCopist.copySharedSource(project, precedence + "Test", target, indicator),
                    precedence
                )
            }
        }

        return Pair(
            SharedSourceCopist.copySharedSource(project, actualSources.first() + "Test", target, indicator),
            actualSources.first()
        )
    }

    private fun useAndroid(project: Project): Pair<Copy, String> {
        val androidDebug = "androidDebugUnitTest"
        val androidDebugKsp = "kspDebugUnitTestKotlinAndroid"

        val androidRelease = "androidReleaseUnitTest"
        val androidReleaseKsp = "kspReleaseUnitTestKotlinAndroid"

        val copyToCommon = SharedSourceCopist
            .copySharedSource(project, androidDebug, target, indicator)
            .dependsOn(androidDebugKsp)
            .mustRunAfter(androidDebugKsp) as Copy

        val cleanUpTaskDebug = createCleanUpTask(
            project,
            androidDebug,
            androidDebugKsp,
            copyToCommon
        )

        val cleanUpTaskRelease = createCleanUpTask(
            project,
            androidRelease,
            androidReleaseKsp,
            copyToCommon
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

    private fun wireSharedSourceTasks(
        project: Project,
        sourceCollector: Map<String, String>
    ) {
        val (copyToCommon, precedence) = if (project.isAndroid()) {
            useAndroid(project)
        } else {
            selectReferenceSource(project, sourceCollector)
        }

        sourceCollector.forEach { (platform, kspTask) ->
            val cleanUpTask = createCleanUpTask(
                project,
                "${platform}Test",
                kspTask,
                copyToCommon
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

    override fun configure(project: Project) {
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
            project.afterEvaluate { wireSharedSourceTasks(project, sourceCollector) }
        }
    }
}
