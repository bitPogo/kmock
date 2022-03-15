/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache License, Version 2.0
 */

package tech.antibytes.gradle.kmock.util

import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.reflect.KClass

internal fun Project.applyIfNotExists(vararg pluginNames: String) {
    pluginNames.forEach { pluginName ->
        if (!this.plugins.hasPlugin(pluginName)) {
            this.plugins.apply(pluginName)
        }
    }
}

internal fun Project.applyIfNotExists(vararg plugins: KClass<out Plugin<*>>) {
    plugins.forEach { plugin ->
        if (!this.plugins.hasPlugin(plugin.java)) {
            this.plugins.apply(plugin.java)
        }
    }
}

internal fun Project.isKmp(): Boolean {
    return this.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
}

fun Project.isAndroid(): Boolean {
    return this.plugins.hasPlugin("com.android.library") || this.plugins.hasPlugin("com.android.application")
}

fun Project.isJs(): Boolean {
    return this.plugins.hasPlugin("org.jetbrains.kotlin.js")
}
