/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.FunMockery
import tech.antibytes.kmock.KMockContract.Mockery
import tech.antibytes.kmock.KMockContract.PropertyMockery
import tech.antibytes.kmock.KMockContract.VerificationHandle

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

fun FunMockery<*, *>.hadBeenCalledWith(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hadBeenCalledWith(*values) }

fun FunMockery<*, *>.hadBeenStrictlyCalledWith(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hadBeenStrictlyCalledWith(*values) }

fun FunMockery<*, *>.hadBeenCalledWithout(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hadBeenCalledWithout(*values) }

fun PropertyMockery<*>.wasGotten(): VerificationHandle = traverseMockAndShare(this) { wasGotten() }

fun PropertyMockery<*>.wasSet(): VerificationHandle = traverseMockAndShare(this) { wasSet() }

fun PropertyMockery<*>.wasSetTo(
    value: Any?
): VerificationHandle = traverseMockAndShare(this) { wasSetTo(value) }
