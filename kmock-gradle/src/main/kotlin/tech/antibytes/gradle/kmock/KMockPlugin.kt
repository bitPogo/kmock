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
import tech.antibytes.gradle.kmock.util.applyIfNotExists
import tech.antibytes.gradle.kmock.util.isKmp

class KMockPlugin : Plugin<Project> {
    private fun configureSources(
        project: Project
    ) {
        val isKMP = project.isKmp()
        project.extensions.getByType(KspExtension::class.java).arg("isKmp", isKMP.toString())

        if (!isKMP) {
            SingleSourceSetConfigurator.configure(project)
        } else {
            KmpSourceSetsConfigurator.configure(project)
        }
    }

    override fun apply(target: Project) {
        target.applyIfNotExists("com.google.devtools.ksp")

        target.extensions.create("kmock", KMockExtension::class.java)

        configureSources(project = target)
    }
}
