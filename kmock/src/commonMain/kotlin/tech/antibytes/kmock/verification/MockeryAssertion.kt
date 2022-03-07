/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.FunProxy

fun FunProxy<*, *>.assertHasBeenCalled(
    exactly: Int,
) {
    verify(exactly = exactly) {
        this.hasBeenCalledWith()
    }
}

fun FunProxy<*, *>.assertHasBeenCalledWith(
    exactly: Int,
    vararg arguments: Any?
) {
    verify(exactly = exactly) {
        this.hasBeenCalledWith(*arguments)
    }
}

fun FunProxy<*, *>.assertHasBeenCalledStrictlyWith(
    exactly: Int,
    vararg arguments: Any?
) {
    verify(exactly = exactly) {
        this.hasBeenStrictlyCalledWith(*arguments)
    }
}

fun FunProxy<*, *>.assertHasNotBeenCalled() {
    verify(exactly = 0) {
        this.hasBeenCalledWith()
    }
}

fun FunProxy<*, *>.assertHasNotBeenCalledWith(vararg illegal: Any?) {
    verify(exactly = 0) {
        this.hasBeenCalledWith(*illegal)
    }
}

/**
 * Assertion call for a FunProxy
 * Fails if a given argument is referenced in a call
 * @param arguments variable amount of nullable Any typed argument
 * @throws AssertionError if a call contains a at least one given argument.
 */
fun FunProxy<*, *>.assertHadBeenCalledWithout(
    vararg arguments: Any?
) {
    verify(atMost = 100) {
        this.hasBeenCalledWithout(*arguments)
    }
}

fun KMockContract.PropertyProxy<*>.assertWasGotten(exactly: Int) {
    verify(exactly = exactly) { this.wasGotten() }
}

fun KMockContract.PropertyProxy<*>.assertWasSet(exactly: Int) {
    verify(exactly = exactly) { this.wasSet() }
}

fun KMockContract.PropertyProxy<*>.assertWasSetTo(exactly: Int, value: Any?) {
    verify(exactly = exactly) { this.wasSetTo(value) }
}
