/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.KSPLogger
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class SourceFilter(
    private val precedences: Map<String, Int>,
    private val logger: KSPLogger
) : ProcessorContract.SourceFilter {
    override fun filter(
        templateSources: List<TemplateSource>,
        filteredBy: List<TemplateSource>
    ): List<TemplateSource> {
        val filter = filteredBy.map { source ->
            source.template.qualifiedName!!.asString()
        }

        return templateSources.filter { source ->
            !filter.contains(source.template.qualifiedName!!.asString())
        }
    }

    private fun resolvePrecedence(indicator: String): Int {
        return precedences.getOrElse(indicator) {
            logger.error("No SharedSource defined for $indicator.")
            -1
        }
    }

    override fun filterSharedSources(
        templateSources: List<TemplateSource>
    ): List<TemplateSource> {
        val filtered: MutableList<TemplateSource> = mutableListOf()
        val filteredNamed: MutableList<String> = mutableListOf()

        templateSources.forEach { source ->
            val qualifiedName = source.template.qualifiedName!!.asString()
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
