/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName")

package tech.antibytes.kmock.verification.contraints

import tech.antibytes.kmock.KMockContract

class isNot(
    private val illegal: Any? = null
) : KMockContract.MatcherConstraint {
    override fun matches(actual: Any?): Boolean {
        return !when {
            illegal is Array<*> && actual is Array<*> -> illegal.contentDeepEquals(actual)
            illegal is ByteArray && actual is ByteArray -> illegal.contentEquals(actual)
            illegal is ShortArray && actual is ShortArray -> illegal.contentEquals(actual)
            illegal is IntArray && actual is IntArray -> illegal.contentEquals(actual)
            illegal is LongArray && actual is LongArray -> illegal.contentEquals(actual)
            illegal is FloatArray && actual is FloatArray -> illegal.contentEquals(actual)
            illegal is DoubleArray && actual is DoubleArray -> illegal.contentEquals(actual)
            illegal is CharArray && actual is CharArray -> illegal.contentEquals(actual)
            illegal is BooleanArray && actual is BooleanArray -> illegal.contentEquals(actual)

            illegal is UByteArray && actual is UByteArray -> illegal.contentEquals(actual)
            illegal is UShortArray && actual is UShortArray -> illegal.contentEquals(actual)
            illegal is UIntArray && actual is UIntArray -> illegal.contentEquals(actual)
            illegal is ULongArray && actual is ULongArray -> illegal.contentEquals(actual)

            else -> illegal == actual
        }
    }
}
