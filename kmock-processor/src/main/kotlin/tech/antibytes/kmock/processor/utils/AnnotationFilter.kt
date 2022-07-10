/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_COMMON_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_PLATFORM_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_MULTI_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANNOTATION_SHARED_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.RELAXATION_NAME

internal class AnnotationFilter(
    private val logger: KSPLogger,
    private val knownSharedSourceSets: Set<String>,
) : ProcessorContract.AnnotationFilter {
    private fun filterBySourceSet(
        annotation: String,
        sourceSet: String,
    ): Boolean {
        return when {
            sourceSet !in knownSharedSourceSets && "${sourceSet}Test" !in knownSharedSourceSets -> {
                logger.warn(
                    "$annotation is not applicable since is SourceSet ($sourceSet) is not a know shared source.",
                )
                false
            }
            annotation in RESERVED -> {
                logger.warn(
                    "$annotation is not applicable since is shadows a build-in annotation.",
                )
                false
            }
            else -> true
        }
    }

    override fun filterAnnotation(
        annotations: Map<String, String>,
    ): Map<String, String> {
        return annotations.filter { (annotation, sourceSet) ->
            filterBySourceSet(
                annotation = annotation,
                sourceSet = sourceSet,
            )
        }
    }

    override fun isApplicableSingleSourceAnnotation(
        annotation: KSAnnotation,
    ): Boolean {
        return annotation.arguments.size == 1 &&
            annotation.arguments[0].value is List<*> &&
            ((annotation.arguments[0].value as List<*>).size == 0 || (annotation.arguments[0].value as List<*>).random() is KSType)
    }

    override fun isApplicableMultiSourceAnnotation(annotation: KSAnnotation): Boolean {
        return annotation.arguments.size == 2 &&
            annotation.arguments[0].value is String &&
            annotation.arguments[1].value is List<*> &&
            ((annotation.arguments[1].value as List<*>).size == 0 || (annotation.arguments[1].value as List<*>).random() is KSType)
    }

    private companion object {
        val RESERVED = sortedSetOf(
            ANNOTATION_PLATFORM_NAME,
            ANNOTATION_PLATFORM_MULTI_NAME,
            ANNOTATION_SHARED_NAME,
            ANNOTATION_SHARED_MULTI_NAME,
            ANNOTATION_COMMON_NAME,
            ANNOTATION_COMMON_MULTI_NAME,
            RELAXATION_NAME,
        )
    }
}
