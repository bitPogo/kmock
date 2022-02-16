/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.MagicStub

@MagicStub(Ill::class)
interface Ill {
    fun <T> foo(payload: T)
    fun <T : Int> foo(payload: T)
    fun <T : String> foo(payload: T)
}
