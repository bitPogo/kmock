/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.commonGeneric.nested

interface Generic2<K, L> where L : Any, L : Comparable<L>, K : Any {
    fun <T> foo(payload: T)
    fun <T> foo(vararg payload: T)

    fun <T : Int> bla(payload: T)
    fun <T : Int> bla(vararg payload: T)

    fun <T : List<Array<String>>> bar(payload: T)
    fun <T : List<Array<String>>> bar(vararg payload: T)

    fun <T : List<Array<String?>>> blubb(payload: T)
    fun <T : List<Array<String?>>> blubb(vararg payload: T)

    fun <T : List<Array<Int>>?> buss(payload: T)
    fun <T : List<Array<Int>>?> buss(vararg payload: T)

    fun <T : List<Array<Int>?>> boss(payload: T)
    fun <T : List<Array<Int>?>> boss(vararg payload: T)

    fun <T : List<Array<Int>>> buzz(payload: T?)
    fun <T : List<Array<Int>>> buzz(vararg payload: T?)

    fun <T : L> ozz(payload: T)
    fun <T : L> ozz(vararg payload: T)

    fun <T> brass(payload: T) where T : Comparable<List<Array<T>>>
    fun <T> brass(vararg payload: T) where T : Comparable<List<Array<T>>>

    fun <T> bliss(payload: T) where T : Comparable<List<Array<T>>>?
    fun <T> bliss(vararg payload: T) where T : Comparable<List<Array<T>>>?

    fun <T> loss(payload: T) where T : Map<String, String>
    fun <T> loss(vararg payload: T) where T : Map<String, String>

    fun <T : R, R> oss(arg0: T, arg1: R)
    fun <T : R, R> oss(arg0: R, vararg arg1: T)

    fun <R, T> xss(arg0: T, arg1: R) where R : Sequence<Char>, R : CharSequence
}
