/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.error.MockError.MissingCall
import tech.antibytes.kmock.error.MockError.MissingStub
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.verification.constraints.any
import tech.antibytes.kmock.verification.constraints.eq
import tech.antibytes.kmock.verification.constraints.isNot
import tech.antibytes.kmock.verification.constraints.isNotSame
import tech.antibytes.kmock.verification.constraints.isSame
import kotlin.reflect.KClass

/**
 * Contract Container of KMock
 * @author Matthias Geisler
 */
object KMockContract {
    /**
     * Base Proxy definition
     * @param ReturnValue the return value of the Proxy.
     * @param Arguments the arguments which are delegated to the Proxy.
     * @author Matthias Geisler
     */
    sealed interface Proxy<ReturnValue, Arguments> {
        /**
         * Unique Id of the proxy derived from the Interface which it build upon.
         */
        val id: String

        /**
         * Counter of the actual invocations of the proxy.
         */
        val calls: Int

        /**
         * Reference to its correspondent VerificationChain. This Property is intended for internal use only!
         * @suppress
         */
        var verificationChain: VerificationChain?

        /**
         * Resolves given arguments of an invocation.
         * @param callIndex index of an invocation.
         * @return the Arguments of the given invocation or null if the proxy is used for void invocations.
         * @throws MissingCall if the callIndex is invalid.
         */
        fun getArgumentsForCall(callIndex: Int): Arguments

        /**
         * Clears the Proxies captured arguments
         */
        fun clear()
    }

    /**
     * Wrapper for injected Relaxers, which is internally used to reference a Relaxer. The relaxer is invoke per Proxy.
     * @param ReturnValue the return type of the Relaxer.
     * @see tech.antibytes.kmock.Relaxer
     * @author Matthias Geisler
     */
    internal fun interface Relaxer<ReturnValue> {
        /**
         * Invokes the injected Relaxer.
         * @param id of the invoking Proxy in order to enable fine grained differentiation between overlapping types.
         * @return the given Relaxer Type.
         */
        fun relax(id: String): ReturnValue
    }

    /**
     * Extractor of bounded SpyTarget.
     * @param Value the value type of the hosting Proxy.
     * @param SpyTarget the function signature of the spy target closure.
     * @author Matthias Geisler
     */
    internal interface SpyTarget<Value, SpyTarget : Function<Value>> {
        /**
         * Indicates if a SpyTarget was bound or not.
         */
        fun isSpyable(): Boolean

        /**
         * Unwraps a bounded SpyTarget for an invocation.
         */
        fun unwrapSpy(): SpyTarget?
    }

    /**
     * Binds a SpyTarget to a invocation.
     * @param Value the value type of the hosting Proxy.
     * @author Matthias Geisler
     */
    interface PropertySpyTargetInvocation<Value> {
        /**
         * Binds the given function to the Proxy.
         * It wipes a given relaxer and buildInRelaxer.
         * @param spyTarget the referenced object which is spied upon.
         * @param spyOn the referenced Spy method.
         */
        fun useSpyIf(spyTarget: Any?, spyOn: Function0<Value>)
    }

    /**
     * Binds a SpyTarget to a invocation.
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SpyTarget the function signature of the spy target closure.
     * @author Matthias Geisler
     */
    interface MethodSpyTargetInvocation<ReturnValue, SpyTarget : Function<ReturnValue>> {
        /**
         * Binds the given function to the Proxy.
         * @param spyTarget the referenced object which is spied upon.
         * @param spyOn the referenced Spy method.
         */
        fun useSpyIf(spyTarget: Any?, spyOn: SpyTarget)

        /**
         * Binds the given function, which should be equals, to the Proxy.
         * The spy equals method will be used if the other object is not of the same type as the given Class of the hosting mock.
         * The equals method will be used if the other object is of the same type as the given Class of the hosting mock.
         * @param spyTarget the referenced object which is spied upon.
         * @param other the object to compare to if the hosting mock is used instead of the spy target.
         * @param spyOn function which should reference the spy target equals method.
         * @param mockKlass the KClass of the hosting mock.
         */
        fun useSpyOnEqualsIf(
            spyTarget: Any?,
            other: Any?,
            spyOn: Function1<Any?, Boolean>,
            mockKlass: KClass<out Any>,
        )
    }

