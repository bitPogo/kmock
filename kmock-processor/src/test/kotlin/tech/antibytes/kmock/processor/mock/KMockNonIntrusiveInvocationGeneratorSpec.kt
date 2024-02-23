/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.util.test.fulfils

class KMockNonIntrusiveInvocationGeneratorSpec {
    @Test
    fun `It fulfils NonIntrusiveInvocationGenerator`() {
        KMockNonIntrusiveInvocationGenerator(mockk(), mockk()) fulfils NonIntrusiveInvocationGenerator::class
    }
}
