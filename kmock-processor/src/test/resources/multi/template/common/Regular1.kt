/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.common

import multi.template.common.nested.Regular3
import tech.antibytes.kmock.MultiMockCommon

@MultiMockCommon(
    "CommonMulti",
    Regular1::class,
    CommonContractRegular.Regular2::class,
    Regular3::class,
)
interface Regular1 {
    val something: Int

    fun doSomething(): Int
}