    /**
     * Extractor of bounded Relaxer.
     * @param Value the value type of the hosting Proxy.
     * @author Matthias Geisler
     */
    internal interface RelaxationTarget<Value> {
        /**
         * Indicates if a Relaxer was bound or not.
         */
        fun isRelaxable(): Boolean

        /**
         * Unwraps a bounded Relaxer for an invocation.
         */
        fun unwrapRelaxer(): Relaxer<Value>?
    }

    /**
     * Configures non intrusive behaviour for Proxies.
     * @param Value the value type of the hosting PropertyProxy.
     * @author Matthias Geisler
     */
    interface RelaxationConfigurator<Value> {
        /**
         * Binds a given Relaxer function to a Proxy if the condition is true.
         * This will wipe a given relaxer.
         * @param condition which determines if the relaxer should be invoked or not.
         * @param relaxer the relaxer method.
         */
        fun useRelaxerIf(condition: Boolean, relaxer: Function1<String, Value>)
    }

    /**
     * Configures non intrusive Behaviour for FunProxies.
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SideEffect the function signature of the hosting Proxy.
     * @see RelaxationConfigurator
     * @author Matthias Geisler
     */
    interface RelaxationFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> : RelaxationConfigurator<ReturnValue> {

        /**
         * Binds the internal UnitFunRelaxer to the Proxy, if the given condition is true.
         * It wipes a given relaxer.
         * @param condition Boolean to determine if the the function is relaxed or not.
         */
        fun useUnitFunRelaxerIf(condition: Boolean)
    }

    /**
     * Configures non intrusive Behaviour for PropertyProxies.
     * @param Value the value type of the hosting PropertyProxy.
     * @see RelaxationConfigurator
     * @author Matthias Geisler
     */
    interface RelaxationPropertyConfigurator<Value> : RelaxationConfigurator<Value>

    /**
     * Configurator for non intrusive behaviour of PropertyProxies.
     * @author Matthias Geisler
     */
    interface NonIntrusivePropertyConfigurator<Value> : RelaxationPropertyConfigurator<Value>, PropertySpyTargetInvocation<Value>

    /**
     * Configurator for non intrusive behaviour of FunProxies.
     * @author Matthias Geisler
     */
    interface NonIntrusiveFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> :
        RelaxationFunConfigurator<ReturnValue, SideEffect>, MethodSpyTargetInvocation<ReturnValue, SideEffect>

    /**
     * Extractor for non intrusive behaviour of PropertyProxies.
     * @author Matthias Geisler
     */
    internal interface NonIntrusivePropertyTarget<Value> : RelaxationTarget<Value>, SpyTarget<Value, Function0<Value>>

    /**
     * Extractor for non intrusive behaviour of FunProxies.
     * @author Matthias Geisler
     */
    internal interface NonIntrusiveFunTarget<ReturnValue, SideEffect : Function<ReturnValue>> : RelaxationTarget<ReturnValue>, SpyTarget<ReturnValue, SideEffect>

    /**
     * Base State definition for FunProxies and PropertyProxies of shared mutable states to separate frozen and unfrozen behaviour.
     * @param Value the return value of the hosting Proxy.
     * @param InvocationType invocation type of the hosting proxy.
     * @param Arguments type of arguments the hosting proxy can capture.
     * @see FunProxyState
     * @see PropertyProxyState
     * @see FunProxyInvocationType
     * @see PropertyProxyInvocationType
     * @author Matthias Geisler
     */
    internal interface ProxyState<Value, InvocationType, Arguments> {
        /**
         * Holds the indicated the InvocationType.
         */
        var invocationType: InvocationType

        /**
         * Holds a given Collector/Verifier
         * @see Verifier
         * @see Collector
         */
        val collector: Collector

        /**
         * Holds the current call index/total amount of invocation of the hosting proxy
         */
        val calls: Int

        /**
         * Holds the captured arguments of invocations of the hosting proxy
         */
        val arguments: MutableList<Arguments>

