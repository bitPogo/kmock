/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.generic

import tech.antibytes.kmock.Mock

@Mock(AllowedRecursive::class)
interface AllowedRecursive<K, L> where L : Any, L : Comparable<L>, K : Any {
    var template: L

    fun <T> oss(payload: T) where T : Sequence<Char>, T : CharSequence, T : Comparable<List<T>>
    fun <T> oss(): T where T : Sequence<Char>, T : CharSequence, T : Comparable<List<T>>

    fun <T> brass(payload: T) where T : Comparable<List<T>>
    fun <T> brass(): T where T : Comparable<List<T>>

    fun <T> iss(payload: T) where T : Comparable<T>
    fun <T> iss(): T where T : Comparable<T>
}
