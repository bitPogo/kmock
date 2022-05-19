/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.mixed

import multi.template.mixed.PlatformContractRegular
import multi.template.mixed.nested.Regular3
import multi.template.mixed.nested.Generic2
import tech.antibytes.kmock.MultiMock
import tech.antibytes.kmock.MultiMockCommon

@MultiMockCommon(
    "CommonMulti",
    Regular1::class,
    PlatformContractRegular.Regular2::class,
    Regular3::class,
)
@MultiMock(
    "PlatformMulti",
    Generic1::class,
    GenericPlatformContract.Generic3::class,
    Generic2::class,
)
interface Regular1 {
    val something: Int

    fun doSomething(): Int
}
