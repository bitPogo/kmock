/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.buildIn

import tech.antibytes.kmock.MockShared

@MockShared("TesT", Shared::class)
interface Shared {
    val foo: String
    val bar: Int
        get() = foo.length

    var buzz: Any
}
