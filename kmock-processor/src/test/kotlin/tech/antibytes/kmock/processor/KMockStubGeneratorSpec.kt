/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils

class KMockStubGeneratorSpec {
    @Test
    fun `It fulfils StubGenerator`() {
        KMockStubGenerator(mockk(), mockk()) fulfils ProcessorContract.StubGenerator::class
    }
}
