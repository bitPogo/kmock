/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.spy

import mock.template.async.Platform
import tech.antibytes.kmock.Mock

@Mock(Platform::class)
interface Platform {
    val uzz: Int
    var fzz: Int

    fun foo(fuzz: Int, ozz: Any): dynamic

    fun bar(buzz: Int, bozz: Any): Any = bozz
}
