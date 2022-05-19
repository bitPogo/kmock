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

@Mock(Spied::class)
interface Spied<L> {
    fun Something.equals(): Int
    fun Something.doSomething(): Int
    fun SomethingElse<Any>.doSomethingElse(): List<Any>
    fun Spied<*>.mutabor(): Int
    fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T>
    fun <T> T.doNothing(): Unit where T : Any
    fun <T> T.doSomethingElse(): L
    fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T : Comparable<T>
    fun AnythingElse.SomethingInside.doInside(): Int

    fun iDo()
    fun mutabor(x: Spied<*>)
    fun doNothingElse(a: Any): Any
    fun doSomethingElse(x: SomethingElse<Any>)
}
