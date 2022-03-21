/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.STRICT_CALL_NOT_FOUND
import tech.antibytes.kmock.KMockContract.STRICT_CALL_NOT_MATCH
import tech.antibytes.kmock.KMockContract.VerificationChain
import tech.antibytes.kmock.KMockContract.VerificationInsurance
import tech.antibytes.kmock.KMockContract.VerificationHandle
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.STRICT_CALL_IDX_NOT_FOUND
import tech.antibytes.kmock.KMockContract.STRICT_CALL_IDX_NOT_MATCH
import tech.antibytes.kmock.KMockContract.STRICT_MISSING_EXPECTATION
import tech.antibytes.kmock.util.format

internal class StrictVerificationChain(
    private val references: List<Reference>
) : VerificationChain, VerificationInsurance {
    private var callCounter = 0
    private val invokedProxies: MutableMap<String, Int> = mutableMapOf()

    override fun ensureVerificationOf(vararg proxies: Proxy<*, *>) {
        TODO()
    }

    private fun assertProxy(
        actual: Reference?,
        expected: VerificationHandle
    ) {
        when {
            actual == null -> throw AssertionError(
                STRICT_CALL_NOT_FOUND.format(expected.proxy.id)
            )
            actual.proxy !== expected.proxy -> throw AssertionError(
                STRICT_CALL_NOT_MATCH.format(expected.proxy.id, actual.proxy.id)
            )
        }
    }

    private fun assertInvocation(
        actual: Reference,
        expected: VerificationHandle
    ) {
        val expectedCallIdxReference = invokedProxies[actual.proxy.id] ?: 0
        val expectedCallIdx = expected.callIndices.getOrNull(expectedCallIdxReference)

        when {
            expectedCallIdx == null -> throw AssertionError(
                STRICT_CALL_IDX_NOT_FOUND.format(expectedCallIdxReference + 1, expected.proxy.id)
            )
            expectedCallIdx != actual.callIndex -> throw AssertionError(
                STRICT_CALL_IDX_NOT_MATCH.format(
                    expectedCallIdxReference + 1,
                    expected.proxy.id,
                    actual.callIndex + 1
                )
            )
        }

        invokedProxies[actual.proxy.id] = expectedCallIdxReference + 1
    }

    override fun propagate(expected: VerificationHandle) {
        val actual = references.getOrNull(callCounter)

        assertProxy(
            actual = actual,
            expected = expected
        )
        assertInvocation(
            actual = actual!!,
            expected = expected
        )

        callCounter++
    }

    override fun ensureAllReferencesAreEvaluated() {
        if (callCounter != references.size) {
            throw AssertionError(
                STRICT_MISSING_EXPECTATION.format(
                    references.size,
                    callCounter,
                    references.joinToString(", ") { reference -> reference.proxy.id }
                )
            )
        }
    }
}
