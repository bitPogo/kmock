/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.FunProxy
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.Proxy

private fun <T> traverseMockAndShare(
    mock: Proxy<*, T>,
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
    mock: Proxy<*, *>,
    handle: VerificationHandle
) {
    if (mock.verificationBuilderReference != null) {
        mock.verificationBuilderReference!!.add(handle)
    }
}

fun FunProxy<*, *>.hasBeenCalled(): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWith() }

fun FunProxy<*, *>.hasBeenCalledWith(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWith(*values) }

fun FunProxy<*, *>.hasBeenStrictlyCalledWith(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenStrictlyCalledWith(*values) }

fun FunProxy<*, *>.hasBeenCalledWithout(
    vararg values: Any?
): VerificationHandle = traverseMockAndShare(this) { hasBeenCalledWithout(*values) }

fun PropertyProxy<*>.wasGotten(): VerificationHandle = traverseMockAndShare(this) { wasGotten() }

fun PropertyProxy<*>.wasSet(): VerificationHandle = traverseMockAndShare(this) { wasSet() }

fun PropertyProxy<*>.wasSetTo(
    value: Any?
): VerificationHandle = traverseMockAndShare(this) { wasSetTo(value) }
