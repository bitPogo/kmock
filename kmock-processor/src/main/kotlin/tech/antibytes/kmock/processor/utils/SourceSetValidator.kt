/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import tech.antibytes.kmock.processor.ProcessorContract

internal class SourceSetValidator(
    private val logger: KSPLogger,
    private val knownSourceSets: Set<String>,
) : ProcessorContract.SourceSetValidator {
    override fun isValidateSourceSet(sourceSet: Any?): Boolean {
        return when (sourceSet) {
            !is String -> false
            !in knownSourceSets -> {
                logger.warn("$sourceSet is not a applicable sourceSet!")
                false
            }
            else -> true
        }
    }
}
