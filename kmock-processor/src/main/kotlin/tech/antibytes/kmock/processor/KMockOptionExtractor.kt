/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import tech.antibytes.kmock.processor.ProcessorContract.Companion.ALIASES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.CUSTOM_ANNOTATION
import tech.antibytes.kmock.processor.ProcessorContract.Companion.CUSTOM_METHOD_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.DISABLE_FACTORIES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.INTERFACES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMP_FLAG
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSP_DIR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.OVERLOAD_NAME_FEATURE_FLAG
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PRECEDENCE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ROOT_PACKAGE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPIES_ONLY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_ON
import tech.antibytes.kmock.processor.ProcessorContract.Companion.TYPE_PREFIXES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.USELESS_PREFIXES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.USE_BUILD_IN
import tech.antibytes.kmock.processor.ProcessorContract.OptionExtractor
import tech.antibytes.kmock.processor.ProcessorContract.Options

internal object KMockOptionExtractor : OptionExtractor {
    private fun extractMappedValue(
        prefix: String,
        key: String,
        value: String,
        action: (String, String) -> Unit
    ) {
        val mappedKey = key.substringAfter(prefix)
        action(mappedKey, value)
    }

    private fun extractSourceSets(
        precedences: MutableMap<String, Int>
    ): Set<String> = precedences.keys.filterNot { sourceSet -> sourceSet == "commonTest" }.toSet()

    override fun convertOptions(
        kspRawOptions: Map<String, String>
    ): Options {
        var kspDir: String? = null
        var rootPackage: String? = null
        var isKmp: Boolean? = null
        val precedences: MutableMap<String, Int> = mutableMapOf()
        val aliases: MutableMap<String, String> = mutableMapOf()
        val useBuildInProxiesOn: MutableSet<String> = mutableSetOf()
        val spyOn: MutableSet<String> = mutableSetOf()
        var freezeOnDefault = true
        var allowInterfaces = false
        var spiesOnly = false
        var disableFactories = false
        var enableNewOverloadingNames = true
        val uselessPrefixes: MutableSet<String> = mutableSetOf()
        val useTypePrefixFor: MutableMap<String, String> = mutableMapOf()
        val customMethodNames: MutableMap<String, String> = mutableMapOf()
        val customAnnotations: MutableMap<String, String> = mutableMapOf()

        kspRawOptions.forEach { (key, value) ->
            when {
                key == KSP_DIR -> kspDir = value
                key == ROOT_PACKAGE -> rootPackage = value
                key == KMP_FLAG -> isKmp = value.toBoolean()
                key == FREEZE -> freezeOnDefault = value.toBoolean()
                key == INTERFACES -> allowInterfaces = value.toBoolean()
                key == SPIES_ONLY -> spiesOnly = value.toBoolean()
                key == DISABLE_FACTORIES -> disableFactories = value.toBoolean()
                key.startsWith(PRECEDENCE) -> extractMappedValue(PRECEDENCE, key, value) { sourceSet, precedence ->
                    precedences[sourceSet] = precedence.toInt()
                }
                key.startsWith(ALIASES) -> extractMappedValue(ALIASES, key, value) { qualifiedName, alias ->
                    aliases[qualifiedName] = alias
                }
                key == OVERLOAD_NAME_FEATURE_FLAG -> enableNewOverloadingNames = value.toBoolean()
                key.startsWith(TYPE_PREFIXES) -> extractMappedValue(
                    TYPE_PREFIXES,
                    key,
                    value
                ) { qualifiedName, prefix ->
                    useTypePrefixFor[qualifiedName] = prefix
                }
                key.startsWith(CUSTOM_METHOD_NAME) -> extractMappedValue(
                    CUSTOM_METHOD_NAME,
                    key,
                    value
                ) { proxyId, replacement ->
                    customMethodNames[proxyId] = replacement
                }
                key.startsWith(CUSTOM_ANNOTATION) -> extractMappedValue(
                    CUSTOM_ANNOTATION,
                    key,
                    value
                ) { annotation, sourceSet ->
                    customAnnotations[annotation] = sourceSet
                }
                key.startsWith(USELESS_PREFIXES) -> uselessPrefixes.add(value)
                key.startsWith(USE_BUILD_IN) -> useBuildInProxiesOn.add(value)
                key.startsWith(SPY_ON) -> {
                    useBuildInProxiesOn.add(value)
                    spyOn.add(value)
                }
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
            knownSourceSets = extractSourceSets(precedences),
            precedences = precedences,
            aliases = aliases,
            useBuildInProxiesOn = useBuildInProxiesOn,
            spyOn = spyOn,
            enableNewOverloadingNames = enableNewOverloadingNames,
            useTypePrefixFor = useTypePrefixFor,
            customMethodNames = customMethodNames,
            uselessPrefixes = uselessPrefixes,
        )
    }
}
