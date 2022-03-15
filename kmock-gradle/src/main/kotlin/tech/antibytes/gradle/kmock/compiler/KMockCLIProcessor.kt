/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import tech.antibytes.gradle.kmock.compiler.KMockCompilerPluginContract.Companion.ARG_ENABLE_COMPILER_PLUGIN_FIELD
import tech.antibytes.gradle.kmock.compiler.KMockCompilerPluginContract.Companion.ENABLE_COMPILER_PLUGIN_FIELD
import tech.antibytes.gradle.kmock.config.MainConfig
import java.util.Locale

class KMockCLIProcessor : CommandLineProcessor {
    override val pluginId: String = MainConfig.id
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = ENABLE_COMPILER_PLUGIN_FIELD,
            description = "Enables/Disables classes to opened for testing",
            valueDescription = "boolean",
            required = true,
            allowMultipleOccurrences = false
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        return when (option.optionName) {
            ENABLE_COMPILER_PLUGIN_FIELD -> configuration.put(
                ARG_ENABLE_COMPILER_PLUGIN_FIELD,
                value.toLowerCase(Locale.ROOT) == "true"
            )
            else -> throw IllegalArgumentException("Unknown config option ${option.optionName}")
        }
    }
}
