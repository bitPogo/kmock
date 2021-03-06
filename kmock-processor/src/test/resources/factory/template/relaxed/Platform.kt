/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package factory.template.relaxed

import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.Relaxer

@Relaxer
internal inline fun <reified T> relaxed(id: String): T {
    return id as T
}

@Mock(Platform::class)
interface Platform {
    val buzz: String

    fun foo(payload: Any): String
    suspend fun bar(payload: Any): String
    fun buzz()
}

