/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Plugin
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.util.applyIfNotExists

public class KMockPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.applyIfNotExists("com.google.devtools.ksp")

        target.extensions.create("kmock", KMockExtension::class.java)
    }
}
