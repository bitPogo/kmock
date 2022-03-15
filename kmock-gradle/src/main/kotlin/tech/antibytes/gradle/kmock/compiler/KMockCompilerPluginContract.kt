/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal interface KMockCompilerPluginContract {
    companion object {
        const val ENABLE_COMPILER_PLUGIN_FIELD = "enableOpenClasses"
        val ARG_ENABLE_COMPILER_PLUGIN_FIELD = CompilerConfigurationKey<Boolean>(ENABLE_COMPILER_PLUGIN_FIELD)
    }
}
