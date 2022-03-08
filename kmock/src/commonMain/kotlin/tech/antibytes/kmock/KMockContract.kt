/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.error.MockError

interface KMockContract {
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
         * Counter of the actual invocation of the proxy.
         */
        val calls: Int

        /**
         * Reference to its correspondent VerificationChain. This Property is intended for internal use only!
         */
        var verificationBuilderReference: VerificationChainBuilder?

        /**
         * Resolves given arguments of an invocation.
         * @param callIndex index of an invocation.
         * @return the Arguments of the given invocation or null if the proxy is used for void invocations.
         * @throws IndexOutOfBoundsException if the invocationIndex is invalid.
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
     * @see Relaxer
     * @author Matthias Geisler
     */
    fun interface Relaxer<ReturnValue> {
        /**
         * Invokes the injected Relaxer.
         * @param id a Proxy#Id in order to enable fine grained differentiation between overlapping types.
         * @return the given Relaxer Type.
         */
        fun relax(id: String): ReturnValue
    }

    /**
     * Builder for chained SideEffects.
     * @param ReturnValue the return value of the hosting Proxy.
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
    }

    /**
     * Container for chained SideEffects. If the given Container has
     * a smaller size than the actual invocation the last value of is repeatably used.
     * It acts in a FIFO manner.
     * @param ReturnValue the return value of the hosting Proxy.
     * @param SideEffect the function signature of the hosting Proxy.
     * @see SideEffectChainBuilder
     * @author Matthias Geisler
     */
    interface SideEffectChain<ReturnValue, SideEffect : Function<ReturnValue>> : SideEffectChainBuilder<ReturnValue, SideEffect> {
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
     * Shared Properties of synchronous and asynchronous functions Proxies.
     * @param ReturnValue the return value of the Function.
     * @param SideEffect the function signature.
     * @author Matthias Geisler
     */
    interface FunProxy<ReturnValue, SideEffect : Function<ReturnValue>> : Proxy<ReturnValue, Array<out Any?>?> {
        /**
         * Setter/Getter in order to set/get constant ReturnValue of the function.
         * @throws NullPointerException on get if no value was set.
         */
        var returnValue: ReturnValue

        /**
         * Setter/Getter in order to set/get a List of ReturnValues of the function. If the given List has
         * a smaller size than the actual invocation the last value of the list is used for any further invocation.
         * @throws NullPointerException on get if no value was set.
         * @throws MockError.MissingStub if the given List is empty.
         */
        var returnValues: List<ReturnValue>

        /**
         * Setter/Getter in order to set/get a constant error which is thrown on the invocation of the Proxy.
         * @throws NullPointerException on get if no value was set.
         */
        var throws: Throwable

        /**
         * Setter/Getter in order to set/get custom SideEffect for the function. SideEffects can be for fine grained behaviour of a Proxy
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
     * @param ReturnValue return value of the Proxy.
     * @param SideEffect the function signature.
     * @author Matthias Geisler
     */
    interface SyncFunProxy<ReturnValue, SideEffect : Function<ReturnValue>> : FunProxy<ReturnValue, SideEffect> {

        /**
         * Invocation of function without arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun invoke(): ReturnValue

        /**
         * Invocation of function with 1 argument. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun <Arg0> invoke(arg0: Arg0): ReturnValue

        /**
         * Invocation of function with 2 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun <Arg0, Arg1> invoke(
            arg0: Arg0,
            arg1: Arg1
        ): ReturnValue

        /**
         * Invocation of function with 3 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun <Arg0, Arg1, Arg2> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2
        ): ReturnValue

        /**
         * Invocation of function with 4 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun <Arg0, Arg1, Arg2, Arg3> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3
        ): ReturnValue

        /**
         * Invocation of function with 5 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4
        ): ReturnValue

        /**
         * Invocation of function with 6 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5
        ): ReturnValue

        /**
         * Invocation of function with 7 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6
        ): ReturnValue

        /**
         * Invocation of function with 8 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7
        ): ReturnValue

        /**
         * Invocation of function with 9 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
            arg8: Arg8
        ): ReturnValue

        /**
         * Invocation of function with 10 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
            arg9: Arg9
        ): ReturnValue

        /**
         * Invocation of function with 11 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
            arg10: Arg10
        ): ReturnValue

        /**
         * Invocation of function with 12 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
            arg11: Arg11
        ): ReturnValue

        /**
         * Invocation of function with 13 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
        ): ReturnValue
    }

    /**
     * Asynchronous function Proxy in order to stub/mock asynchronous function behaviour.
     * @param ReturnValue return value of the Proxy.
     * @param SideEffect the function signature.
     * @author Matthias Geisler
     */
    interface AsyncFunProxy<ReturnValue, SideEffect : Function<ReturnValue>> : FunProxy<ReturnValue, SideEffect> {

