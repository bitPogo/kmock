/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.mock

import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.MockError

class FunMockeryStub(
    override val id: String,
    override val calls: Int,
    var getArgumentsForCall: ((Int) -> Array<out Any?>?)? = null
) : KMockContract.FunMockery<Any> {
    override var returnValue: Any
        get() = TODO("Not yet implemented")
        set(value) {}
    override var returnValues: List<Any>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var sideEffect: (Array<out Any?>) -> Any
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun invoke(vararg arguments: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?>? {
        return if (getArgumentsForCall == null) {
            throw MockError.MissingStub("Missing sideeffect getArgumentsForCall")
        } else {
            getArgumentsForCall!!.invoke(callIndex)
        }
    }
}
