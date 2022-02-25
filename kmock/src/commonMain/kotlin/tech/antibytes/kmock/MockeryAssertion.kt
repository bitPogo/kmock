/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.FunMockery

fun FunMockery<*, *>.assertHadBeenCalled(
    exactly: Int,
) {
    verify(exactly = exactly) {
        this.hasBeenCalledWith()
    }
}

fun FunMockery<*, *>.assertHadBeenCalledWith(
    exactly: Int,
    vararg arguments: Any?
) {
    verify(exactly = exactly) {
        this.hasBeenCalledWith(*arguments)
    }
}

fun FunMockery<*, *>.assertHadBeenCalledStrictlyWith(
    exactly: Int,
    vararg arguments: Any?
) {
    verify(exactly = exactly) {
        this.hasBeenStrictlyCalledWith(*arguments)
    }
}

fun FunMockery<*, *>.assertHadNotBeenCalled() {
    verify(exactly = 0) {
        this.hasBeenCalledWith()
    }
}

fun FunMockery<*, *>.assertHadNotBeenCalledWith(vararg illegal: Any?) {
    verify(exactly = 0) {
        this.hasBeenCalledWith(*illegal)
    }
}

/**
 * Assertion call for a FunMockery
 * Fails if a given argument is referenced in a call
 * @param arguments variable amount of nullable Any typed argument
 * @throws AssertionError if a call contains a at least one given argument.
 */
fun FunMockery<*, *>.assertHadBeenCalledWithout(
    vararg arguments: Any?
) {
    verify(atMost = 100) {
        this.hasBeenCalledWithout(*arguments)
    }
}

fun KMockContract.PropertyMockery<*>.assertWasGotten(exactly: Int) {
    verify(exactly = exactly) { this.wasGotten() }
}

fun KMockContract.PropertyMockery<*>.assertWasSet(exactly: Int) {
    verify(exactly = exactly) { this.wasSet() }
}

fun KMockContract.PropertyMockery<*>.assertWasSetTo(exactly: Int, value: Any?) {
    verify(exactly = exactly) { this.wasSetTo(value) }
}
