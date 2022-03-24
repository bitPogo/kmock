/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ALIAS_PREFIX
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.BUILD_IN_PREFIX
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KMP_FLAG
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KSP_DIR
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.RECURSIVE_PREFIX
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ROOT_PACKAGE
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.SPY_ON
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.USELESS_PREFIXES_PREFIX

abstract class KMockExtension(
    project: Project
) : KMockPluginContract.Extension {
    private val ksp: KspExtension = project.extensions.getByType(KspExtension::class.java)
    private val illegalNames = setOf(
        ROOT_PACKAGE,
        KMP_FLAG,
        KSP_DIR
    )
    private val legalAliases = "^[a-zA-Z][a-zA-Z0-9]*$".toRegex()

    private var _rootPackage: String = ""
    private var _aliasNameMapping: Map<String, String> = emptyMap()
    private var _allowedRecursiveTypes: Set<String> = emptySet()
    private var _useBuildInProxiesOn: Set<String> = emptySet()
    private var _uselessPrefixes: Set<String> = setOf(
        "kotlin.collections",
        "kotlin",
    )
    private var _spyOn: Set<String> = emptySet()

    private fun propagateRootPackage(rootPackage: String) {
        ksp.arg(ROOT_PACKAGE, rootPackage)
    }

    override var rootPackage: String
        get() = _rootPackage
        set(value) {
            propagateRootPackage(value)
            _rootPackage = value
        }

    private fun guardMapping(qualifiedName: String, alias: String) {
        when {
            qualifiedName in illegalNames -> throw IllegalArgumentException("$qualifiedName is not allowed!")
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

    private fun propagateIterable(prefix: String, values: Iterable<String>) {
        values.forEachIndexed { idx, type ->
            ksp.arg("$prefix$idx", type)
        }
    }

    override var allowedRecursiveTypes: Set<String>
        get() = _allowedRecursiveTypes
        set(value) {
            propagateIterable(
                prefix = RECURSIVE_PREFIX,
                values = value
            )

            _allowedRecursiveTypes = value
        }

    override var useBuildInProxiesOn: Set<String>
        get() = _useBuildInProxiesOn
        set(value) {
            propagateIterable(
                prefix = BUILD_IN_PREFIX,
                values = value
            )

            _useBuildInProxiesOn = value
        }

    override var uselessPrefixes: Set<String>
        get() = _uselessPrefixes
        set(value) {
            propagateIterable(
                prefix = USELESS_PREFIXES_PREFIX,
                values = value
            )

            _uselessPrefixes = value
        }

    override var spyOn: Set<String>
        get() = _spyOn
        set(value) {
            propagateIterable(
                prefix = SPY_ON,
                values = value
            )

            _spyOn = value
        }
}
