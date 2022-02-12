/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.FunMockery
import tech.antibytes.kmock.KMockContract.Mockery
import tech.antibytes.kmock.KMockContract.PropMockery
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

fun FunMockery<*, *>.wasCalledWithArguments(
    vararg values: Any?
): VerificationHandle = traverseMock(this) {
    wasCalledWithArguments(*values)
}

fun FunMockery<*, *>.wasCalledWithArgumentsStrict(
    vararg values: Any?
): VerificationHandle = traverseMock(this) { wasCalledWithArgumentsStrict(*values) }

fun FunMockery<*, *>.wasCalledWithoutArguments(
    vararg values: Any?
): VerificationHandle = traverseMock(this) { wasCalledWithoutArguments(*values) }

fun PropMockery<*>.wasGotten(): VerificationHandle = traverseMock(this) { wasGotten() }

fun PropMockery<*>.wasSet(): VerificationHandle = traverseMock(this) { wasSet() }

fun PropMockery<*>.wasSetTo(
    value: Any?
): VerificationHandle = traverseMock(this) { wasSetTo(value) }
