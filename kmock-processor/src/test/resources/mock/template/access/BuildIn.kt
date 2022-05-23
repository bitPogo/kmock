/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.access

import tech.antibytes.kmock.Mock

@Mock(BuildIn::class)
interface BuildIn {
    fun foo(fuzz: Int, ozz: Any): Any
}