        /**
         * Holds a given VerificationChain.
         * @see VerificationChain
         * @see tech.antibytes.kmock.verification.verify
         * @see tech.antibytes.kmock.verification.verifyStrictOrder
         */
        var verificationChain: VerificationChain?

        /**
         * Increments calls.
         */
        fun incrementInvocations()

        /**
         * Resets all states to their default state (null/empty).
         * @param defaultInvocationType the default state of invocationType.
         */
        fun clear(defaultInvocationType: InvocationType)
    }

    /**
     * Stae Container for SideEffectChains to separate frozen and unfrozen behaviour.
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SideEffect the function signature of the hosting Proxy.
     * @see SideEffectChain
     * @author Matthias Geisler
     */
    internal interface SideEffectChainState<ReturnValue, SideEffect : Function<ReturnValue>> {
        val onAdd: Function0<Unit>
        val sideEffects: MutableList<SideEffect>
    }

    /**
     * Builder for chained SideEffects.
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SideEffect the function signature of the hosting Proxy.
     * @author Matthias Geisler
     */
    interface SideEffectChainBuilder<ReturnValue, SideEffect : Function<ReturnValue>> {
        /**
         * Adds a SideEffect to chain.
         * @param sideEffect the given SideEffect.
         * @return SideEffectChainBuilder the current builder.
         */
        fun add(sideEffect: SideEffect): SideEffectChainBuilder<ReturnValue, SideEffect>

        /**
         * Adds a multiple SideEffects to chain.
         * @param sideEffect the given SideEffect.
         * @return SideEffectChainBuilder the current builder.
         */
        fun addAll(sideEffect: Iterable<SideEffect>): SideEffectChainBuilder<ReturnValue, SideEffect>
    }

    /**
     * Container for chained SideEffects. If the given Container has
     * a smaller size than the actual invocation the last value of is repeatably used.
     * It acts in a FIFO manner.
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SideEffect the function signature of the hosting Proxy.
     * @see SideEffectChainBuilder
     * @author Matthias Geisler
     */
    internal interface SideEffectChain<ReturnValue, SideEffect : Function<ReturnValue>> : SideEffectChainBuilder<ReturnValue, SideEffect> {
        /**
         * Returns the oldest chained SideEffect. If no SideEffects in the chain it fails.
         * @return SideEffect a previous stored SideEffect.
         * @throws IllegalStateException if no SideEffect was stored previously.
         */
        fun next(): SideEffect

        /**
         * Clears the chain
         */
        fun clear()
    }

    /**
     * Invocation types for FunProxies.
     * @param value indicates the invocation precedence.
     * @author Matthias Geisler
     */
    internal enum class FunProxyInvocationType(val value: Int) {
        NO_GIVEN_VALUE(0),
        RELAXED(1),
        THROWS(2),
        RETURN_VALUE(3),
        RETURN_VALUES(4),
        SIDE_EFFECT(5),
        SIDE_EFFECT_CHAIN(6),
        SPY(7),
    }

    /**
     * Mutable State Container for FunProxies to separate frozen and unfrozen behaviour.
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SideEffect the function signature of the hosting Proxy.
     * @author Matthias Geisler
     */
    internal interface FunProxyState<ReturnValue, SideEffect : Function<ReturnValue>> :
        ProxyState<ReturnValue, FunProxyInvocationType, Array<out Any?>> {
        /**
         * Holds a given Throwable.
         */
        var throws: Throwable?

        /**
         * Holds a given ReturnValue.
         */
        var returnValue: ReturnValue?

        /**
         * Holds given ReturnValues.
         */
        val returnValues: MutableList<ReturnValue>

        /**
         * Holds a given SideEffect.
         */
        var sideEffect: SideEffect?

        /**
         * Holds a given SideEffect.
         */
        val sideEffects: SideEffectChain<ReturnValue, SideEffect>
    }

    /**
     * Shared Properties of synchronous and asynchronous functions Proxies.
     * @param ReturnValue the return value of the Function.
     * @param SideEffect the function signature.
     * @author Matthias Geisler
     */
    interface FunProxy<ReturnValue, SideEffect : Function<ReturnValue>> : Proxy<ReturnValue, Array<out Any?>> {
        /**
         * Marks the proxy as ignore during verification (e.g. build-in methods). Meant for internal usage only!
         */
        val ignorableForVerification: Boolean

