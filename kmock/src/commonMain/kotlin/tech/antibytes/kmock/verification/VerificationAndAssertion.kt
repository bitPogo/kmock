/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Asserter
import tech.antibytes.kmock.KMockContract.AssertionContext
import tech.antibytes.kmock.KMockContract.ChainedAssertion
import tech.antibytes.kmock.KMockContract.Expectation
import tech.antibytes.kmock.KMockContract.NOT_CALLED
import tech.antibytes.kmock.KMockContract.TOO_LESS_CALLS
import tech.antibytes.kmock.KMockContract.TOO_MANY_CALLS
import tech.antibytes.kmock.util.format

private fun determineAtLeastMessage(actual: Int, expected: Int): String {
    return if (actual == 0) {
        NOT_CALLED
    } else {
        TOO_LESS_CALLS.format(expected, actual)
    }
}

private infix fun Expectation.mustBeAtLeast(value: Int) {
    if (this.callIndices.size < value) {
        val message = determineAtLeastMessage(
            expected = value,
            actual = this.callIndices.size
        )

        throw AssertionError(message)
    }
}

private infix fun Expectation.mustBeAtMost(value: Int) {
    if (this.callIndices.size > value) {
        val message = TOO_MANY_CALLS.format(value, this.callIndices.size)

        throw AssertionError(message)
    }
}

/**
 * Verifies the last produced VerificationHandle.
 * @param exactly optional parameter which indicates the exact amount of calls.
 * This parameter overrides atLeast and atMost.
 * @param atLeast optional parameter which indicates the minimum amount of calls.
 * @param atMost optional parameter which indicates the maximum amount of calls.
 * @param action producer of a VerificationHandle.
 * @throws AssertionError if given criteria are not met.
 * @see KMockContract.VerificationContext
 * @author Matthias Geisler
 */
fun verify(
    exactly: Int? = null,
    atLeast: Int = 1,
    atMost: Int? = null,
    action: KMockContract.VerificationContext.() -> Expectation
) {
    val handle = action(VerificationContext)
    val minimum = exactly ?: atLeast
    val maximum = exactly ?: atMost

    handle mustBeAtLeast minimum

    if (maximum != null) {
        handle mustBeAtMost maximum
    }
}

/**
 * Asserts Expectations of proxies. Each Expectation relates to a specific Proxy and they must be in order.
 * Also multiple Proxies are allowed but no relation between them is covered (use assertOrder in this case).
 * @param action Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @see AssertionContext
 * @author Matthias Geisler
 */
fun assertProxy(action: AssertionContext.() -> Unit) = action(UnchainedAssertion())

private fun <T> runChainedAssertion(
    chain: T,
    action: ChainedAssertion.() -> Any
) where T : ChainedAssertion, T : KMockContract.AssertionChain {
    action(chain)

    chain.ensureAllReferencesAreEvaluated()
}

/**
* Asserts a chain of Expectations. Each Expectation must be in strict order of the referenced Proxy invocations
* and all invocations must be present.
* @param action chain of Expectation Methods.
* @throws AssertionError if given criteria are not met.
* @see AssertionContext
* @author Matthias Geisler
*/
fun Asserter.assertOrder(action: ChainedAssertion.() -> Any) = runChainedAssertion(AssertionChain(references), action)

/**
 * Verifies a chain of Expectations. Expectation between different proxies can contain gaps.
 * Also the chain does not need to be exhaustive.
 * @param action chain of Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @author Matthias Geisler
 */
fun Asserter.verifyStrictOrder(action: ChainedAssertion.() -> Any) {
    runChainedAssertion(StrictVerificationChain(references), action)
}

/**
 * Verifies a chain of Expectations. Each Expectation must be in order but gaps are allowed.
 * Also the chain does not need to be exhaustive.
 * @param action chain of Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @see AssertionContext
 * @author Matthias Geisler
 */
fun Asserter.verifyOrder(action: ChainedAssertion.() -> Any) = runChainedAssertion(VerificationChain(references), action)
