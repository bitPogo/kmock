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
public object KMockContract {
    /**
     * Base Proxy definition
     * @param ReturnValue the return value of the Proxy.
     * @param Arguments the arguments which are delegated to the Proxy.
     * @author Matthias Geisler
     */
    public sealed interface Proxy<ReturnValue, Arguments> {
        /**
         * Unique Id of the Proxy derived from the Interface which it build upon.
         */
        public val id: String

        /**
         * Counter of the actual invocations of the Proxy.
         */
        public val calls: Int

        /**
         * Indicates that the proxies uses the frozen memory model.
         */
        public val frozen: Boolean

        /**
         * Resolves given arguments of an invocation.
         * @param callIndex index of an invocation.
         * @return the Arguments of the given invocation or null if the Proxy is used for void invocations.
         * @throws MissingCall if the callIndex is invalid.
         */
        public fun getArgumentsForCall(callIndex: Int): Arguments

        /**
         * Alias for getArgumentsForCall
         * @param callIndex index of an invocation.
         * @return the Arguments of the given invocation or null if the Proxy is used for void invocations.
         * @throws MissingCall if the callIndex is invalid.
         */
        public operator fun get(callIndex: Int): Arguments

        /**
         * Clears the Proxies captured arguments
         */
        public fun clear()
    }

    /**
     * Definition of simple setter methods which are equal for Fun- and PropertyProxies.
     * @author Matthias Geisler
     */
    public sealed interface ProxyReturnValueSetter<ReturnValue> {
        /**
         * An Alias method which sets a ReturnValue.
         * @param value which is returned when the Proxy is invoked.
         */
        public infix fun returns(value: ReturnValue)

        /**
         * An Alias method which sets ReturnValues.
         * @param values which are returned when the Proxy is invoked.
         */
        public infix fun returnsMany(values: List<ReturnValue>)
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
    public interface PropertySpyTargetInvocation<Value> {
        /**
         * Binds the given function to the Proxy.
         * It wipes a given relaxer and buildInRelaxer.
         * @param spyTarget the referenced object which is spied upon.
         * @param spyOn the referenced Spy method.
         */
        public fun useSpyIf(spyTarget: Any?, spyOn: Function0<Value>)
    }

    /**
     * Binds a SpyTarget to a invocation.
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SpyTarget the function signature of the spy target closure.
     * @author Matthias Geisler
     */
    public interface MethodSpyTargetInvocation<ReturnValue, SpyTarget : Function<ReturnValue>> {
        /**
         * Binds the given function to the Proxy.
         * @param spyTarget the referenced object which is spied upon.
         * @param spyOn the referenced Spy method.
         */
        public fun useSpyIf(spyTarget: Any?, spyOn: SpyTarget)

