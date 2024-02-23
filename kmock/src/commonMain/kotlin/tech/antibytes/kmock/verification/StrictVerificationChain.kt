/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import tech.antibytes.kmock.KMockContract.AssertionChain
import tech.antibytes.kmock.KMockContract.Assertions
import tech.antibytes.kmock.KMockContract.CALL_NOT_FOUND
import tech.antibytes.kmock.KMockContract.ChainedAssertion
import tech.antibytes.kmock.KMockContract.NOT_PART_OF_CHAIN
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.util.format

internal class StrictVerificationChain(
    private val references: List<Reference>,
    assertions: Assertions = Assertions,
) : AssertionChain, ChainedAssertion, BaseAssertionContext(assertions) {
    private val invocation = atomic(0)

    private fun getProxyIdSet(): Set<Proxy<*, *>> {
        val set: MutableSet<Proxy<*, *>> = mutableSetOf()

        references.forEach { reference -> set.add(reference.proxy) }

        return set
    }

    override fun ensureVerificationOf(vararg proxies: Proxy<*, *>) {
        val actual = getProxyIdSet()

        proxies.forEach { proxy ->
            if (proxy !in actual) {
                throw IllegalStateException(NOT_PART_OF_CHAIN.format(proxy.id))
            }
        }
    }

    private fun findProxy(expected: Proxy<*, *>): Int? {
        for (idx in invocation.value until references.size) {
            if (references[idx].proxy === expected) {
                return idx
            }
        }

        return null
    }

    override fun runAssertion(
        proxy: Proxy<*, *>,
        action: (callIndex: Int) -> Unit,
    ) {
        val actual = findProxy(proxy)
            ?: throw AssertionError(CALL_NOT_FOUND.format(proxy.id))

        val expectedCallIdxReference = references[actual].callIndex

        action(expectedCallIdxReference)

        invocation.update { actual + 1 }
    }

    @Throws(AssertionError::class)
    override fun ensureAllReferencesAreEvaluated() = Unit
}
