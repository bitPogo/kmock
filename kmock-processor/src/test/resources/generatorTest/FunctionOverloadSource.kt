/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.Mock

@Mock(SyncFunctionOverload::class)
interface SyncFunctionOverload {
    val foo: Any
    fun foo(fuzz: Int, ozz: Any): Any
    fun foo(fuzz: Any, ozz: Int): Any
    fun foo(fuzz: Any, ozz: String): Any
    fun foo(fuzz: String, ozz: Any): Any
    fun foo(fuzz: String, ozz: Abc): Any
    fun foo(fuzz: (Any) -> Unit): Any
    fun <T> foo(fuzz: T)
    fun <T : SyncFunctionOverload> foo(fuzz: T)
    fun <T : LPG> foo(fuzz: T)
}

data class Abc(
    val value: String
)

interface LPG
