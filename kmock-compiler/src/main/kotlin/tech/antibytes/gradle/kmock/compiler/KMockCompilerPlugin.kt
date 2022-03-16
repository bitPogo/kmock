/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import tech.antibytes.gradle.kmock.compiler.KMockCompilerPluginContract.Companion.ENABLE_COMPILER_PLUGIN_FIELD
import tech.antibytes.gradle.kmock.compiler.config.MainConfig

class KMockCompilerPlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.create("kmockCompilation", KMockCompilerExtension::class.java)
    }

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension: KMockCompilerExtension = project.extensions.getByType(KMockCompilerExtension::class.java)

        return project.provider {
            listOf(
                SubpluginOption(
                    key = ENABLE_COMPILER_PLUGIN_FIELD,
                    value = extension.useExperimentalCompilerPlugin.get().toString()
                )
            )
        }
    }

    override fun getCompilerPluginId(): String = MainConfig.pluginId

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = MainConfig.group,
            artifactId = MainConfig.artifactId,
            version = MainConfig.version
        )
    }

    override fun getPluginArtifactForNative(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = MainConfig.group,
            artifactId = MainConfig.artifactId,
            version = MainConfig.version
        )
    }

    override fun isApplicable(
        kotlinCompilation: KotlinCompilation<*>
    ): Boolean = kotlinCompilation.compileKotlinTaskName.contains("TestKotlin")
}
