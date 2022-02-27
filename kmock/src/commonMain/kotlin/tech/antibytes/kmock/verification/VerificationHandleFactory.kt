/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.FunMockery
import tech.antibytes.kmock.KMockContract.Mockery
import tech.antibytes.kmock.KMockContract.PropertyMockery

private fun <T> traverseMockAndShare(
    mock: Mockery<*, T>,
    action: T.() -> Boolean
): VerificationHandle {
    val callIndices = mutableListOf<Int>()

    for (idx in 0 until mock.calls) {
        if (action(mock.getArgumentsForCall(idx))) {
            callIndices.add(idx)
        }
    }

    val handle = VerificationHandle(mock.id, callIndices)
    shareHandle(mock, handle)

    return handle
}

private fun shareHandle(
    mock: Mockery<*, *>,
    handle: VerificationHandle
) {
    if (mock.verificationBuilderReference != null) {
        mock.verificationBuilderReference!!.add(handle)
    }
}

fun FunMockery<*, *>.hasBeenCalled(): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWith() }

fun FunMockery<*, *>.hasBeenCalledWith(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWith(*values) }

fun FunMockery<*, *>.hasBeenStrictlyCalledWith(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenStrictlyCalledWith(*values) }

fun FunMockery<*, *>.hasBeenCalledWithout(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWithout(*values) }

fun PropertyMockery<*>.wasGotten(): VerificationHandle = traverseMockAndShare(this) { wasGotten() }

fun PropertyMockery<*>.wasSet(): VerificationHandle = traverseMockAndShare(this) { wasSet() }

fun PropertyMockery<*>.wasSetTo(
    value: Any?
): VerificationHandle = traverseMockAndShare(this) { wasSetTo(value) }
