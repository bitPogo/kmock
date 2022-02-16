/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.MagicStub

@MagicStub(SyncFunctionOverload::class)
interface SyncFunctionOverload {
    fun foo(fuzz: Int, ozz: Any): Any
    fun foo(fuzz: Any, ozz: Int): Any
    fun foo(fuzz: Any, ozz: String): Any
    fun foo(fuzz: String, ozz: Any): Any
    fun foo(fuzz: String, ozz: Abc): Any
}

data class Abc(
    val value: String
)
