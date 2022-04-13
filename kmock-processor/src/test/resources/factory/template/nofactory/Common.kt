/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package factory.template.nofactory

import tech.antibytes.kmock.MockCommon

@MockCommon(Common::class)
interface Common {
    val foo: String
    val bar: Int
        get() = foo.length

    var buzz: Any
}
