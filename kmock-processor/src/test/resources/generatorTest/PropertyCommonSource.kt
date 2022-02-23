/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.MockCommon

@MockCommon(PropertyCommon::class)
interface PropertyCommon {
    val foo: String
    val bar: Int
        get() = foo.length

    var buzz: Any
}
