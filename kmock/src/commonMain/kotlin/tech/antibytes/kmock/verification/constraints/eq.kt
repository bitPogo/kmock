/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName", "ktlint:standard:filename")

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract.ArgumentConstraint

@OptIn(ExperimentalUnsignedTypes::class)
/**
 * VerificationConstraint matches if the actual and expected value are equal.
 * @param expected the expected value which should match.
 * @property expected the expected value which should match.
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
public class eq(
    private val expected: Any?,
) : ArgumentConstraint {
    override fun matches(actual: Any?): Boolean {
        return when {
            expected is Array<*> && actual is Array<*> -> expected.contentDeepEquals(actual)
            expected is ByteArray && actual is ByteArray -> expected.contentEquals(actual)
            expected is ShortArray && actual is ShortArray -> expected.contentEquals(actual)
            expected is IntArray && actual is IntArray -> expected.contentEquals(actual)
            expected is LongArray && actual is LongArray -> expected.contentEquals(actual)
            expected is FloatArray && actual is FloatArray -> expected.contentEquals(actual)
            expected is DoubleArray && actual is DoubleArray -> expected.contentEquals(actual)
            expected is CharArray && actual is CharArray -> expected.contentEquals(actual)
            expected is BooleanArray && actual is BooleanArray -> expected.contentEquals(actual)

            expected is UByteArray && actual is UByteArray -> expected.contentEquals(actual)
            expected is UShortArray && actual is UShortArray -> expected.contentEquals(actual)
            expected is UIntArray && actual is UIntArray -> expected.contentEquals(actual)
            expected is ULongArray && actual is ULongArray -> expected.contentEquals(actual)

            else -> expected == actual
        }
    }

    override fun toString(): String = expected.toString()
}
