/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

interface KMockContract {
    sealed interface Mockery<ReturnValue, Arguments> {
        val id: String
        val calls: Int
        var verificationBuilderReference: VerificationChainBuilder?

        fun getArgumentsForCall(callIndex: Int): Arguments
        fun clear()
    }

    interface FunMockery<ReturnValue, SideEffect : Function<ReturnValue>> : Mockery<ReturnValue, Array<out Any?>?> {
        var returnValue: ReturnValue
        var returnValues: List<ReturnValue>
        var sideEffect: SideEffect
    }

    interface SyncFunMockery<ReturnValue, SideEffect : Function<ReturnValue>> : FunMockery<ReturnValue, SideEffect> {

        fun invoke(): ReturnValue

        fun <Arg0> invoke(arg0: Arg0): ReturnValue

        fun <Arg0, Arg1> invoke(
            arg0: Arg0,
            arg1: Arg1
        ): ReturnValue

        fun <Arg0, Arg1, Arg2> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2
        ): ReturnValue

        fun <Arg0, Arg1, Arg2, Arg3> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3
        ): ReturnValue

        fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4
        ): ReturnValue

        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5
        ): ReturnValue

        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6
        ): ReturnValue

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
    }

    interface AsyncFunMockery<ReturnValue, SideEffect : Function<ReturnValue>> : FunMockery<ReturnValue, SideEffect> {

        suspend fun invoke(): ReturnValue

        suspend fun <Arg0> invoke(arg0: Arg0): ReturnValue

        suspend fun <Arg0, Arg1> invoke(
            arg0: Arg0,
            arg1: Arg1
        ): ReturnValue

        suspend fun <Arg0, Arg1, Arg2> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2
        ): ReturnValue

        suspend fun <Arg0, Arg1, Arg2, Arg3> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3
        ): ReturnValue

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4
        ): ReturnValue

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5
        ): ReturnValue

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6
        ): ReturnValue

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7
        ): ReturnValue

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
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

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
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

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
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

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
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

        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
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
    }

    sealed class GetOrSet(val value: Any?) {
        object Get : GetOrSet(null)
        class Set(newValue: Any?) : GetOrSet(newValue)
    }

    interface PropertyMockery<Value> : Mockery<Value, GetOrSet> {
        var get: Value
        var getMany: List<Value>
        var getSideEffect: Function0<Value>
        var set: (Value) -> Unit

        fun onGet(): Value
        fun onSet(value: Value)
    }

    interface VerificationHandle {
        val id: String
        val callIndices: List<Int>
    }

    data class Reference(
        val mockery: Mockery<*, *>,
        val callIndex: Int
    )

    fun interface Collector {
        fun addReference(referredMock: Mockery<*, *>, referredCall: Int)
    }

    interface VerificationReferenceBuilder {
        fun ensureVerificationOf(vararg mocks: Mockery<*, *>)
    }

    interface VerificationReferenceCleaner {
        fun ensureVerificationOf(vararg mocks: Mockery<*, *>)
    }

    interface VerificationChainBuilder {
        fun add(handle: VerificationHandle)
        fun toList(): List<VerificationHandle>
    }

    interface Verifier {
        val references: List<Reference>

        fun clear()
    }

    fun interface Relaxer<T> {
        fun relax(id: String): T
    }

    companion object {
        const val NOT_CALLED = "Call not found."
        const val TOO_LESS_CALLS = "Expected at least \$1 calls, but found only \$2."
        const val TOO_MANY_CALLS = "Expected at most \$1 calls, but exceeded with \$2."
        const val NOTHING_TO_STRICTLY_VERIFY = "The given verification chain (has \$1 items) does not match the captured calls (\$2 were captured)."
        const val NOTHING_TO_VERIFY = "The given verification chain (has \$1 items) is exceeding the captured calls (\$2 were captured)."
        const val NO_MATCHING_CALL_IDX = "The captured calls of \$1 exceeds the captured calls."
        const val MISMATCHING_FUNCTION = "Excepted '\$1', but got '\$2'."
        const val MISMATCHING_CALL_IDX = "Excepted the \$1, but the \$2 was referenced."
        const val CALL_NOT_FOUND = "Last referred invocation of \$1 was not found."
    }
}
