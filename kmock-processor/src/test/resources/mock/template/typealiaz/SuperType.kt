/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.typealiaz

import tech.antibytes.kmock.Mock

interface GenericSuper<T>

typealias Alias31 = (Any) -> Unit
typealias Alias33 = (Any) -> Any
typealias Alias97<T> = GenericSuper<T>
typealias Alias93<T> = Map<String, T>
typealias Alias105<T> = Alias97<T>
typealias Alias199<W> = Alias105<W>
typealias Alias200<W, G> = (Alias105<W>) -> Alias199<G>

@Mock(Inherited::class)
interface Inherited<R : Alias33> : SuperType<R>

interface SuperType<L : Alias33> {
    val prop: Alias93<String>

    fun doSomething(
        arg0: Alias97<Any>,
        arg1: Alias33,
        arg2: Alias31
    ): Any

    fun doSomething(
        arg0: Alias97<String>
    ): Any

    fun doAnythingElse(
        arg1: Alias31,
        arg2: Alias33
    )

    fun doAnythingElse(arg1: Alias97<Alias97<Alias31>>)

    fun <T : Alias97<Alias31>, X : Comparable<X>> doOtherThing(arg1: Alias97<T>, arg0: X)

    fun <T : Alias31, L : Alias33> doSomethingElse(
        arg1: T,
        arg2: L
    )

    fun <T : Alias97<K>, K> doSomethingElse(arg1: T)

    fun <T : Alias93<K>, K> foo(arg1: T)
    fun foo(arg0: Any, arg1: Alias93<String>)
    fun foo(arg0: Char, vararg arg1: Alias93<IntArray>)
    fun foo(arg0: Int, vararg arg1: Alias93<out String>)
    fun <T : Alias93<out Alias97<Alias93<Int>>>> foo(arg0: Long, vararg arg1: T)

    fun bar(arg1: Alias199<String>)
    fun <T : Alias199<String>> bar(vararg arg1: T)
    fun <T : Alias199<out Alias97<Alias93<Int>>>> bar(arg0: Long, vararg arg1: T): T
    fun <T : Alias200<Alias33, out Alias97<Alias93<Int>>>> bar(vararg arg1: T)

    fun run(arg: L): Alias31
}
