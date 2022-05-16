/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.relaxed

import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.Relaxer

@Relaxer
internal inline fun <reified T> relaxed(id: String): T {
    return id as T
}

@MockCommon(Common::class)
interface Common<K, L> where L : Any, L : Comparable<L>, K : Any {
    var template: L
    val buzz: String

    fun foo(payload: Any): String
    fun <T> foo(): T
    fun foo(payload: String): L
    fun <T: Any> fooBar(payload: T)
    fun <T> fooBar(): T where T : K?
    fun oo(vararg payload: Any): String
    suspend fun bar(payload: Any): String
    suspend fun ar(vararg payload: Any): String
    fun buzz()
}

