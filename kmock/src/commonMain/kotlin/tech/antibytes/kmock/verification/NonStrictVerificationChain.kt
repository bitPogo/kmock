/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.NON_STRICT_CALL_IDX_NOT_FOUND
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.VerificationHandle
import tech.antibytes.kmock.KMockContract.VerificationChain
import tech.antibytes.kmock.KMockContract.VerificationInsurance
import tech.antibytes.kmock.util.format

internal class NonStrictVerificationChain(
    private val references: List<Reference>
) : VerificationChain, VerificationInsurance {
    private var callCounter = 0
    private val invokedProxies: MutableMap<String, Int> = mutableMapOf()

    override fun ensureVerificationOf(vararg proxies: Proxy<*, *>) {
        TODO("Not yet implemented")
    }

    private fun findProxy(expected: Proxy<*, *>): Int? {
        for (idx in callCounter until references.size) {
            if (references[idx].proxy === expected) {
                return idx
            }
        }

        return null
    }

    private fun assertProxy(
        expected: VerificationHandle,
        actual: Int?
    ) {
        if (actual == null) {
            throw AssertionError(
                KMockContract.NON_STRICT_CALL_NOT_FOUND.format(expected.proxy.id)
            )
        }
    }

    private fun assertInvocation(
        actual: Reference,
        expected: VerificationHandle,
        expectedCallIdx: Int?,
        expectedCallIdxReference: Int
    ) {
        if (expectedCallIdx == null) {
            throw AssertionError(NON_STRICT_CALL_IDX_NOT_FOUND.format(expected.proxy.id))
        }

        invokedProxies[actual.proxy.id] = expectedCallIdxReference + 1
    }

    private fun hasExpectedInvocation(
        expected: VerificationHandle,
        actual: Reference,
    ): Boolean {
        val expectedCallIdxReference = invokedProxies[actual.proxy.id] ?: 0
        val expectedCallIdx = expected.callIndices.getOrNull(expectedCallIdxReference)

        assertInvocation(
            actual = actual,
            expected = expected,
            expectedCallIdx = expectedCallIdx,
            expectedCallIdxReference = expectedCallIdxReference
        )


        return expectedCallIdx == actual.callIndex
    }

    override fun propagate(expected: VerificationHandle) {

        var referenceIdx: Int

        do {
            val currentIdx = findProxy(expected.proxy)

            assertProxy(
                actual = currentIdx,
                expected = expected,
            )

            referenceIdx = currentIdx!!
            callCounter++
        } while (!hasExpectedInvocation(expected = expected, actual = references[referenceIdx]))
    }

    override fun ensureAllReferencesAreEvaluated() = Unit
}
