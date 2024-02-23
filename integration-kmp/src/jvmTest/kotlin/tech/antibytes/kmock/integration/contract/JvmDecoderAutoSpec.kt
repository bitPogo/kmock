/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration.contract

import org.junit.Test
import tech.antibytes.kmock.Mock
import tech.antibytes.util.test.fulfils

@Mock(
    ExampleContractJvm.JvmDecoder::class,
)
class JvmDecoderAutoSpec {
    @Test
    fun `It fulfils JvmDecoder`() {
        JvmDecoderMock() fulfils ExampleContractJvm.JvmDecoder::class
    }
}
