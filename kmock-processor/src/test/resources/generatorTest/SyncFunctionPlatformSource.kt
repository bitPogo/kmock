/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.Mock

@Mock(SyncFunctionPlatform::class)
interface SyncFunctionPlatform {
    fun foo(fuzz: Int, ozz: Any): dynamic

    fun bar(buzz: Int, bozz: Any): Any = bozz
}
