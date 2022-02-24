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

private fun <T> traverseMock(
    mock: Mockery<*, T>,
    action: T.() -> Boolean
): VerificationHandle {
    val callIndices = mutableListOf<Int>()

    for (idx in 0 until mock.calls) {
        if (action(mock.getArgumentsForCall(idx))) {
            callIndices.add(idx)
        }
    }

    return VerificationHandle(mock.id, callIndices)
}

fun FunMockery<*, *>.hadBeenCalledWith(
    vararg values: Any?
): VerificationHandle = traverseMock(this) {
    hadBeenCalledWith(*values)
}

fun FunMockery<*, *>.hadBeenStrictlyCalledWith(
    vararg values: Any?
): VerificationHandle = traverseMock(this) { hadBeenStrictlyCalledWith(*values) }

fun FunMockery<*, *>.hadBeenCalledWithout(
    vararg values: Any?
): VerificationHandle = traverseMock(this) { hadBeenCalledWithout(*values) }

fun PropertyMockery<*>.wasGotten(): VerificationHandle = traverseMock(this) { wasGotten() }

fun PropertyMockery<*>.wasSet(): VerificationHandle = traverseMock(this) { wasSet() }

fun PropertyMockery<*>.wasSetTo(
    value: Any?
): VerificationHandle = traverseMock(this) { wasSetTo(value) }
