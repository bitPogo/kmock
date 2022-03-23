/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package factory.template.spy

import mock.template.async.Platform
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.Relaxer

@Mock(Platform::class)
interface Platform {
    val buzz: String

    fun foo(payload: Any): String
    suspend fun bar(payload: Any): String
    fun buzz()
}

