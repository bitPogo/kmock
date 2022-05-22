/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.access

import tech.antibytes.kmock.Mock

@Mock(AsyncFun::class)
interface AsyncFun<L, T> where T : CharSequence, T : Comparable<T> {
    suspend fun foo(fuzz: Int, ozz: Any): dynamic

    suspend fun bar(buzz: Int, bozz: Any): Any = bozz

    suspend fun ozz(vararg buzz: Int): Any

    suspend fun izz(vararg buzz: Any): Any

    suspend fun uzz()

    suspend fun tuz(): Int

    suspend fun uz(): L

    suspend fun tzz(): T

    suspend fun <T> lol(arg: T): T

    suspend fun veryLongMethodNameWithABunchOfVariables(
        arg0: Int,
        arg1: Int,
        arg2: Int,
        arg3: Int,
        arg4: Int,
        arg5: Int,
        arg6: Int,
        arg7: Int,
        arg8: Int,
        arg9: Int
    )
}
