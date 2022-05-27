/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.typealiaz

import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.Relaxer

interface Generic<T>

@Relaxer
internal inline fun <reified T> smooth(id: String): T {
    return id as T
}

typealias Alias101 = (Any) -> Unit
typealias Alias102 = (Any) -> Any
typealias Alias107<T> = Generic<T>

@Mock(Relaxed::class)
interface Relaxed<L : Alias102> {
    fun doSomething(
        arg0: Alias107<Any>,
        arg1: Alias102,
        arg2: Alias101
    ): Any

    fun doAnythingElse(
        arg1: Alias101,
        arg2: Alias102
    )

    fun doAnythingElse(arg1: Alias107<Alias107<Alias101>>)

    fun <T : Alias107<Alias101>, X : Comparable<X>> doOtherThing(arg1: Alias107<T>, arg0: X): T

    fun <T : Alias101, L : Alias102> doSomethingElse(
        arg1: T,
        arg2: L
    ): L

    fun <T : Alias107<K>, K> doSomethingElse(arg1: T): T

    fun run(arg: L): Alias101
}
