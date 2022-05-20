/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.async

import mock.template.generic.Shared
import tech.antibytes.kmock.MockShared

@MockShared("shared", Shared::class)
interface Shared {
    suspend fun foo(fuzz: Int, ozz: Any): Any

    suspend fun bar(buzz: Int, bozz: Any): Any = bozz
}
