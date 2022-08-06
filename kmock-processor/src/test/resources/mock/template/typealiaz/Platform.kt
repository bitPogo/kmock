/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.typealiaz

import tech.antibytes.kmock.Mock

interface GenericAliasPlatform<Z>

typealias Alias21 = (Any) -> Unit
typealias Alias23 = (Any) -> Any
typealias Alias77<Q> = GenericAliasPlatform<Q>
typealias Alias78<Q> = Alias77<Q>
typealias Alias73<Q> = Map<String, Q>
typealias Alias73a = Map<String, Int>
typealias Alias55<Q> = Alias73<Q>
typealias Alias99<Q> = Alias55<Q>

@Mock(Platform::class)
interface Platform<L : Alias23> {
    val prop: Alias73<String>

    fun doSomething(
        arg0: Alias77<Any>,
        arg1: Alias23,
        arg2: Alias21
    ): Any

    fun doMore(
        arg0: List<Alias73a>,
    ): Any

    fun doAnythingElse(
        arg1: Alias21,
        arg2: Alias23
    )

    fun doAnythingElse(arg1: Alias78<Alias77<Alias21>>)

    fun <T : Alias77<Alias21>, X : Comparable<X>> doOtherThing(arg1: Alias77<T>, arg0: X)

    fun <T : Alias21, L : Alias23> doSomethingElse(
        arg1: T,
        arg2: L
    )

    fun <T : Alias77<K>, K> doSomethingElse(arg1: T)

    fun <T : Alias73<K>, K> foo(arg1: T)
    fun foo(arg0: Any, arg1: Alias73<String>)
    fun foo(arg0: Char, vararg arg1: Alias73<IntArray>)
    fun foo(arg0: Int, vararg arg1: Alias73<out String>)
    fun <T : Alias73<out Alias77<Alias73<Int>>>> foo(arg0: Long, vararg arg1: T)
    fun <T : List<Alias73a>> foo(arg0: T): Any

    fun bar(arg1: Alias99<String>)
    fun <T : Alias99<String>> bar(vararg arg1: T)
    fun <T : Alias99<out Alias77<Alias73<Int>>>> bar(arg0: Long, vararg arg1: T): T

    fun run(arg: L): Alias21
    fun <T : Alias73<K>, K> rol(arg1: T) where K : CharSequence, K : Comparable<K>
    fun <T : Alias73<K>, K> lol(arg1: T) where K : CharSequence, K : Comparable<T>
    fun <T : Map<String, K>, K> fol(arg1: T) where K : CharSequence, K : Comparable<K>
}
