/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import co.touchlab.stately.collections.sharedMutableMapOf
import tech.antibytes.kmock.KMockContract.Assertions
import tech.antibytes.kmock.KMockContract.CloseableAssertionContext
import tech.antibytes.kmock.KMockContract.Proxy

internal class UnchainedAssertion(
    assertions: Assertions = Assertions,
) : BaseAssertionContext(assertions), CloseableAssertionContext {
    private val frozenInvocations: MutableMap<Proxy<*, *>, Int> = sharedMutableMapOf()
    private val unfrozenInvocations: MutableMap<Proxy<*, *>, Int> = mutableMapOf()

    private fun getCallIndices(
        proxy: Proxy<*, *>,
    ): MutableMap<Proxy<*, *>, Int> {
        return if (proxy.frozen) {
            frozenInvocations
        } else {
            unfrozenInvocations
        }
    }

    override fun Proxy<*, *>.hasNoFurtherInvocations() {
        runAssertion(this) { callIndex ->
            if (callIndex < calls) {
                throw AssertionError("Only $callIndex of $calls invocations had been asserted.")
            }
        }
    }

    override fun runAssertion(
        proxy: Proxy<*, *>,
        action: (callIndex: Int) -> Unit,
    ) {
        val callIndices = getCallIndices(proxy)
        val callIndex = callIndices.getOrElse(proxy) { 0 }

        action(callIndex)

        callIndices[proxy] = callIndex + 1
    }
}
