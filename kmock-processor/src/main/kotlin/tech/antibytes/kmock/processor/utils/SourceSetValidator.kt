/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import tech.antibytes.kmock.processor.ProcessorContract

internal class SourceSetValidator(
    private val logger: KSPLogger,
    private val knownSharedSourceSets: Set<String>,
) : ProcessorContract.SourceSetValidator {
    private fun extractSourceSet(sourceSet: Any?): Any? {
        return if (sourceSet is KSAnnotation) {
            sourceSet.arguments.firstOrNull()?.value
        } else {
            sourceSet
        }
    }

    override fun isValidateSourceSet(sourceSet: Any?): Boolean {
        val extractedSourceSet = extractSourceSet(sourceSet)
        return when {
            extractedSourceSet !is String -> {
                logger.warn("Unexpected annotation payload!")
                false
            }
            extractedSourceSet !in knownSharedSourceSets && "${extractedSourceSet}Test" !in knownSharedSourceSets -> {
                logger.warn("$extractedSourceSet is not a applicable sourceSet!")
                false
            }
            else -> true
        }
    }
}
