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
import tech.antibytes.kmock.KMockContract.FunProxyInvocationType
import tech.antibytes.kmock.KMockContract.FunProxyState
import tech.antibytes.kmock.KMockContract.SideEffectChainBuilder
import tech.antibytes.kmock.KMockContract.VerificationChain
import tech.antibytes.kmock.error.MockError
import kotlin.math.max

/**
 * @suppress
 */
abstract class FunProxy<ReturnValue, SideEffect : Function<ReturnValue>> internal constructor(
    override val id: String,
    override val ignorableForVerification: Boolean,
    collector: Collector = NoopCollector,
    freeze: Boolean,
) : KMockContract.FunProxy<ReturnValue, SideEffect> {
    private class FreezingFunProxyState<ReturnValue, SideEffect : Function<ReturnValue>>(
        defaultInvocationType: FunProxyInvocationType,
        collector: Collector,
        setProvider: (FunProxyInvocationType) -> Unit,
    ) : FunProxyState<ReturnValue, SideEffect> {
        private val _throws: AtomicRef<Throwable?> = atomic(null)
        private val _returnValue: AtomicRef<ReturnValue?> = atomic(null)
        private val _sideEffect: AtomicRef<SideEffect?> = atomic(null)
        override val sideEffects = SideEffectChain<ReturnValue, SideEffect>(true) {
            setProvider(FunProxyInvocationType.SIDE_EFFECT_CHAIN)
        }

        private val _calls: AtomicInt = atomic(0)

        private val _collector: AtomicRef<Collector> = atomic(collector)
        private val _invocationType: AtomicRef<FunProxyInvocationType> = atomic(defaultInvocationType)

        private val _verificationChain: AtomicRef<VerificationChain?> = atomic(null)

        override var throws: Throwable? by _throws
        override var returnValue: ReturnValue? by _returnValue
        override val returnValues: MutableList<ReturnValue> = sharedMutableListOf()

        override var sideEffect: SideEffect? by _sideEffect

        override var invocationType: FunProxyInvocationType by _invocationType
        override val collector: Collector by _collector

        override val calls: Int by _calls
        override val arguments: MutableList<Array<out Any?>> = sharedMutableListOf()

        override var verificationChain: VerificationChain? by _verificationChain

        override fun incrementInvocations() {
            this._calls.incrementAndGet()
        }

        override fun clear(defaultInvocationType: FunProxyInvocationType) {
            _throws.update { null }
            _returnValue.update { null }
            returnValues.clear()
            _sideEffect.update { null }
            sideEffects.clear()

            _calls.update { 0 }
            arguments.clear()

            _verificationChain.update { null }
            _invocationType.update { defaultInvocationType }
        }
    }

    private class NonFreezingFunProxyState<ReturnValue, SideEffect : Function<ReturnValue>>(
        defaultInvocationType: FunProxyInvocationType,
        override val collector: Collector,
        setProvider: (FunProxyInvocationType) -> Unit,
    ) : FunProxyState<ReturnValue, SideEffect> {
        private var _calls = 0

        override var throws: Throwable? = null
        override var returnValue: ReturnValue? = null
        override val returnValues: MutableList<ReturnValue> = mutableListOf()
        override var sideEffect: SideEffect? = null
        override val sideEffects = SideEffectChain<ReturnValue, SideEffect>(false) {
            setProvider(FunProxyInvocationType.SIDE_EFFECT_CHAIN)
        }

        override var invocationType: FunProxyInvocationType = defaultInvocationType
        override val calls: Int
            get() = _calls
        override val arguments: MutableList<Array<out Any?>> = mutableListOf()

        override var verificationChain: VerificationChain? = null

        override fun incrementInvocations() {
            _calls += 1
        }

        override fun clear(defaultInvocationType: FunProxyInvocationType) {
            throws = null
            returnValue = null
            returnValues.clear()
            sideEffect = null
            sideEffects.clear()

            _calls = 0
            arguments.clear()

            verificationChain = null
            invocationType = defaultInvocationType
        }
    }

    private val state: FunProxyState<ReturnValue, SideEffect> = if (freeze) {
        FreezingFunProxyState(
            defaultInvocationType = FunProxyInvocationType.NO_GIVEN_VALUE,
            collector = collector,
            setProvider = ::setFunProxyInvocationType,
        )
    } else {
        NonFreezingFunProxyState(
            defaultInvocationType = FunProxyInvocationType.NO_GIVEN_VALUE,
            collector = collector,
            setProvider = ::setFunProxyInvocationType,
        )
    }
    internal val invocationType
        get() = state.invocationType

    override var verificationChain: VerificationChain?
        get() = state.verificationChain
        set(value) {
            state.verificationChain = value
        }

    private fun setFunProxyInvocationType(invocationType: FunProxyInvocationType) {
        val activeInvocationType = max(
            invocationType.value,
            state.invocationType.value
        )

        if (activeInvocationType == invocationType.value) {
            state.invocationType = invocationType
        }
    }

    override var throws: Throwable
        get() {
            return if (state.throws is Throwable) {
                state.throws as Throwable
            } else {
                throw NullPointerException()
            }
        }
        set(value) {
            setFunProxyInvocationType(FunProxyInvocationType.THROWS)
            state.throws = value
        }

    override var returnValue: ReturnValue
        @Suppress("UNCHECKED_CAST")
        get() = state.returnValue as ReturnValue
        set(value) {
            setFunProxyInvocationType(FunProxyInvocationType.RETURN_VALUE)
            state.returnValue = value
        }

    private fun _setReturnValues(values: List<ReturnValue>) {
        state.returnValues.clear()
        state.returnValues.addAll(values)
    }

    override var returnValues: List<ReturnValue>
        get() = state.returnValues.toList()
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                setFunProxyInvocationType(FunProxyInvocationType.RETURN_VALUES)
                _setReturnValues(values)
            }
        }

    override var sideEffect: SideEffect
        get() {
            return if (state.sideEffect is SideEffect) {
                state.sideEffect as SideEffect
            } else {
                throw NullPointerException()
            }
        }
        set(value) {
            setFunProxyInvocationType(FunProxyInvocationType.SIDE_EFFECT)
            state.sideEffect = value
        }

    override val sideEffects: SideEffectChainBuilder<ReturnValue, SideEffect> = state.sideEffects

    override val calls: Int
        get() = state.calls

    protected fun retrieveFromValues(): ReturnValue {
        val returnValues = state.returnValues

        return if (returnValues.size == 1) {
            returnValues.first()
        } else {
            returnValues.removeAt(0)
        }
    }

    protected fun fail(): ReturnValue {
        throw MockError.MissingStub("Missing stub value for $id")
    }

    protected fun retrieveSideEffect(): SideEffect = state.sideEffects.next()

    private fun captureArguments(arguments: Array<out Any?>) = state.arguments.add(arguments)

    private fun notifyCollector() {
        state.collector.addReference(
            this,
            state.calls
        )
    }

    protected fun onEvent(arguments: Array<out Any?>) {
        notifyCollector()
        captureArguments(arguments)
        state.incrementInvocations()
    }

    private fun <Value, Invocation : Function<Value>> setInvocationType(
        nonIntrusiveHook: NonIntrusiveFunConfigurator<Value, Invocation>
    ) {
        if (nonIntrusiveHook.isSpyable()) {
            setFunProxyInvocationType(FunProxyInvocationType.SPY)
        }

        if (nonIntrusiveHook.isRelaxable()) {
            setFunProxyInvocationType(FunProxyInvocationType.RELAXED)
        }
    }

    internal fun <Value, Invocation : Function<Value>> configureNonIntrusiveBehaviour(
        nonIntrusiveHook: KMockContract.NonIntrusiveFunConfigurator<Value, Invocation>.() -> Unit,
    ): KMockContract.NonIntrusiveFunTarget<Value, Invocation> {
        val nonIntrusiveFunConfiguration = NonIntrusiveFunConfigurator<Value, Invocation>()

        nonIntrusiveHook(nonIntrusiveFunConfiguration)

        setInvocationType(nonIntrusiveFunConfiguration)

        return nonIntrusiveFunConfiguration
    }

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?> {
        return state.arguments.getOrElse(callIndex) {
            throw throw MockError.MissingCall("$callIndex was not found for $id!")
        }
    }

    override fun clear() {
        state.clear(FunProxyInvocationType.NO_GIVEN_VALUE)
    }
}
