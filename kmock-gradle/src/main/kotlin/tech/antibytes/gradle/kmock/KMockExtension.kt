/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project

abstract class KMockExtension(
    project: Project
) : KMockPluginContract.Extension {
    private val ksp: KspExtension = project.extensions.getByType(KspExtension::class.java)

    private var _rootPackage: String = ""

    init {
        useExperimentalCompilerPlugin.convention(false)
    }

    private fun propagateRootPackage(rootPackage: String) {
        ksp.arg("rootPackage", rootPackage)
    }

    override var rootPackage: String
        get() = _rootPackage
        set(value) {
            propagateRootPackage(value)
            _rootPackage = value
        }
}
