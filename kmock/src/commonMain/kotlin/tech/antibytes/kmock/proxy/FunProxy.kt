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
import tech.antibytes.kmock.KMockContract.FunProxyInvocationType
import tech.antibytes.kmock.KMockContract.ParameterizedRelaxer
import tech.antibytes.kmock.KMockContract.Relaxer
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
    relaxer: Relaxer<ReturnValue>?,
    unitFunRelaxer: Relaxer<ReturnValue?>?,
    buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>?,
    freeze: Boolean,
    protected val spyOn: SideEffect?
) : KMockContract.FunProxy<ReturnValue, SideEffect> {
    private class FunProxyValueContainer<ReturnValue, SideEffect : Function<ReturnValue>>(
        defaultInvocationType: FunProxyInvocationType,
        collector: Collector,
        relaxer: Relaxer<ReturnValue>?,
        unitFunRelaxer: Relaxer<ReturnValue?>?,
        buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>?,
        setProvider: (FunProxyInvocationType) -> Unit,
    ) : KMockContract.FunProxyValueContainer<ReturnValue, SideEffect> {
        private val _throws: AtomicRef<Throwable?> = atomic(null)
        private val _returnValue: AtomicRef<ReturnValue?> = atomic(null)
        private val _returnValues: IsoMutableList<ReturnValue> = sharedMutableListOf()
        private val _sideEffect: AtomicRef<SideEffect?> = atomic(null)
        override val sideEffects = SideEffectChain<ReturnValue, SideEffect> {
            setProvider(FunProxyInvocationType.SIDE_EFFECT_CHAIN)
        }

        private val _calls: AtomicInt = atomic(0)

        private val _collector: AtomicRef<Collector> = atomic(collector)
        private val _invocationType: AtomicRef<FunProxyInvocationType> = atomic(defaultInvocationType)

        private val _relaxer: AtomicRef<Relaxer<ReturnValue>?> = atomic(relaxer)
        private val _unitFunRelaxer: AtomicRef<Relaxer<ReturnValue?>?> = atomic(unitFunRelaxer)
        private val _buildInRelaxer: AtomicRef<ParameterizedRelaxer<Any?, ReturnValue>?> = atomic(buildInRelaxer)
        private val _verificationChain: AtomicRef<VerificationChain?> = atomic(null)

        override var throws: Throwable? by _throws
        override var returnValue: ReturnValue? by _returnValue
        override val returnValues: MutableList<ReturnValue> = _returnValues

        override var sideEffect: SideEffect? by _sideEffect
        override var invocationType: FunProxyInvocationType by _invocationType

        override val collector: Collector by _collector

        override val calls: Int by _calls
        override val arguments: MutableList<Array<out Any?>> = sharedMutableListOf()

        override val relaxer: Relaxer<ReturnValue>? by _relaxer
        override val unitFunRelaxer: Relaxer<ReturnValue?>? by _unitFunRelaxer
        override val buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>? by _buildInRelaxer
        override var verificationChain: VerificationChain? by _verificationChain

        override fun incrementInvocations() {
            this._calls.incrementAndGet()
        }

        override fun clear(defaultInvocationType: FunProxyInvocationType) {
            _throws.update { null }
            _returnValue.update { null }
            _returnValues.clear()
            _sideEffect.update { null }
            sideEffects.clear()

            _calls.update { 0 }
            arguments.clear()

            _verificationChain.update { null }
            _invocationType.update { defaultInvocationType }
        }
    }

    private class NonFreezingFunProxyValueContainer<ReturnValue, SideEffect : Function<ReturnValue>>(
        defaultInvocationType: FunProxyInvocationType,
        override val collector: Collector,
        override val relaxer: Relaxer<ReturnValue>?,
        override val unitFunRelaxer: Relaxer<ReturnValue?>?,
        override val buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>?,
        setProvider: (FunProxyInvocationType) -> Unit,
    ) : KMockContract.FunProxyValueContainer<ReturnValue, SideEffect> {
        override val sideEffects = NonFreezingSideEffectChain<ReturnValue, SideEffect> {
            setProvider(FunProxyInvocationType.SIDE_EFFECT_CHAIN)
        }
        private var _calls = 0

        override var throws: Throwable? = null
        override var returnValue: ReturnValue? = null
        override val returnValues: MutableList<ReturnValue> = mutableListOf()

        override var sideEffect: SideEffect? = null
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

    private val valueContainer: KMockContract.FunProxyValueContainer<ReturnValue, SideEffect> = if (freeze) {
        FunProxyValueContainer(
            defaultInvocationType = useSpyOrDefault(),
            collector = collector,
            relaxer = relaxer,
            buildInRelaxer = buildInRelaxer,
            unitFunRelaxer = unitFunRelaxer,
            setProvider = ::setProvider,
        )
    } else {
        NonFreezingFunProxyValueContainer(
            defaultInvocationType = useSpyOrDefault(),
            collector = collector,
            relaxer = relaxer,
            buildInRelaxer = buildInRelaxer,
            unitFunRelaxer = unitFunRelaxer,
            setProvider = ::setProvider,
        )
    }
    internal val provider
        get() = valueContainer.invocationType

    override var verificationChain: VerificationChain?
        get() = valueContainer.verificationChain
        set(value) {
            valueContainer.verificationChain = value
        }

    private fun useSpyOrDefault(): FunProxyInvocationType {
        return if (spyOn == null) {
            FunProxyInvocationType.NO_GIVEN_VALUE
        } else {
            FunProxyInvocationType.SPY
        }
    }

    private fun setProvider(invocationType: FunProxyInvocationType) {
        val activeProvider = max(
            invocationType.value,
            valueContainer.invocationType.value
        )

        if (activeProvider == invocationType.value) {
            valueContainer.invocationType = invocationType
        }
    }

    override var throws: Throwable
        get() {
            return if (valueContainer.throws is Throwable) {
                valueContainer.throws as Throwable
            } else {
                throw NullPointerException()
            }
        }
        set(value) {
            setProvider(FunProxyInvocationType.THROWS)
            valueContainer.throws = value
        }

    override var returnValue: ReturnValue
        @Suppress("UNCHECKED_CAST")
        get() = valueContainer.returnValue as ReturnValue
        set(value) {
            setProvider(FunProxyInvocationType.RETURN_VALUE)
            valueContainer.returnValue = value
        }

    private fun _setReturnValues(values: List<ReturnValue>) {
        valueContainer.returnValues.clear()
        valueContainer.returnValues.addAll(values)
    }

    override var returnValues: List<ReturnValue>
        get() = valueContainer.returnValues.toList()
        set(values) {
            if (values.isEmpty()) {
                throw MockError.MissingStub("Empty Lists are not valid as value provider.")
            } else {
                setProvider(FunProxyInvocationType.RETURN_VALUES)
                _setReturnValues(values)
            }
        }

    override var sideEffect: SideEffect
        get() {
            return if (valueContainer.sideEffect is SideEffect) {
                valueContainer.sideEffect as SideEffect
            } else {
                throw NullPointerException()
            }
        }
        set(value) {
            setProvider(FunProxyInvocationType.SIDE_EFFECT)
            valueContainer.sideEffect = value
        }

    override val sideEffects: SideEffectChainBuilder<ReturnValue, SideEffect> = valueContainer.sideEffects

    override val calls: Int
        get() = valueContainer.calls

    protected fun retrieveFromValues(): ReturnValue {
        val returnValues = valueContainer.returnValues

        return if (returnValues.size == 1) {
            returnValues.first()
        } else {
            returnValues.removeAt(0)
        }
    }

    protected fun invokeRelaxerOrFail(payload: Any?): ReturnValue {
        return valueContainer.buildInRelaxer?.invoke(payload)
            ?: valueContainer.unitFunRelaxer?.relax(id)
            ?: valueContainer.relaxer?.relax(id)
            ?: throw MockError.MissingStub("Missing stub value for $id")
    }

    protected fun retrieveSideEffect(): SideEffect = valueContainer.sideEffects.next()

    private fun captureArguments(arguments: Array<out Any?>) = valueContainer.arguments.add(arguments)

    private fun notifyCollector() {
        valueContainer.collector.addReference(
            this,
            valueContainer.calls
        )
    }

    protected fun onEvent(arguments: Array<out Any?>) {
        notifyCollector()
        captureArguments(arguments)
        valueContainer.incrementInvocations()
    }

    override fun getArgumentsForCall(callIndex: Int): Array<out Any?> {
        return valueContainer.arguments.getOrElse(callIndex) {
            throw throw MockError.MissingCall("$callIndex was not found for $id!")
        }
    }

    override fun clear() {
        valueContainer.clear(useSpyOrDefault())
    }
}
