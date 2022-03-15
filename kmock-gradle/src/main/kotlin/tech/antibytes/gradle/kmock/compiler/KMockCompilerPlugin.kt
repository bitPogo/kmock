/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import tech.antibytes.gradle.kmock.config.MainConfig
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import tech.antibytes.gradle.kmock.KMockExtension
import tech.antibytes.gradle.kmock.compiler.KMockCompilerPluginContract.Companion.ENABLE_COMPILER_PLUGIN_FIELD

internal class KMockCompilerPlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension: KMockExtension = project.extensions.getByType(KMockExtension::class.java)

        return project.provider {
            listOf(
                SubpluginOption(
                    key = ENABLE_COMPILER_PLUGIN_FIELD,
                    value = extension.useExperimentalCompilerPlugin.get().toString()
                )
            )
        }
    }

    override fun getCompilerPluginId(): String = MainConfig.id

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = MainConfig.group,
            artifactId = MainConfig.id,
            version = MainConfig.version
        )
    }

    override fun getPluginArtifactForNative(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = MainConfig.group,
            artifactId = MainConfig.id,
            version = MainConfig.version
        )
    }

    override fun isApplicable(
        kotlinCompilation: KotlinCompilation<*>
    ): Boolean = kotlinCompilation.compileKotlinTaskName.contains("TestKotlin")
}
