/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

internal class SourceFilter : ProcessorContract.SourceFilter {
    override fun filter(
        sources: List<ProcessorContract.InterfaceSource>,
        filteredBy: List<ProcessorContract.InterfaceSource>
    ): List<ProcessorContract.InterfaceSource> {
        val filter = filteredBy.map { source ->
            source.interfaze.qualifiedName!!.asString()
        }

        return sources.filter { source ->
            !filter.contains(source.interfaze.qualifiedName!!.asString())
        }
    }
}
