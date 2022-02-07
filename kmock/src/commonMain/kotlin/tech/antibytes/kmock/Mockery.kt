/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.isolate.IsolateState
import tech.antibytes.util.test.MockError
import kotlin.math.max

class Mockery<ReturnValue>(
    private val name: String
) : KMockContract.Mockery<ReturnValue> {
    private val _returnValue: AtomicReference<ReturnValue?> = AtomicReference(null)
    private val _returnValues: AtomicReference<List<ReturnValue>?> = AtomicReference(null)
    private val _sideEffect: AtomicReference<((Array<out Any?>) -> ReturnValue)?> = AtomicReference(null)
    private val _calls: AtomicReference<Int> = AtomicReference(0)
    private val provider: AtomicReference<PROVIDER> = AtomicReference(PROVIDER.NO_PROVIDER)
    private val arguments: IsolateState<MutableList<Array<out Any?>>> = IsolateState { mutableListOf() }

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

    override var sideEffect: (Array<out Any?>) -> ReturnValue
        get() = _sideEffect.get() as (Array<out Any?>) -> ReturnValue
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

    private fun captureArguments(arguments: Array<out Any?>) {
        this.arguments.access { it.add(arguments) }
    }

    private fun incrementInvocations() {
        val calls = this._calls.get()

        this._calls.compareAndSet(
            calls,
            calls + 1
        )
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(MockError.MissingStub::class)
    override fun invoke(vararg arguments: Any?): ReturnValue {
        captureArguments(arguments)
        incrementInvocations()

        return when (provider.get()) {
            PROVIDER.RETURN_VALUE -> _returnValue.get() as ReturnValue
            PROVIDER.RETURN_VALUES -> retrieveValue()
            PROVIDER.SIDE_EFFECT -> _sideEffect.get()!!.invoke(arguments)
            else -> throw MockError.MissingStub("Missing stub value for $name")
        }
    }

    private enum class PROVIDER(val value: Int) {
        NO_PROVIDER(0),
        RETURN_VALUE(1),
        RETURN_VALUES(2),
        SIDE_EFFECT(3)
    }

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?> = arguments.access { it[callIndex] }
}
