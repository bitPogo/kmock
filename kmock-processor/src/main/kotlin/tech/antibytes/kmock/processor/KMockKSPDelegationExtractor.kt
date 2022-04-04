/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import tech.antibytes.kmock.processor.ProcessorContract.Companion.ALIASES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.USE_BUILD_IN
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.INTERFACES_KMOCK
import tech.antibytes.kmock.processor.ProcessorContract.Companion.INTERFACES_KSPY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMP_FLAG
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSP_DIR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PRECEDENCE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ALLOWED_RECURSIVE_TYPES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ROOT_PACKAGE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPIES_ONLY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_ON
import tech.antibytes.kmock.processor.ProcessorContract.Companion.USELESS_PREFIXES
import tech.antibytes.kmock.processor.ProcessorContract.KSPDelegationExtractor
import tech.antibytes.kmock.processor.ProcessorContract.Options

internal object KMockKSPDelegationExtractor : KSPDelegationExtractor {
    private fun extractPrecedence(
        key: String,
        value: String,
        action: (String, Int) -> Unit
    ) {
        val sourceSet = key.substringAfter(PRECEDENCE)
        action(sourceSet, value.toInt())
    }

    private fun extractAliases(
        key: String,
        value: String,
        action: (String, String) -> Unit
    ) {
        val qualifiedName = key.substringAfter(ALIASES)
        action(qualifiedName, value)
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
        val allowedRecursiveTypes: MutableSet<String> = mutableSetOf()
        val useBuildInProxiesOn: MutableSet<String> = mutableSetOf()
        val uselessPrefixes: MutableSet<String> = mutableSetOf()
        val spyOn: MutableSet<String> = mutableSetOf()
        var freezeOnDefault = true
        var allowInterfacesOnKmock = false
        var allowInterfacesOnKspy = false
        var spiesOnly = false

        kspRawOptions.forEach { (key, value) ->
            when {
                key == KSP_DIR -> kspDir = value
                key == ROOT_PACKAGE -> rootPackage = value
                key == KMP_FLAG -> isKmp = value.toBoolean()
                key == FREEZE -> freezeOnDefault = value.toBoolean()
                key == INTERFACES_KMOCK -> allowInterfacesOnKmock = value.toBoolean()
                key == INTERFACES_KSPY -> allowInterfacesOnKspy = value.toBoolean()
                key == SPIES_ONLY -> spiesOnly = value.toBoolean()
                key.startsWith(PRECEDENCE) -> extractPrecedence(key, value) { sourceSet, precedence ->
                    precedences[sourceSet] = precedence
                }
                key.startsWith(ALIASES) -> extractAliases(key, value) { qualifiedName, alias ->
                    aliases[qualifiedName] = alias
                }
                key.startsWith(ALLOWED_RECURSIVE_TYPES) -> allowedRecursiveTypes.add(value)
                key.startsWith(USE_BUILD_IN) -> useBuildInProxiesOn.add(value)
                key.startsWith(USELESS_PREFIXES) -> uselessPrefixes.add(value)
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
            allowInterfacesOnKmock = allowInterfacesOnKmock,
            allowInterfacesOnKspy = allowInterfacesOnKspy,
            spiesOnly = spiesOnly,
            knownSourceSets = extractSourceSets(precedences),
            precedences = precedences,
            aliases = aliases,
            allowedRecursiveTypes = allowedRecursiveTypes,
            useBuildInProxiesOn = useBuildInProxiesOn,
            spyOn = spyOn,
            uselessPrefixes = uselessPrefixes,
        )
    }
}
