/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.util

import org.gradle.api.Project

internal fun Project.applyIfNotExists(vararg pluginNames: String) {
    pluginNames.forEach { pluginName ->
        if (!this.plugins.hasPlugin(pluginName)) {
            this.plugins.apply(pluginName)
        }
    }
}

internal fun Project.isKmp(): Boolean {
    return this.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
}

internal fun Project.isAndroid(): Boolean {
    return this.plugins.hasPlugin("com.android.library") || this.plugins.hasPlugin("com.android.application")
}

internal fun Project.isJs(): Boolean {
    return this.plugins.hasPlugin("org.jetbrains.kotlin.js")
}
