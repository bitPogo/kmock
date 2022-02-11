/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.util.test.MockError

class AsyncFunMockery<ReturnValue, SideEffect : Function<ReturnValue>>(
    id: String,
    collector: Collector = Collector { _, _ -> Unit }
) : KMockContract.AsyncFunMockery<ReturnValue, SideEffect>, FunMockery<ReturnValue, SideEffect>(id, collector) {
    private suspend fun execute(
        function: suspend () -> ReturnValue,
        vararg arguments: Any?
    ): ReturnValue {
        onEvent(arguments)

        return when (provider.get()) {
            PROVIDER.RETURN_VALUE -> retrieveValue()
            PROVIDER.RETURN_VALUES -> retrieveFromValues()
            PROVIDER.SIDE_EFFECT -> function()
            else -> throw MockError.MissingStub("Missing stub value for $id")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun invoke(): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend () -> ReturnValue)
                .invoke()
        }

        return execute(invocation)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0> invoke(arg0: Arg0): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0) -> ReturnValue)
                .invoke(arg0)
        }

        return execute(invocation, arg0)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1> invoke(arg0: Arg0, arg1: Arg1): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1) -> ReturnValue)
                .invoke(arg0, arg1)
        }

        return execute(invocation, arg0, arg1)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2> invoke(arg0: Arg0, arg1: Arg1, arg2: Arg2): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2) -> ReturnValue)
                .invoke(arg0, arg1, arg2)
        }

        return execute(invocation, arg0, arg1, arg2)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3> invoke(arg0: Arg0, arg1: Arg1, arg2: Arg2, arg3: Arg3): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3)
        }

        return execute(invocation, arg0, arg1, arg2, arg3)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        arg7: Arg7
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> invoke(
        arg0: Arg0,
        arg1: Arg1,
        arg2: Arg2,
        arg3: Arg3,
        arg4: Arg4,
        arg5: Arg5,
        arg6: Arg6,
        arg7: Arg7,
        arg8: Arg8
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9> invoke(
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
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10> invoke(
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
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11> invoke(
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
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12> invoke(
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
        arg12: Arg12
    ): ReturnValue {
        val invocation = suspend {
            (retrieveSideEffect() as suspend (Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, Arg12) -> ReturnValue)
                .invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12)
        }

        return execute(invocation, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12)
    }
}
