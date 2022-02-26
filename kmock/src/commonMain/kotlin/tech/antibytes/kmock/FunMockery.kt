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
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.util.test.MockError
import kotlin.math.max

abstract class FunMockery<ReturnValue, SideEffect : Function<ReturnValue>>(
    override val id: String,
    collector: Collector = Collector { _, _ -> Unit },
    relaxer: Relaxer<ReturnValue>?,
    private val freeze: Boolean,
    protected val spyOn: SideEffect?
) : KMockContract.FunMockery<ReturnValue, SideEffect> {
    private val _returnValue: AtomicRef<ReturnValue?> = atomic(null)
    private val _returnValues: IsoMutableList<ReturnValue> = sharedMutableListOf()
    private val _sideEffect: AtomicRef<SideEffect?> = atomic(null)

    private var _returnValueUnfrozen: ReturnValue? = null
    protected abstract var _returnValuesUnfrozen: MutableList<ReturnValue>
    private var _sideEffectUnfrozen: SideEffect? = null

    private val _calls: AtomicInt = atomic(0)
    private val _provider: AtomicRef<Provider> = atomic(useSpyOrDefault())
    protected val provider by _provider

    private val arguments: IsoMutableList<Array<out Any?>?> = sharedMutableListOf()
    private val collector: AtomicRef<Collector> = atomic(collector)
    private val relaxer: AtomicRef<Relaxer<ReturnValue>?> = atomic(relaxer)

    private val _verificationBuilder: AtomicRef<KMockContract.VerificationChainBuilder?> = atomic(null)
    override var verificationBuilderReference: KMockContract.VerificationChainBuilder? by _verificationBuilder

    protected enum class Provider(val value: Int) {
        NO_PROVIDER(0),
        RETURN_VALUE(1),
        RETURN_VALUES(2),
        SIDE_EFFECT(3),
        SPY(4),
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

    protected fun invokeRelaxerOrFail(): ReturnValue {
        return relaxer.value?.relax(id)
            ?: throw MockError.MissingStub("Missing stub value for $id")
    }

    private fun guardArguments(arguments: Array<out Any?>): Array<out Any?>? {
        return if (arguments.isEmpty()) {
            null
        } else {
            arguments
        }
    }

    private fun captureArguments(arguments: Array<out Any?>) {
        this.arguments.add(guardArguments(arguments))
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
    }

    override fun clear() {
        clearValueHolders()
        _calls.update { 0 }
        _provider.update { useSpyOrDefault() }
        arguments.clear()
    }
}
