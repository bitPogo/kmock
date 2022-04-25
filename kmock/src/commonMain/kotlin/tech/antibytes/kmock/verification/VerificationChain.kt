/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AssertionChain
import tech.antibytes.kmock.KMockContract.Assertions
import tech.antibytes.kmock.KMockContract.CALL_NOT_FOUND
import tech.antibytes.kmock.KMockContract.NOT_PART_OF_CHAIN
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.util.format

internal class VerificationChain(
    private val references: List<Reference>,
    private val assertions: Assertions = Assertions,
) : AssertionChain, KMockContract.Assert {
    private val invocation = atomic(0)

    private fun getProxyIdSet(): Set<String> {
        val set: MutableSet<String> = mutableSetOf()

        references.forEach { reference -> set.add(reference.proxy.id) }

        return set
    }

    override fun ensureVerificationOf(vararg proxies: Proxy<*, *>) {
        val actual = getProxyIdSet()

        proxies.forEach { proxy ->
            if (proxy.id !in actual) {
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

    private fun runAssertion(
        expected: Proxy<*, *>,
        action: (callIndex: Int) -> Unit
    ) {
        val actual = findProxy(expected)
            ?: throw AssertionError(CALL_NOT_FOUND.format(expected.id))

        val expectedCallIdxReference = references[actual].callIndex

        action(expectedCallIdxReference)

        invocation.update { actual + 1 }
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenCalled() {
        runAssertion(this) { callIndex ->
            assertions.hasBeenCalledAtIndex(this, callIndex)
        }
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenCalledWithVoid() {
        runAssertion(this) { callIndex ->
            assertions.hasBeenCalledWithVoidAtIndex(this, callIndex)
        }
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenCalledWith(vararg arguments: Any?) {
        runAssertion(this) { callIndex ->
            assertions.hasBeenCalledWithAtIndex(
                proxy = this,
                callIndex = callIndex,
                arguments = arguments
            )
        }
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenStrictlyCalledWith(vararg arguments: Any?) {
        runAssertion(this) { callIndex ->
            assertions.hasBeenStrictlyCalledWithAtIndex(
                proxy = this,
                callIndex = callIndex,
                arguments = arguments
            )
        }
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenCalledWithout(vararg illegal: Any?) {
        runAssertion(this) { callIndex ->
            assertions.hasBeenCalledWithoutAtIndex(
                proxy = this,
                callIndex = callIndex,
                illegal = illegal
            )
        }
    }

    override fun KMockContract.PropertyProxy<*>.wasGotten() {
        runAssertion(this) { callIndex ->
            assertions.wasGottenAtIndex(proxy = this, callIndex = callIndex)
        }
    }

    override fun KMockContract.PropertyProxy<*>.wasSet() {
        runAssertion(this) { callIndex ->
            assertions.wasSetAtIndex(proxy = this, callIndex = callIndex)
        }
    }

    override fun KMockContract.PropertyProxy<*>.wasSetTo(value: Any?) {
        runAssertion(this) { callIndex ->
            assertions.wasSetToAtIndex(
                proxy = this,
                callIndex = callIndex,
                value = value
            )
        }
    }

    @Throws(AssertionError::class)
    override fun ensureAllReferencesAreEvaluated() = Unit
}
