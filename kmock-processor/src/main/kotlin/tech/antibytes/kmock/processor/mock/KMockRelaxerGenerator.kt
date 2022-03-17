/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import tech.antibytes.kmock.processor.ProcessorContract

internal class KMockRelaxerGenerator : ProcessorContract.RelaxerGenerator {
    override fun buildRelaxers(
        relaxer: ProcessorContract.Relaxer?,
        isFunction: Boolean
    ): String {
        val unitFunRelaxerStr = if (isFunction) {
            "unitFunRelaxer = if (relaxUnitFun) { { ${ProcessorContract.UNIT_RELAXER.simpleName}() } } else { null }"
        } else {
            ""
        }

        val buildInRelaxerStr = if (isFunction) {
            "buildInRelaxer = null"
        } else {
            ""
        }

        val relaxerStr = if (relaxer != null) {
            "relaxer = if (relaxed) { { mockId -> ${relaxer.functionName}(mockId) } } else { null }"
        } else {
            "relaxer = null"
        }

        return if (unitFunRelaxerStr.isEmpty()) {
            relaxerStr
        } else {
            "$unitFunRelaxerStr, $relaxerStr, $buildInRelaxerStr"
        }
    }
}
