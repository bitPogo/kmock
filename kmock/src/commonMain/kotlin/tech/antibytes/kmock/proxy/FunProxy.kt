/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.collections.IsoMutableList
import co.touchlab.stately.collections.sharedMutableListOf
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.ParameterizedRelaxer
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.kmock.KMockContract.SideEffectChainBuilder
import tech.antibytes.kmock.KMockContract.VerificationChain
import tech.antibytes.kmock.error.MockError
import kotlin.math.max

/**
 * @suppress
 */
abstract class FunProxy<ReturnValue, SideEffect : Function<ReturnValue>>(
    override val id: String,
    override val ignorableForVerification: Boolean,
    collector: Collector = NoopCollector,
    relaxer: Relaxer<ReturnValue>?,
    unitFunRelaxer: Relaxer<ReturnValue?>?,
    buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>?,
    private val freeze: Boolean,
    protected val spyOn: SideEffect?
) : KMockContract.FunProxy<ReturnValue, SideEffect> {
    private val _throws: AtomicRef<Throwable?> = atomic(null)
    private val _returnValue: AtomicRef<ReturnValue?> = atomic(null)
    private val _returnValues: IsoMutableList<ReturnValue> = sharedMutableListOf()
    private val _sideEffect: AtomicRef<SideEffect?> = atomic(null)

    private var _throwsUnfrozen: Throwable? = null
    private var _returnValueUnfrozen: ReturnValue? = null
    private val _returnValuesUnfrozen: MutableList<ReturnValue> = mutableListOf()
    private var _sideEffectUnfrozen: SideEffect? = null

    private val _sideEffects = SideEffectChain<ReturnValue, SideEffect>(freeze) {
        setProvider(Provider.SIDE_EFFECT_CHAIN)
    }

    private val _calls: AtomicInt = atomic(0)
    private val _provider: AtomicRef<Provider> = atomic(useSpyOrDefault())
    protected val provider by _provider

    private val collector: AtomicRef<Collector?> = atomic(resolveAtomicCollector(collector))
    private val collectorNonFreezing: Collector? = if (!freeze) {
        collector
    } else {
        null
    }

    private val arguments: IsoMutableList<Array<out Any?>> = sharedMutableListOf()
    private val relaxer: AtomicRef<Relaxer<ReturnValue>?> = atomic(relaxer)

    private val unitFunRelaxer: AtomicRef<Relaxer<ReturnValue?>?> = atomic(unitFunRelaxer)

    private val buildInRelaxer: AtomicRef<ParameterizedRelaxer<Any?, ReturnValue>?> = atomic(buildInRelaxer)

    private val _verificationChain: AtomicRef<VerificationChain?> = atomic(null)

    override var verificationChain: VerificationChain? by _verificationChain

    protected enum class Provider(val value: Int) {
        NO_PROVIDER(0),
        THROWS(1),
        RETURN_VALUE(2),
        RETURN_VALUES(3),
        SIDE_EFFECT(4),
        SIDE_EFFECT_CHAIN(5),
        SPY(6),
    }

    private fun resolveAtomicCollector(collector: Collector): Collector? {
        return if (freeze) {
            collector
        } else {
            null
        }
    }

    private fun useSpyOrDefault(): Provider {
        return if (spyOn == null) {
            Provider.NO_PROVIDER
        } else {
            Provider.SPY
        }
    }

    private fun setProvider(provider: Provider) {
        val activeProvider = max(
            provider.value,
            this._provider.value.value
        )

        if (activeProvider == provider.value) {
            this._provider.update { provider }
        }
    }

    private fun setThrowableValue(value: Throwable) {
        if (freeze) {
            _throws.update { value }
        } else {
            _throwsUnfrozen = value
        }
    }

    override var throws: Throwable
        @Suppress("UNCHECKED_CAST")
        get() {
            return if (freeze) {
                _throws.value
            } else {
                _throwsUnfrozen
            } as Throwable
        }
        set(value) {
            setProvider(Provider.THROWS)
            setThrowableValue(value)
        }

    private fun _setReturnValue(value: ReturnValue) {
        if (freeze) {
            _returnValue.update { value }
        } else {
            _returnValueUnfrozen = value
        }
    }

    override var returnValue: ReturnValue
        get() {
            @Suppress("UNCHECKED_CAST")
            return if (freeze) {
                _returnValue.value
            } else {
                _returnValueUnfrozen
            } as ReturnValue
        }
        set(value) {
            setProvider(Provider.RETURN_VALUE)
            _setReturnValue(value)
        }

    private fun _setReturnValues(values: List<ReturnValue>) {
        if (freeze) {
            _returnValues.clear()
            _returnValues.addAll(values)
        } else {
            _returnValuesUnfrozen.clear()
            _returnValuesUnfrozen.addAll(values)
        }
    }

    private fun _getReturnValues(): MutableList<ReturnValue> {
        return if (freeze) {
            _returnValues
        } else {
            _returnValuesUnfrozen
        }
    }

    override var returnValues: List<ReturnValue>
        get() = _getReturnValues().toList()
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                setProvider(Provider.RETURN_VALUES)
                _setReturnValues(values)
            }
        }

    private fun _setSideEffect(sideEffect: SideEffect) {
        if (freeze) {
            _sideEffect.update { sideEffect }
        } else {
            _sideEffectUnfrozen = sideEffect
        }
    }

    override var sideEffect: SideEffect
        get() {
            return if (freeze) {
                _sideEffect.value
            } else {
                _sideEffectUnfrozen
            } as SideEffect
        }
        set(value) {
            setProvider(Provider.SIDE_EFFECT)
            _setSideEffect(value)
        }

    override val sideEffects: SideEffectChainBuilder<ReturnValue, SideEffect> = _sideEffects

    override val calls: Int
        get() = _calls.value

    protected fun retrieveFromValues(): ReturnValue {
        val returnValues = _getReturnValues()

        return if (returnValues.size == 1) {
            returnValues.first()
        } else {
            returnValues.removeAt(0)
        }
    }

    protected fun invokeRelaxerOrFail(payload: Any?): ReturnValue {
        return buildInRelaxer.value?.invoke(payload)
            ?: unitFunRelaxer.value?.relax(id)
            ?: relaxer.value?.relax(id)
            ?: throw MockError.MissingStub("Missing stub value for $id")
    }

    protected fun retrieveSideEffect(): SideEffect = _sideEffects.next()

    private fun captureArguments(arguments: Array<out Any?>) = this.arguments.add(arguments)

    private fun incrementInvocations() {
        this._calls.incrementAndGet()
    }

    private fun notifyCollector() {
        if (freeze) {
            collector.value!!.addReference(
                this,
                this._calls.value
            )
        } else {
            collectorNonFreezing!!.addReference(
                this,
                this._calls.value
            )
        }
    }

    protected fun onEvent(arguments: Array<out Any?>) {
        notifyCollector()
        captureArguments(arguments)
        incrementInvocations()
    }

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?> {
        return arguments.getOrElse(callIndex) {
            throw throw MockError.MissingCall("$callIndex was not found for $id!")
        }
    }

    private fun clearValueHolders() {
        if (freeze) {
            _returnValue.update { null }
            _returnValues.clear()
            _sideEffect.update { null }
        } else {
            _returnValueUnfrozen = null
            _returnValuesUnfrozen.clear()
            _sideEffectUnfrozen = null
        }

        _sideEffects.clear()
    }

    override fun clear() {
        clearValueHolders()
        _calls.update { 0 }
        _provider.update { useSpyOrDefault() }
        arguments.clear()
    }
}
