/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.collections.sharedMutableListOf
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
import tech.antibytes.kmock.KMockContract.AssertionChain
import tech.antibytes.kmock.error.MockError
import kotlin.math.max

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
    private class FreezingPropertyProxyState<Value>(
        defaultInvocationType: PropertyProxyInvocationType,
        collector: Collector,
    ) : PropertyProxyState<Value> {
        private val _get: AtomicRef<Value?> = atomic(null)
        private val _sideEffect: AtomicRef<Function0<Value>?> = atomic(null)

        private val _set: AtomicRef<Function1<Value, Unit>?> = atomic { /*Do Nothing on Default*/ }

        private val _calls: AtomicInt = atomic(0)

        private val _collector: AtomicRef<Collector> = atomic(collector)
        private val _invocationType: AtomicRef<PropertyProxyInvocationType> = atomic(defaultInvocationType)

        private val _assertionChain: AtomicRef<AssertionChain?> = atomic(null)

        override var get: Value? by _get
        override val getMany: MutableList<Value> = sharedMutableListOf()
        override var sideEffect: Function0<Value>? by _sideEffect
        override var set: Function1<Value, Unit>? by _set

        override var invocationType: PropertyProxyInvocationType by _invocationType
        override val collector: Collector by _collector

        override val calls: Int by _calls
        override val arguments: MutableList<GetOrSet> = sharedMutableListOf()

        override var assertionChain: AssertionChain? by _assertionChain

        override fun incrementInvocations() {
            this._calls.incrementAndGet()
        }

        override fun clear(defaultInvocationType: PropertyProxyInvocationType) {
            _get.update { null }
            getMany.clear()
            _sideEffect.update { null }

            _set.update { null }
            _calls.update { 0 }
            arguments.clear()

            _assertionChain.update { null }
            _invocationType.update { defaultInvocationType }
        }
    }

    private class NonFreezingPropertyProxyState<Value>(
        defaultInvocationType: PropertyProxyInvocationType,
        override val collector: Collector,
    ) : PropertyProxyState<Value> {
        private var _calls = 0

        override var get: Value? = null
        override val getMany: MutableList<Value> = mutableListOf()
        override var sideEffect: Function0<Value>? = null
        override var set: Function1<Value, Unit>? = null

        override var invocationType: PropertyProxyInvocationType = defaultInvocationType
        override val calls: Int
            get() = _calls
        override val arguments: MutableList<GetOrSet> = mutableListOf()

        override var assertionChain: AssertionChain? = null

        override fun incrementInvocations() {
            _calls += 1
        }

        override fun clear(defaultInvocationType: PropertyProxyInvocationType) {
            get = null
            getMany.clear()
            sideEffect = null

            set = null
            _calls = 0
            arguments.clear()

            assertionChain = null
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
            state.invocationType.value
        )

        if (activePropertyProxyInvocationType == invocationType.value) {
            state.invocationType = invocationType
        }
    }

    override var get: Value
        @Suppress("UNCHECKED_CAST")
        get() = state.get as Value
        set(value) {
            setPropertyProxyInvocationType(PropertyProxyInvocationType.VALUE)
            state.get = value
        }

    private fun setGetManyValue(values: List<Value>) {
        state.getMany.clear()
        state.getMany.addAll(values)
    }

    override var getMany: List<Value>
        get() = state.getMany.toList()
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                setPropertyProxyInvocationType(PropertyProxyInvocationType.VALUES)
                setGetManyValue(values)
            }
        }

    override var getSideEffect: Function0<Value>
        get() {
            return if (state.sideEffect is Function0<Value>) {
                state.sideEffect as Function0<Value>
            } else {
                throw NullPointerException()
            }
        }
        set(value) {
            setPropertyProxyInvocationType(PropertyProxyInvocationType.SIDE_EFFECT)
            state.sideEffect = value
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

    override val calls: Int
        get() = state.calls

    override var assertionChain: AssertionChain?
        get() = state.assertionChain
        set(value) {
            state.assertionChain = value
        }

    private fun retrieveValue(): Value {
        val values = state.getMany

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
            state.calls
        )
    }

    private fun onEvent(argument: GetOrSet) {
        captureArguments(argument)
        notifyCollector()
        state.incrementInvocations()
    }

    private fun <Value> setInvocationType(
        nonIntrusiveHook: NonIntrusivePropertyConfigurator<Value>
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

    override fun onGet(
        nonIntrusiveHook: KMockContract.NonIntrusivePropertyConfigurator<Value>.() -> Unit,
    ): Value {
        val nonIntrusiveConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook)
        onEvent(GetOrSet.Get)

        return when (state.invocationType) {
            PropertyProxyInvocationType.VALUE -> get
            PropertyProxyInvocationType.VALUES -> retrieveValue()
            PropertyProxyInvocationType.SIDE_EFFECT -> getSideEffect.invoke()
            PropertyProxyInvocationType.SPY -> nonIntrusiveConfiguration.unwrapSpy()!!.invoke()
            PropertyProxyInvocationType.RELAXED -> nonIntrusiveConfiguration.unwrapRelaxer()!!.relax(id)
            else -> throw MockError.MissingStub("Missing stub value for $id")
        }
    }

    override fun onSet(
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

    override fun clear() {
        state.clear(PropertyProxyInvocationType.NO_PROVIDER)
    }
}
