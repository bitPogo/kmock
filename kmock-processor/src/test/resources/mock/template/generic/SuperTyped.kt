/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.generic

import tech.antibytes.kmock.Mock

@Mock(SuperTyped::class)
interface SuperTyped<K, L> : Parent<K, L> where L : Any, L : Comparable<L>, K : Any

interface Parent<K, L> where L : Any, L : Comparable<L>, K : Any {
    fun <T> ppt(vararg x: T)
    fun <T> ppt(vararg x: T) where T : CharSequence, T : Comparable<T>
    fun <T> ppt(vararg x: T) where T : Comparable<T>
    fun <T, K> lol(arg: K, vararg x: T) where T : Comparable<T>
    fun <T, K> lol(vararg x: T) where T : K

    fun <nulled : List<Array<Int>>> buzz(vararg payload: nulled?)

    fun <T> ppt(x: T)
    fun <T> ppt(x: T) where T : CharSequence?, T : Comparable<T>?
    fun <T> ppt(x: T) where T : Comparable<T>?
    fun <T, K> lol(arg: K, x: T) where T : Comparable<T>
    fun <T, K> lol(x: T) where T : K

    fun narv(vararg x: L)
    fun narv(vararg x: Array<out Any>)
    fun narv(vararg x: IntArray)

    fun oo(fuzz: Int, vararg ozz: Any): Any
    fun oo(fuzz: Any?, vararg ozz: Int): Any
    fun <T> oo(fuzz: Any, vararg ozz: T): Any
    fun <T> oo(fuzz: Any, vararg ozz: RRR<out T>): Any
    fun <T> oo(fuzz: Any, ozz: CharArray): Any

    fun <T> roo(fuzz: Any, vararg ozz: RRR<*>): Any
}

class RRR<L>
