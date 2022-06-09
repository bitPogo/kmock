/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.overloaded

import tech.antibytes.kmock.MockCommon

@MockCommon(Common::class)
interface Common {
    val foo: Any
    var hashCode: Int
    fun foo(fuzz: Int, ozz: dynamic): Any
    fun foo(fuzz: Any, ozz: Int): Any
    fun foo(fuzz: Any, ozz: String): Any
    fun foo(fuzz: String, ozz: Any): Any
    fun foo(fuzz: String, ozz: Abc): Any
    fun foo(fuzz: (Any) -> Unit): Any
    fun <T> foo(fuzz: T)
    fun <T : Common> foo(fuzz: T)
    fun <T : LPG> foo(fuzz: T)
    fun foo(vararg fuzz: Any): Any
}

data class Abc(
    val value: String
)

interface LPG
