/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import tech.antibytes.kmock.processor.ProcessorContract.Companion.ALIAS_PREFIX
import tech.antibytes.kmock.processor.ProcessorContract.Companion.BUILD_IN_PREFIX
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMP_FLAG
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSP_DIR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PRECEDENCE_PREFIX
import tech.antibytes.kmock.processor.ProcessorContract.Companion.RECURSIVE_PREFIX
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ROOT_PACKAGE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.USELESS_PREFIXES_PREFIX
import tech.antibytes.kmock.processor.ProcessorContract.KSPDelegationExtractor
import tech.antibytes.kmock.processor.ProcessorContract.Options

internal object KMockKSPDelegationExtractor : KSPDelegationExtractor {
    private fun extractPrecedence(
        key: String,
        value: String,
        action: (String, Int) -> Unit
    ) {
        val sourceSet = key.substringAfter(PRECEDENCE_PREFIX)
        action(sourceSet, value.toInt())
    }

    private fun extractAliases(
        key: String,
        value: String,
        action: (String, String) -> Unit
    ) {
        val qualifiedName = key.substringAfter(ALIAS_PREFIX)
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

        kspRawOptions.forEach { (key, value) ->
            when {
                key == KSP_DIR -> kspDir = value
                key == ROOT_PACKAGE -> rootPackage = value
                key == KMP_FLAG -> isKmp = value.toBoolean()
                key.startsWith(PRECEDENCE_PREFIX) -> extractPrecedence(key, value) { sourceSet, precedence ->
                    precedences[sourceSet] = precedence
                }
                key.startsWith(ALIAS_PREFIX) -> extractAliases(key, value) { qualifiedName, alias ->
                    aliases[qualifiedName] = alias
                }
                key.startsWith(RECURSIVE_PREFIX) -> allowedRecursiveTypes.add(value)
                key.startsWith(BUILD_IN_PREFIX) -> useBuildInProxiesOn.add(value)
                key.startsWith(USELESS_PREFIXES_PREFIX) -> uselessPrefixes.add(value)
            }
        }

        return Options(
            kspDir = kspDir!!,
            rootPackage = rootPackage!!,
            isKmp = isKmp!!,
            knownSourceSets = extractSourceSets(precedences),
            precedences = precedences,
            aliases = aliases,
            allowedRecursiveTypes = allowedRecursiveTypes,
            useBuildInProxiesOn = useBuildInProxiesOn,
            uselessPrefixes = uselessPrefixes
        )
    }
}
