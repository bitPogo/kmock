/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.overloaded

import tech.antibytes.kmock.Mock

typealias alias = () -> Any

@Mock(Refined::class)
interface Refined<Q : List<Int>> : Parent {
    val foo: Any
    var hashCode: Int
    fun foo(fuzz: Int, ozz: Any): Any
    fun foo(fuzz: Any?, ozz: Int): Any
    fun foo(fuzz: Any, ozz: String): Any
    fun foo(fuzz: String, ozz: dynamic): Any
    fun foo(fuzz: String?, ozz: Abc): Any
    fun foo(fuzz: (Any) -> Unit): Any
    fun <T> foo(fuzz: T)
    fun <T : Refined<*>> foo(fuzz: T)
    fun <T : LPG> foo(fuzz: T)
    fun foo(vararg fuzz: Any): Any
    @JvmName("foo1")
    fun <T> foo(fuzz: Comparable<Array<Map<String, T>>>): Any
    @JvmName("foo2")
    fun <T> foo(fuzz: Comparable<Array<Map<T, Any>>>): Any
    fun foo(fuzz: Comparable<Array<Map<String, Any>>>): Any
    @JvmName("foo3")
    fun foo(fuzz: Array<in Any>): Any
    @JvmName("foo4")
    fun <T> foo(fuzz: T?, ozz: Abc): Any where T : CharSequence?, T : Comparable<T>
    @JvmName("foo5")
    fun <T> foo(fuzz: T, ozz: Abc): Any where T : CharSequence, T : Comparable<T>?
    @JvmName("foo6")
    fun <T> foo(fuzz: T, ozz: Abc): Any where T : CharSequence?, T : Comparable<T>?
    @JvmName("foo7")
    fun <T> foo(fuzz: T?, ozz: Abc): Any where T : CharSequence, T : Comparable<T>
    fun foo(fuzz: alias): Any
    @JvmName("foo8")
    fun <T, R> foo(fuzz: R) where T : Comparable<Array<R>>, R : List<T>
    @JvmName("foo9")
    fun <T, R> foo(fuzz: R) where T : Comparable<Array<R>>, R : List<T?>
    @JvmName("foo10")
    fun <T> foo(fuzz: T) where T : Comparable<*>
    @JvmName("foo11")
    fun foo(fuzz: Q)
    @JvmName("foo12")
    fun <T> foo(fuzz: T) where T : Comparable<Q>
    @JvmName("foo13")
    fun foo(fuzz: Q?)
    @JvmName("foo14")
    fun <T> foo(fuzz: T) where T : Comparable<Q?>
    fun <T : Q> foo(arg0: T, arg1: String)
    fun <T : Q, Q> foo(arg0: T, arg1: Q) where Q : CharSequence, Q : Comparable<Q>?
}

data class Abc(
    val value: String
)

interface LPG

class RRR<L>

interface Parent {
    fun oo(fuzz: Int, vararg ozz: Any): Any
    fun oo(fuzz: Any?, vararg ozz: Int): Any
    fun <T> oo(fuzz: Any, vararg ozz: T): Any
    fun <T> oo(fuzz: Any, vararg ozz: RRR<T>): Any
}
