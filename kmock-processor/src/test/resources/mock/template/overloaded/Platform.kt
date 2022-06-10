/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.overloaded

import tech.antibytes.kmock.Mock

@Mock(Platform::class)
interface Platform<Q : List<Int>> {
    val foo: Any
    var hashCode: Int
    fun foo(fuzz: Int, ozz: Any): Any
    fun foo(fuzz: Any?, ozz: Int): Any
    fun foo(fuzz: Any, ozz: String): Any
    fun foo(fuzz: String, ozz: Any): Any
    fun foo(fuzz: String, ozz: Abc): Any
    fun foo(fuzz: (Any) -> Unit): Any
    fun <T> foo(fuzz: T)
    fun <T : Platform<*>> foo(fuzz: T)
    fun <T : LPG> foo(fuzz: T)
    fun <T> foo(fuzz: T?): Any where T : CharSequence?, T : Comparable<T>
    fun foo(vararg fuzz: Any): Any
    fun foo(arg: Q)
    fun <T : Q> foo(arg0: T, arg1: String)
    fun <T : Q, Q> foo(arg0: T, arg1: Q) where Q : CharSequence, Q : Comparable<Q>?
}

data class Abc(
    val value: String
)

interface LPG
