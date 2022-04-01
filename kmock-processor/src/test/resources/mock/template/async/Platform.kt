/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.async

import mock.template.generic.Platform
import tech.antibytes.kmock.Mock

@Mock(Platform::class)
interface Platform {
    suspend fun foo(fuzz: Int, ozz: Any): dynamic

    suspend fun bar(buzz: Int, bozz: Any): Any = bozz

    suspend fun ozz(vararg buzz: Int): Any

    suspend fun izz(vararg buzz: Any): Any
}
