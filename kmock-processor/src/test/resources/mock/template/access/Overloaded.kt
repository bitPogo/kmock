/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.access

import tech.antibytes.kmock.Mock

interface SomeGeneric<T>

@Mock(Overloaded::class)
interface Overloaded<K, L, U : Int?, W> where L : Any, L : Comparable<L>, K : Any {
    var template: L

    fun trr()
    fun trr(arg: Any)
    fun trr(arg: Int)

    fun urr()
    fun urr(arg: U)
    fun urr(arg: W)
    fun <U : CharSequence?> urr(arg: U)

    fun <T : Int> krr(arg: T)
    fun <T : Int?> krr(arg: T)
    fun <T : Any?> krr(arg: T)

    fun foo(payload: Any): String
    fun <T : Any?> foo(): T
    fun <T> foo(payload: T)
    fun <T> foo(vararg payload: T)

    fun lol(arg: Int)
    fun lol(vararg payload: Array<out Any>)

    fun <T> brass(): T where T : Comparable<List<Array<T>>>
    fun <T> brass(payload: T) where T : Comparable<List<Array<T>>>
    fun <T> brass(vararg payload: T) where T : Comparable<List<Array<T>>>

    suspend fun <T : Int> bla(): T
    suspend fun <T : Int> bla(payload: T)
    suspend fun <T : Int> bla(vararg payload: T)

    fun <T : List<Array<String>>> bar(): T
    fun <T : List<Array<String>>> bar(payload: T)
    fun <T : List<Array<String>>> bar(vararg payload: T)

    fun blubb(arg0: String, arg1: CharArray, arg2: Boolean, arg3: Int)
    fun <T : List<Array<String?>>> blubb(): T
    fun <T : List<Array<String?>>> blubb(payload: T)
    fun <T : List<Array<String?>>> blubb(vararg payload: T)

    fun <T : List<Array<Int>>?> buss(): T
    fun <T : List<Array<Int>>?> buss(payload: T)
    fun <T : List<Array<Int>>?> buss(vararg payload: T)

    fun <T : List<Array<Int>?>> boss(): T
    fun <T : List<Array<Int>?>> boss(payload: T)
    fun <T : List<Array<Int>?>> boss(vararg payload: T)

    fun <T : List<Array<Int>>> buzz(): T?
    fun <T : List<Array<Int>>> buzz(payload: T?)
    fun <T : List<Array<Int>>> buzz(vararg payload: T?)

    fun <T : L> ozz(): T
    fun <T : L> ozz(payload: T)
    fun <T : L> ozz(vararg payload: T)

    fun <T> bliss(): T where T : Comparable<List<Array<T>>>?
    fun <T> bliss(payload: T) where T : Comparable<List<Array<T>>>?
    fun <T> bliss(vararg payload: T) where T : Comparable<List<Array<T>>>?

    fun <T> loss(): T where T : Map<String, String>
    fun <T> loss(payload: T) where T : Map<String, String>
    fun <T> loss(vararg payload: T) where T : Map<String, String>

    fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String>
    fun <T> uzz(payload: T) where T : SomeGeneric<String>, T : List<String>
    fun <T> uzz(vararg payload: T) where T : SomeGeneric<String>, T : List<String>

    fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>?
    fun <T> lzz(payload: T) where T : SomeGeneric<String>, T : List<String>?
    fun <T> lzz(vararg payload: T) where T : SomeGeneric<String>, T : List<String>?

    suspend fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>?
    suspend fun <T> tzz(payload: T) where T : SomeGeneric<String>?, T : List<String>?
    suspend fun <T> tzz(vararg payload: T) where T : SomeGeneric<String>?, T : List<String>?

    fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String>
    fun <T> rzz(payload: T) where T : SomeGeneric<String>, T : Map<String, String>
    fun <T> rzz(vararg payload: T) where T : SomeGeneric<String>, T : Map<String, String>

    fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>>
    fun <T> izz(payload: T) where T : SomeGeneric<String>, T : Comparable<List<Array<T>>>
    fun <T> izz(vararg payload: T) where T : SomeGeneric<String>, T : Comparable<List<Array<T>>>

    suspend fun <T : R, R> oss(arg0: T): R
    suspend fun <T : R, R> oss(arg0: T, arg1: R)
    suspend fun <T : R, R> oss(arg0: R, vararg arg1: T)

    fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R : Comparable<List<Array<R>>>
    fun <T : R, R> kss(arg0: T, arg1: R) where R : SomeGeneric<String>, R : Comparable<List<Array<R>>>

    suspend fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R : Comparable<List<Array<T>>>
    suspend fun <R, T> iss(arg0: T, arg1: R) where R : SomeGeneric<String>, R : Comparable<List<Array<T>>>

    fun <R, T : X, X : SomeGeneric<String>> pss(arg0: T): R where R : T
    fun <R, T : X, X : SomeGeneric<String>> pss(arg0: T, arg1: R) where R : T

    fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence
    fun <R, T> xss(arg0: T, arg1: R) where R : Sequence<Char>?, R : CharSequence
}
