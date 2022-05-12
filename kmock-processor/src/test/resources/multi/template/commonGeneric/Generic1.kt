/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.commonGeneric

import multi.template.commonGeneric.nested.Generic2
import tech.antibytes.kmock.MultiMockCommon

interface SomeGeneric<T>

@MultiMockCommon(
    "CommonGenericMulti",
    Generic1::class,
    Generic2::class,
    GenericCommonContract.Generic3::class
)
interface Generic1<K, L> where L : Any, L : Comparable<L>, K : Any {
    var template: L

    fun <T> foo(): T

    fun <T : Int> bla(): T

    fun <T : List<Array<String>>> bar(): T

    fun <T : List<Array<String?>>> blubb(): T

    fun <T : List<Array<Int>>?> buss(): T

    fun <T : List<Array<Int>?>> boss(): T

    fun <T : List<Array<Int>>> buzz(): T?

    fun <T : L> ozz(): T

    fun <T> brass(): T where T : Comparable<List<Array<T>>>

    fun <T> bliss(): T where T : Comparable<List<Array<T>>>?

    fun <T> loss(): T where T : Map<String, String>

    fun <T> uzz(): T where T : SomeGeneric<String>, T : List<String>

    fun <T> lzz(): T where T : SomeGeneric<String>, T : List<String>?

    fun <T> tzz(): T where T : SomeGeneric<String>?, T : List<String>?

    fun <T> rzz(): T where T : SomeGeneric<String>, T : Map<String, String>

    fun <T> izz(): T where T : SomeGeneric<String>, T : Comparable<List<Array<T>>>

    fun <T : R, R> oss(arg0: T): R

    fun <T : R, R> kss(arg0: T): R where R : SomeGeneric<String>, R : Comparable<List<Array<R>>>

    fun <R, T> iss(arg0: T): R where R : SomeGeneric<String>, R : Comparable<List<Array<T>>>

    fun <R, T : X, X : SomeGeneric<String>> pss(arg0: T): R where R : T

    fun <R, T> xss(arg0: T): R where R : Sequence<Char>, R : CharSequence
}
