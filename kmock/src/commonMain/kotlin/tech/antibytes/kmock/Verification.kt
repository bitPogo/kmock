/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.Companion.CALL_NOT_FOUND
import tech.antibytes.kmock.KMockContract.Companion.MISMATCHING_CALL_IDX
import tech.antibytes.kmock.KMockContract.Companion.MISMATCHING_FUNCTION
import tech.antibytes.kmock.KMockContract.Companion.NOTHING_TO_STRICTLY_VERIFY
import tech.antibytes.kmock.KMockContract.Companion.NOTHING_TO_VERIFY
import tech.antibytes.kmock.KMockContract.Companion.NOT_CALLED
import tech.antibytes.kmock.KMockContract.Companion.NO_MATCHING_CALL_IDX
import tech.antibytes.kmock.KMockContract.Companion.TOO_LESS_CALLS
import tech.antibytes.kmock.KMockContract.Companion.TOO_MANY_CALLS
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.VerificationHandle
import tech.antibytes.kmock.KMockContract.VerificationReferenceBuilder
import tech.antibytes.kmock.KMockContract.Verifier

private fun formatMessage(
    message: String,
    actual: Int,
    expected: Int
): String = formatMessage(message, actual.toString(), expected.toString())

private fun formatMessage(
    message: String,
    actual: Int,
    expected: String
): String = formatMessage(message, actual.toString(), expected)

private fun formatMessage(
    message: String,
    arg: String
): String = formatMessage(message, "", arg)

private fun formatMessage(message: String, actual: String, expected: String): String {
    return message
        .replace("\$1", expected)
        .replace("\$2", actual)
}

private fun determineAtLeastMessage(actual: Int, expected: Int): String {
    return if (actual == 0) {
        NOT_CALLED
    } else {
        formatMessage(TOO_LESS_CALLS, actual, expected)
    }
}

private infix fun VerificationHandle.mustBeAtLeast(value: Int) {
    if (this.callIndices.size < value) {
        val message = determineAtLeastMessage(this.callIndices.size, value)

        throw AssertionError(message)
    }
}

private infix fun VerificationHandle.mustBeAtMost(value: Int) {
    if (this.callIndices.size > value) {
        val message = formatMessage(TOO_MANY_CALLS, this.callIndices.size, value)

        throw AssertionError(message)
    }
}

/**
 * Verification function for a given assertion.
 * Note:<ul>
 *      <li>any boundary parameter will cause a failure in combination with wasCalledWithoutArguments if at least one argument matches.</li>
 * </ul>
 * @param exactly Int or null - the exact amount of calls. This parameter overrides atLeast and atMost. Use null to deactivate the criteria.
 * @param atLeast Int - the minimum amount of calls.
 * @param atMost Int or null - the maximum amount of calls. Use null to deactivate the criteria.
 * @param action ArgumentMatcher - Primary criteria to select VerificationHandles.
 * @throws AssertionError if it does not match the given criteria.
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
    scope: VerificationReferenceBuilder.() -> Any,
    references: List<Reference>
): List<VerificationHandle> {
    val container = VerificationChainBuilder()

    references.forEach { reference ->
        reference.mockery.verificationBuilderReference = container
    }

    scope(container)

    references.forEach { reference ->
        reference.mockery.verificationBuilderReference = null
    }

    return container.toList()
}

private fun guardStrictChain(references: List<Reference>, handles: List<VerificationHandle>) {
    if (handles.size != references.size) {
        val message = formatMessage(NOTHING_TO_STRICTLY_VERIFY, references.size, handles.size)

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
    if (reference.mockery.id != functionName) {
        val message = formatMessage(MISMATCHING_FUNCTION, reference.mockery.id, functionName)
        throw AssertionError(message)
    }

    if (call == null) {
        val message = formatMessage(
            NO_MATCHING_CALL_IDX,
            reference.mockery.id
        )

        throw AssertionError(message)
    }

    if (reference.callIndex != call) {
        val message = formatMessage(
            MISMATCHING_CALL_IDX,
            reference.callIndex,
            "$call of ${reference.mockery.id}"
        )

        throw AssertionError(message)
    }
}

fun Verifier.verifyStrictOrder(
    scope: VerificationReferenceBuilder.() -> Any,
) {
    val handleCalls: MutableMap<String, Int> = mutableMapOf()
    val handles = initChainVerification(scope, this.references)

    guardStrictChain(this.references, handles)

    this.references.forEachIndexed { idx, reference ->
        val functionName = handles[idx].id
        val lastCall = handleCalls[functionName] ?: -1
        val call = scanHandleStrictly(lastCall, handles[idx].callIndices)

        evaluateStrictReference(reference, functionName, call)

        handleCalls[functionName] = call ?: Int.MAX_VALUE
    }
}

private fun guardChain(references: List<Reference>, handles: List<VerificationHandle>) {
    if (handles.size > references.size) {
        val message = formatMessage(NOTHING_TO_VERIFY, handles.size, references.size)

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
        reference.mockery.id != functionName -> false
        reference.callIndex != call -> false
        else -> true
    }
}

private fun ensureAllHandlesAreDone(
    handles: List<VerificationHandle>,
    handleOffset: Int
) {
    if (handleOffset != handles.size) {
        val message = formatMessage(
            CALL_NOT_FOUND,
            handles[handleOffset].id
        )

        throw AssertionError(message)
    }
}

fun Verifier.verifyOrder(
    scope: VerificationReferenceBuilder.() -> Any
) {
    val handleCalls: MutableMap<String, Int> = mutableMapOf()
    val handles = initChainVerification(scope, this.references)
    var handleOffset = 0

    guardChain(this.references, handles)

    this.references.forEach { reference ->
        if (handleOffset == handles.size) {
            return@forEach
        }

        val functionName = handles[handleOffset].id
        val lastCall = handleCalls[functionName] ?: -1
        val call = scanHandle(lastCall, handles[handleOffset].callIndices)

        if (evaluateReference(reference, functionName, call)) {
            handleCalls[functionName] = call!!
            handleOffset += 1
        }
    }

    ensureAllHandlesAreDone(handles, handleOffset)
}
