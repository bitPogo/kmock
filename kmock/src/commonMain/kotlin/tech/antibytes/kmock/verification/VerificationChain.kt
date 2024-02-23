/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AssertionChain
import tech.antibytes.kmock.KMockContract.CALL_NOT_FOUND
import tech.antibytes.kmock.KMockContract.CALL_WITH_ARGS_NOT_FOUND
import tech.antibytes.kmock.KMockContract.ChainedAssertion
import tech.antibytes.kmock.KMockContract.FunProxy
import tech.antibytes.kmock.KMockContract.ILLEGAL_VALUE
import tech.antibytes.kmock.KMockContract.MISSING_INVOCATION
import tech.antibytes.kmock.KMockContract.NOT_GET
import tech.antibytes.kmock.KMockContract.NOT_PART_OF_CHAIN
import tech.antibytes.kmock.KMockContract.NOT_SET
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.VOID_FUNCTION
import tech.antibytes.kmock.util.format

internal class VerificationChain(
    private val references: List<KMockContract.Reference>,
) : AssertionChain, ChainedAssertion {
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

    private fun runAssertion(
        expected: Proxy<*, *>,
        action: (callIndex: Int) -> Unit,
    ) {
        val actual = findProxy(expected)
            ?: throw AssertionError(CALL_NOT_FOUND.format(expected.id))

        val expectedCallIdxReference = references[actual].callIndex

        invocation.update { actual + 1 }

        action(expectedCallIdxReference)
    }

    private fun <T> Proxy<*, T>.guardActualRetrieval(callIndex: Int): T {
        return try {
            this.getArgumentsForCall(callIndex)
        } catch (_: Throwable) {
            throw AssertionError(MISSING_INVOCATION.format(callIndex + 1, this.id))
        }
    }

    private fun mapValues(vararg values: Any?): List<Any?> = values.map { it }

    override fun FunProxy<*, *>.hasBeenCalled() = runAssertion(this) { callIdx -> guardActualRetrieval(callIdx) }

    override fun FunProxy<*, *>.hasBeenCalledWithVoid() {
        runAssertion(this) { callIndex ->
            val valid = this.guardActualRetrieval(callIndex).hasBeenCalledWithVoid()

            if (!valid) {
                try {
                    this.hasBeenCalledWithVoid()
                } catch (e: AssertionError) {
                    throw AssertionError(VOID_FUNCTION.format(this.id))
                }
            }
        }
    }

    override fun FunProxy<*, *>.hasBeenCalledWith(vararg arguments: Any?) {
        runAssertion(this) { callIndex ->
            val valid = this.guardActualRetrieval(callIndex).hasBeenCalledWith(*arguments)

            if (!valid) {
                try {
                    this.hasBeenCalledWith(*arguments)
                } catch (e: AssertionError) {
                    throw AssertionError(
                        CALL_WITH_ARGS_NOT_FOUND.format(
                            this.id,
                            mapValues(*arguments),
                        ),
                    )
                }
            }
        }
    }

    override fun FunProxy<*, *>.hasBeenStrictlyCalledWith(vararg arguments: Any?) {
        runAssertion(this) { callIndex ->
            val valid = this.guardActualRetrieval(callIndex).hasBeenStrictlyCalledWith(*arguments)

            if (!valid) {
                try {
                    this.hasBeenStrictlyCalledWith(*arguments)
                } catch (e: AssertionError) {
                    throw AssertionError(
                        CALL_WITH_ARGS_NOT_FOUND.format(
                            this.id,
                            mapValues(*arguments),
                        ),
                    )
                }
            }
        }
    }

    override fun FunProxy<*, *>.hasBeenCalledWithout(vararg illegal: Any?) {
        runAssertion(this) { callIndex ->
            val valid = this.guardActualRetrieval(callIndex).hasBeenCalledWithout(*illegal)

            if (!valid) {
                try {
                    this.hasBeenCalledWithout(*illegal)
                } catch (e: AssertionError) {
                    throw AssertionError(ILLEGAL_VALUE.format(mapValues(*illegal)))
                }
            }
        }
    }

    override fun PropertyProxy<*>.wasGotten() {
        runAssertion(this) { callIndex ->
            val valid = this.guardActualRetrieval(callIndex).wasGotten()

            if (!valid) {
                try {
                    this.wasGotten()
                } catch (e: AssertionError) {
                    throw AssertionError(NOT_GET)
                }
            }
        }
    }

    override fun PropertyProxy<*>.wasSet() {
        runAssertion(this) { callIndex ->
            val valid = this.guardActualRetrieval(callIndex).wasSet()

            if (!valid) {
                try {
                    this.wasSet()
                } catch (e: AssertionError) {
                    throw AssertionError(NOT_SET)
                }
            }
        }
    }

    override fun PropertyProxy<*>.wasSetTo(value: Any?) {
        runAssertion(this) { callIndex ->
            val valid = this.guardActualRetrieval(callIndex).wasSetTo(value)

            if (!valid) {
                try {
                    this.wasSetTo(value)
                } catch (e: AssertionError) {
                    throw AssertionError(
                        CALL_WITH_ARGS_NOT_FOUND.format(
                            this.id,
                            value,
                        ),
                    )
                }
            }
        }
    }

    @Throws(AssertionError::class)
    override fun ensureAllReferencesAreEvaluated() = Unit
}
