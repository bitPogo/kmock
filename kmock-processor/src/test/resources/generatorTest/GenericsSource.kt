/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.Mock

@Mock(Generics::class)
interface Generics<K, L> where L : CharSequence, L : Comparable<L>, K : Any {
    var template: K
    fun <T> foo(payload: T)
    fun <T : L> buzz(payload: T)
    fun <T> bar(payload: T) where T : CharSequence?, T : Map<String, Comparable<List<Array<T>>>>
    fun <K : Int> foo(payload: K)
}

