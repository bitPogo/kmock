/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation

interface ProcessorContract {
    interface Aggregator {
        fun extractInterfaces(
            annotated: Sequence<KSAnnotated>,
            validAnnotations: MutableList<KSAnnotation>
        ): List<KSAnnotated>
    }
}
