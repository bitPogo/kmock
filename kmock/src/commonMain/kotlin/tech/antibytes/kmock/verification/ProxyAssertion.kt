/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.FunProxy

/**
 * Asserts that invocations of a FunProxy happened.
 * @param exactly how often the Proxy was invoked.
 * @throws AssertionError if the amount of calls does not match the expected amount.
 * @see FunProxy
 * @see verify
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.assertHasBeenCalled(
    exactly: Int,
) {
    verify(exactly = exactly) {
        this.hasBeenCalledWith()
    }
}

/**
 * Asserts that invocations of a FunProxy with the given arguments happened.
 * @param exactly how often the Proxy was invoked.
 * @param arguments or constraints which follow the order of the mocked/stubbed function but can contain gaps and do not need to all arguments/constraints.
 * @throws AssertionError if the amount of calls does not match the expected amount.
 * @see FunProxy
 * @see verify
 * @see KMockContract.VerificationConstraint
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.assertHasBeenCalledWith(
    exactly: Int,
    vararg arguments: Any?
) {
    verify(exactly = exactly) {
        this.hasBeenCalledWith(*arguments)
    }
}

/**
 * Asserts that invocations of a FunProxy with the given arguments happened.
 * @param exactly how often the Proxy was invoked.
 * @param arguments or constraints which follow the order of the mocked/stubbed function and need to contain all arguments/constraints.
 * @throws AssertionError if the amount of calls does not match the expected amount.
 * @see FunProxy
 * @see verify
 * @see KMockContract.VerificationConstraint
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.assertHasBeenCalledStrictlyWith(
    exactly: Int,
    vararg arguments: Any?
) {
    verify(exactly = exactly) {
        this.hasBeenStrictlyCalledWith(*arguments)
    }
}

/**
 * Asserts that invocations of a FunProxy did not happened.
 * @throws AssertionError if the amount of calls does not match the expected amount.
 * @see FunProxy
 * @see verify
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.assertHasNotBeenCalled() {
    verify(exactly = 0) {
        this.hasBeenCalledWith()
    }
}

/**
 * Asserts that invocations of a FunProxy happened without given parameter.
 * @param illegal unordered arguments/constraints.
 * @throws AssertionError if at least one call contains a given argument.
 * @see FunProxy
 * @see verify
 * @see KMockContract.VerificationConstraint
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.assertHasBeenCalledWithout(
    vararg illegal: Any?
) {
    verify(atMost = Int.MAX_VALUE) {
        this.hasBeenCalledWithout(*illegal)
    }
}

/**
 * Asserts that invocations of a FunProxy did not happened with given parameter.
 * @param illegal arguments/constraints which follow the order of the mocked/stubbed function but can contain gaps and do not need to all arguments/constraints.
 * @throws AssertionError if the amount of calls does not match the expected amount.
 * @see FunProxy
 * @see verify
 * @see KMockContract.VerificationConstraint
 * @author Matthias Geisler
 */
fun FunProxy<*, *>.assertHasBeenCalledStrictlyWithout(vararg illegal: Any?) {
    verify(exactly = 0) {
        this.hasBeenCalledWith(*illegal)
    }
}

/**
 * Asserts that the getter of a PropertyProxy was invoked.
 * @param exactly how often the Proxy was invoked.
 * @throws AssertionError if at least one call contains a given argument.
 * @see FunProxy
 * @see verify
 * @author Matthias Geisler
 */
fun KMockContract.PropertyProxy<*>.assertWasGotten(exactly: Int) {
    verify(exactly = exactly) { this.wasGotten() }
}

/**
 * Asserts that the setter of a PropertyProxy was invoked.
 * @param exactly how often the Proxy was invoked.
 * @throws AssertionError if at least one call contains a given argument.
 * @see FunProxy
 * @see verify
 * @author Matthias Geisler
 */
fun KMockContract.PropertyProxy<*>.assertWasSet(exactly: Int) {
    verify(exactly = exactly) { this.wasSet() }
}

/**
 * Asserts that the setter of a PropertyProxy was invoked with the given value.
 * @param exactly how often the Proxy was invoked.
 * @param value or constraint of the expected value.
 * @throws AssertionError if at least one call contains a given argument.
 * @see FunProxy
 * @see verify
 * @see KMockContract.VerificationConstraint
 * @author Matthias Geisler
 */
fun KMockContract.PropertyProxy<*>.assertWasSetTo(exactly: Int, value: Any?) {
    verify(exactly = exactly) { this.wasSetTo(value) }
}
