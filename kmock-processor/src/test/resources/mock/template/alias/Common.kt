/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.alias

import mock.template.generic.Common
import tech.antibytes.kmock.MockCommon

@MockCommon(Common::class)
interface Common {
    suspend fun foo(fuzz: Int, ozz: Any): Any

    fun bar(buzz: Int, bozz: Any): Any = bozz
}
