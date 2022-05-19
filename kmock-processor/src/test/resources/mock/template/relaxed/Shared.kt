/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.relaxed

import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.Relaxer

@Relaxer
internal inline fun <reified T> relaxed(id: String): T {
    return id as T
}

@MockShared(
    "sharedTest",
    Shared::class
)
interface Shared<T> {
    val buzz: String
    val uzz: T

    fun foo(payload: Any): String
    suspend fun bar(payload: Any): String
    fun buzz()
}

