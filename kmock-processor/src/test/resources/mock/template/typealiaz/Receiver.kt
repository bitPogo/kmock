/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.typealiaz

import tech.antibytes.kmock.Mock

interface GenericReceiver<R>

typealias Alias11 = (Any) -> Unit
typealias Alias2 = (Any) -> Any
typealias Alias3<R> = GenericReceiver<R>

@Mock(Receiver::class)
interface Receiver<L : Alias11> {
    val List<Alias3<Any>>.any: Int
    var <T : Alias3<T>> T.member: T

    fun Alias11.doSomething(
        arg0: Alias3<Any>,
        arg1: Alias2,
        arg2: Alias11
    ): Any

    fun <T : Comparable<T>> Alias3<T>.doAnythingElse(
        arg1: Alias11,
        arg2: Alias2
    )

    fun doAnythingElse(arg1: Alias3<Alias3<Alias11>>)

    fun <T : Alias3<Alias2>, X : Comparable<X>> T.doOtherThing(arg1: Alias3<T>, arg0: X)

    fun <T : Alias11, L : Alias2> L.doSomethingElse(
        arg1: T,
        arg2: L
    )

    fun <T : Alias3<K>, K> T.doSomethingElse(arg1: T)

    fun run(arg: L): Alias11
}
