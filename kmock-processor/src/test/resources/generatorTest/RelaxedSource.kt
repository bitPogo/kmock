/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.MagicStub
import tech.antibytes.kmock.MagicStubRelaxer

@MagicStubRelaxer
internal inline fun <reified T> relaxed(id: String): T {
    return id as T
}

@MagicStub(Relaxed::class)
interface Relaxed {
    val buzz: String

    fun foo(payload: Any): String
    suspend fun bar(payload: Any): String
}

