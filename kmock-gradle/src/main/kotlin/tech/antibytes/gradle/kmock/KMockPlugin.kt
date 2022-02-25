/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.source.KmpSourceSetsConfigurator
import tech.antibytes.gradle.kmock.source.SingleSourceSetConfigurator
import tech.antibytes.gradle.util.applyIfNotExists
import tech.antibytes.gradle.util.isKmp

class KMockPlugin : Plugin<Project> {
    private fun configureKmp(project: Project, extension: KMockPluginContract.Extension) {
        KmpSourceSetsConfigurator.configure(project)
        project.extensions.getByType(KspExtension::class.java).arg("isKmp", true.toString())

        project.afterEvaluate {
            val kspCommon = project.mkdir("${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp/common/commonTest/kotlin")

            FactoryGenerator.generate(
                kspCommon,
                extension.rootPackage
            )
        }
    }

    override fun apply(target: Project) {
        target.applyIfNotExists("com.google.devtools.ksp")

        val extension = target.extensions.create("kmock", KMockExtension::class.java)

        if (!target.isKmp()) {
            SingleSourceSetConfigurator.configure(target)
        } else {
            configureKmp(target, extension)
        }
    }
}
