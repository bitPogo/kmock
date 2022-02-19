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
import tech.antibytes.util.test.MockError
import kotlin.math.max

abstract class FunMockery<ReturnValue, SideEffect : Function<ReturnValue>>(
    override val id: String,
    collector: Collector = Collector { _, _ -> Unit }
) : KMockContract.FunMockery<ReturnValue, SideEffect> {
    private val _returnValue: AtomicRef<ReturnValue?> = atomic(null)
    private val _returnValues: IsoMutableList<ReturnValue> = sharedMutableListOf()
    private val _sideEffect: AtomicRef<SideEffect?> = atomic(null)
    private val _calls: AtomicInt = atomic(0)
    private val _provider: AtomicRef<PROVIDER> = atomic(PROVIDER.NO_PROVIDER)
    protected val provider by _provider
    private val arguments: IsoMutableList<Array<out Any?>?> = sharedMutableListOf()
    private val collector: AtomicRef<Collector> = atomic(collector)

    protected enum class PROVIDER(val value: Int) {
        NO_PROVIDER(0),
        RETURN_VALUE(1),
        RETURN_VALUES(2),
        SIDE_EFFECT(3)
    }

    private fun setProvider(provider: PROVIDER) {
        val activeProvider = max(
            provider.value,
            this._provider.value.value
        )

        if (activeProvider == provider.value) {
            this._provider.update { provider }
        }
    }

    override var returnValue: ReturnValue
        @Suppress("UNCHECKED_CAST")
        get() = _returnValue.value as ReturnValue
        set(value) {
            setProvider(PROVIDER.RETURN_VALUE)
            _returnValue.update { value }
        }

    override var returnValues: List<ReturnValue>
        get() = _returnValues
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                setProvider(PROVIDER.RETURN_VALUES)
                _returnValues.clear()
                _returnValues.addAll(values)
            }
        }

    override var sideEffect: SideEffect
        get() = _sideEffect.value as SideEffect
        set(value) {
            setProvider(PROVIDER.SIDE_EFFECT)
            _sideEffect.update { value }
        }

    override val calls: Int
        get() = _calls.value

    protected fun retrieveValue(): ReturnValue = _returnValue.value!!

    protected fun retrieveFromValues(): ReturnValue {
        return if (_returnValues.size == 1) {
            _returnValues.first()
        } else {
            _returnValues.removeAt(0)
        }
    }

    protected fun retrieveSideEffect(): SideEffect = _sideEffect.value!!

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
        this._calls.incrementAndGet()
    }

    private fun notifyCollector() {
        collector.value.addReference(this, this._calls.value)
    }

    protected fun onEvent(arguments: Array<out Any?>) {
        notifyCollector()
        captureArguments(arguments)
        incrementInvocations()
    }

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?>? = arguments[callIndex]

    override fun clear() {
        _returnValue.update { null }
        _returnValues.clear()
        _sideEffect.update { null }
        _calls.update { 0 }
        _provider.update { PROVIDER.NO_PROVIDER }
        arguments.access { it.clear() }
    }
}
