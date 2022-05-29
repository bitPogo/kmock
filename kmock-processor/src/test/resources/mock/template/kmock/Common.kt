/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.kmock

import tech.antibytes.kmock.KMock
import tech.antibytes.kmock.KMockExperimental

@OptIn(KMockExperimental::class)
@KMock(Common::class)
interface Common {
    suspend fun foo(fuzz: Int, ozz: Any): Any

    suspend fun bar(buzz: Int, bozz: Any): Any = bozz

    suspend fun ozz(vararg buzz: Int): Any

    suspend fun izz(vararg buzz: Any): Any
}