        /**
         * Setter/Getter in order to set/get constant ReturnValue of the function.
         * @throws NullPointerException on get if no value was set.
         */
        var returnValue: ReturnValue

        /**
         * Setter/Getter in order to set/get a List of ReturnValues of the function. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws NullPointerException on get if no value was set.
         * @throws MissingStub if the given List is empty.
         */
        var returnValues: List<ReturnValue>

        /**
         * Setter/Getter in order to set/get a constant error which is thrown on the invocation of the Proxy.
         * @throws NullPointerException on get if no value was set.
         */
        var throws: Throwable

        /**
         * Setter/Getter in order to set/get a custom SideEffect for the function. SideEffects can be for fine grained behaviour of a Proxy
         * on invocation.
         * @throws NullPointerException on get if no value was set.
         */
        var sideEffect: SideEffect

        /**
         * SideEffectChainBuilder to chain multiple SideEffects.
         */
        val sideEffects: SideEffectChainBuilder<ReturnValue, SideEffect>
    }

    /**
     * Synchronous function Proxy in order to stub/mock synchronous function behaviour.
     * @param ReturnValue the value type of the hosting PropertyProxy.
     * @param SideEffect the function signature.
     * @author Matthias Geisler
     */
    interface SyncFunProxy<ReturnValue, SideEffect : Function<ReturnValue>> : FunProxy<ReturnValue, SideEffect> {

        /**
         * Invocation for functions without arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        fun invoke(
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 1 argument. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        fun <Arg0> invoke(
            arg0: Arg0,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 2 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        fun <Arg0, Arg1> invoke(
            arg0: Arg0,
            arg1: Arg1,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 3 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        fun <Arg0, Arg1, Arg2> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 4 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 5 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 6 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 7 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 8 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 9 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 10 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 11 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 12 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10,
            arg11: Arg11,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 13 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10,
            arg11: Arg11,
            arg12: Arg12,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue
    }

    /**
     * Asynchronous function Proxy in order to stub/mock asynchronous function behaviour.
     * @param ReturnValue the value type of the hosting PropertyProxy.
     * @param SideEffect the function signature.
     * @author Matthias Geisler
     */
    interface AsyncFunProxy<ReturnValue, SideEffect : Function<ReturnValue>> : FunProxy<ReturnValue, SideEffect> {

        /**
         * Invocation for functions without arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun invoke(
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 1 argument. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0> invoke(
            arg0: Arg0,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 2 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1> invoke(
            arg0: Arg0,
            arg1: Arg1,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 3 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 4 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 5 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 6 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 7 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 8 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 9 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 10 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 11 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 12 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10,
            arg11: Arg11,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 13 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7,
            arg8: Arg8,
            arg9: Arg9,
            arg10: Arg10,
            arg11: Arg11,
            arg12: Arg12,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue
    }

    /**
     * Argument Container for PropertyProxies.
     * @param value the given Argument.
     * @author Matthias Geisler
     */
    sealed class GetOrSet(val value: Any?) {
        /**
         * Argument Container for Getter Proxies.
         * @author Matthias Geisler
         */
        object Get : GetOrSet(null)

        /**
         * Argument Container for Setter Proxies
         * @param newValue the new assigned value of the property.
         * @author Matthias Geisler
         */
        class Set(newValue: Any?) : GetOrSet(newValue)
    }

    /**
     * Invocation types for PropertyProxies.
     * @param value indicates the invocation precedence.
     * @author Matthias Geisler
     */
    internal enum class PropertyProxyInvocationType(val value: Int) {
        NO_PROVIDER(0),
        RELAXED(1),
        VALUE(2),
        VALUES(3),
        SIDE_EFFECT(4),
        SPY(5),
    }

    /**
     * Mutable State Container for PropertyProxies to separate frozen and unfrozen behaviour.
     * @param Value the value type of the hosting Proxy.
     * @author Matthias Geisler
     */
    internal interface PropertyProxyState<Value> : ProxyState<Value, PropertyProxyInvocationType, GetOrSet> {
        /**
         * Holds the get value.
         */
        var get: Value?

