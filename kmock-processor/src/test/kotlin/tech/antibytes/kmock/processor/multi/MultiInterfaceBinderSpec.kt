/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import io.mockk.mockk
import org.junit.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.util.test.fulfils

class MultiInterfaceBinderSpec {
    @Test
    fun `It fulfils MultiInterfaceBinder`() {
        KMockMultiInterfaceBinder(
            mockk(),
            mockk(),
            "any",
            mockk(),
        ) fulfils ProcessorContract.MultiInterfaceBinder::class
    }
}
