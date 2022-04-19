/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.customshared

import mock.template.async.Shared
import tech.antibytes.kmock.MockShared
import kotlin.reflect.KClass

annotation class CustomShared(vararg val interfaces: KClass<*>)

@CustomShared(Shared::class)
interface Shared {
    fun foo(fuzz: Int, ozz: Any): Any

    fun bar(buzz: Int, bozz: Any): Any = bozz
}
