/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.methodreceiver

import tech.antibytes.kmock.Mock

interface Something

interface SomethingElse<T>

interface AnythingElse {
    interface SomethingInside
}

typealias Arg = (Any) -> Unit

@Mock(Inherited::class)
interface Inherited<P>: Platform<P>

interface Platform<L> {
    fun Something.equals(): Int
    fun Something.doSomething(): Int
    fun SomethingElse<Any>.doSomethingElse(): List<Any>
    fun Platform<*>.mutabor(): Int
    suspend fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T>
    suspend fun <T> T.doNothing(): Unit where T : Any
    fun <T> T.doSomethingElse(): L
    fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T : Comparable<T>
    fun AnythingElse.SomethingInside.doInside(): Int

    fun iDo()
    fun mutabor(x: Platform<*>)
    fun doNothingElse(a: Any): Any
    fun doSomethingElse(x: SomethingElse<Any>)
}