        /**
         * Invocation of function without arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun invoke(): ReturnValue

        /**
         * Invocation of function with 1 argument. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun <Arg0> invoke(arg0: Arg0): ReturnValue

        /**
         * Invocation of function with 2 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun <Arg0, Arg1> invoke(
            arg0: Arg0,
            arg1: Arg1
        ): ReturnValue

        /**
         * Invocation of function with 3 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun <Arg0, Arg1, Arg2> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2
        ): ReturnValue

        /**
         * Invocation of function with 4 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3
        ): ReturnValue

        /**
         * Invocation of function with 5 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4
        ): ReturnValue

        /**
         * Invocation of function with 6 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5
        ): ReturnValue

        /**
         * Invocation of function with 7 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6
        ): ReturnValue

        /**
         * Invocation of function with 8 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
            arg0: Arg0,
            arg1: Arg1,
            arg2: Arg2,
            arg3: Arg3,
            arg4: Arg4,
            arg5: Arg5,
            arg6: Arg6,
            arg7: Arg7
        ): ReturnValue

        /**
         * Invocation of function with 9 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
            arg8: Arg8
        ): ReturnValue

        /**
         * Invocation of function with 10 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
            arg9: Arg9
        ): ReturnValue

        /**
         * Invocation of function with 11 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
            arg10: Arg10
        ): ReturnValue

        /**
         * Invocation of function with 12 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
            arg11: Arg11
        ): ReturnValue

        /**
         * Invocation of function with 13 arguments. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
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
     * Proxy in order to stub/mock property behaviour.
     * @param Value the value of the Property.
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
         * @throws MockError.MissingStub if the given List is empty.
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
         * @param Value the delegated value.
         * @throws NullPointerException on get if no value was set.
         */
        var set: (Value) -> Unit

        /**
         * Invocation of property getter. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun onGet(): Value

        /**
         * Invocation of property setter. This is meant for internal use only.
         * @throws MockError.MissingStub if no behaviour instruction was given.
         */
        fun onSet(value: Value)
    }

    /**
     * Collector of Proxy invocations.
     * @author Matthias Geisler
     */
    fun interface Collector {
        /**
         * Collects a invocation of a Proxy. Meant for internal use only.
         * @param referredProxy the proxy it is referring to.
         * @param referredCall the invocation index of the Proxy it refers to.
         */
        fun addReference(referredProxy: Proxy<*, *>, referredCall: Int)
    }

    /**
     * Handle with the aggregated information of a Proxy invocation.
     * Meant for internal usage only!
     * @author Matthias Geisler
     */
    interface VerificationHandle {
        /**
         * Id of the Proxy.
         */
        val id: String

        /**
         * List with aggregated indices of invocation of the refered Proxy.
         */
        val callIndices: List<Int>
    }

    /**
     * Constraint to for granular Argument verification.
     * @see tech.antibytes.kmock.verification.constraints.any
     * @see tech.antibytes.kmock.verification.constraints.eq
     * @see tech.antibytes.kmock.verification.constraints.isNot
     * @see tech.antibytes.kmock.verification.constraints.isNotSame
     * @see tech.antibytes.kmock.verification.constraints.isSame
     * @author Matthias Geisler
     */
    fun interface VerificationConstraint {
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
        val Proxy: Proxy<*, *>,

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
         * Ensures that given Proxies are covered by the VerificationChain.
         * @throws IllegalStateException if a given Proxy is not covered by a VerificationChain.
         */
        fun ensureVerificationOf(vararg proxies: Proxy<*, *>)
    }

    /**
     * Builder for a VerificationChain.
     * Meant for internal usage only!
     * @author Matthias Geisler
     */
    interface VerificationChainBuilder {
        /**
         * Adds a VerificationHandle to the Chain.
         * Meant for internal usage only!
         * @param VerificationHandle a handle which will be used for verification
         */
        fun add(handle: VerificationHandle)

        /**
         * Transforms the chain into a list.
         * Meant for internal usage only!
         * @return a List of VerificationHandle.
         */
        fun toList(): List<VerificationHandle>
    }

    /**
     * Container to which holds actual references of proxy calls. The references are ordered by their invocation.
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

    companion object {
        const val NOT_CALLED = "Call not found."
        const val TOO_LESS_CALLS = "Expected at least \$1 calls, but found only \$2."
        const val TOO_MANY_CALLS = "Expected at most \$1 calls, but exceeded with \$2."
        const val NOTHING_TO_STRICTLY_VERIFY = "The given verification chain (has \$1 items) does not match the captured calls (\$2 were captured)."
        const val NOTHING_TO_VERIFY = "The given verification chain (has \$1 items) is exceeding the captured calls (\$2 were captured)."
        const val NO_MATCHING_CALL_IDX = "The captured calls of \$1 exceeds the captured calls."
        const val MISMATCHING_FUNCTION = "Excepted '\$1', but got '\$2'."
        const val MISMATCHING_CALL_IDX = "Excepted the \$1, but the \$2 was referenced."
        const val CALL_NOT_FOUND = "Last referred invocation of \$1 was not found."
    }
}
