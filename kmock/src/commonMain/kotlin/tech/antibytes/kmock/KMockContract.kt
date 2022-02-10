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

    interface FunMockery<ReturnValue, SideEffect : Function<ReturnValue>> : Mockery<ReturnValue> {
        var returnValue: ReturnValue
        var returnValues: List<ReturnValue>
        var sideEffect: SideEffect

        @Throws(MockError.MissingStub::class)
        fun invoke(): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0> invoke(arg0: Arg0): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1> invoke(
            arg0: Arg0,
            arg1: Arg1
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10,
            arg11: Arg11
        ): ReturnValue

        @Throws(MockError.MissingStub::class)
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10,
            arg11: Arg11,
            arg12: Arg12,
        ): ReturnValue

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

    fun interface Collector {
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
