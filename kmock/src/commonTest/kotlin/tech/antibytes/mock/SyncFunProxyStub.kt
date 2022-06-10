/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.mock

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.error.MockError

class SyncFunProxyStub(
    override val id: String,
    override val calls: Int,
    var getArgumentsForCall: ((Int) -> Array<out Any?>)? = null,
    override val ignorableForVerification: Boolean = false,
    override val frozen: Boolean = true
) : KMockContract.SyncFunProxy<Any, () -> Any> {
    override var throws: Throwable
        get() = TODO("Not yet implemented")
        set(_) {}
    override var returnValue: Any
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override var returnValues: List<Any>
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override var sideEffect: () -> Any
        get() = TODO("Not yet implemented")
        set(_) = TODO("Not yet implemented")
    override val sideEffects: KMockContract.SideEffectChainBuilder<Any, () -> Any>
        get() = TODO("Not yet implemented")

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?> {
        return if (getArgumentsForCall == null) {
            throw MockError.MissingStub("Missing sideeffect getArgumentsForCall")
        } else {
            getArgumentsForCall!!.invoke(callIndex)
        }
    }

    override fun invoke(nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0> invoke(
        arg0: Arg0,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1> invoke(
        arg0: Arg0,
        arg1: Arg1,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        arg7: Arg7,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        arg7: Arg7,
        arg8: Arg8,
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
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
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
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
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
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
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
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
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Any, () -> Any>.() -> Unit
    ): Any {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun runs(action: () -> Any): KMockContract.ProxySideEffectBuilder<Any, () -> Any> {
        TODO("Not yet implemented")
    }

    override fun run(action: () -> Any) {
        TODO("Not yet implemented")
    }

    override var throwsMany: List<Throwable>
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun get(callIndex: Int): Array<out Any?> = getArgumentsForCall(callIndex)

    override fun returns(value: Any) {
        TODO("Not yet implemented")
    }

    override fun returnsMany(values: List<Any>) {
        TODO("Not yet implemented")
    }

    override fun throws(error: Throwable) {
        TODO("Not yet implemented")
    }

    override fun throwsMany(errors: List<Throwable>) {
        TODO("Not yet implemented")
    }
}
