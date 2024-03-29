/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.util.test.fulfils

class KMockMethodGeneratorSpec {
    @Test
    fun `It fulfils MethodGenerator`() {
        KMockMethodGenerator(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
        ) fulfils ProcessorContract.MethodGenerator::class
    }
}
