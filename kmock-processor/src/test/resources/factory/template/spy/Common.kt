/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package factory.template.spy

import mock.template.async.Common
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.Relaxer

@MockCommon(Common::class)
interface Common {
    val buzz: String

    fun foo(payload: Any): String
    suspend fun bar(payload: Any): String
    fun buzz()
}

