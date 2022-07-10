/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.KMockPluginContract.CacheController
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KMP_FLAG
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KSP_DIR
import tech.antibytes.gradle.kmock.KMockPluginContract.SourceSetConfigurator
import tech.antibytes.gradle.kmock.util.isKmp

internal class KSPBridge private constructor(
    private val project: Project,
    private val cacheController: CacheController,
    private val singleSourceSetConfigurator: SourceSetConfigurator,
    private val kmpSourceSetConfigurator: SourceSetConfigurator,
) : KMockPluginContract.KSPBridge {
    private var locked = false
    private val ksp: KspExtension by lazy {
        project.extensions.getByType(KspExtension::class.java)
    }

    private fun configureSources() {
        val isKMP = project.isKmp()

        if (!isKMP) {
            singleSourceSetConfigurator.configure(project)
        } else {
            kmpSourceSetConfigurator.configure(project)
        }

        cacheController.configure(project)

        ksp.arg(KMP_FLAG, isKMP.toString())
        ksp.arg(KSP_DIR, "${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp")
    }

    private fun configureOnce() {
        if (!locked) {
            configureSources()
            locked = true
        }
    }

    override fun propagateValue(rootKey: String, value: String) {
        configureOnce()
        ksp.arg(rootKey, value)
    }

    override fun propagateMapping(
        rootKey: String,
        mapping: Map<String, String>,
        onPropagation: (String, String) -> Unit,
    ) {
        configureOnce()
        mapping.forEach { (key, value) ->
            onPropagation(key, value)

            ksp.arg("$rootKey$key", value)
        }
    }

    override fun <T> propagateIterable(
        rootKey: String,
        values: Iterable<T>,
        action: (T) -> String,
    ) {
        configureOnce()
        values.forEachIndexed { idx, type ->
            ksp.arg("$rootKey$idx", action(type))
        }
    }

    companion object : KMockPluginContract.KSPBridgeFactory {
        override fun getInstance(
            project: Project,
            cacheController: CacheController,
            singleSourceSetConfigurator: SourceSetConfigurator,
            kmpSourceSetConfigurator: SourceSetConfigurator,
        ): KMockPluginContract.KSPBridge {
            return KSPBridge(
                project = project,
                cacheController = cacheController,
                singleSourceSetConfigurator = singleSourceSetConfigurator,
                kmpSourceSetConfigurator = kmpSourceSetConfigurator,
            )
        }
    }
}
