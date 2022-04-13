/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package factory.template.regular

import tech.antibytes.kmock.Mock

@Mock(Platform::class)
interface Platform {
    val foo: String
    val bar: Int
        get() = foo.length

    var buzz: Any
}