        /**
         * Binds the given function, which should be equals, to the Proxy.
         * The spy equals method will be used if the other object is not of the same type as the given Class of the hosting mock.
         * The equals method will be used if the other object is of the same type as the given Class of the hosting mock.
         * @param spyTarget the referenced object which is spied upon.
         * @param other the object to compare to if the hosting mock is used instead of the spy target.
         * @param spyOn function which should reference the spy target equals method.
         * @param mockKlass the KClass of the hosting mock.
         */
        public fun useSpyOnEqualsIf(
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
    public interface RelaxationConfigurator<Value> {
        /**
         * Binds a given Relaxer function to a Proxy if the condition is true.
         * This will wipe a given relaxer.
         * @param condition which determines if the relaxer should be invoked or not.
         * @param relaxer the relaxer method.
         */
        public fun useRelaxerIf(condition: Boolean, relaxer: Function1<String, Value>)
    }

    /**
     * Configures non intrusive Behaviour for FunProxies.
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SideEffect the function signature of the hosting Proxy.
     * @see RelaxationConfigurator
     * @author Matthias Geisler
     */
    public interface RelaxationFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> :
        RelaxationConfigurator<ReturnValue> {

        /**
         * Binds the internal UnitFunRelaxer to the Proxy, if the given condition is true.
         * It wipes a given relaxer.
         * @param condition Boolean to determine if the the function is relaxed or not.
         */
        public fun useUnitFunRelaxerIf(condition: Boolean)
    }

    /**
     * Configures non intrusive Behaviour for PropertyProxies.
     * @param Value the value type of the hosting PropertyProxy.
     * @see RelaxationConfigurator
     * @author Matthias Geisler
     */
    public interface RelaxationPropertyConfigurator<Value> : RelaxationConfigurator<Value>

    /**
     * Configurator for non intrusive behaviour of PropertyProxies.
     * @author Matthias Geisler
     */
    public interface NonIntrusivePropertyConfigurator<Value> :
        RelaxationPropertyConfigurator<Value>,
        PropertySpyTargetInvocation<Value>

    /**
     * Configurator for non intrusive behaviour of FunProxies.
     * @author Matthias Geisler
     */
    public interface NonIntrusiveFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> :
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
    internal interface NonIntrusiveFunTarget<ReturnValue, SideEffect : Function<ReturnValue>> :
        RelaxationTarget<ReturnValue>, SpyTarget<ReturnValue, SideEffect>

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
         * @see Asserter
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
    public interface SideEffectChainBuilder<ReturnValue, SideEffect : Function<ReturnValue>> {
        /**
         * Adds a SideEffect to chain.
         * @param sideEffect the given SideEffect.
         * @return SideEffectChainBuilder the current builder.
         */
        public fun add(sideEffect: SideEffect): SideEffectChainBuilder<ReturnValue, SideEffect>

        /**
         * Adds a multiple SideEffects to chain.
         * @param sideEffect the given SideEffect.
         * @return SideEffectChainBuilder the current builder.
         */
        public fun addAll(sideEffect: Iterable<SideEffect>): SideEffectChainBuilder<ReturnValue, SideEffect>
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
    internal interface SideEffectChain<ReturnValue, SideEffect : Function<ReturnValue>> :
        SideEffectChainBuilder<ReturnValue, SideEffect> {
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
        THROWS_MANY(3),
        RETURN_VALUE(4),
        RETURN_VALUES(5),
        SIDE_EFFECT(6),
        SIDE_EFFECT_CHAIN(7),
        SPY(8),
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
        var error: Throwable?

        /**
         * Holds a given Throwables.
         */
        val errors: MutableList<Throwable>

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
     * Mitigator of the strict assignment policy for multiple SideEffects
     * @param ReturnValue the return value type of the hosting Proxy.
     * @param SideEffect the function signature of the hosting Proxy.
     * @author Matthias Geisler
     */
    public interface ProxySideEffectBuilder<ReturnValue, SideEffect : Function<ReturnValue>> {
        /**
         * Setter/Getter in order to set/get a custom SideEffect for the function. SideEffects can be used for fine grained behaviour of a Proxy
         * on invocation.
         * @throws NullPointerException on get if no value was set.
         */
        public var sideEffect: SideEffect

        /**
         * SideEffectChainBuilder to chain multiple SideEffects.
         */
        public val sideEffects: SideEffectChainBuilder<ReturnValue, SideEffect>

        /**
         * Alias method for sideEffect
         * @param action - a SideEffect which is chained into the sideEffect property.
         */
        public infix fun run(action: SideEffect)

        /**
         * Convenient method for multiple SideEffects, which uses under the sideEffects property.
         * This chains SideEffects together according to their given order.
         * @param action - a SideEffect which is chained into the sideEffects property.
         * @return ProxySideEffectBuilder.
         */
        public infix fun runs(action: SideEffect): ProxySideEffectBuilder<ReturnValue, SideEffect>
    }

    /**
     * Shared Properties of synchronous and asynchronous functions Proxies.
     * @param ReturnValue the return value of the Function.
     * @param SideEffect the function signature.
     * @author Matthias Geisler
     */
    public interface FunProxy<ReturnValue, SideEffect : Function<ReturnValue>> :
        Proxy<ReturnValue, Array<out Any?>>,
        ProxyReturnValueSetter<ReturnValue>,
        ProxySideEffectBuilder<ReturnValue, SideEffect> {
        /**
         * Marks the Proxy as ignore during verification (e.g. build-in methods). Intended for internal usage only!
         */
        @KMockInternal
        public val ignorableForVerification: Boolean

        /**
         * Setter/Getter in order to set/get constant ReturnValue of the function.
         * @throws NullPointerException on get if no value was set.
         */
        public var returnValue: ReturnValue

        /**
         * Alias setter of returnValue.
         * @param value which is returned when the Proxy is invoked.
         */
        public override infix fun returns(value: ReturnValue)

        /**
         * Setter/Getter in order to set/get a List of ReturnValues of the Proxy. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws MissingStub if the given List is empty.
         */
        public var returnValues: List<ReturnValue>

        /**
         * Alias setter of returnValues.
         * @param values which are returned when the Proxy is invoked. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws MissingStub if the given List is empty.
         */
        public override infix fun returnsMany(values: List<ReturnValue>)

        /**
         * Setter/Getter in order to set/get a constant error which is thrown on the invocation of the Proxy.
         * @throws NullPointerException on get if no value was set.
         */
        @Deprecated(
            message = "This property will be replaced with 0.3.0 by error.",
            replaceWith = ReplaceWith("error"),
            level = DeprecationLevel.WARNING
        )
        public var throws: Throwable

        /**
         * Setter/Getter in order to set/get a constant error which is thrown on the invocation of the Proxy.
         * @throws NullPointerException on get if no value was set.
         */
        public var error: Throwable

        /**
         * Alias setter of error.
         * @param error which is thrown when the Proxy is invoked.
         */
        public infix fun throws(error: Throwable)

        /**
         * Setter/Getter in order to set/get a constant error which is thrown on the invocation of the Proxy. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws MissingStub if the given List is empty.
         */
        public var errors: List<Throwable>

        /**
         * Setter/Getter in order to set/get a constant error which is thrown on the invocation of the Proxy. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws MissingStub if the given List is empty.
         */
        @Deprecated(
            message = "This property will be replaced with 0.3.0 by errors.",
            replaceWith = ReplaceWith("errors"),
            level = DeprecationLevel.WARNING
        )
        public var throwsMany: List<Throwable>

        /**
         * Alias setter of errors.
         * @param errors which are thrown when the Proxy is invoked. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws MissingStub if the given List is empty.
         */
        public infix fun throwsMany(errors: List<Throwable>)
    }

    /**
     * Synchronous function Proxy in order to stub/mock synchronous function behaviour.
     * @param ReturnValue the value type of the hosting PropertyProxy.
     * @param SideEffect the function signature.
     * @author Matthias Geisler
     */
    public interface SyncFunProxy<ReturnValue, SideEffect : Function<ReturnValue>> : FunProxy<ReturnValue, SideEffect> {

        /**
         * Invocation for functions without arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        public fun invoke(
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 1 argument. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        public fun <Arg0> invoke(
            arg0: Arg0,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 2 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         * @suppress
         */
        public fun <Arg0, Arg1> invoke(
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
        public fun <Arg0, Arg1, Arg2> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
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
        public fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
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
    public interface AsyncFunProxy<ReturnValue, SideEffect : Function<ReturnValue>> :
        FunProxy<ReturnValue, SideEffect> {

        /**
         * Invocation for functions without arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        public suspend fun invoke(
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 1 argument. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        public suspend fun <Arg0> invoke(
            arg0: Arg0,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 2 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        public suspend fun <Arg0, Arg1> invoke(
            arg0: Arg0,
            arg1: Arg1,
            nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, suspend () -> ReturnValue>.() -> Unit = {},
        ): ReturnValue

        /**
         * Invocation for functions with 3 arguments. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        public suspend fun <Arg0, Arg1, Arg2> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
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
        public suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
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
    public sealed class GetOrSet(
        public val value: Any?
    ) {
        /**
         * Argument Container for Getter Proxies.
         * @author Matthias Geisler
         */
        internal object Get : GetOrSet(null)

        /**
         * Argument Container for Setter Proxies
         * @param newValue the new assigned value of the property.
         * @author Matthias Geisler
         */
        internal class Set(newValue: Any?) : GetOrSet(newValue)
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
    public interface PropertyProxy<Value> : Proxy<Value, GetOrSet>, ProxyReturnValueSetter<Value> {
        /**
         * Setter/Getter in order to set/get constant Value of the property.
         * @throws NullPointerException on get if no value was set.
         */
        public var get: Value

        /**
         * Alias setter of get.
         * @param value which is returned when the getter is invoked.
         */
        override infix fun returns(value: Value)

        /**
         * Setter/Getter in order to set/get a List of Values of the property. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws NullPointerException on get if no value was set.
         * @throws MissingStub if the given List is empty.
         */
        public var getMany: List<Value>

        /**
         * Alias getMany get.
         * @param values which are returned when the getter is invoked. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws MissingStub if the given List is empty.
         */
        override infix fun returnsMany(values: List<Value>)

        /**
         * Setter/Getter in order to set/get custom SideEffect for the properties getter. SideEffects can be used for fine grained behaviour of a Proxy
         * on invocation.
         * @throws NullPointerException on get if no value was set.
         */
        public var getSideEffect: Function0<Value>

        /**
         * Alias setter of getSideEffect.
         * @param sideEffect which is executed when the getter is invoked.
         * SideEffects can be used for fine grained behaviour of a Proxy
         * on invocation.
         */
        public infix fun runOnGet(sideEffect: Function0<Value>)

        /**
         * Setter/Getter in order to set/get custom SideEffect for the properties setter.
         * SideEffects can be used for fine grained behaviour of a Proxy
         * on invocation.
         * @throws NullPointerException on get if no value was set.
         */
        public var set: Function1<Value, Unit>

        /**
         * Alias setter of set.
         * @param sideEffect which is executed when the setter is invoked.
         * SideEffects can be used for fine grained behaviour of a Proxy
         * on invocation.
         */
        public infix fun runOnSet(sideEffect: Function1<Value, Unit>)

        /**
         * Invocation of property getter. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        @KMockInternal
        public fun executeOnGet(
            nonIntrusiveHook: NonIntrusivePropertyConfigurator<Value>.() -> Unit = {},
        ): Value

        /**
         * Invocation of property setter. This is meant for internal use only.
         * @throws MissingStub if no behaviour instruction was given.
         * @suppress
         */
        @KMockInternal
        public fun executeOnSet(
            value: Value,
            nonIntrusiveHook: NonIntrusivePropertyConfigurator<Unit>.() -> Unit = {},
        )
    }

    /**
     * Entrypoint to instantiate a Proxy.
     * @author Matthias Geisler
     */
    public interface ProxyFactory {
        /**
         * Instantiates a SyncFunProxy.
         * @param ReturnValue the return value of the Proxy.
         * @param SideEffect the function signature.
         * @param id a unique identifier for this Proxy.
         * @param collector a optional Collector for AssertionChains. Default is a NoopCollector.
         * @param ignorableForVerification marks the Proxy as ignorable for verification. Default is false and is intended for internal usage only.
         * @param freeze boolean which indicates if freezing can be used or not. Default is true.
         * Default is null.
         * @see Collector
         * @see SyncFunProxy
         */
        public fun <ReturnValue, SideEffect : Function<ReturnValue>> createSyncFunProxy(
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
         * @param collector a optional Collector for AssertionChains. Default is a NoopCollector.
         * @param ignorableForVerification marks the Proxy as ignorable for verification. Default is false and is intended for internal usage only.
         * @param freeze boolean which indicates if freezing can be used or not. Default is true.
         * Default is null.
         * @see Collector
         * @see AsyncFunProxy
         */
        public fun <ReturnValue, SideEffect : Function<ReturnValue>> createAsyncFunProxy(
            id: String,
            collector: Collector = NoopCollector,
            ignorableForVerification: Boolean = false,
            freeze: Boolean = true,
        ): AsyncFunProxy<ReturnValue, SideEffect>

        /**
         * Instantiates a PropertyProxy.
         * @param Value the value type of the hosting PropertyProxy.
         * @param id a unique identifier for this Proxy.
         * @param collector a optional Collector for AssertionChains. Default is a NoopCollector.
         * @param freeze boolean which indicates if freezing can be used or not. Default is true.
         * Default is null.
         * @see Collector
         */
        public fun <Value> createPropertyProxy(
            id: String,
            collector: Collector = NoopCollector,
            freeze: Boolean = true,
        ): PropertyProxy<Value>
    }

    /**
     * Collector of a Proxy invocations.
     * @author Matthias Geisler
     */
    public fun interface Collector {
        /**
         * Collects a invocation of a Proxy. Intended for internal use only.
         * @param referredProxy the Proxy it is referring to.
         * @param referredCall the invocation index of the Proxy it refers to.
         * @suppress
         */
        public fun addReference(referredProxy: Proxy<*, *>, referredCall: Int)
    }

    /**
     * Handle with the aggregated information of a Proxy invocation.
     * Intended for internal usage only!
     * @author Matthias Geisler
     */
    public interface Expectation {
        /**
         * Reference of the Proxy.
         */
        public val proxy: Proxy<*, *>

        /**
         * List with aggregated indices of invocation of the referred Proxy.
         */
        public val callIndices: List<Int>
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
    public fun interface ArgumentConstraint {
        /**
         * Resolves if the constraint matches the given Proxy Argument.
         * @param actual arbitrary argument provided by an Proxy.
         * @return true if the constraint is fulfiled otherwise false.
         */
        public fun matches(actual: Any?): Boolean
    }

    /**
     * Wrapper for Arguments-Constraints
     */
    internal interface ArgumentConstraintWrapper {
        /**
         * Wraps a arbitrary value to eq-Constraint if it is not a Arguments-Constraints.
         * @param value a arbitrary value.
         * @return ArgumentConstraint
         */
        fun wrapValue(value: Any?): ArgumentConstraint

        /**
         * Wraps a arbitrary value to eq-Constraint if it is not a Arguments-Constraints and wraps that into a not-Constraint.
         * @param value a arbitrary value.
         * @return ArgumentConstraint the resulting not-Constraint.
         */
        fun wrapNegatedValue(value: Any?): ArgumentConstraint
    }

    /**
     * Reference to a Proxy invocation.
     * Intended for internal usage only!
     * @author Matthias Geisler
     */
    @KMockInternal
    public data class Reference(
        /**
         * The referenced Proxy.
         */
        public val proxy: Proxy<*, *>,

        /**
         * The referenced Call.
         */
        public val callIndex: Int
    )

    /**
     * Internal Executor of Assertions.
     * @author Matthias Geisler
     */
    internal interface Assertions {
        /**
         * Asserts that a FunProxy was called.
         * @param proxy the actual proxy.
         * @param callIndex the index of the invocation from the Proxy.
         * @throws AssertionError if the assertion fails.
         */
        fun hasBeenCalledAtIndex(
            proxy: FunProxy<*, *>,
            callIndex: Int
        )

        /**
         * Asserts that a FunProxy was called without any parameter.
         * @param proxy the actual proxy.
         * @param callIndex the index of the invocation from the Proxy.
         * @throws AssertionError if the assertion fails.
         */
        fun hasBeenCalledWithVoidAtIndex(
            proxy: FunProxy<*, *>,
            callIndex: Int
        )

        /**
         * Asserts that a FunProxy was called with n-parameter.
         * The arguments do not need to be complete.
         * @param proxy the actual proxy.
         * @param callIndex the index of the invocation from the Proxy.
         * @param arguments the expected arguments.
         * @throws AssertionError if the assertion fails
         */
        fun hasBeenCalledWithAtIndex(
            proxy: FunProxy<*, *>,
            callIndex: Int,
            vararg arguments: Any?
        )

        /**
         * Asserts that a FunProxy was called with n-parameter.
         * The arguments must be complete.
         * @param proxy the actual proxy.
         * @param callIndex the index of the invocation from the Proxy.
         * @param arguments the expected arguments.
         * @throws AssertionError if the assertion fails
         */
        fun hasBeenStrictlyCalledWithAtIndex(
            proxy: FunProxy<*, *>,
            callIndex: Int,
            vararg arguments: Any?
        )

        /**
         * Asserts that a FunProxy was without called n-parameter.
         * The arguments do not need to be complete.
         * @param proxy the actual proxy.
         * @param callIndex the index of the invocation from the Proxy.
         * @param illegal the forbidden arguments.
         * @throws AssertionError if the assertion fails
         */
        fun hasBeenCalledWithoutAtIndex(
            proxy: FunProxy<*, *>,
            callIndex: Int,
            vararg illegal: Any?
        )

        /**
         * Asserts that a PropertyProxy was invoked as a Getter.
         * @param proxy the actual proxy.
         * @param callIndex the index of the invocation from the Proxy.
         * @throws AssertionError if the assertion fails.
         */
        fun wasGottenAtIndex(
            proxy: PropertyProxy<*>,
            callIndex: Int
        )

        /**
         * Asserts that a PropertyProxy was invoked as a Setter.
         * @param proxy the actual proxy.
         * @param callIndex the index of the invocation from the Proxy.
         * @throws AssertionError if the assertion fails.
         */
        fun wasSetAtIndex(
            proxy: PropertyProxy<*>,
            callIndex: Int
        )

        /**
         * Asserts that a PropertyProxy was invoked as a Setter with a given value.
         * @param proxy the actual proxy.
         * @param callIndex the index of the invocation from the Proxy.
         * @param value the expected value.
         * @throws AssertionError if the assertion fails.
         */
        fun wasSetToAtIndex(
            proxy: PropertyProxy<*>,
            callIndex: Int,
            value: Any?
        )
    }

    /**
     * Provider for Assertion.
     * @author Matthias Geisler
     */
    public interface AssertionContext {
        /**
         * Asserts that a FunProxy was called.
         * @throws AssertionError if the assertion fails.
         */
        public fun FunProxy<*, *>.hasBeenCalled()

        /**
         * Asserts that a FunProxy was called without any parameter.
         * @throws AssertionError if the assertion fails.
         */
        public fun FunProxy<*, *>.hasBeenCalledWithVoid()

        /**
         * Asserts that a FunProxy was called with n-parameter.
         * @param arguments or constraints which calls must match. The arguments/constraints must follow the order of the mocked/stubbed function but can contain gaps and do not need to all arguments.
         * @throws AssertionError if the assertion fails
         */
        public fun FunProxy<*, *>.hasBeenCalledWith(vararg arguments: Any?)

        /**
         * Asserts that a FunProxy was called with n-parameter.
         * @param arguments or constraints which calls must match. The arguments/constraints must follow the order of the mocked/stubbed function and need to contain all arguments/constraints.
         * @throws AssertionError if the assertion fails
         */
        public fun FunProxy<*, *>.hasBeenStrictlyCalledWith(vararg arguments: Any?)

        /**
         * Asserts that a FunProxy was without called n-parameter.
         * @param illegal arguments or constraints which calls is not allowed not match.
         * @throws AssertionError if the assertion fails
         */
        public fun FunProxy<*, *>.hasBeenCalledWithout(vararg illegal: Any?)

        /**
         * Asserts that a PropertyProxy was invoked as a Getter.
         * @throws AssertionError if the assertion fails.
         */
        public fun PropertyProxy<*>.wasGotten()

        /**
         * Asserts that a PropertyProxy was invoked as a Setter.
         * @throws AssertionError if the assertion fails.
         */
        public fun PropertyProxy<*>.wasSet()

        /**
         * Asserts that a PropertyProxy was invoked as a Setter with a given value.
         * @param value the expected value.
         * @throws AssertionError if the assertion fails.
         */
        public fun PropertyProxy<*>.wasSetTo(value: Any?)
    }

    /**
     * Provider for Verification.
     * @author Matthias Geisler
     */
    public interface VerificationContext {
        /**
         * Collects all invocation of a FunProxy.
         * @return Expectation
         */
        public fun FunProxy<*, *>.hasBeenCalled(): Expectation

        /**
         * Collects all invocation of a FunProxy which contain no Arguments.
         * @return Expectation
         */
        public fun FunProxy<*, *>.hasBeenCalledWithVoid(): Expectation

        /**
         * Collects all invocation of an FunProxy which matches the given Arguments.
         * @param arguments or constraints which calls must match. The arguments/constraints must follow the order of the mocked/stubbed function but can contain gaps and do not need to all arguments.
         * @return Expectation
         * @see ArgumentConstraint
         */
        public fun FunProxy<*, *>.hasBeenCalledWith(vararg arguments: Any?): Expectation

        /**
         * Collects all invocation of an FunProxy which matches the given Arguments.
         * @param arguments or constraints which calls must match. The arguments/constraints must follow the order of the mocked/stubbed function and need to contain all arguments/constraints.
         * @return Expectation
         * @see ArgumentConstraint
         */
        public fun FunProxy<*, *>.hasBeenStrictlyCalledWith(vararg arguments: Any?): Expectation

        /**
         * Collects all invocation of an FunProxy which matches the given Arguments.
         * @param illegal arguments or constraints which calls is not allowed not match.
         * @return Expectation
         * @see ArgumentConstraint
         */
        public fun FunProxy<*, *>.hasBeenCalledWithout(vararg illegal: Any?): Expectation

        /**
         * Collects all invocation of an PropertyProxy Getter.
         */
        public fun PropertyProxy<*>.wasGotten(): Expectation

        /**
         * Collects all invocation of an PropertyProxy Setter.
         */
        public fun PropertyProxy<*>.wasSet(): Expectation

        /**
         * Collects all invocation of an PropertyProxy Setter with the given Value.
         * @return Expectation
         * @param value argument/constraint which calls must match.
         * @see ArgumentConstraint
         */
        public fun PropertyProxy<*>.wasSetTo(value: Any?): Expectation
    }

    /**
     * Insurance that given Proxies are covered by the AssertionChain.
     * @author Matthias Geisler
     */
    public fun interface AssertionInsurance {
        /**
         * Ensures that given Proxies are covered by the AssertionChain.
         * @throws IllegalStateException if a given Proxy is not covered by a AssertionChain.
         */
        public fun ensureVerificationOf(vararg proxies: Proxy<*, *>)
    }

    /**
     * Combination of AssertionInsurance and AssertionContext
     * @see AssertionInsurance
     * @see AssertionContext
     */
    public interface ChainedAssertion : AssertionInsurance, AssertionContext

    /**
     * AssertionChain in order to verify over multiple Handles.
     * Intended for internal purpose only!
     * @author Matthias Geisler
     */
    internal interface AssertionChain {
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
    public interface Asserter {
        /**
         * Holds the actual references
         */
        public val references: List<Reference>

        /**
         * Clears the Container
         */
        public fun clear()
    }

    internal const val CALL_NOT_FOUND = "Expected %0 to be invoked, but no further calls were captured."
    internal const val STRICT_CALL_NOT_MATCH = "Expected %0 to be invoked, but %1 was called."
    internal const val STRICT_MISSING_EXPECTATION =
        "The given verification chain covers %0 items, but only %1 were expected (%2 were referenced)."

    internal const val MISSING_INVOCATION = "Expected %0th call of %1 was not made."
    internal const val MISMATCH = "Expected <%0> got actual <%1>."
    internal const val CALL_WITH_ARGS_NOT_FOUND = "Expected %0 to be invoked with %1, but no matching call was found."
    internal const val HAD_BEEN_CALLED_NO_MATCHER = "The given matcher %0 has not been found."
    internal const val MISMATCHING_SIZE = "Expected <%0> arguments got actual <%1>."
    internal const val ILLEGAL_VALUE = "Illegal value <%0> detected."
    internal const val NON_VOID_FUNCTION = "Expected a non void function invocation."
    internal const val VOID_FUNCTION = "Expected %0 to be void, but the invocation contains Arguments."
    internal const val NOT_GET = "Expected a getter and got a setter."
    internal const val NOT_SET = "Expected a setter and got a getter."

    internal const val NOT_CALLED = "Call not found."
    internal const val TOO_LESS_CALLS = "Expected at least %0 calls, but found only %1."
    internal const val TOO_MANY_CALLS = "Expected at most %0 calls, but exceeded with %1."

    internal const val NOT_PART_OF_CHAIN = "The given proxy %0 is not part of this chain."
}
