/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.MockShared

@MockShared("TEST", AsyncFunctionShared::class)
interface AsyncFunctionShared {
    suspend fun foo(fuzz: Int, ozz: Any): Any

    fun bar(buzz: Int, bozz: Any): Any = bozz
}
