/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import co.touchlab.stately.collections.IsoMutableMap
import co.touchlab.stately.collections.sharedMutableMapOf
import kotlinx.atomicfu.atomic
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Expectation
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.STRICT_CALL_IDX_NOT_FOUND
import tech.antibytes.kmock.KMockContract.STRICT_CALL_IDX_NOT_MATCH
import tech.antibytes.kmock.KMockContract.STRICT_CALL_NOT_FOUND
import tech.antibytes.kmock.KMockContract.STRICT_CALL_NOT_MATCH
import tech.antibytes.kmock.KMockContract.STRICT_MISSING_EXPECTATION
import tech.antibytes.kmock.KMockContract.VerificationChain
import tech.antibytes.kmock.KMockContract.VerificationInsurance
import tech.antibytes.kmock.util.format

internal class StrictVerificationChain(
    private val references: List<Reference>
) : VerificationChain, VerificationInsurance {
    private val callCounter = atomic(0)
    private val invokedProxies: IsoMutableMap<String, Int> = sharedMutableMapOf()

    override fun ensureVerificationOf(vararg proxies: Proxy<*, *>) {
        proxies.forEach { proxy ->
            if (proxy.verificationChain != this) {
                throw IllegalStateException(KMockContract.NOT_PART_OF_CHAIN.format(proxy.id))
            }
        }
    }

    private fun assertProxy(
        actual: Reference?,
        expected: Expectation
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
        expected: Expectation
    ) {
        val expectedCallIdxReference = invokedProxies[actual.proxy.id] ?: 0
        val expectedCallIdx = expected.callIndices.firstOrNull { call -> call == expectedCallIdxReference }

        invokedProxies[actual.proxy.id] = expectedCallIdxReference + 1

        when {
            expectedCallIdx == null -> {
                throw AssertionError(
                    STRICT_CALL_IDX_NOT_FOUND.format(expectedCallIdxReference + 1, expected.proxy.id)
                )
            }
            expectedCallIdx != actual.callIndex -> throw AssertionError(
                STRICT_CALL_IDX_NOT_MATCH.format(
                    expectedCallIdxReference + 1,
                    expected.proxy.id,
                    actual.callIndex + 1
                )
            )
        }
    }

    @Throws(AssertionError::class)
    override fun propagate(expected: Expectation) {
        val actual = references.getOrNull(callCounter.value)

        try {
            assertProxy(
                actual = actual,
                expected = expected
            )
            assertInvocation(
                actual = actual!!,
                expected = expected
            )
        } catch (e: AssertionError) {
            throw e
        } finally {
            callCounter.incrementAndGet()
        }
    }

    @Throws(AssertionError::class)
    override fun ensureAllReferencesAreEvaluated() {
        if (callCounter.value != references.size) {
            throw AssertionError(
                STRICT_MISSING_EXPECTATION.format(
                    references.size,
                    callCounter.value,
                    references.joinToString(", ") { reference -> reference.proxy.id }
                )
            )
        }
    }
}
