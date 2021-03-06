/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.sync

import mock.template.async.Platform
import tech.antibytes.kmock.Mock

@Mock(Platform::class)
interface Platform {
    fun foo(fuzz: Int, ozz: Any): dynamic

    fun bar(buzz: Int, bozz: Any): Any = bozz

    fun ozz(vararg buzz: Int): Any

    fun izz(vararg buzz: Any): Any
}
