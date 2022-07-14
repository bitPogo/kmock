/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.buildIn

import tech.antibytes.kmock.Mock

@Mock(NoBuildIns::class)
interface NoBuildIns {
    fun equals(other: Int)
    fun toString(radix: Int): String
    fun hashCode(base: Any): Int
}
