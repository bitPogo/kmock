/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.custom

import multi.template.custom.nested.Regular3
import kotlin.reflect.KClass

annotation class CustomShared(
    val mockName: String,
    vararg val interfaces: KClass<*>
)

@CustomShared(
    mockName = "SharedMulti",
    Regular1::class,
    SharedContractRegular.Regular2::class,
    Regular3::class,
)
interface Regular1 {
    val something: Int

    fun doSomething(): Int
}
