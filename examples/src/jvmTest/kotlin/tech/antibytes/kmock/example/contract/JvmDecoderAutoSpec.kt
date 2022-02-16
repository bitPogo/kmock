/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example.contract

import org.junit.Test
import tech.antibytes.kmock.MagicStub
import tech.antibytes.util.test.fulfils

@MagicStub(
    ExampleContractJvm.JvmDecoder::class
)
class JvmDecoderAutoSpec {
    @Test
    fun `It fulfils JvmDecoder`() {
        JvmDecoderStub() fulfils ExampleContractJvm.JvmDecoder::class
    }
}
