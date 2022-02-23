/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.MagicStub
import tech.antibytes.kmock.MagicStubRelaxer

@MagicStubRelaxer
internal inline fun <reified T> test(id: String): T {

}

@MagicStub(Relaxed::class)
interface Relaxed {
    fun foo(payload: Any)
}

