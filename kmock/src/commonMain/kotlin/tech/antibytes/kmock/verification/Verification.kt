/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.CALL_NOT_FOUND
import tech.antibytes.kmock.KMockContract.MISMATCHING_CALL_IDX
import tech.antibytes.kmock.KMockContract.MISMATCHING_FUNCTION
import tech.antibytes.kmock.KMockContract.NOTHING_TO_STRICTLY_VERIFY
import tech.antibytes.kmock.KMockContract.NOTHING_TO_VERIFY
import tech.antibytes.kmock.KMockContract.NOT_CALLED
import tech.antibytes.kmock.KMockContract.NO_MATCHING_CALL_IDX
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.TOO_LESS_CALLS
import tech.antibytes.kmock.KMockContract.TOO_MANY_CALLS
import tech.antibytes.kmock.KMockContract.VerificationHandle
import tech.antibytes.kmock.KMockContract.VerificationInsurance
import tech.antibytes.kmock.KMockContract.Verifier
import tech.antibytes.kmock.util.format

private fun determineAtLeastMessage(actual: Int, expected: Int): String {
    return if (actual == 0) {
        NOT_CALLED
    } else {
        TOO_LESS_CALLS.format(expected, actual)
    }
}

private infix fun VerificationHandle.mustBeAtLeast(value: Int) {
    if (this.callIndices.size < value) {
        val message = determineAtLeastMessage(
            expected = value,
            actual = this.callIndices.size
        )

        throw AssertionError(message)
    }
}

private infix fun VerificationHandle.mustBeAtMost(value: Int) {
    if (this.callIndices.size > value) {
        val message = TOO_MANY_CALLS.format(value, this.callIndices.size)

        throw AssertionError(message)
    }
}

/**
 * Verifies the last produced VerificationHandle.
 * @param exactly optional parameter which indicates the exact amount of calls.
 * This parameter overrides atLeast and atMost.
 * @param atLeast optional parameter which indicates the minimum amount of calls.
 * @param atMost optional parameter which indicates the maximum amount of calls.
 * @param action producer of VerificationHandle.
 * @throws AssertionError if given criteria are not met.
 * @see hasBeenCalled
 * @see hasBeenCalledWith
 * @see hasBeenStrictlyCalledWith
 * @see hasBeenCalledWithout
 * @see wasGotten
 * @see wasSet
 * @see wasSetTo
 * @author Matthias Geisler
 */
fun verify(
    exactly: Int? = null,
    atLeast: Int = 1,
    atMost: Int? = null,
    action: () -> VerificationHandle
) {
    val handle = action()
    val minimum = exactly ?: atLeast
    val maximum = exactly ?: atMost

    handle mustBeAtLeast minimum

    if (maximum != null) {
        handle mustBeAtMost maximum
    }
}

private fun initChainVerification(
    scope: VerificationInsurance.() -> Any,
    references: List<Reference>
): List<VerificationHandle> {
    val container = VerificationChainBuilder()

    references.forEach { reference ->
        reference.proxy.verificationBuilderReference = container
    }

    scope(container)

    references.forEach { reference ->
        reference.proxy.verificationBuilderReference = null
    }

    return container.toList()
}

private fun guardStrictChain(references: List<Reference>, handles: List<VerificationHandle>) {
    if (handles.size != references.size) {
        val message = NOTHING_TO_STRICTLY_VERIFY.format(handles.size, references.size)

        throw AssertionError(message)
    }
}

private fun scanHandleStrictly(
    latestCall: Int,
    applicableIdx: List<Int>
): Int? {
    val next = latestCall + 1
    return applicableIdx.firstOrNull { value -> value == next }
}

private fun evaluateStrictReference(
    reference: Reference,
    functionName: String,
    call: Int?
) {
    if (reference.proxy.id != functionName) {
        val message = MISMATCHING_FUNCTION.format(functionName, reference.proxy.id)
        throw AssertionError(message)
    }

    if (call == null) {
        val message = NO_MATCHING_CALL_IDX.format(reference.proxy.id)

        throw AssertionError(message)
    }

    if (reference.callIndex != call) {
        val message = MISMATCHING_CALL_IDX.format(
            call,
            reference.proxy.id,
            reference.callIndex,
        )

        throw AssertionError(message)
    }
}

/**
 * Verifies a chain VerificationHandles. Each Handle must be in strict order of the referenced Proxy invocation
 * and all invocations must be present.
 * @param scope chain of VerificationHandle factory.
 * @throws AssertionError if given criteria are not met.
 * @see hasBeenCalled
 * @see hasBeenCalledWith
 * @see hasBeenStrictlyCalledWith
 * @see hasBeenCalledWithout
 * @see wasGotten
 * @see wasSet
 * @see wasSetTo
 * @author Matthias Geisler
 */
fun Verifier.verifyStrictOrder(
    scope: VerificationInsurance.() -> Any,
) {
    val handleCalls: MutableMap<String, Int> = mutableMapOf()
    val handles = initChainVerification(scope, this.references)

    guardStrictChain(this.references, handles)

    this.references.forEachIndexed { idx, reference ->
        val functionName = handles[idx].proxy.id
        val lastCall = handleCalls[functionName] ?: -1
        val call = scanHandleStrictly(lastCall, handles[idx].callIndices)

        evaluateStrictReference(reference, functionName, call)

        handleCalls[functionName] = call ?: Int.MAX_VALUE
    }
}

private fun guardChain(references: List<Reference>, handles: List<VerificationHandle>) {
    if (handles.size > references.size) {
        val message = NOTHING_TO_VERIFY.format(references.size, handles.size)

        throw AssertionError(message)
    }
}

private fun scanHandle(
    latestCall: Int,
    applicableIdx: List<Int>
): Int? = applicableIdx.firstOrNull { value -> value > latestCall }

private fun evaluateReference(
    reference: Reference,
    functionName: String,
    call: Int?
): Boolean {
    return when {
        call == null -> false
        reference.proxy.id != functionName -> false
        reference.callIndex != call -> false
        else -> true
    }
}

private fun ensureAllHandlesAreDone(
    handles: List<VerificationHandle>,
    handleOffset: Int
) {
    if (handleOffset != handles.size) {
        val message = CALL_NOT_FOUND.format(handles[handleOffset].proxy.id)

        throw AssertionError(message)
    }
}

/**
 * Verifies a chain VerificationHandles. Each Handle must be in order but gaps are allowed.
 * Also the chain does not need to be exhaustive.
 * @param scope chain of VerificationHandle factory.
 * @throws AssertionError if given criteria are not met.
 * @see hasBeenCalled
 * @see hasBeenCalledWith
 * @see hasBeenStrictlyCalledWith
 * @see hasBeenCalledWithout
 * @see wasGotten
 * @see wasSet
 * @see wasSetTo
 * @author Matthias Geisler
 */
fun Verifier.verifyOrder(
    scope: VerificationInsurance.() -> Any
) {
    val handleCalls: MutableMap<String, Int> = mutableMapOf()
    val handles = initChainVerification(scope, this.references)
    var handleOffset = 0

    guardChain(this.references, handles)

    this.references.forEach { reference ->
        if (handleOffset == handles.size) {
            return@forEach
        }

        val functionName = handles[handleOffset].proxy.id
        val lastCall = handleCalls[functionName] ?: -1
        val call = scanHandle(lastCall, handles[handleOffset].callIndices)

        if (evaluateReference(reference, functionName, call)) {
            handleCalls[functionName] = call!!
            handleOffset += 1
        }
    }

    ensureAllHandlesAreDone(handles, handleOffset)
}
