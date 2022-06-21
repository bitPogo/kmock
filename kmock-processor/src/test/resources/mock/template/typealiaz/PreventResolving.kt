/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.typealiaz

import tech.antibytes.kmock.Mock

interface Generics<T>

typealias Alias921 = (Any) -> Unit
typealias Alias923 = (Any) -> Any
typealias Alias977<X> = Generics<X>
typealias Alias973<X> = Map<String, X>
typealias Alias955<X> = Alias973<X>
typealias Alias999<X> = Alias955<X>
typealias Alias1000<X> = Alias955<X>

@Mock(PreventResolving::class)
interface PreventResolving<L : Alias923> {
    val prop: Alias973<String>

    fun doSomething(
        arg0: Alias977<Any>,
        arg1: Alias923,
        arg2: Alias921
    ): Any

    fun doAnythingElse(
        arg1: Alias921,
        arg2: Alias923
    ): Alias923

    fun doElse(
        arg1: (Any) -> Unit,
        arg2: (Any) -> Any
    )

    fun doAnythingElse(arg1: Alias977<Alias977<Alias921>>)

    fun <T : Alias977<Alias921>, X : Comparable<X>> doOtherThing(arg1: Alias977<T>, arg0: X)

    fun <T : Alias921, L : Alias923> doSomethingElse(
        arg1: T,
        arg2: L
    )

    fun <T : (Any) -> Unit, L : (Any) -> Any> doMoreElse(
        arg1: T,
        arg2: L
    )

    fun <T : Alias977<K>, K> doSomethingElse(arg1: T): T

    fun <T : Alias973<K>, K> foo(arg1: T)
    fun foo(arg0: Any, arg1: Alias973<String>?): Alias973<String>
    fun foo(arg0: Char, vararg arg1: Alias973<IntArray>)
    fun foo(arg0: Int, vararg arg1: Alias973<out String>)
    fun <T : Alias973<out Alias977<Alias973<Int>>>> foo(arg0: Long, vararg arg1: T)

    fun bar(arg1: Alias999<String>)
    fun <T : Alias999<String>> bar(vararg arg1: T): T
    fun <T : Alias999<out Alias977<Alias973<Int>>>> bar(arg0: Long, vararg arg1: T): T

    fun run(arg: L): Alias921

    fun <T : Alias1000<K>, K> rol(arg: T)
    fun <T : Alias977<K>, K> toll(arg: T) where K : CharSequence, K : Comparable<K>
}
