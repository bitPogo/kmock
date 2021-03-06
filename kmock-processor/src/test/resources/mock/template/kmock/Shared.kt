/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.kmock

import tech.antibytes.kmock.KMock
import tech.antibytes.kmock.KMockExperimental

@OptIn(KMockExperimental::class)
@KMock(Shared::class)
interface Shared {
    fun foo(fuzz: Int, ozz: Any): Any

    fun bar(buzz: Int, bozz: Any): Any = bozz
}
