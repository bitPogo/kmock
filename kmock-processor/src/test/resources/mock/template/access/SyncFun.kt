/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.access

import tech.antibytes.kmock.Mock

@Mock(SyncFun::class)
interface SyncFun<L, T> where T : CharSequence, T : Comparable<T> {
    fun foo(fuzz: Int, ozz: Any): dynamic

    fun bar(buzz: Int, bozz: Any): Any = bozz

    fun ozz(vararg buzz: Int): Any

    fun izz(vararg buzz: Any): Any

    fun uzz()

    fun tuz(): Int

    fun uz(): L

    fun tzz(): T

    fun <T> lol(arg: T): T

    fun veryLongMethodNameWithABunchOfVariables(
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
