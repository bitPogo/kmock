/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ALIAS_PREFIX

abstract class KMockExtension(
    project: Project
) : KMockPluginContract.Extension {
    private val ksp: KspExtension = project.extensions.getByType(KspExtension::class.java)
    private val legalAliases = "^[a-zA-Z][a-zA-Z0-9]*$".toRegex()

    private var _rootPackage: String = ""

    private var _aliasNameMapping: Map<String, String> = emptyMap()

    private fun propagateRootPackage(rootPackage: String) {
        ksp.arg("rootPackage", rootPackage)
    }

    override var rootPackage: String
        get() = _rootPackage
        set(value) {
            propagateRootPackage(value)
            _rootPackage = value
        }

    private fun guardMapping(qualifiedName: String, alias: String) {
        when {
            qualifiedName == "rootPackage" -> throw IllegalArgumentException("$qualifiedName is not allowed!")
            qualifiedName == "isKmp" -> throw IllegalArgumentException("$qualifiedName is not allowed!")
            !legalAliases.matches(alias) -> throw IllegalArgumentException("$alias is not a valid alias!")
        }
    }

    private fun propagateMapping(mapping: Map<String, String>) {
        mapping.forEach { (qualifiedName, alias) ->
            guardMapping(qualifiedName, alias)

            ksp.arg("$ALIAS_PREFIX$qualifiedName", alias)
        }
    }

    override var aliasNameMapping: Map<String, String>
        get() = _aliasNameMapping
        set(value) {
            propagateMapping(value)
            _aliasNameMapping = value
        }
}