        /**
         * Holds the (chained) get values
         */
        val getMany: MutableList<Value>

        /**
         * Holds the SideEffect for get
         */
        var sideEffect: Function0<Value>?

        /**
         * Holds the SideEffect for set
         */
        var set: Function1<Value, Unit>?
    }

    /**
     * Proxy in order to stub/mock property behaviour.
     * @param Value the value type of the hosting PropertyProxy.
     * @author Matthias Geisler
     */
    interface PropertyProxy<Value> : Proxy<Value, GetOrSet> {
        /**
         * Setter/Getter in order to set/get constant Value of the property.
         * @throws NullPointerException on get if no value was set.
         */
        var get: Value

        /**
         * Setter/Getter in order to set/get a List of Values of the property. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws NullPointerException on get if no value was set.
         * @throws MissingStub if the given List is empty.
         */
        var getMany: List<Value>

        /**
         * Setter/Getter in order to set/get custom SideEffect for the properties getter. SideEffects can be for fine grained behaviour of a Proxy
         * on invocation.
         * @throws NullPointerException on get if no value was set.
         */
        var getSideEffect: Function0<Value>

        /**
         * Setter/Getter in order to set/get custom SideEffect for the properties setter. SideEffects can be for fine grained behaviour of a Proxy
         * on invocation.
         * @throws NullPointerException on get if no value was set.
         */
        var set: Function1<Value, Unit>

        /**
         * Invocation of property getter. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun onGet(
            nonIntrusiveHook: NonIntrusivePropertyConfigurator<Value>.() -> Unit = {},
        ): Value

        /**
         * Invocation of property setter. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        fun onSet(
            value: Value,
            nonIntrusiveHook: NonIntrusivePropertyConfigurator<Unit>.() -> Unit = {},
        )
    }

    /**
     * Entrypoint to instantiate a Proxy.
     * @author Matthias Geisler
     */
    interface ProxyFactory {
        /**
         * Instantiates a SyncFunProxy.
         * @param ReturnValue the return value of the Proxy.
         * @param SideEffect the function signature.
         * @param id a unique identifier for this Proxy.
         * @param collector a optional Collector for VerificationChains. Default is a NoopCollector.
         * @param ignorableForVerification marks the Proxy as ignorable for verification. Default is false and is intended for internal usage only.
         * @param freeze boolean which indicates if freezing can be used or not. Default is true.
         * Default is null.
         * @see Collector
         * @see SyncFunProxy
         */
        fun <ReturnValue, SideEffect : Function<ReturnValue>> createSyncFunProxy(
            id: String,
            collector: Collector = NoopCollector,
            ignorableForVerification: Boolean = false,
            freeze: Boolean = true,
        ): SyncFunProxy<ReturnValue, SideEffect>

        /**
         * Instantiates a AsyncFunProxy.
         * @param ReturnValue the return value of the Proxy.
         * @param SideEffect the function signature.
         * @param id a unique identifier for this Proxy.
         * @param collector a optional Collector for VerificationChains. Default is a NoopCollector.
         * @param ignorableForVerification marks the Proxy as ignorable for verification. Default is false and is intended for internal usage only.
         * @param freeze boolean which indicates if freezing can be used or not. Default is true.
         * Default is null.
         * @see Collector
         * @see AsyncFunProxy
         */
        fun <ReturnValue, SideEffect : Function<ReturnValue>> createAsyncFunProxy(
            id: String,
            collector: Collector = NoopCollector,
            ignorableForVerification: Boolean = false,
            freeze: Boolean = true,
        ): AsyncFunProxy<ReturnValue, SideEffect>

        /**
         * Instantiates a PropertyProxy.
         * @param Value the value type of the hosting PropertyProxy.
         * @param id a unique identifier for this Proxy.
         * @param collector a optional Collector for VerificationChains. Default is a NoopCollector.
         * @param freeze boolean which indicates if freezing can be used or not. Default is true.
         * Default is null.
         * @see Collector
         */
        fun <Value> createPropertyProxy(
            id: String,
            collector: Collector = NoopCollector,
            freeze: Boolean = true,
        ): PropertyProxy<Value>
    }

