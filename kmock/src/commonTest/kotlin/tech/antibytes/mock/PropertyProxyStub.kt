/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.mock

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.GetOrSet
import tech.antibytes.kmock.error.MockError

class PropertyProxyStub(
    override val id: String,
    override val calls: Int,
    var getArgumentsForCall: ((Int) -> GetOrSet)? = null,
) : KMockContract.PropertyProxy<Any> {
    override fun getArgumentsForCall(callIndex: Int): GetOrSet {
        return if (getArgumentsForCall == null) {
            throw MockError.MissingStub("Missing SideEffect getArgumentsForCall")
        } else {
            getArgumentsForCall!!.invoke(callIndex)
        }
    }

    override var get: Any
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override var getMany: List<Any>
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override var getSideEffect: () -> Any
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override var set: (Any) -> Unit
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")

    override fun onGet(nonIntrusiveHook: KMockContract.NonIntrusivePropertyConfigurator<Any>.() -> Unit): Any {
        TODO("Not yet implemented")
    }

    override fun onSet(value: Any, nonIntrusiveHook: KMockContract.NonIntrusivePropertyConfigurator<Unit>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}
