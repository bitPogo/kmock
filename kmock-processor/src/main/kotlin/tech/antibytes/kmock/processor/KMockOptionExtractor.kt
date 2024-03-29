/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import java.util.SortedSet
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ALIASES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ALTERNATIVE_PROXY_ACCESS
import tech.antibytes.kmock.processor.ProcessorContract.Companion.CUSTOM_ANNOTATION
import tech.antibytes.kmock.processor.ProcessorContract.Companion.CUSTOM_METHOD_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.DEPENDENCIES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.DISABLE_FACTORIES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FINE_GRAINED_PROXY_NAMES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE_OPTION
import tech.antibytes.kmock.processor.ProcessorContract.Companion.INTERFACES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMP_FLAG
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSP_DIR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PREVENT_ALIAS_RESOLVING
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ROOT_PACKAGE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPIES_ONLY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_ALL
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_ON
import tech.antibytes.kmock.processor.ProcessorContract.Companion.TYPE_PREFIXES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.USE_BUILD_IN
import tech.antibytes.kmock.processor.ProcessorContract.OptionExtractor
import tech.antibytes.kmock.processor.ProcessorContract.Options

internal object KMockOptionExtractor : OptionExtractor {
    private fun extractDependencies(
        key: String,
        value: String,
        dependencies: MutableMap<String, MutableSet<String>>,
    ) {
        val mappedKey = key.substringAfter(DEPENDENCIES).substringBeforeLast('#')
        val ancestors = dependencies.getOrElse(mappedKey) { mutableSetOf() }

        ancestors.add(value)

        dependencies[mappedKey] = ancestors
    }

    private fun extractMappedValue(
        prefix: String,
        key: String,
        value: String,
        action: (String, String) -> Unit,
    ) {
        val mappedKey = key.substringAfter(prefix)
        action(mappedKey, value)
    }

    private fun extractSourceSets(
        dependencies: MutableMap<String, MutableSet<String>>,
    ): Set<String> = dependencies.keys.filterNot { sourceSet -> sourceSet == "commonTest" }.toSet()

    private fun MutableMap<String, MutableSet<String>>.sealDependencies(): Map<String, SortedSet<String>> {
        return this.map { (sourceSet, ancestors) ->
            sourceSet to ancestors.toSortedSet()
        }.toMap()
    }

    override fun convertOptions(
        kspRawOptions: Map<String, String>,
    ): Options {
        var kspDir: String? = null
        var rootPackage: String? = null
        var isKmp: Boolean? = null
        val dependencies: MutableMap<String, MutableSet<String>> = mutableMapOf()
        val aliases: MutableMap<String, String> = mutableMapOf()
        val useBuildInProxiesOn: MutableSet<String> = mutableSetOf()
        val spyOn: MutableSet<String> = mutableSetOf()
        var freezeOnDefault = false
        var allowInterfaces = false
        var spiesOnly = false
        var spyAll = false
        var disableFactories = false
        var allowExperimentalProxyAccess = false
        var enableFineGrainedNames = false
        val useTypePrefixFor: MutableMap<String, String> = mutableMapOf()
        val customMethodNames: MutableMap<String, String> = mutableMapOf()
        val customAnnotations: MutableMap<String, String> = mutableMapOf()
        val preventResolvingOfAliases: MutableSet<String> = mutableSetOf()

        kspRawOptions.forEach { (key, value) ->
            when {
                key == KSP_DIR -> kspDir = value
                key == ROOT_PACKAGE -> rootPackage = value
                key == KMP_FLAG -> isKmp = value.toBoolean()
                key == FREEZE_OPTION -> freezeOnDefault = value.toBoolean()
                key == INTERFACES -> allowInterfaces = value.toBoolean()
                key == SPIES_ONLY -> spiesOnly = value.toBoolean()
                key == SPY_ALL -> spyAll = value.toBoolean()
                key == DISABLE_FACTORIES -> disableFactories = value.toBoolean()
                key == ALTERNATIVE_PROXY_ACCESS -> allowExperimentalProxyAccess = value.toBoolean()
                key == FINE_GRAINED_PROXY_NAMES -> enableFineGrainedNames = value.toBoolean()
                key.startsWith(DEPENDENCIES) -> extractDependencies(key, value, dependencies)
                key.startsWith(ALIASES) -> extractMappedValue(ALIASES, key, value) { qualifiedName, alias ->
                    aliases[qualifiedName] = alias
                }
                key.startsWith(TYPE_PREFIXES) -> extractMappedValue(
                    TYPE_PREFIXES,
                    key,
                    value,
                ) { qualifiedName, prefix ->
                    useTypePrefixFor[qualifiedName] = prefix
                }
                key.startsWith(CUSTOM_METHOD_NAME) -> extractMappedValue(
                    CUSTOM_METHOD_NAME,
                    key,
                    value,
                ) { proxyId, replacement ->
                    customMethodNames[proxyId] = replacement
                }
                key.startsWith(CUSTOM_ANNOTATION) -> extractMappedValue(
                    CUSTOM_ANNOTATION,
                    key,
                    value,
                ) { annotation, sourceSet ->
                    customAnnotations[annotation] = sourceSet
                }
                key.startsWith(USE_BUILD_IN) -> useBuildInProxiesOn.add(value)
                key.startsWith(SPY_ON) -> {
                    useBuildInProxiesOn.add(value)
                    spyOn.add(value)
                }
                key.startsWith(PREVENT_ALIAS_RESOLVING) -> preventResolvingOfAliases.add(value)
            }
        }

        return Options(
            kspDir = kspDir!!,
            rootPackage = rootPackage!!,
            isKmp = isKmp!!,
            freezeOnDefault = freezeOnDefault,
            customAnnotations = customAnnotations,
            allowInterfaces = allowInterfaces,
            spiesOnly = spiesOnly,
            disableFactories = disableFactories,
            knownSharedSourceSets = extractSourceSets(dependencies),
            dependencies = dependencies.sealDependencies(),
            aliases = aliases,
            useBuildInProxiesOn = useBuildInProxiesOn,
            spyOn = spyOn,
            spyAll = spyAll,
            useTypePrefixFor = useTypePrefixFor,
            customMethodNames = customMethodNames,
            allowExperimentalProxyAccess = allowExperimentalProxyAccess,
            enableFineGrainedNames = enableFineGrainedNames,
            preventResolvingOfAliases = preventResolvingOfAliases.toSortedSet(),
        )
    }
}
