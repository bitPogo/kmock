/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.util.test.MockError

internal interface KMockContract {
    interface Mockery<ReturnValue> {
        var returnValue: ReturnValue
        var returnValues: List<ReturnValue>
        var sideEffect: (Array<out Any?>) -> ReturnValue
        val calls: Int

        @Throws(MockError.MissingStub::class)
        fun invoke(vararg arguments: Any?): ReturnValue

        fun getArgumentsForCall(callIndex: Int): Array<out Any?>
    }

    interface Verifier
}
