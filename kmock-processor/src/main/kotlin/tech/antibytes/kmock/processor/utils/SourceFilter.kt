/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Source
import java.util.SortedSet

internal class SourceFilter(
    private val dependencies: Map<String, SortedSet<String>>,
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

    private fun String.guardedSource(action: () -> Unit) {
        if (this !in dependencies) {
            logger.error("No SharedSource defined for $this.")
        } else {
            action()
        }
    }

    private fun <T : Source> updatesCandidates(
        newSource: T,
        overrides: List<Int>,
        indicators: List<String>,
        filtered: List<T>,
    ): Pair<List<String>, List<T>> {
        val updatedIndicators = indicators.toMutableList()
        val updatedSources = filtered.toMutableList()
        var removed = 0

        overrides.forEach { toRemove ->
            updatedIndicators.removeAt(toRemove - removed)
            updatedSources.removeAt(toRemove - removed)
            removed += 1
        }

        updatedIndicators.add(newSource.indicator)
        updatedSources.add(newSource)

        return Pair(updatedIndicators, updatedSources)
    }

    private fun <T : Source> filterCandidates(
        currentSource: T,
        indicators: List<String>,
        filtered: List<T>,
    ): Pair<List<String>, List<T>> {
        val overrideIndices: MutableList<Int> = mutableListOf()
        val parents = dependencies[currentSource.indicator]!!
        var add = true

        indicators.forEachIndexed { idx, sourceSet ->
            when {
                currentSource.indicator in dependencies[sourceSet]!! -> {
                    overrideIndices.add(idx)
                    add = false
                }
                sourceSet in parents -> {
                    add = false
                }
            }
        }

        return if (overrideIndices.isEmpty() && !add) {
            Pair(indicators, filtered)
        } else {
            updatesCandidates(
                newSource = currentSource,
                overrides = overrideIndices.sorted(),
                indicators = indicators,
                filtered = filtered
            )
        }
    }

    override fun <T : Source> filterByDependencies(templateSources: List<T>): List<T> {
        val filteredSourceSet: MutableMap<String, List<String>> = mutableMapOf()
        val filtered: MutableMap<String, List<T>> = mutableMapOf()

        templateSources.forEach { source ->
            source.indicator.guardedSource {
                val qualifiedName = "${source.packageName}.${source.templateName}"
                val filteredIndicators = filteredSourceSet.getOrElse(qualifiedName) { emptyList() }
                val filteredSources = filtered.getOrElse(qualifiedName) { emptyList() }

                val (updatedIndicators, updatedFiltered) = filterCandidates(
                    currentSource = source,
                    indicators = filteredIndicators,
                    filtered = filteredSources
                )

                filteredSourceSet[qualifiedName] = updatedIndicators
                filtered[qualifiedName] = updatedFiltered
            }
        }

        return filtered.map { (_, source) -> source }.flatten()
    }
}
