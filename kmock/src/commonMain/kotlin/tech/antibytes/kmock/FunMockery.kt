/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.isolate.IsolateState
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.util.test.MockError
import kotlin.math.max

class FunMockery<ReturnValue, SideEffect : Function<ReturnValue>>(
    override val id: String,
    collector: Collector = Collector { _, _ -> Unit }
) : KMockContract.FunMockery<ReturnValue, SideEffect> {
    private val _returnValue: AtomicReference<ReturnValue?> = AtomicReference(null)
    private val _returnValues: AtomicReference<List<ReturnValue>?> = AtomicReference(null)
    private val _sideEffect: AtomicReference<SideEffect?> = AtomicReference(null)
    private val _calls: AtomicReference<Int> = AtomicReference(0)
    private val provider: AtomicReference<PROVIDER> = AtomicReference(PROVIDER.NO_PROVIDER)
    private val arguments: IsolateState<MutableList<Array<out Any?>?>> = IsolateState { mutableListOf() }
    private val collector = AtomicReference(collector)

    private fun setProvider(provider: PROVIDER) {
        val activeProvider = max(
            provider.value,
            this.provider.get().value
        )

        if (activeProvider == provider.value) {
            this.provider.set(provider)
        }
    }

    override var returnValue: ReturnValue
        @Suppress("UNCHECKED_CAST")
        get() = _returnValue.get() as ReturnValue
        set(value) {
            setProvider(PROVIDER.RETURN_VALUE)
            _returnValue.set(value)
        }

    override var returnValues: List<ReturnValue>
        get() = _returnValues.get() as List<ReturnValue>
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                setProvider(PROVIDER.RETURN_VALUES)
                _returnValues.set(values)
            }
        }

    override var sideEffect: SideEffect
        get() = _sideEffect.get() as SideEffect
        set(value) {
            setProvider(PROVIDER.SIDE_EFFECT)
            _sideEffect.set(value)
        }

    override val calls: Int
        get() = _calls.get()

    private fun determineNewValues(currentValues: List<ReturnValue>): List<ReturnValue> {
        return if (currentValues.size == 1) {
            currentValues
        } else {
            currentValues.drop(1)
        }
    }

    private fun retrieveValue(): ReturnValue {
        val currentValues = _returnValues.value
        val value = currentValues!!.first()

        val newValues = determineNewValues(currentValues)

        _returnValues.compareAndSet(currentValues, newValues)

        return value
    }

    private fun guardArguments(arguments: Array<out Any?>): Array<out Any?>? {
        return if (arguments.isEmpty()) {
            null
        } else {
            arguments
        }
    }

    private fun captureArguments(arguments: Array<out Any?>) {
        this.arguments.access { it.add(guardArguments(arguments)) }
    }

    private fun incrementInvocations() {
        val calls = this._calls.get()

        this._calls.compareAndSet(
            calls,
            calls + 1
        )
    }

    private fun notifyCollector() {
        collector.get().addReference(
            this,
            this._calls.get()
        )
    }

    private enum class PROVIDER(val value: Int) {
        NO_PROVIDER(0),
        RETURN_VALUE(1),
        RETURN_VALUES(2),
        SIDE_EFFECT(3)
    }

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?>? = arguments.access { it[callIndex] }

    private fun execute(
        function: () -> ReturnValue,
        vararg arguments: Any?
    ): ReturnValue {
        notifyCollector()
        captureArguments(arguments)
        incrementInvocations()

        return when (provider.get()) {
            PROVIDER.RETURN_VALUE -> _returnValue.get()!!
            PROVIDER.RETURN_VALUES -> retrieveValue()
            PROVIDER.SIDE_EFFECT -> function()
            else -> throw MockError.MissingStub("Missing stub value for $id")
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun invoke(): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as () -> ReturnValue).invoke()
        }

        return execute(invocation)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0> invoke(arg0: Arg0): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0) -> ReturnValue)
                .invoke(arg0)
        }

        return execute(invocation, arg0)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0, Arg1> invoke(arg0: Arg0, arg1: Arg1): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1) -> ReturnValue)
                .invoke(arg0, arg1)
        }

        return execute(invocation, arg0, arg1)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0, Arg1, Arg2> invoke(arg0: Arg0, arg1: Arg1, arg2: Arg2): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2) -> ReturnValue)
                .invoke(arg0, arg1, arg2)
        }

        return execute(invocation, arg0, arg1, arg2)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0, Arg1, Arg2, Arg3> invoke(arg0: Arg0, arg1: Arg1, arg2: Arg2, arg3: Arg3): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3)
        }

        return execute(invocation, arg0, arg1, arg2, arg3)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        arg7: Arg7
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        arg7: Arg7,
        arg8: Arg8
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
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
        arg9: Arg9
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
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
        arg10: Arg10
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
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
        arg11: Arg11
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
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
        arg12: Arg12
    ): ReturnValue {
        val invocation = {
            (this._sideEffect.get() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12)
    }
}
