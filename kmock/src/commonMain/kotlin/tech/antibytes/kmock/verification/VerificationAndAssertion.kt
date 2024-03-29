/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlin.math.abs
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Asserter
import tech.antibytes.kmock.KMockContract.AssertionContext
import tech.antibytes.kmock.KMockContract.ChainedAssertion
import tech.antibytes.kmock.KMockContract.CloseableAssertionContext
import tech.antibytes.kmock.KMockContract.Expectation
import tech.antibytes.kmock.KMockContract.NOT_CALLED
import tech.antibytes.kmock.KMockContract.TOO_LESS_CALLS
import tech.antibytes.kmock.KMockContract.TOO_MANY_CALLS
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.util.format

private fun determineAtLeastMessage(actual: Int, expected: Int): String {
    return if (actual == 0) {
        NOT_CALLED
    } else {
        TOO_LESS_CALLS.format(expected, actual)
    }
}

private infix fun Expectation.mustBeAtLeast(value: Int) {
    val boundary = abs(value)

    if (this.callIndices.size < boundary) {
        val message = determineAtLeastMessage(
            expected = boundary,
            actual = this.callIndices.size,
        )

        throw AssertionError(message)
    }
}

private infix fun Expectation.mustBeAtMost(value: Int) {
    val boundary = abs(value)

    if (this.callIndices.size > boundary) {
        val message = TOO_MANY_CALLS.format(boundary, this.callIndices.size)

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
public fun verify(
    exactly: Int? = null,
    atLeast: Int = 1,
    atMost: Int? = null,
    action: KMockContract.VerificationContext.() -> Expectation,
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
 * Verifies the last produced VerificationHandle in a Coroutine Context.
 * Use that context only in combination with proxy access methods.
 * @param exactly optional parameter which indicates the exact amount of calls.
 * This parameter overrides atLeast and atMost.
 * @param atLeast optional parameter which indicates the minimum amount of calls.
 * @param atMost optional parameter which indicates the maximum amount of calls.
 * @param action producer of a VerificationHandle.
 * @throws AssertionError if given criteria are not met.
 * @see KMockContract.VerificationContext
 * @author Matthias Geisler
 */
@KMockExperimental
public suspend fun asyncVerify(
    exactly: Int? = null,
    atLeast: Int = 1,
    atMost: Int? = null,
    action: suspend KMockContract.VerificationContext.() -> Expectation,
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
public fun assertProxy(action: CloseableAssertionContext.() -> Unit): Unit = action(UnchainedAssertion())

/**
 * Asserts Expectations of proxies in Coroutine Context. Each Expectation relates to a specific Proxy and they must be in order.
 * Also multiple Proxies are allowed but no relation between them is covered (use assertOrder in this case).
 * Use that context only in combination with proxy access methods.
 * @param action Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @see AssertionContext
 * @author Matthias Geisler
 */
@KMockExperimental
public suspend fun asyncAssertProxy(
    action: suspend CloseableAssertionContext.() -> Unit,
): Unit = action(UnchainedAssertion())

private fun <T> runChainedAssertion(
    chain: T,
    action: ChainedAssertion.() -> Any,
) where T : ChainedAssertion, T : KMockContract.AssertionChain {
    action(chain)

    chain.ensureAllReferencesAreEvaluated()
}

private suspend fun <T> runAsyncChainedAssertion(
    chain: T,
    action: suspend ChainedAssertion.() -> Any,
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
public fun Asserter.assertOrder(action: ChainedAssertion.() -> Any) {
    return runChainedAssertion(AssertionChain(references), action)
}

/**
 * Asserts a chain of Expectations in a coroutine Context.
 * Each Expectation must be in strict order of the referenced Proxy invocations and all invocations must be present.
 * Use that context only in combination with proxy access methods.
 * @param action chain of Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @see AssertionContext
 * @author Matthias Geisler
 */
@KMockExperimental
public suspend fun Asserter.asyncAssertOrder(action: suspend ChainedAssertion.() -> Any) {
    return runAsyncChainedAssertion(AssertionChain(references), action)
}

/**
 * Verifies a chain of Expectations. Expectation between different proxies can contain gaps.
 * Also the chain does not need to be exhaustive.
 * @param action chain of Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @author Matthias Geisler
 */
public fun Asserter.verifyStrictOrder(action: ChainedAssertion.() -> Any) {
    runChainedAssertion(StrictVerificationChain(references), action)
}

/**
 * Verifies a chain of Expectations in a Coroutine Context. Expectation between different proxies can contain gaps.
 * Also the chain does not need to be exhaustive.
 * Use that context only in combination with proxy access methods.
 * @param action chain of Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @author Matthias Geisler
 */
@KMockExperimental
public suspend fun Asserter.asyncVerifyStrictOrder(action: suspend ChainedAssertion.() -> Any) {
    runAsyncChainedAssertion(StrictVerificationChain(references), action)
}

/**
 * Verifies a chain of Expectations. Each Expectation must be in order but gaps are allowed.
 * Also the chain does not need to be exhaustive.
 * @param action chain of Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @see AssertionContext
 * @author Matthias Geisler
 */
public fun Asserter.verifyOrder(action: ChainedAssertion.() -> Any) {
    return runChainedAssertion(VerificationChain(references), action)
}

/**
 * Verifies a chain of Expectations in a Coroutine Context. Each Expectation must be in order but gaps are allowed.
 * Also the chain does not need to be exhaustive.
 * Use that context only in combination with proxy access methods.
 * @param action chain of Expectation Methods.
 * @throws AssertionError if given criteria are not met.
 * @see AssertionContext
 * @author Matthias Geisler
 */
@KMockExperimental
public suspend fun Asserter.asyncVerifyOrder(action: suspend ChainedAssertion.() -> Any) {
    return runAsyncChainedAssertion(VerificationChain(references), action)
}
