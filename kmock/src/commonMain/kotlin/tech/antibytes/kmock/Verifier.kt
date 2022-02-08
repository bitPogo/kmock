/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.isolate.IsolateState
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.Mockery
import tech.antibytes.kmock.KMockContract.VerificationHandle
import tech.antibytes.kmock.KMockContract.Verifier.Companion.NOT_CALLED
import tech.antibytes.kmock.KMockContract.Verifier.Companion.TOO_LESS_CALLS
import tech.antibytes.kmock.KMockContract.Verifier.Companion.TOO_MANY_CALLS

class Verifier : KMockContract.Verifier, KMockContract.Collector {
    private val _references: IsolateState<MutableList<Reference>> = IsolateState { mutableListOf() }

    override val references: List<Reference>
        get() = _references.access { it.toList() }

    override fun addReference(referredMock: Mockery<*>, referredCall: Int) {
        _references.access { references ->
            references.add(Reference(referredMock, referredCall))
        }
    }

    private fun formatMessage(message: String, actual: Int, expected: Int): String {
        return message
            .replace("\$1", expected.toString())
            .replace("\$2", actual.toString())
    }

    private fun determineAtLeastMessage(actual: Int, expected: Int): String {
        return if (actual == 0) {
            NOT_CALLED
        } else {
            formatMessage(TOO_LESS_CALLS, actual, expected)
        }
    }

    private infix fun VerificationHandle.mustBeAtLeast(value: Int) {
        if (this.callIndices.size < value) {
            val message = determineAtLeastMessage(this.callIndices.size, value)

            throw AssertionError(message)
        }
    }

    private infix fun VerificationHandle.mustBeAtMost(value: Int) {
        if (this.callIndices.size > value) {
            val message = formatMessage(TOO_MANY_CALLS, this.callIndices.size, value)

            throw AssertionError(message)
        }
    }

    override fun verify(
        exactly: Int?,
        atLeast: Int,
        atMost: Int?,
        action: () -> VerificationHandle
    ) {
        val handle = action()
        val minimum = exactly ?: atLeast
        val maximum = exactly ?: atMost

        handle mustBeAtLeast minimum

        if (maximum != null) {
            handle mustBeAtMost maximum
        }
    }
}