    /**
     * Collector of a Proxy invocations.
     * @author Matthias Geisler
     */
    fun interface Collector {
        /**
         * Collects a invocation of a Proxy. Meant for internal use only.
         * @param referredProxy the proxy it is referring to.
         * @param referredCall the invocation index of the Proxy it refers to.
         * @suppress
         */
        fun addReference(referredProxy: Proxy<*, *>, referredCall: Int)
    }

    /**
     * Handle with the aggregated information of a Proxy invocation.
     * Meant for internal usage only!
     * @author Matthias Geisler
     */
    interface Expectation {
        /**
         * Reference of the Proxy.
         */
        val proxy: Proxy<*, *>

        /**
         * List with aggregated indices of invocation of the referred Proxy.
         */
        val callIndices: List<Int>
    }

    /**
     * Constraint for granular Argument verification.
     * @see any
     * @see eq
     * @see isNot
     * @see isNotSame
     * @see isSame
     * @author Matthias Geisler
     */
    fun interface ArgumentConstraint {
        /**
         * Resolves if the constraint matches the given Proxy Argument.
         * @param actual arbitrary argument provided by an Proxy.
         * @return true if the constraint is fulfiled otherwise false.
         */
        fun matches(actual: Any?): Boolean
    }

    /**
     * Reference to a Proxy invocation.
     * Meant for internal usage only!
     * @author Matthias Geisler
     */
    data class Reference(
        /**
         * The referenced Proxy.
         */
        val proxy: Proxy<*, *>,

        /**
         * The referenced Call.
         */
        val callIndex: Int
    )

    /**
     * Insurance that given Proxies are covered by the VerificationChain.
     * @author Matthias Geisler
     */
    fun interface VerificationInsurance {
        /**
         * Ensures that given Proxies are covered by the VerificationChain. Use this method with caution!
         * @throws IllegalStateException if a given Proxy is not covered by a VerificationChain.
         */
        fun ensureVerificationOf(vararg proxies: Proxy<*, *>)
    }

    /**
     * VerificationChain in order to verify over multiple Handles.
     * Meant for internal purpose only!
     * @author Matthias Geisler
     */
    interface VerificationChain {
        /**
         * Propagates the expected invocation to the Chain and asserts it against the actual values.
         * @param expected the expected Invocation.
         * @throws AssertionError if the expected value does not match the actual value.
         */
        @Throws(AssertionError::class)
        fun propagate(expected: Expectation)

        /**
         * Ensures that all expected or actual values are covered depending on the context.
         * @throws AssertionError if the context needs to be exhaustive and not all expected or actual values are covered.
         */
        @Throws(AssertionError::class)
        fun ensureAllReferencesAreEvaluated()
    }

    /**
     * Container which holds actual references of proxy calls. The references are ordered by their invocation.
     * @author Matthias Geisler
     */
    interface Verifier {
        /**
         * Holds the actual references
         */
        val references: List<Reference>

        /**
         * Clears the Container
         */
        fun clear()
    }

    internal const val STRICT_CALL_NOT_FOUND = "Expected %0 to be invoked, but no further calls were captured."
    internal const val STRICT_CALL_NOT_MATCH = "Expected %0 to be invoked, but %1 was called."
    internal const val STRICT_CALL_IDX_NOT_FOUND = "Expected %0th call of %1 was not made."
    internal const val STRICT_CALL_IDX_NOT_MATCH = "Expected %0th call of %1, but it refers to the %2th call."
    internal const val STRICT_MISSING_EXPECTATION =
        "The given verification chain covers %0 items, but only %1 were expected (%2 were referenced)."

    internal const val NON_STRICT_CALL_NOT_FOUND =
        "Expected %0 to be invoked, but no call was captured with the given arguments."
    internal const val NON_STRICT_CALL_IDX_NOT_FOUND = "Expected call of %0 was not made."

    internal const val NOT_CALLED = "Call not found."
    internal const val TOO_LESS_CALLS = "Expected at least %0 calls, but found only %1."
    internal const val TOO_MANY_CALLS = "Expected at most %0 calls, but exceeded with %1."

    internal const val NOT_PART_OF_CHAIN = "The given proxy %0 is not part of this VerificationChain."
}
