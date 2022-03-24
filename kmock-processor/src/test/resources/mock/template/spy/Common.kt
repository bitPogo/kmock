/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.spy

import tech.antibytes.kmock.MockCommon

@MockCommon(Common::class)
interface Common<K, L> where L : Any, L : Comparable<L>, K : Any {
    var template: L
    val ozz: Int

    fun <T> foo(payload: T)
    fun bar(arg0: Int): Any
    suspend fun buzz(arg0: String): L
}
