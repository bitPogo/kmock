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

abstract class FunMockery<ReturnValue, SideEffect : Function<ReturnValue>>(
    override val id: String,
    collector: Collector = Collector { _, _ -> Unit }
) : KMockContract.FunMockery<ReturnValue, SideEffect> {
    private val _returnValue: AtomicReference<ReturnValue?> = AtomicReference(null)
    private val _returnValues: AtomicReference<List<ReturnValue>?> = AtomicReference(null)
    private val _sideEffect: AtomicReference<SideEffect?> = AtomicReference(null)
    private val _calls: AtomicReference<Int> = AtomicReference(0)
    protected val provider: AtomicReference<PROVIDER> = AtomicReference(PROVIDER.NO_PROVIDER)
    private val arguments: IsolateState<MutableList<Array<out Any?>?>> = IsolateState { mutableListOf() }
    private val collector = AtomicReference(collector)

    protected enum class PROVIDER(val value: Int) {
        NO_PROVIDER(0),
        RETURN_VALUE(1),
        RETURN_VALUES(2),
        SIDE_EFFECT(3)
    }

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

    protected fun retrieveValue(): ReturnValue = _returnValue.get()!!

    protected fun retrieveFromValues(): ReturnValue {
        val currentValues = _returnValues.value
        val value = currentValues!!.first()

        val newValues = determineNewValues(currentValues)

        _returnValues.compareAndSet(currentValues, newValues)

        return value
    }

    protected fun retrieveSideEffect(): SideEffect = _sideEffect.get()!!

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

    protected fun onEvent(arguments: Array<out Any?>) {
        notifyCollector()
        captureArguments(arguments)
        incrementInvocations()
    }

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?>? = arguments.access { it[callIndex] }
}
