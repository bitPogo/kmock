/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.typealiaz

import tech.antibytes.kmock.Mock

interface Generic<T>

typealias Alias21 = (Any) -> Unit
typealias Alias23 = (Any) -> Any
typealias Alias77<T> = Generic<T>

@Mock(Platform::class)
interface Platform<L : Alias23> {
    fun doSomething(
        arg0: Alias77<Any>,
        arg1: Alias23,
        arg2: Alias21
    ): Any

    fun doAnythingElse(
        arg1: Alias21,
        arg2: Alias23
    )

    fun doAnythingElse(arg1: Alias77<Alias77<Alias21>>)

    fun <T : Alias77<Alias21>, X : Comparable<X>> doOtherThing(arg1: Alias77<T>, arg0: X)

    fun <T : Alias21, L : Alias23> doSomethingElse(
        arg1: T,
        arg2: L
    )

    fun <T : Alias77<K>, K> doSomethingElse(arg1: T)

    fun run(arg: L): Alias21
}
