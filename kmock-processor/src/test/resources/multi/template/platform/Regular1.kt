/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.platform

import multi.template.platform.nested.Regular3
import tech.antibytes.kmock.MultiMock

@MultiMock(
    "PlatformMulti",
    Regular1::class,
    PlatformContractRegular.Regular2::class,
    Regular3::class,
)
interface Regular1 {
    val something: Int

    fun doSomething(): Int
}
