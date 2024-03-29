/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.property

import mock.template.async.Shared
import tech.antibytes.kmock.MockShared

@MockShared("sharedTest", Shared::class)
interface Shared {
    val foo: String
    val bar: Int
        get() = foo.length

    val enum: Enum<*>
    var buzz: Any
}
