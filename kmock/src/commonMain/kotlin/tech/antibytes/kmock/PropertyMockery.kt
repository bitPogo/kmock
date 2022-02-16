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
import tech.antibytes.kmock.KMockContract.GetOrSet
import tech.antibytes.util.test.MockError

class PropertyMockery<Value>(
    override val id: String,
    collector: Collector = Collector { _, _ -> Unit }
) : KMockContract.PropertyMockery<Value> {
    private val provider: AtomicReference<Boolean?> = AtomicReference(null)
    private val _get: AtomicReference<Value?> = AtomicReference(null)
    private val _getMany: AtomicReference<List<Value>?> = AtomicReference(null)
    private val _set: AtomicReference<((Value) -> Unit)> = AtomicReference { /*Do Nothing on Default*/ }
    private val _calls: AtomicReference<Int> = AtomicReference(0)
    private val arguments: IsolateState<MutableList<GetOrSet>> = IsolateState { mutableListOf() }
    private val collector = AtomicReference(collector)

    override var get: Value
        @Suppress("UNCHECKED_CAST")
        get() = _get.get() as Value
        set(value) {
            provider.set(false)
            _get.set(value)
        }

    override var getMany: List<Value>
        get() = _getMany.get() as List<Value>
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                provider.set(true)
                _getMany.set(values)
            }
        }

    override var set: (Value) -> Unit
        get() = _set.get()
        set(value) {
            _set.set(value)
        }

    override val calls: Int
        get() = _calls.get()

    private fun determineNewValues(currentValues: List<Value>): List<Value> {
        return if (currentValues.size == 1) {
            currentValues
        } else {
            currentValues.drop(1)
        }
    }

    private fun retrieveValue(): Value {
        val currentValues = _getMany.value
        val value = currentValues!!.first()

        val newValues = determineNewValues(currentValues)

        _getMany.compareAndSet(currentValues, newValues)

        return value
    }

    private fun incrementInvocations() {
        val calls = this._calls.get()

        this._calls.compareAndSet(
            calls,
            calls + 1
        )
    }

    private fun captureArguments(argument: GetOrSet) {
        this.arguments.access { it.add(argument) }
    }

    private fun notifyCollector() {
        collector.get().addReference(
            this,
            this._calls.get()
        )
    }

    private fun onEvent(argument: GetOrSet) {
        captureArguments(argument)
        notifyCollector()
        incrementInvocations()
    }

    override fun onGet(): Value {
        onEvent(GetOrSet.Get)

        return when (provider.get()) {
            false -> _get.get()!!
            true -> retrieveValue()
            else -> throw MockError.MissingStub("Missing stub value for $id")
        }
    }

    override fun onSet(value: Value) {
        onEvent(GetOrSet.Set(value))

        set(value)
    }

    override fun getArgumentsForCall(callIndex: Int): GetOrSet = arguments.access { it[callIndex] }

    override fun clear() {
        provider.set(null)
        _get.set(null)
        _getMany.set(null)
        _set.set { /*Do Nothing on Default*/ }
        _calls.set(0)
        arguments.access { it.clear() }
    }
}
