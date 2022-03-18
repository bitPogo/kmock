/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import tech.antibytes.kmock.processor.ProcessorContract.Companion.ALIAS_PREFIX
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PRECEDENCE_PREFIX
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

    override fun convertOptions(
        kspRawOptions: Map<String, String>
    ): Options {
        var rootPackage: String? = null
        var isKmp: Boolean? = null
        val precedences: MutableMap<String, Int> = mutableMapOf()
        val aliases: MutableMap<String, String> = mutableMapOf()

        kspRawOptions.forEach { (key, value) ->
            when {
                key == "rootPackage" -> rootPackage = value
                key == "isKmp" -> isKmp = value.toBoolean()
                key.startsWith(PRECEDENCE_PREFIX) -> extractPrecedence(key, value) { sourceSet, precedence ->
                    precedences[sourceSet] = precedence
                }
                key.startsWith(ALIAS_PREFIX) -> extractAliases(key, value) { qualifiedName, alias ->
                    aliases[qualifiedName] = alias
                }
            }
        }

        return Options(
            rootPackage = rootPackage!!,
            isKmp = isKmp!!,
            precedences = precedences,
            aliases = aliases
        )
    }
}
