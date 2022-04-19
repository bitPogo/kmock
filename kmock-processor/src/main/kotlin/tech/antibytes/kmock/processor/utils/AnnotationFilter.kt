/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import tech.antibytes.kmock.processor.ProcessorContract

internal class AnnotationFilter(
    private val logger: KSPLogger,
    private val knownSources: Set<String>,
) : ProcessorContract.AnnotationFilter {
    private fun filterBySourceSet(
        annotation: String,
        sourceSet: String
    ): Boolean {
        return if (sourceSet !in knownSources) {
            logger.warn(
                "$annotation is not applicable since is SourceSet ($sourceSet) is not a know shared source."
            )
            false
        } else {
            true
        }
    }

    override fun filterAnnotation(
        annotations: Map<String, String>
    ): Map<String, String> {
        return annotations.filter { (annotation, sourceSet) ->
            filterBySourceSet(
                annotation = annotation,
                sourceSet = sourceSet
            )
        }
    }

    override fun isApplicableAnnotation(annotation: KSAnnotation) {
        TODO()
    }
}
