/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

internal class KMockRegistration : ComponentRegistrar {
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        val isEnabled: Boolean = configuration.get(
            KMockCompilerPluginContract.ARG_ENABLE_COMPILER_PLUGIN_FIELD,
            false
        )

        if (isEnabled) {
            TODO("Not yet implemented")
        }
    }
}
