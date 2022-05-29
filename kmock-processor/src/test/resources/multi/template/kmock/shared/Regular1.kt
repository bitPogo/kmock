/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.kmock.shared

import multi.template.kmock.common.Regular1
import multi.template.kmock.shared.nested.Regular3
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.KMockMulti

@OptIn(KMockExperimental::class)
@KMockMulti(
    "SharedMulti",
    Regular1::class,
    SharedContractRegular.Regular2::class,
    Regular3::class,
)
interface Regular1 {
    val something: Int

    fun doSomething(): Int
}
