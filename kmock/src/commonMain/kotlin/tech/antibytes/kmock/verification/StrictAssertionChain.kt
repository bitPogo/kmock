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
import tech.antibytes.kmock.KMockContract.AssertionChain
import tech.antibytes.kmock.KMockContract.Assertion
import tech.antibytes.kmock.KMockContract.VOID_FUNCTION
import tech.antibytes.kmock.KMockContract.MISMATCH
import tech.antibytes.kmock.KMockContract.HAD_BEEN_CALLED_NO_MATCHER
import tech.antibytes.kmock.KMockContract.ILLEGAL_VALUE
import tech.antibytes.kmock.KMockContract.MISMATCHING_SIZE
import tech.antibytes.kmock.KMockContract.NON_VOID_FUNCTION
import tech.antibytes.kmock.KMockContract.NOT_GET
import tech.antibytes.kmock.KMockContract.NOT_SET
import tech.antibytes.kmock.util.format

internal class StrictAssertionChain(
    private val references: List<Reference>
) : AssertionChain, Assertion {
    private val invocation = atomic(0)
    private val invokedProxies: IsoMutableMap<String, Int> = sharedMutableMapOf()

    override fun ensureVerificationOf(vararg proxies: Proxy<*, *>) {
        proxies.forEach { proxy ->
            if (proxy.assertionChain != this) {
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
        val actual = references.getOrNull(invocation.value)

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
            invocation.incrementAndGet()
        }
    }

    private fun guardProxy(
        expected: Proxy<*, *>
    ) {
        val actual = references.getOrNull(invocation.value)
            ?: throw AssertionError(STRICT_CALL_NOT_FOUND.format(expected.id))

        if (actual.proxy !== expected) {
            throw AssertionError(
                STRICT_CALL_NOT_MATCH.format(expected.id, actual.proxy.id)
            )
        }
    }

    private fun runAssertion(
        expected: Proxy<*, *>,
        action: (callIndex: Int) -> Unit
    ) {
        guardProxy(expected)

        val expectedCallIdxReference = references[invocation.value].callIndex

        action(expectedCallIdxReference)

        invocation.incrementAndGet()
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenCalled() = runAssertion(this) { /* Do nothing */ }

    override fun KMockContract.FunProxy<*, *>.hasBeenCalledWithVoid() {
        runAssertion(this) { callIndex ->
            val actual = this.getArgumentsForCall(callIndex).hasBeenCalledWithVoid()

            if (!actual) {
                throw AssertionError(VOID_FUNCTION)
            }
        }
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenCalledWith(vararg arguments: Any?) {
        runAssertion(this) { callIndex ->
            val actual = this.getArgumentsForCall(callIndex)

            val valid = actual.hasBeenCalledWith(*arguments) { _, idx ->
                throw AssertionError(
                    MISMATCH.format(
                        arguments[idx],
                        actual[idx]
                    )
                )
            }

            if (!valid) {
                throw AssertionError(
                    HAD_BEEN_CALLED_NO_MATCHER.format(arguments.first())
                )
            }
        }
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenStrictlyCalledWith(vararg arguments: Any?) {
        runAssertion(this) { callIndex ->
            val actual = this.getArgumentsForCall(callIndex)
            val valid = actual.hasBeenStrictlyCalledWith(*arguments) { actualArgument, idx ->
                throw AssertionError(
                    MISMATCH.format(
                        arguments[idx],
                        actualArgument
                    )
                )
            }

            if (!valid) {
                throw AssertionError(
                    MISMATCHING_SIZE.format(
                        arguments.size,
                        actual.size
                    )
                )
            }
        }
    }

    override fun KMockContract.FunProxy<*, *>.hasBeenCalledWithout(vararg illegal: Any?) {
        runAssertion(this) { callIndex ->
            val actual = this.getArgumentsForCall(callIndex)
            val valid = actual.hasBeenCalledWithout(*illegal) { actualArgument, idx ->
                throw AssertionError(
                    ILLEGAL_VALUE.format(actualArgument)
                )
            }

            if (!valid) {
                throw AssertionError(NON_VOID_FUNCTION)
            }
        }
    }

    override fun KMockContract.PropertyProxy<*>.wasGotten() {
        runAssertion(this) { callIndex ->
            val actual = this.getArgumentsForCall(callIndex)

            if (!actual.wasGotten()) {
                throw AssertionError(NOT_GET)
            }
        }
    }

    override fun KMockContract.PropertyProxy<*>.wasSet() {
        runAssertion(this) { callIndex ->
            val actual = this.getArgumentsForCall(callIndex)

            if (!actual.wasSet()) {
                throw AssertionError(NOT_SET)
            }
        }
    }

    override fun KMockContract.PropertyProxy<*>.wasSetTo(value: Any?) {
        runAssertion(this) { callIndex ->
            val actual = this.getArgumentsForCall(callIndex)
            val valid = actual.wasSetTo(value)

            if (!valid) {
                if (actual is KMockContract.GetOrSet.Get) {
                    throw AssertionError(NOT_SET)
                } else {
                    throw AssertionError(
                        MISMATCH.format(value, actual.value)
                    )
                }
            }
        }
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
