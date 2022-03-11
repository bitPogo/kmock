/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.KSPLogger
import tech.antibytes.kmock.processor.ProcessorContract.InterfaceSource

internal class SourceFilter(
    precedences: Map<String, String>,
    private val logger: KSPLogger
) : ProcessorContract.SourceFilter {
    private val precedences: Map<String, Int> = precedences
        .filter { (key, _) ->
            key != "isKmp" && key != "rootPackage"
        }
        .mapValues { (_, value) -> value.toInt() }

    override fun filter(
        sources: List<InterfaceSource>,
        filteredBy: List<InterfaceSource>
    ): List<InterfaceSource> {
        val filter = filteredBy.map { source ->
            source.interfaze.qualifiedName!!.asString()
        }

        return sources.filter { source ->
            !filter.contains(source.interfaze.qualifiedName!!.asString())
        }
    }

    private fun resolvePrecedence(indicator: String): Int {
        return precedences.getOrElse(indicator) {
            logger.error("No SharedSource defined for $indicator.")
            -1
        }
    }

    override fun filterSharedSources(
        sources: List<InterfaceSource>
    ): List<InterfaceSource> {
        val filtered: MutableList<InterfaceSource> = mutableListOf()
        val filteredNamed: MutableList<String> = mutableListOf()

        sources.forEach { source ->
            val qualifiedName = source.interfaze.qualifiedName!!.asString()
            val currentFieldIdx = filteredNamed.indexOf(qualifiedName)

            if (currentFieldIdx == -1) {
                filtered.add(source)
                filteredNamed.add(qualifiedName)
            } else {
                val currentSourceMarker = resolvePrecedence(source.marker)
                val addedSourceMarker = resolvePrecedence(filtered[currentFieldIdx].marker)

                if (currentSourceMarker > addedSourceMarker) {
                    filtered[currentFieldIdx] = source
                }
            }
        }

        return filtered
    }
}
