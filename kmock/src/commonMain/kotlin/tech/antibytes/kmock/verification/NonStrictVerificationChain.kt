/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import co.touchlab.stately.collections.IsoMutableMap
import co.touchlab.stately.collections.sharedMutableMapOf
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Expectation
import tech.antibytes.kmock.KMockContract.NON_STRICT_CALL_IDX_NOT_FOUND
import tech.antibytes.kmock.KMockContract.NOT_PART_OF_CHAIN
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.VerificationChain
import tech.antibytes.kmock.KMockContract.VerificationInsurance
import tech.antibytes.kmock.util.format

internal class NonStrictVerificationChain(
    private val references: List<Reference>
) : VerificationChain, VerificationInsurance {
    private val callCounter = atomic(0)
    private val invokedProxies: IsoMutableMap<String, Int> = sharedMutableMapOf()

    override fun ensureVerificationOf(vararg proxies: Proxy<*, *>) {
        proxies.forEach { proxy ->
            if (proxy.verificationChain != this) {
                throw IllegalStateException(NOT_PART_OF_CHAIN.format(proxy.id))
            }
        }
    }

    private fun findProxy(expected: Proxy<*, *>): Int? {
        for (idx in callCounter.value until references.size) {
            if (references[idx].proxy === expected) {
                return idx
            }
        }

        return null
    }

    private fun assertProxy(
        expected: Expectation,
        actual: Int?
    ) {
        if (actual == null) {
            throw AssertionError(
                KMockContract.NON_STRICT_CALL_NOT_FOUND.format(expected.proxy.id)
            )
        }
    }

    private fun assertInvocation(
        expected: Expectation,
        expectedCallIdx: Int?,
    ) {
        if (expectedCallIdx == null) {
            throw AssertionError(NON_STRICT_CALL_IDX_NOT_FOUND.format(expected.proxy.id))
        }
    }

    private fun findInvocation(
        expected: Expectation,
        actual: Reference,
    ): Int? {
        val expectedCallIdxReference = invokedProxies[actual.proxy.id] ?: 0
        val expectedCallIdx = expected.callIndices.firstOrNull { call -> call >= expectedCallIdxReference }

        invokedProxies[actual.proxy.id] = expectedCallIdxReference + 1

        return expectedCallIdx
    }

    @Throws(AssertionError::class)
    override fun propagate(expected: Expectation) {
        while (callCounter.value <= references.size) {
            val actualReferenceIdx = findProxy(expected.proxy)

            try {
                assertProxy(
                    actual = actualReferenceIdx,
                    expected = expected,
                )
            } catch (e: AssertionError) {
                throw e
            }

            val actualInvocation = findInvocation(
                expected = expected,
                actual = references[actualReferenceIdx!!]
            )

            try {
                assertInvocation(
                    expected = expected,
                    expectedCallIdx = actualInvocation,
                )
            } catch (e: AssertionError) {
                throw e
            } finally {
                callCounter.update { actualReferenceIdx + 1 }
            }

            if (actualInvocation == references[actualReferenceIdx].callIndex) {
                break
            }
        }
    }

    @Throws(AssertionError::class)
    override fun ensureAllReferencesAreEvaluated() = Unit
}
