/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.FunMockery

fun FunMockery<*, *>.assertWasCalled(
    exactly: Int,
) {
    verify(exactly = exactly) {
        this.wasCalledWithArguments()
    }
}

fun FunMockery<*, *>.assertWasCalledWith(
    exactly: Int,
    vararg arguments: Any?
) {
    verify(exactly = exactly) {
        this.wasCalledWithArguments(*arguments)
    }
}

fun FunMockery<*, *>.assertWasCalledStrictlyWith(
    exactly: Int,
    vararg arguments: Any?
) {
    verify(exactly = exactly) {
        this.wasCalledWithArgumentsStrict(*arguments)
    }
}

fun FunMockery<*, *>.assertWasNotCalled() {
    verify(exactly = 0) {
        this.wasCalledWithArguments()
    }
}

fun FunMockery<*, *>.assertWasNotCalledWith(vararg illegal: Any?) {
    verify(exactly = 0) {
        this.wasCalledWithArguments(*illegal)
    }
}

/**
 * Assertion call for a FunMockery
 * Fails if a given argument is referenced in a call
 * @param arguments variable amount of nullable Any typed argument
 * @throws AssertionError if a call contains a at least one given argument.
 */
fun FunMockery<*, *>.assertWasCalledWithout(
    vararg arguments: Any?
) {
    verify(atMost = 100) {
        this.wasCalledWithoutArguments(*arguments)
    }
}

fun KMockContract.PropMockery<*>.assertWasGotten(exactly: Int) {
    verify(exactly = exactly) { this.wasGotten() }
}

fun KMockContract.PropMockery<*>.assertWasSet(exactly: Int) {
    verify(exactly = exactly) { this.wasSet() }
}

fun KMockContract.PropMockery<*>.assertWasSetTo(exactly: Int, value: Any?) {
    verify(exactly = exactly) { this.wasSetTo(value) }
}
