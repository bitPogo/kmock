/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.spy

import tech.antibytes.kmock.Mock

@Mock(BuildIn::class)
interface BuildIn {
    val uzz: Int
    var fzz: Int

    fun foo(fuzz: Int, ozz: Any): Any

    fun bar(buzz: Int, bozz: Any): Any = bozz
}
