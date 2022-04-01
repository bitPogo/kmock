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
import tech.antibytes.kmock.KMockContract.GetOrSet
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.kmock.KMockContract.VerificationChain
import tech.antibytes.kmock.error.MockError
import kotlin.math.max

/**
 * Proxy in order to stub/mock property behaviour.
 * @constructor Creates a PropertyProxy
 * @param Value the value of the Property.
 * @param id a unique identifier for this Proxy.
 * @param collector a optional Collector for VerificationChains. Default is a NoopCollector.
 * @param relaxer a optional Relaxer for autogenerated values. Default is null.
 * @param freeze boolean which indicates if freezing can be used or not. Default is true.
 * @param spyOnGet a optional getter function reference which is wrapped by this proxy and will be invoked if given.
 * @param spyOnSet a optional setter function reference which is wrapped by this proxy and will be invoked if given.
 * Default is null.
 * @see Collector
 * @see Relaxer
 * @author Matthias Geisler
 */
internal class PropertyProxy<Value>(
    override val id: String,
    collector: Collector = NoopCollector,
    relaxer: Relaxer<Value>? = null,
    private val freeze: Boolean = true,
    private val spyOnGet: Function0<Value>? = null,
    private val spyOnSet: Function1<Value, Unit>? = null
) : KMockContract.PropertyProxy<Value> {
    private val provider: AtomicRef<Provider> = atomic(useSpyOrDefault())

    private val _get: AtomicRef<Value?> = atomic(null)
    private val _getMany: IsoMutableList<Value> = sharedMutableListOf()
    private val _sideEffect: AtomicRef<Function0<Value>?> = atomic(null)

    private var _getUnfrozen: Value? = null
    private val _getManyUnfrozen: MutableList<Value> = mutableListOf()
    private var _sideEffectUnfrozen: Function0<Value>? = null

    private val collector: AtomicRef<Collector?> = atomic(resolveAtomicCollector(collector))
    private val collectorNonFreezing: Collector? = if (!freeze) {
        collector
    } else {
        null
    }

    private val _set: AtomicRef<((Value) -> Unit)> = atomic { /*Do Nothing on Default*/ }
    private var _setUnfrozen: Function1<Value, Unit> = { /*Do Nothing on Default*/ }

    private val _calls: AtomicInt = atomic(0)
    private val arguments: IsoMutableList<GetOrSet> = sharedMutableListOf()
    private val relaxer: AtomicRef<Relaxer<Value>?> = atomic(relaxer)

    private val _verificationChain: AtomicRef<VerificationChain?> = atomic(null)
    override var verificationChain: VerificationChain? by _verificationChain

    private enum class Provider(val value: Int) {
        NO_PROVIDER(0),
        VALUE(1),
        VALUES(2),
        SIDE_EFFECT(3),
        SPY(4),
    }

    private fun resolveAtomicCollector(collector: Collector): Collector? {
        return if (freeze) {
            collector
        } else {
            null
        }
    }

    private fun useSpyOrDefault(): Provider {
        return if (spyOnGet == null) {
            Provider.NO_PROVIDER
        } else {
            Provider.SPY
        }
    }

    private fun setProvider(provider: Provider) {
        val activeProvider = max(
            provider.value,
            this.provider.value.value
        )

        if (activeProvider == provider.value) {
            this.provider.update { provider }
        }
    }

    private fun setGetValue(value: Value) {
        if (freeze) {
            _get.update { value }
        } else {
            _getUnfrozen = value
        }
    }

    override var get: Value
        @Suppress("UNCHECKED_CAST")
        get() {
            return if (freeze) {
                _get.value
            } else {
                _getUnfrozen
            } as Value
        }
        set(value) {
            setProvider(Provider.VALUE)
            setGetValue(value)
        }

    private fun setGetManyValue(values: List<Value>) {
        if (freeze) {
            _getMany.clear()
            _getMany.addAll(values)
        } else {
            _getManyUnfrozen.clear()
            _getManyUnfrozen.addAll(values)
        }
    }

    private fun _getGetMany(): MutableList<Value> {
        return if (freeze) {
            _getMany
        } else {
            _getManyUnfrozen
        }
    }

    override var getMany: List<Value>
        get() = _getGetMany().toList()
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                setProvider(Provider.VALUES)
                setGetManyValue(values)
            }
        }

    private fun _setGetSideEffect(sideEffect: Function0<Value>) {
        if (freeze) {
            _sideEffect.update { sideEffect }
        } else {
            _sideEffectUnfrozen = sideEffect
        }
    }

    override var getSideEffect: Function0<Value>
        get() {
            return if (freeze) {
                _sideEffect.value
            } else {
                _sideEffectUnfrozen
            } as Function0<Value>
        }
        set(value) {
            setProvider(Provider.SIDE_EFFECT)
            _setGetSideEffect(value)
        }

    private fun setSetSideEffect(sideEffect: Function1<Value, Unit>) {
        if (freeze) {
            _set.update { sideEffect }
        } else {
            _setUnfrozen = sideEffect
        }
    }

    override var set: (Value) -> Unit
        get() {
            return if (freeze) {
                _set.value
            } else {
                _setUnfrozen
            }
        }
        set(value) {
            setSetSideEffect(value)
        }

    override val calls: Int
        get() = _calls.value

    private fun retrieveValue(): Value {
        val values = _getGetMany()

        return if (values.size == 1) {
            values.first()
        } else {
            values.removeAt(0)
        }
    }

    private fun incrementInvocations() {
        _calls.incrementAndGet()
    }

    private fun captureArguments(argument: GetOrSet) {
        this.arguments.add(argument)
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
            Provider.VALUE -> get
            Provider.VALUES -> retrieveValue()
            Provider.SIDE_EFFECT -> getSideEffect.invoke()
            Provider.SPY -> spyOnGet!!.invoke()
            else -> invokeRelaxerOrFail()
        }
    }

    override fun onSet(value: Value) {
        onEvent(GetOrSet.Set(value))

        set(value)

        spyOnSet?.invoke(value)
    }

    override fun getArgumentsForCall(callIndex: Int): GetOrSet = arguments[callIndex]

    private fun clearValueHolders() {
        if (freeze) {
            _get.update { null }
            _getMany.clear()
            _sideEffect.update { null }
            _set.update { { /*Do Nothing on Default*/ } }
        } else {
            _getUnfrozen = null
            _getManyUnfrozen.clear()
            _sideEffectUnfrozen = null
            _setUnfrozen = { /*Do Nothing on Default*/ }
        }
    }

    override fun clear() {
        provider.update { useSpyOrDefault() }
        clearValueHolders()
        _calls.update { 0 }
        arguments.clear()
    }
}
