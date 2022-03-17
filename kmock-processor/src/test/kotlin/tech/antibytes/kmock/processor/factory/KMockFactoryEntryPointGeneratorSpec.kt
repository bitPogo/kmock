/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.util.test.fulfils

internal class KMockFactoryEntryPointGeneratorSpec {
    @Test
    fun `It fulfils KMockFactoryEntryPointGenerator`() {
        KMockFactoryEntryPointGenerator(
            mockk(),
            mockk(),
            mockk()
        ) fulfils ProcessorContract.MockFactoryEntryPointGenerator::class
    }
}
