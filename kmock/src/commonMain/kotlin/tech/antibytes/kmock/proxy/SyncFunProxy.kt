/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.FunProxyInvocationType
import tech.antibytes.kmock.KMockContract.NonIntrusiveFunConfigurator
import tech.antibytes.kmock.KMockContract.Relaxer

/**
 * Synchronous function Proxy in order to stub/mock synchronous function behaviour.
 * @constructor Creates a SyncFunProxy
 * @param ReturnValue the value type of the hosting PropertyProxy.
 * @param SideEffect the function signature.
 * @param id a unique identifier for this Proxy.
 * @param collector a optional Collector for VerificationChains. Default is a NoopCollector.
 * @param ignorableForVerification marks the Proxy as ignorable for verification. Default is false and is intended for internal usage only.
 * @param freeze boolean which indicates if freezing can be used or not. Default is true.
 * Default is null.
 * @see Collector
 * @see Relaxer
 * @author Matthias Geisler
 */
internal class SyncFunProxy<ReturnValue, SideEffect : Function<ReturnValue>>(
    id: String,
    collector: Collector = NoopCollector,
    ignorableForVerification: Boolean = false,
    freeze: Boolean = true,
) : KMockContract.SyncFunProxy<ReturnValue, SideEffect>,
    FunProxy<ReturnValue, SideEffect>(
        id = id,
        ignorableForVerification = ignorableForVerification,
        collector = collector,
        freeze = freeze,
    ) {
    private fun execute(
        method: () -> ReturnValue,
        chainFunction: () -> ReturnValue,
        nonIntrusiveFunConfiguration: KMockContract.NonIntrusiveFunTarget<ReturnValue, () -> ReturnValue>,
        vararg arguments: Any?
    ): ReturnValue {
        onEvent(arguments)

        return when (invocationType) {
            FunProxyInvocationType.THROWS -> throw error
            FunProxyInvocationType.THROWS_MANY -> throw retrieveFromThrowables()
            FunProxyInvocationType.RETURN_VALUE -> returnValue
            FunProxyInvocationType.RETURN_VALUES -> retrieveFromValues()
            FunProxyInvocationType.SIDE_EFFECT -> method()
            FunProxyInvocationType.SIDE_EFFECT_CHAIN -> chainFunction()
            FunProxyInvocationType.SPY -> nonIntrusiveFunConfiguration.unwrapSpy()!!.invoke()
            FunProxyInvocationType.RELAXED -> nonIntrusiveFunConfiguration.unwrapRelaxer()!!.relax(id)
            else -> fail()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as () -> ReturnValue)
                .invoke()
        }

        val chainInvocation = {
            (retrieveSideEffect() as () -> ReturnValue)
                .invoke()
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook)
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0> invoke(
        arg0: Arg0,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0) -> ReturnValue)
                .invoke(arg0)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0) -> ReturnValue)
                .invoke(arg0)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1> invoke(
        arg0: Arg0,
        arg1: Arg1,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1) -> ReturnValue)
                .invoke(arg0, arg1)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1) -> ReturnValue)
                .invoke(arg0, arg1)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2) -> ReturnValue)
                .invoke(arg0, arg1, arg2)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2) -> ReturnValue)
                .invoke(arg0, arg1, arg2)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4,
            arg5
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4,
            arg5,
            arg6
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        arg7: Arg7,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4,
            arg5,
            arg6,
            arg7
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        arg7: Arg7,
        arg8: Arg8,
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4,
            arg5,
            arg6,
            arg7,
            arg8
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
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
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4,
            arg5,
            arg6,
            arg7,
            arg8,
            arg9
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
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
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4,
            arg5,
            arg6,
            arg7,
            arg8,
            arg9,
            arg10
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
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
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4,
            arg5,
            arg6,
            arg7,
            arg8,
            arg9,
            arg10,
            arg11
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
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
        nonIntrusiveHook: NonIntrusiveFunConfigurator<ReturnValue, Function0<ReturnValue>>.() -> Unit,
    ): ReturnValue {
        val invocation = {
            (sideEffect as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12)
        }

        val chainInvocation = {
            (retrieveSideEffect() as (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12)
        }

        return execute(
            method = invocation,
            chainFunction = chainInvocation,
            nonIntrusiveFunConfiguration = configureNonIntrusiveBehaviour(nonIntrusiveHook),
            arg0,
            arg1,
            arg2,
            arg3,
            arg4,
            arg5,
            arg6,
            arg7,
            arg8,
            arg9,
            arg10,
            arg11,
            arg12
        )
    }
}
