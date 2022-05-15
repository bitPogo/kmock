/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Source

internal class SourceFilter(
    private val precedences: Map<String, Int>,
    private val logger: KSPLogger
) : ProcessorContract.SourceFilter {
    override fun <T : Source> filter(
        templateSources: List<T>,
        filteredBy: List<T>
    ): List<T> {
        val filter = filteredBy.map { source ->
            "${source.packageName}.${source.templateName}"
        }

        return templateSources.filter { source ->
            !filter.contains("${source.packageName}.${source.templateName}")
        }
    }

    private fun resolvePrecedence(indicator: String): Int {
        return precedences.getOrElse(indicator) {
            logger.error("No SharedSource defined for $indicator.")
            -1
        }
    }

    override fun <T : Source> filterByPrecedence(
        templateSources: List<T>
    ): List<T> {
        val filtered: MutableList<T> = mutableListOf()
        val filteredNamed: MutableList<String> = mutableListOf()

        templateSources.forEach { source ->
            val qualifiedName = "${source.packageName}.${source.templateName}"
            val currentFieldIdx = filteredNamed.indexOf(qualifiedName)

            if (currentFieldIdx == -1) {
                filtered.add(source)
                filteredNamed.add(qualifiedName)
            } else {
                val currentSourceMarker = resolvePrecedence(source.indicator)
                val addedSourceMarker = resolvePrecedence(filtered[currentFieldIdx].indicator)

                if (currentSourceMarker > addedSourceMarker) {
                    filtered[currentFieldIdx] = source
                }
            }
        }

        return filtered
    }
}
