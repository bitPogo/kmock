/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils

internal class KMockCommonEntryPointGeneratorSpec {
    @Test
    fun `It fulfils MockFactoryCommonEntryPointGenerator`() {
        KMockCommonEntryPointGenerator(mockk()) fulfils ProcessorContract.MockFactoryCommonEntryPointGenerator::class
    }
}
