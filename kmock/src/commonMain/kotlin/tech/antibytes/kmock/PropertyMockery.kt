/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.collections.IsoMutableList
import co.touchlab.stately.collections.sharedMutableListOf
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.GetOrSet
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.util.test.MockError

class PropertyMockery<Value>(
    override val id: String,
    collector: Collector = Collector { _, _ -> Unit },
    relaxer: Relaxer<Value>? = null,
) : KMockContract.PropertyMockery<Value> {
    private val provider: AtomicRef<Boolean?> = atomic(null)
    private val _get: AtomicRef<Value?> = atomic(null)
    private val _getMany: IsoMutableList<Value> = sharedMutableListOf()
    private val _set: AtomicRef<((Value) -> Unit)> = atomic { /*Do Nothing on Default*/ }
    private val _calls: AtomicInt = atomic(0)
    private val arguments: IsoMutableList<GetOrSet> = sharedMutableListOf()
    private val collector: AtomicRef<Collector> = atomic(collector)
    private val relaxer: AtomicRef<Relaxer<Value>?> = atomic(relaxer)

    override var get: Value
        @Suppress("UNCHECKED_CAST")
        get() = _get.value as Value
        set(value) {
            provider.getAndSet(false)
            _get.getAndSet(value)
        }

    override var getMany: List<Value>
        get() = _getMany.toList()
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                provider.getAndSet(true)
                _getMany.clear()
                _getMany.addAll(values)
            }
        }

    override var set: (Value) -> Unit
        get() = _set.value
        set(value) {
            _set.update { value }
        }

    override val calls: Int
        get() = _calls.value

    private fun retrieveValue(): Value {
        return if (_getMany.size == 1) {
            _getMany.first()
        } else {
            _getMany.removeAt(0)
        }
    }

    private fun incrementInvocations() {
        _calls.incrementAndGet()
    }

    private fun captureArguments(argument: GetOrSet) {
        this.arguments.add(argument)
    }

    private fun notifyCollector() {
        collector.value.addReference(
            this,
            this._calls.value
        )
    }

    private fun onEvent(argument: GetOrSet) {
        captureArguments(argument)
        notifyCollector()
        incrementInvocations()
    }

    private fun invokeRelaxerOrFail(): Value {
        return relaxer.value?.relax(id)
            ?: throw MockError.MissingStub("Missing stub value for $id")
    }

    override fun onGet(): Value {
        onEvent(GetOrSet.Get)

        return when (provider.value) {
            false -> _get.value!!
            true -> retrieveValue()
            else -> invokeRelaxerOrFail()
        }
    }

    override fun onSet(value: Value) {
        onEvent(GetOrSet.Set(value))

        set(value)
    }

    override fun getArgumentsForCall(callIndex: Int): GetOrSet = arguments[callIndex]

    override fun clear() {
        provider.update { null }
        _get.update { null }
        _getMany.clear()
        _set.update { { /*Do Nothing on Default*/ } }
        _calls.update { 0 }
        arguments.clear()
    }
}
