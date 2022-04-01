/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import tech.antibytes.kmock.processor.ProcessorContract

internal class KMockRelaxerGenerator : ProcessorContract.RelaxerGenerator {
    override fun addRelaxer(
        relaxerDefinitions: StringBuilder,
        relaxer: ProcessorContract.Relaxer?
    ) {
        if (relaxer != null) {
            relaxerDefinitions.append("useRelaxerIf(relaxed) { mockId -> ${relaxer.functionName}(mockId) }\n")
        }
    }
}
