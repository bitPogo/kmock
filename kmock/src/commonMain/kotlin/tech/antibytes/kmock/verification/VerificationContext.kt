/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Expectation
import tech.antibytes.kmock.KMockContract.FunProxy
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.Proxy

internal object VerificationContext : KMockContract.VerificationContext {
    private fun <T> traverseMock(
        proxy: Proxy<*, T>,
        action: T.() -> Boolean,
    ): Expectation {
        val callIndices = mutableListOf<Int>()

        for (idx in 0 until proxy.calls) {
            if (action(proxy[idx])) {
                callIndices.add(idx)
            }
        }

        return Expectation(proxy, callIndices)
    }

    override fun FunProxy<*, *>.hasBeenCalled(): Expectation = traverseMock(this) { hasBeenCalledWith() }

    override fun FunProxy<*, *>.hasBeenCalledWithVoid(): Expectation = traverseMock(this) { hasBeenCalledWithVoid() }

    override fun FunProxy<*, *>.hasBeenCalledWith(
        vararg arguments: Any?,
    ): Expectation = traverseMock(this) { hasBeenCalledWith(*arguments) }

    override fun FunProxy<*, *>.hasBeenStrictlyCalledWith(
        vararg arguments: Any?,
    ): Expectation = traverseMock(this) { hasBeenStrictlyCalledWith(*arguments) }

    override fun FunProxy<*, *>.hasBeenCalledWithout(
        vararg illegal: Any?,
    ): Expectation = traverseMock(this) { hasBeenCalledWithout(*illegal) }

    override fun PropertyProxy<*>.wasGotten(): Expectation = traverseMock(this) { wasGotten() }

    override fun PropertyProxy<*>.wasSet(): Expectation = traverseMock(this) { wasSet() }

    override fun PropertyProxy<*>.wasSetTo(
        value: Any?,
    ): Expectation = traverseMock(this) { wasSetTo(value) }
}
