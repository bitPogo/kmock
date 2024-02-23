/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.collections.sharedMutableListOf
import kotlin.math.max
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.GetOrSet
import tech.antibytes.kmock.KMockContract.PropertyProxyInvocationType
import tech.antibytes.kmock.KMockContract.PropertyProxyState
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.kmock.error.MockError

/**
 * Proxy in order to stub/mock property behaviour.
 * @constructor Creates a PropertyProxy
 * @param Value the value type of the hosting PropertyProxy.
 * @param id a unique identifier for this Proxy.
 * @param collector a optional Collector for VerificationChains. Default is a NoopCollector.
 * @param freeze boolean which indicates if freezing can be used or not. Default is true.
 * Default is null.
 * @see Collector
 * @see Relaxer
 * @author Matthias Geisler
 */
internal class PropertyProxy<Value>(
    override val id: String,
    collector: Collector = NoopCollector,
    freeze: Boolean = true,
) : KMockContract.PropertyProxy<Value> {
    override val frozen: Boolean = freeze

    private class FreezingPropertyProxyState<Value>(
        defaultInvocationType: PropertyProxyInvocationType,
        collector: Collector,
    ) : PropertyProxyState<Value> {
        private val _getValue: AtomicRef<Value?> = atomic(null)
        private val _get: AtomicRef<Function0<Value>?> = atomic(null)

        private val _set: AtomicRef<Function1<Value, Unit>?> = atomic { /*Do Nothing on Default*/ }

        private val _calls: AtomicInt = atomic(0)

        private val _collector: AtomicRef<Collector> = atomic(collector)
        private val _invocationType: AtomicRef<PropertyProxyInvocationType> = atomic(defaultInvocationType)

        override var getValue: Value? by _getValue
        override val getValues: MutableList<Value> = sharedMutableListOf()
        override var get: Function0<Value>? by _get
        override var set: Function1<Value, Unit>? by _set

        override var invocationType: PropertyProxyInvocationType by _invocationType
        override val collector: Collector by _collector

        override val calls: Int by _calls
        override val arguments: MutableList<GetOrSet> = sharedMutableListOf()

        override fun incrementInvocations() {
            this._calls.incrementAndGet()
        }

        override fun clear(defaultInvocationType: PropertyProxyInvocationType) {
            _getValue.update { null }
            getValues.clear()
            _get.update { null }

            _set.update { null }
            _calls.update { 0 }
            arguments.clear()

            _invocationType.update { defaultInvocationType }
        }
    }

    private class NonFreezingPropertyProxyState<Value>(
        defaultInvocationType: PropertyProxyInvocationType,
        override val collector: Collector,
    ) : PropertyProxyState<Value> {
        private var _calls = 0

        override var getValue: Value? = null
        override val getValues: MutableList<Value> = mutableListOf()
        override var get: Function0<Value>? = null
        override var set: Function1<Value, Unit>? = null

        override var invocationType: PropertyProxyInvocationType = defaultInvocationType
        override val calls: Int
            get() = _calls
        override val arguments: MutableList<GetOrSet> = mutableListOf()

        override fun incrementInvocations() {
            _calls += 1
        }

        override fun clear(defaultInvocationType: PropertyProxyInvocationType) {
            getValue = null
            getValues.clear()
            get = null

            set = null
            _calls = 0
            arguments.clear()

            invocationType = defaultInvocationType
        }
    }

    private val state: PropertyProxyState<Value> = if (freeze) {
        FreezingPropertyProxyState(
            defaultInvocationType = PropertyProxyInvocationType.NO_PROVIDER,
            collector = collector,
        )
    } else {
        NonFreezingPropertyProxyState(
            defaultInvocationType = PropertyProxyInvocationType.NO_PROVIDER,
            collector = collector,
        )
    }

    private fun setPropertyProxyInvocationType(invocationType: PropertyProxyInvocationType) {
        val activePropertyProxyInvocationType = max(
            invocationType.value,
            state.invocationType.value,
        )

        if (activePropertyProxyInvocationType == invocationType.value) {
            state.invocationType = invocationType
        }
    }

    override var getValue: Value
        @Suppress("UNCHECKED_CAST")
        get() = state.getValue as Value
        set(value) {
            setPropertyProxyInvocationType(PropertyProxyInvocationType.VALUE)
            state.getValue = value
        }

    override fun returns(value: Value) {
        getValue = value
    }

    private fun setGetManyValue(values: List<Value>) {
        state.getValues.clear()
        state.getValues.addAll(values)
    }

    override var getValues: List<Value>
        get() = state.getValues.toList()
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                setPropertyProxyInvocationType(PropertyProxyInvocationType.VALUES)
                setGetManyValue(values)
            }
        }

    @Deprecated(
        "This property will be replaced with 0.3.0 by getValues.",
        replaceWith = ReplaceWith("error"),
        level = DeprecationLevel.WARNING,
    )
    override var getMany: List<Value>
        get() = getValues
        set(value) {
            getValues = value
        }

    override fun returnsMany(values: List<Value>) {
        getValues = values
    }

    override var get: () -> Value
        get() {
            return if (state.get is Function0<Value>) {
                state.get as Function0<Value>
            } else {
                throw NullPointerException()
            }
        }
        set(value) {
            setPropertyProxyInvocationType(PropertyProxyInvocationType.SIDE_EFFECT)
            state.get = value
        }

    override fun runOnGet(sideEffect: () -> Value) {
        get = sideEffect
    }

    override var set: Function1<Value, Unit>
        get() {
            return if (state.set is Function1<*, *>) {
                state.set as Function1<Value, Unit>
            } else {
                throw NullPointerException()
            }
        }
        set(value) {
            state.set = value
        }

    override fun runOnSet(sideEffect: (Value) -> Unit) {
        set = sideEffect
    }

    override val calls: Int
        get() = state.calls

    private fun retrieveValue(): Value {
        val values = state.getValues

        return if (values.size == 1) {
            values.first()
        } else {
            values.removeAt(0)
        }
    }

    private fun captureArguments(argument: GetOrSet) {
        state.arguments.add(argument)
    }

    private fun notifyCollector() {
        state.collector.addReference(
            this,
            state.calls,
        )
    }

    private fun onEvent(argument: GetOrSet) {
        captureArguments(argument)
        notifyCollector()
        state.incrementInvocations()
    }

    private fun <Value> setInvocationType(
        nonIntrusiveHook: NonIntrusivePropertyConfigurator<Value>,
    ) {
        if (nonIntrusiveHook.isSpyable()) {
            setPropertyProxyInvocationType(PropertyProxyInvocationType.SPY)
        }

        if (nonIntrusiveHook.isRelaxable()) {
            setPropertyProxyInvocationType(PropertyProxyInvocationType.RELAXED)
        }
    }

    private fun <Value> configureNonIntrusiveBehaviour(
        nonIntrusiveHook: KMockContract.NonIntrusivePropertyConfigurator<Value>.() -> Unit,
    ): KMockContract.NonIntrusivePropertyTarget<Value> {
        val nonIntrusiveConfiguration = NonIntrusivePropertyConfigurator<Value>()

        nonIntrusiveHook(nonIntrusiveConfiguration)

        setInvocationType(nonIntrusiveConfiguration)

        return nonIntrusiveConfiguration
    }

    override fun executeOnGet(
        nonIntrusiveHook: KMockContract.NonIntrusivePropertyConfigurator<Value>.() -> Unit,
    ): Value {
        val nonIntrusiveConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook)
        onEvent(GetOrSet.Get)

        return when (state.invocationType) {
            PropertyProxyInvocationType.VALUE -> getValue
            PropertyProxyInvocationType.VALUES -> retrieveValue()
            PropertyProxyInvocationType.SIDE_EFFECT -> get.invoke()
            PropertyProxyInvocationType.SPY -> nonIntrusiveConfiguration.unwrapSpy()!!.invoke()
            PropertyProxyInvocationType.RELAXED -> nonIntrusiveConfiguration.unwrapRelaxer()!!.relax(id)
            else -> throw MockError.MissingStub("Missing stub value for $id")
        }
    }

    override fun executeOnSet(
        value: Value,
        nonIntrusiveHook: KMockContract.NonIntrusivePropertyConfigurator<Unit>.() -> Unit,
    ) {
        val nonIntrusiveConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook)
        onEvent(GetOrSet.Set(value))

        state.set?.invoke(value)

        nonIntrusiveConfiguration.unwrapSpy()?.invoke()
    }

    override fun getArgumentsForCall(callIndex: Int): GetOrSet {
        return state.arguments.getOrElse(callIndex) {
            throw throw MockError.MissingCall("$callIndex was not found for $id!")
        }
    }

    override fun get(callIndex: Int): GetOrSet = getArgumentsForCall(callIndex)

    override fun clear() {
        state.clear(PropertyProxyInvocationType.NO_PROVIDER)
    }
}
