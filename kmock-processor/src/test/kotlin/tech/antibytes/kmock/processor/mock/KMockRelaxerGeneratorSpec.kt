/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.util.test.fulfils

class KMockRelaxerGeneratorSpec {
    @Test
    fun `It fulfils RelaxerGenerator`() {
        KMockRelaxerGenerator() fulfils ProcessorContract.RelaxerGenerator::class
    }
}
