/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import java.util.Locale
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.KMockPluginContract

internal object AndroidSourceBinder : KMockPluginContract.AndroidSourceBinder {
    private fun Project.resolveAndroidExtension(
        action: (CommonExtension<*, *, *, *, *>) -> Unit,
    ) {
        if (plugins.findPlugin("com.android.application") is Plugin<*>) {
            extensions.configure(ApplicationExtension::class.java) {
                action(this)
            }
        } else {
            extensions.configure(LibraryExtension::class.java) {
                action(this)
            }
        }
    }

    private fun NamedDomainObjectContainer<out AndroidSourceSet>.getUnitTest(
        buildType: String,
    ): AndroidSourceSet = getByName("test${buildType.capitalize(Locale.ROOT)}")

    private fun NamedDomainObjectContainer<out AndroidSourceSet>.getAndroidTest(
        buildType: String,
    ): AndroidSourceSet = getByName("androidTest${buildType.capitalize(Locale.ROOT)}")

    private fun String.toKSPUnitTestPath(project: Project): String {
        return "${project.layout.buildDirectory.get().asFile.absolutePath.trimEnd('/')}/generated/ksp/${this}UnitTest"
    }

    private fun String.toKSPInstrumentedTestPath(project: Project): String {
        return "${project.layout.buildDirectory.get().asFile.absolutePath.trimEnd('/')}/generated/ksp/${this}AndroidTest"
    }

    private fun CommonExtension<*, *, *, *, *>.configureKsp(project: Project) {
        defaultBuildTypes.forEach { buildType ->
            val unitTest = sourceSets.getUnitTest(buildType)
            val instrumentedTest = sourceSets.getAndroidTest(buildType)

            val unitTestPath = buildType.toKSPUnitTestPath(project)
            val instrumentedTestPath = buildType.toKSPInstrumentedTestPath(project)

            unitTest.java.srcDir(unitTestPath)
            instrumentedTest.java.srcDir(instrumentedTestPath)
        }
    }

    override fun bind(project: Project) {
        project.resolveAndroidExtension { androidExtension ->
            androidExtension.configureKsp(project)
        }
    }

    private val defaultBuildTypes = listOf("debug", "release")
}
