/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ALIASES
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.USE_BUILD_IN
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.FREEZE
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.INTERFACES_KMOCK
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.INTERFACES_KSPY
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KMP_FLAG
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KSP_DIR
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.OVERLOAD_NAME_FEATURE_FLAG
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ALLOWED_RECURSIVE_TYPES
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ROOT_PACKAGE
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.SPIES_ONLY
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.SPY_ON
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.TYPE_PREFIXES
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.USELESS_PREFIXES

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

    private var _enableNewOverloadingNames = true
    private var _typePrefixMapping: Map<String, String> = emptyMap()
    private var _uselessPrefixes: Set<String> = setOf(
        "kotlin.collections",
        "kotlin",
    )

    private var _freeze = true

    private var _allowInterfacesOnKmock = false
    private var _allowInterfacesOnKspy = false

    private var _spyOn: Set<String> = emptySet()
    private var _spiesOnly = false

    private fun propagateValue(
        id: String,
        value: String
    ) = ksp.arg(id, value)

    private fun guardMapping(qualifiedName: String, name: String) {
        when {
            qualifiedName in illegalNames -> throw IllegalArgumentException("$qualifiedName is not allowed!")
            !legalAliases.matches(name) -> throw IllegalArgumentException("$name is not applicable!")
        }
    }

    private fun propagateMapping(
        kmockKey: String,
        mapping: Map<String, String>
    ) {
        mapping.forEach { (qualifiedName, alias) ->
            guardMapping(qualifiedName, alias)

            ksp.arg("$kmockKey$qualifiedName", alias)
        }
    }

    override var rootPackage: String
        get() = _rootPackage
        set(value) {
            propagateValue(ROOT_PACKAGE, value)
            _rootPackage = value
        }

    override var aliasNameMapping: Map<String, String>
        get() = _aliasNameMapping
        set(value) {
            propagateMapping(
                kmockKey = ALIASES,
                mapping = value
            )
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
                prefix = ALLOWED_RECURSIVE_TYPES,
                values = value
            )

            _allowedRecursiveTypes = value
        }

    override var useBuildInProxiesOn: Set<String>
        get() = _useBuildInProxiesOn
        set(value) {
            propagateIterable(
                prefix = USE_BUILD_IN,
                values = value
            )

            _useBuildInProxiesOn = value
        }

    override var uselessPrefixes: Set<String>
        get() = _uselessPrefixes
        set(value) {
            propagateIterable(
                prefix = USELESS_PREFIXES,
                values = value
            )

            _uselessPrefixes = value
        }

    override var enableNewOverloadingNames: Boolean
        get() = _enableNewOverloadingNames
        set(value) {
            propagateValue(OVERLOAD_NAME_FEATURE_FLAG, value.toString())
            _enableNewOverloadingNames = value
        }

    override var useTypePrefixFor: Map<String, String>
        get() = _typePrefixMapping
        set(value) {
            propagateMapping(
                kmockKey = TYPE_PREFIXES,
                mapping = value
            )

            _typePrefixMapping = value
        }

    override var freezeOnDefault: Boolean
        get() = _freeze
        set(value) {
            propagateValue(FREEZE, value.toString())
            _freeze = value
        }

    override var allowInterfacesOnKmock: Boolean
        get() = _allowInterfacesOnKmock
        set(value) {
            propagateValue(INTERFACES_KMOCK, value.toString())
            _allowInterfacesOnKmock = value
        }

    override var allowInterfacesOnKspy: Boolean
        get() = _allowInterfacesOnKspy
        set(value) {
            propagateValue(INTERFACES_KSPY, value.toString())
            _allowInterfacesOnKspy = value
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

    override var spiesOnly: Boolean
        get() = _spiesOnly
        set(value) {
            propagateValue(SPIES_ONLY, value.toString())
            _spiesOnly = value
        }
}
