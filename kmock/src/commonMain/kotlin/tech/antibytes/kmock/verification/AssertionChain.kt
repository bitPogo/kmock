/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlinx.atomicfu.atomic
import tech.antibytes.kmock.KMockContract.AssertionChain
import tech.antibytes.kmock.KMockContract.Assertions
import tech.antibytes.kmock.KMockContract.CALL_NOT_FOUND
import tech.antibytes.kmock.KMockContract.ChainedAssertion
import tech.antibytes.kmock.KMockContract.NOT_PART_OF_CHAIN
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.STRICT_CALL_NOT_MATCH
import tech.antibytes.kmock.KMockContract.STRICT_MISSING_EXPECTATION
import tech.antibytes.kmock.util.format

internal class AssertionChain(
    private val references: List<Reference>,
    assertions: Assertions = Assertions
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

    private fun guardProxy(
        expected: Proxy<*, *>
    ) {
        val actual = references.getOrNull(invocation.value)
            ?: throw AssertionError(CALL_NOT_FOUND.format(expected.id))

        if (actual.proxy !== expected) {
            throw AssertionError(
                STRICT_CALL_NOT_MATCH.format(expected.id, actual.proxy.id)
            )
        }
    }

    override fun runAssertion(
        proxy: Proxy<*, *>,
        action: (callIndex: Int) -> Unit
    ) {
        guardProxy(proxy)

        val expectedCallIdxReference = references[invocation.value].callIndex

        action(expectedCallIdxReference)

        invocation.incrementAndGet()
    }

    @Throws(AssertionError::class)
    override fun ensureAllReferencesAreEvaluated() {
        if (invocation.value != references.size) {
            throw AssertionError(
                STRICT_MISSING_EXPECTATION.format(
                    references.size,
                    invocation.value,
                    references.joinToString(", ") { reference -> reference.proxy.id }
                )
            )
        }
    }
}
