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

    data class Reference(
        val mockery: Mockery<*>,
        val callIndex: Int
    )

    interface Collector {
        fun addReference(referredMock: Mockery<*>, referredCall: Int)
    }

    interface Verifier {
        val references: List<Reference>

        fun verify(
            exactly: Int? = null,
            atLeast: Int? = null,
            atMost: Int? = null,
            action: () -> VerificationHandle
        )
    }

    interface VerificationHandle {
        val id: String
        val callIndices: List<Int>
    }
}
