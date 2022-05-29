/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.kmock.common

import multi.template.kmock.common.nested.Regular3
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.KMockMulti

@OptIn(KMockExperimental::class)
@KMockMulti(
    "CommonMulti",
    Regular1::class,
    CommonContractRegular.Regular2::class,
    Regular3::class,
)
interface Regular1 {
    val something: Int

    fun doSomething(): Int
}
