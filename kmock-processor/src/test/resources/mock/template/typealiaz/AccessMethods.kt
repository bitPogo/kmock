/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.typealiaz

import tech.antibytes.kmock.Mock

interface GenericsAccess<T>

typealias Alias621 = (Any) -> Unit
typealias Alias623 = (Any) -> Any
typealias Alias677<I> = GenericsAccess<I>
typealias Alias673<X> = Map<String, X>
typealias Alias655<X> = Alias673<X>
typealias Alias699<X> = Alias655<X>
typealias Alias700<X> = Alias655<X>
typealias Alias701<E> = Alias677<E>
typealias Alias702<E> = Alias701<E>
typealias Alias703<Z, Q> = (Alias701<Z>) -> Alias702<Q>

@Mock(Access::class)
interface Access<L : Alias623> {
    val prop: Alias673<String>

    fun doSomething(
        arg0: Alias677<Any>,
        arg1: Alias623,
        arg2: Alias621
    ): Any

    fun doAnythingElse(
        arg1: Alias621,
        arg2: Alias623
    ): Alias623

    fun doElse(
        arg1: (Any) -> Unit,
        arg2: (Any) -> Any
    )

    fun doAnythingElse(arg1: Alias677<Alias677<Alias621>>)

    fun <T : Alias677<Alias621>, X : Comparable<X>> doOtherThing(arg1: Alias677<T>, arg0: X)

    fun <T : Alias621, L : Alias623> doSomethingElse(
        arg1: T,
        arg2: L
    )

    fun <T : (Any) -> Unit, L : (Any) -> Any> doMoreElse(
        arg1: T,
        arg2: L
    )

    fun <T : Alias677<K>, K> doSomethingElse(arg1: T): T

    fun <T : Alias673<K>, K> foo(arg1: T)
    fun foo(arg0: Any, arg1: Alias673<String>): Alias673<String>
    fun foo(arg0: Char, vararg arg1: Alias673<IntArray>)
    fun foo(arg0: Int, vararg arg1: Alias673<out String>)
    fun <T : Alias673<out Alias677<Alias673<Int>>>> foo(arg0: Long, vararg arg1: T)

    fun bar(arg1: Alias699<String>)
    fun <T : Alias699<String>> bar(vararg arg1: T): T
    fun <T : Alias699<out Alias677<Alias673<Int>>>> bar(arg0: Long, vararg arg1: T): T

    fun run(arg: L): Alias621

    fun <T : Alias700<K>, K> rol(arg: T)
    fun <T : Alias677<K>, K> toll(arg: T) where K : CharSequence, K : Comparable<K>
    // TODO: fun <T : Alias703<Alias623, in Alias677<Alias673<Int>>>> bar(vararg arg1: T)
}
