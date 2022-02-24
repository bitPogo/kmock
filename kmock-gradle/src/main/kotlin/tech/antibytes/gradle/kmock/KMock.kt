/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Plugin
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.source.KMPSourceSetsConfigurator
import tech.antibytes.gradle.kmock.source.SingleSourceSetConfigurator
import tech.antibytes.gradle.util.applyIfNotExists
import tech.antibytes.gradle.util.isKmp

class KMock : Plugin<Project> {
    private fun addKSP(project: Project) {
        project.applyIfNotExists("com.google.devtools.ksp")
    }

    override fun apply(target: Project) {
        addKSP(target)

        target.extensions.create("kmock", KMockExtension::class.java)

        if (!target.isKmp()) {
            SingleSourceSetConfigurator.configure(target)
        } else {
            KMPSourceSetsConfigurator.configure(target)
        }
    }
}
