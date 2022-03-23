/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package factory.template.spy

import tech.antibytes.kmock.MockCommon

interface SomeGeneric<T>

@MockCommon(GenericCommon::class)
interface GenericCommon<K, L> where L : Any, L : Comparable<L>, K : Any {
    var template: L

    fun <T> foo(): T
    fun <T> foo(payload: T)

    fun <T : Int> bla(): T
    fun <T : Int> bla(payload: T)

    fun <T : List<Array<String>>> bar(payload: T)
    fun <T : List<Array<String>>> bar(): T

    fun <T : List<Array<String?>>> blubb(payload: T)
    fun <T : List<Array<String?>>> blubb(): T

    fun <T : List<Array<Int>>?> buss(payload: T)
    fun <T : List<Array<Int>>?> buss(): T

    fun <T : List<Array<Int>?>> boss(payload: T)
    fun <T : List<Array<Int>?>> boss(): T

    fun <T : List<Array<Int>>> buzz(payload: T?)
    fun <T : List<Array<Int>>> buzz(): T?

    fun <T : L> ozz(payload: T)
    fun <T : L> ozz(): T

    fun <T> brass(payload: T) where T : Comparable<List<Array<T>>>
    fun <T> brass(): T where T : Comparable<List<Array<T>>>

    fun <T> bliss(payload: T) where T : Comparable<List<Array<T>>>?
    fun <T> bliss(): T where T : Comparable<List<Array<T>>>?

    fun <T> loss(payload: T) where T : Map<String, String>
    fun <T> loss(): T where T : Map<String, String>

    fun <T> uzz(payload: T) where T : SomeGeneric<String>, T : List<String>
    fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String>

    fun <T> lzz(payload: T) where T : SomeGeneric<String>, T : List<String>?
    fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>?

    fun <T> tzz(payload: T) where T : SomeGeneric<String>?, T : List<String>?
    fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>?

    fun <T> rzz(payload: T) where T : SomeGeneric<String>, T : Map<String, String>
    fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String>

    fun <T> izz(payload: T) where T : SomeGeneric<String>, T : Comparable<List<Array<T>>>
    fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>>

    fun <T : R, R> oss(arg0: T, arg1: R)
    fun <T : R, R> oss(arg0: T): R

    fun <T : R, R> kss(arg0: T, arg1: R) where R : SomeGeneric<String>, R : Comparable<List<Array<R>>>
    fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R : Comparable<List<Array<R>>>

    fun <R, T> iss(arg0: T, arg1: R) where R : SomeGeneric<String>, R : Comparable<List<Array<T>>>
    fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R : Comparable<List<Array<T>>>
}
