/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.spy

import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.Relaxer

@Relaxer
internal inline fun <reified T> relaxed(id: String): T {
    return Any() as T
}

@Mock(Relaxed::class)
interface Relaxed<K, L> where L : Any, L : Comparable<L>, K : Any {
    var template: L
    val ozz: Int

    fun <T> foo(payload: T)
    fun bar(arg0: Int): Any
    suspend fun buzz(arg0: String): L
}
