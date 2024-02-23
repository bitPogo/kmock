/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.AssertionContext
import tech.antibytes.kmock.KMockContract.Assertions
import tech.antibytes.kmock.KMockContract.FunProxy
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.Proxy

internal abstract class BaseAssertionContext(
    private val assertions: Assertions,
) : AssertionContext {
    protected abstract fun runAssertion(proxy: Proxy<*, *>, action: (callIndex: Int) -> Unit)

    override fun FunProxy<*, *>.hasBeenCalled() {
        runAssertion(this) { callIndex ->
            assertions.hasBeenCalledAtIndex(this, callIndex)
        }
    }

    override fun FunProxy<*, *>.hasBeenCalledWithVoid() {
        runAssertion(this) { callIndex ->
            assertions.hasBeenCalledWithVoidAtIndex(this, callIndex)
        }
    }

    override fun FunProxy<*, *>.hasBeenCalledWith(vararg arguments: Any?) {
        runAssertion(this) { callIndex ->
            assertions.hasBeenCalledWithAtIndex(
                proxy = this,
                callIndex = callIndex,
                arguments = arguments,
            )
        }
    }

    override fun FunProxy<*, *>.hasBeenStrictlyCalledWith(vararg arguments: Any?) {
        runAssertion(this) { callIndex ->
            assertions.hasBeenStrictlyCalledWithAtIndex(
                proxy = this,
                callIndex = callIndex,
                arguments = arguments,
            )
        }
    }

    override fun FunProxy<*, *>.hasBeenCalledWithout(vararg illegal: Any?) {
        runAssertion(this) { callIndex ->
            assertions.hasBeenCalledWithoutAtIndex(
                proxy = this,
                callIndex = callIndex,
                illegal = illegal,
            )
        }
    }

    override fun PropertyProxy<*>.wasGotten() {
        runAssertion(this) { callIndex ->
            assertions.wasGottenAtIndex(proxy = this, callIndex = callIndex)
        }
    }

    override fun PropertyProxy<*>.wasSet() {
        runAssertion(this) { callIndex ->
            assertions.wasSetAtIndex(proxy = this, callIndex = callIndex)
        }
    }

    override fun PropertyProxy<*>.wasSetTo(value: Any?) {
        runAssertion(this) { callIndex ->
            assertions.wasSetToAtIndex(
                proxy = this,
                callIndex = callIndex,
                value = value,
            )
        }
    }
}
