/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Plugin
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KSP_PLUGIN
import tech.antibytes.gradle.kmock.util.applyIfNotExists
import tech.antibytes.gradle.kmock.util.isJs

public class KMockPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.isJs()) {
            target.applyIfNotExists(KSP_PLUGIN)
        }

        target.extensions.create("kmock", KMockExtension::class.java)
    }
}
