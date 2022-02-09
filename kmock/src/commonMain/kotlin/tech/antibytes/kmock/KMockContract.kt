/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.util.test.MockError

interface KMockContract {
    sealed interface Mockery<ReturnValue> {
        val id: String
        val calls: Int
    }

    interface FunMockery<ReturnValue> : Mockery<ReturnValue> {
        var returnValue: ReturnValue
        var returnValues: List<ReturnValue>
        var sideEffect: (Array<out Any?>) -> ReturnValue

        @Throws(MockError.MissingStub::class)
        fun invoke(vararg arguments: Any?): ReturnValue

        fun getArgumentsForCall(callIndex: Int): Array<out Any?>?
    }

    interface PropMockery<ReturnValue> : Mockery<ReturnValue>

    interface VerificationHandle {
        val id: String
        val callIndices: List<Int>
    }

    data class Reference(
        val mockery: Mockery<*>,
        val callIndex: Int
    )

    interface Collector {
        fun addReference(referredMock: Mockery<*>, referredCall: Int)
    }

    interface VerificationHandleContainer {
        fun add(handle: VerificationHandle)

        fun toList(): List<VerificationHandle>
    }

    interface Verifier {
        val references: List<Reference>
    }

    companion object {
        const val NOT_CALLED = "Call not found."
        const val TOO_LESS_CALLS = "Expected at least \$1 calls, but found only \$2."
        const val TOO_MANY_CALLS = "Expected at most \$1 calls, but exceeded with \$2."
        const val NOTHING_TO_STRICTLY_VERIFY = "The given verification chain (has \$1 items) does not match the captured calls (\$2 were captured)."
        const val NOTHING_TO_VERIFY = "The given verification chain (has \$1 items) is exceeding the captured calls (\$2 were captured)."
        const val NO_MATCHING_CALL_IDX = "The captured calls of \$1 exceeds the captured calls."
        const val MISMATCHING_FUNCTION = "Excepted \$1, but got \$2."
        const val MISMATCHING_CALL_IDX = "Excepted the \$1, but the \$2 was referenced."
        const val CALL_NOT_FOUND = "Last referred invocation of \$1 was not found."
    }
}
