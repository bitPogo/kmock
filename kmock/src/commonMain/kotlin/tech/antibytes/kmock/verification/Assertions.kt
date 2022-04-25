/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.FunProxy
import tech.antibytes.kmock.KMockContract.HAD_BEEN_CALLED_NO_MATCHER
import tech.antibytes.kmock.KMockContract.ILLEGAL_VALUE
import tech.antibytes.kmock.KMockContract.MISMATCH
import tech.antibytes.kmock.KMockContract.MISMATCHING_SIZE
import tech.antibytes.kmock.KMockContract.MISSING_INVOCATION
import tech.antibytes.kmock.KMockContract.NON_VOID_FUNCTION
import tech.antibytes.kmock.KMockContract.NOT_GET
import tech.antibytes.kmock.KMockContract.NOT_SET
import tech.antibytes.kmock.KMockContract.VOID_FUNCTION
import tech.antibytes.kmock.util.format

internal object Assertions : KMockContract.Assertions {
    private fun <T> Proxy<*, T>.guardActualRetrieval(callIndex: Int): T {
        return try {
            this.getArgumentsForCall(callIndex)
        } catch (_: Throwable) {
            throw AssertionError(MISSING_INVOCATION.format(callIndex + 1, this.id))
        }
    }

    override fun hasBeenCalledAtIndex(proxy: FunProxy<*, *>, callIndex: Int) {
        proxy.guardActualRetrieval(callIndex)
    }

    override fun hasBeenCalledWithVoidAtIndex(proxy: FunProxy<*, *>, callIndex: Int) {
        val actual = proxy.guardActualRetrieval(callIndex).hasBeenCalledWithVoid()

        if (!actual) {
            throw AssertionError(VOID_FUNCTION.format(proxy.id))
        }
    }

    override fun hasBeenCalledWithAtIndex(proxy: FunProxy<*, *>, callIndex: Int, vararg arguments: Any?) {
        val actual = proxy.guardActualRetrieval(callIndex)

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

    override fun hasBeenStrictlyCalledWithAtIndex(
        proxy: FunProxy<*, *>,
        callIndex: Int,
        vararg arguments: Any?
    ) {
        val actual = proxy.guardActualRetrieval(callIndex)
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

    override fun hasBeenCalledWithoutAtIndex(
        proxy: FunProxy<*, *>,
        callIndex: Int,
        vararg illegal: Any?
    ) {
        val actual = proxy.guardActualRetrieval(callIndex)
        val valid = actual.hasBeenCalledWithout(*illegal) { actualArgument, _ ->
            throw AssertionError(
                ILLEGAL_VALUE.format(actualArgument)
            )
        }

        if (!valid) {
            throw AssertionError(NON_VOID_FUNCTION)
        }
    }

    override fun wasGottenAtIndex(proxy: KMockContract.PropertyProxy<*>, callIndex: Int) {
        val actual = proxy.guardActualRetrieval(callIndex)

        if (!actual.wasGotten()) {
            throw AssertionError(NOT_GET)
        }
    }

    override fun wasSetAtIndex(proxy: KMockContract.PropertyProxy<*>, callIndex: Int) {
        val actual = proxy.guardActualRetrieval(callIndex)

        if (!actual.wasSet()) {
            throw AssertionError(NOT_SET)
        }
    }

    override fun wasSetToAtIndex(proxy: KMockContract.PropertyProxy<*>, callIndex: Int, value: Any?) {
        val actual = proxy.guardActualRetrieval(callIndex)
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
