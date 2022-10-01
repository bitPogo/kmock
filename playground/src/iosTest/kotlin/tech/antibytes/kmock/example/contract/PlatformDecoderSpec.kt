/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example.contract

import kotlin.test.Test
import tech.antibytes.kmock.Mock

@Mock(PlatformThingContract::class)
class PlatformDecoderSpec {
    @Test
    fun doSomething() {
        // Nothing
    }
}
