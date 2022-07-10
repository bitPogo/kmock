/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Project
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ALIASES
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ALTERNATIVE_PROXY_ACCESS
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.CUSTOM_ANNOTATION
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.CUSTOM_METHOD_NAME
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.DISABLE_FACTORIES
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.FINE_GRAINED_PROXY_NAMES
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.FREEZE
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.INTERFACES
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KMP_FLAG
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.KSP_DIR
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.PREVENT_ALIAS_RESOLVING
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.ROOT_PACKAGE
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.SPIES_ONLY
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.SPY_ALL
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.SPY_ON
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.TYPE_PREFIXES
import tech.antibytes.gradle.kmock.KMockPluginContract.Companion.USE_BUILD_IN
import tech.antibytes.gradle.kmock.source.KmpSourceSetsConfigurator
import tech.antibytes.gradle.kmock.source.SingleSourceSetConfigurator

public abstract class KMockExtension(
    project: Project,
) : KMockPluginContract.Extension {
    private val kspBridge = KSPBridge.getInstance(
        project,
        CacheController,
        SingleSourceSetConfigurator,
        KmpSourceSetsConfigurator,
    )
    private val illegalNames = setOf(
        ROOT_PACKAGE,
        KMP_FLAG,
        KSP_DIR,
    )
    private val legalAliases = "^[a-zA-Z][a-zA-Z0-9]*$".toRegex()

    private var _rootPackage: String = ""

    private var _aliasNameMapping: Map<String, String> = emptyMap()
    private var _useBuildInProxiesOn: Set<String> = emptySet()

    private var _typePrefixMapping: Map<String, String> = emptyMap()
    private var _customMethodNames: Map<String, String> = emptyMap()

    private var _freeze = true

    private var _allowInterfaces = false

    private var _spyOn: Set<String> = emptySet()
    private var _spyAll = false
    private var _spiesOnly = false

    private var _disableFactories = false

    private var _customSharedAnnotations: Map<String, String> = emptyMap()

    private var _alternativeAccess = false

    private var _enableFineGrainedNames = false

    private var _preventResolvingOfAliases: Set<String> = emptySet()

    private fun propagateValue(
        id: String,
        value: String,
    ) = kspBridge.propagateValue(id, value)

    private fun guardMapping(qualifiedName: String, name: String) {
        when {
            qualifiedName in illegalNames -> throw IllegalArgumentException("$qualifiedName is not allowed!")
            !legalAliases.matches(name) -> throw IllegalArgumentException("$name is not applicable!")
        }
    }

    private fun propagateMapping(
        kmockKey: String,
        mapping: Map<String, String>,
    ) {
        kspBridge.propagateMapping(
            kmockKey,
            mapping,
            ::guardMapping,
        )
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
                mapping = value,
            )
            _aliasNameMapping = value
        }

    private fun <T> propagateIterable(
        prefix: String,
        values: Iterable<T>,
        action: (T) -> String = { it.toString() },
    ) {
        kspBridge.propagateIterable(
            prefix,
            values,
            action,
        )
    }

    override var useBuildInProxiesOn: Set<String>
        get() = _useBuildInProxiesOn
        set(value) {
            propagateIterable(
                prefix = USE_BUILD_IN,
                values = value,
            )

            _useBuildInProxiesOn = value
        }

    override var useTypePrefixFor: Map<String, String>
        get() = _typePrefixMapping
        set(value) {
            propagateMapping(
                kmockKey = TYPE_PREFIXES,
                mapping = value,
            )

            _typePrefixMapping = value
        }

    @KMockGradleExperimental
    override var customMethodNames: Map<String, String>
        get() = _customMethodNames
        set(value) {
            propagateMapping(
                kmockKey = CUSTOM_METHOD_NAME,
                mapping = value,
            )

            _customMethodNames = value
        }

    override var freezeOnDefault: Boolean
        get() = _freeze
        set(value) {
            propagateValue(FREEZE, value.toString())
            _freeze = value
        }

    override var allowInterfaces: Boolean
        get() = _allowInterfaces
        set(value) {
            propagateValue(INTERFACES, value.toString())
            _allowInterfaces = value
        }

    override var spyOn: Set<String>
        get() = _spyOn
        set(value) {
            propagateIterable(
                prefix = SPY_ON,
                values = value,
            )

            _spyOn = value
        }

    override var spiesOnly: Boolean
        get() = _spiesOnly
        set(value) {
            propagateValue(SPIES_ONLY, value.toString())
            _spiesOnly = value
        }

    override var spyAll: Boolean
        get() = _spyAll
        set(value) {
            propagateValue(SPY_ALL, value.toString())
            _spyAll = value
        }

    override var disableFactories: Boolean
        get() = _disableFactories
        set(value) {
            propagateValue(DISABLE_FACTORIES, value.toString())
            _disableFactories = value
        }

    override var customAnnotationsForMeta: Map<String, String>
        get() = _customSharedAnnotations
        set(value) {
            propagateMapping(
                CUSTOM_ANNOTATION,
                value,
            )

            _customSharedAnnotations = value
        }

    @KMockGradleExperimental
    override var allowExperimentalProxyAccess: Boolean
        get() = _alternativeAccess
        set(value) {
            propagateValue(ALTERNATIVE_PROXY_ACCESS, value.toString())
            _alternativeAccess = value
        }

    @KMockGradleExperimental
    override var enableFineGrainedNames: Boolean
        get() = _enableFineGrainedNames
        set(value) {
            propagateValue(FINE_GRAINED_PROXY_NAMES, value.toString())
            _enableFineGrainedNames = value
        }

    @KMockGradleExperimental
    override var preventResolvingOfAliases: Set<String>
        get() = _preventResolvingOfAliases
        set(value) {
            propagateIterable(
                prefix = PREVENT_ALIAS_RESOLVING,
                values = value,
            )

            _preventResolvingOfAliases = value
        }
}
