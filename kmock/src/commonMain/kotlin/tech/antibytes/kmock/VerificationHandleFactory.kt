/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.FunMockery
import tech.antibytes.kmock.KMockContract.Mockery

private fun traverseFunMock(
    mock: FunMockery<*>,
    action: Array<out Any?>?.() -> Boolean
): KMockContract.VerificationHandle {
    val callIndices = mutableListOf<Int>()

    for (idx in 0 until mock.calls) {
        if (action(mock.getArgumentsForCall(idx))) {
            callIndices.add(idx)
        }
    }

    return VerificationHandle(mock.id, callIndices)
}

private fun applyMatcher(
    mock: Mockery<*>,
    action: Array<out Any?>?.() -> Boolean
): KMockContract.VerificationHandle {
    return if (mock is FunMockery<*>) {
        traverseFunMock(mock, action)
    } else {
        TODO()
    }
}

fun FunMockery<*>.withArguments(
    vararg values: Any?
): KMockContract.VerificationHandle = applyMatcher(this) { withArguments(*values) }

fun FunMockery<*>.withSameArguments(
    vararg values: Any?
): KMockContract.VerificationHandle = applyMatcher(this) { withSameArguments(*values) }

fun FunMockery<*>.withoutArguments(
    vararg values: Any?
): KMockContract.VerificationHandle = applyMatcher(this) { withoutArguments(*values) }
