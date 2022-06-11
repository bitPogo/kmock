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
    override val frozen: Boolean = true,
) : KMockContract.PropertyProxy<Any> {
    override fun getArgumentsForCall(callIndex: Int): GetOrSet {
        return if (getArgumentsForCall == null) {
            throw MockError.MissingStub("Missing SideEffect getArgumentsForCall")
        } else {
            getArgumentsForCall!!.invoke(callIndex)
        }
    }

    override var getValue: Any
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override var getValues: List<Any>
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")

    @Deprecated(
        "This property will be replaced with 0.3.0 by getValues.",
        replaceWith = ReplaceWith("error"),
        level = DeprecationLevel.WARNING
    )
    override var getMany: List<Any>
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override var get: () -> Any
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override var set: (Any) -> Unit
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")

    override fun executeOnGet(nonIntrusiveHook: KMockContract.NonIntrusivePropertyConfigurator<Any>.() -> Unit): Any {
        TODO("Not yet implemented")
    }

    override fun executeOnSet(value: Any, nonIntrusiveHook: KMockContract.NonIntrusivePropertyConfigurator<Unit>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun get(callIndex: Int): GetOrSet = getArgumentsForCall(callIndex)

    override fun returns(value: Any) {
        TODO("Not yet implemented")
    }

    override fun returnsMany(values: List<Any>) {
        TODO("Not yet implemented")
    }

    override fun runOnGet(sideEffect: () -> Any) {
        TODO("Not yet implemented")
    }

    override fun runOnSet(sideEffect: (Any) -> Unit) {
        TODO("Not yet implemented")
    }
}
