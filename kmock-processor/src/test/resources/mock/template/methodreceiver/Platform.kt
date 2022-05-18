/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.methodreceiver

import mock.template.receiver.AnythingElse
import mock.template.receiver.Something
import mock.template.receiver.SomethingElse
import tech.antibytes.kmock.Mock

interface Something

interface SomethingElse<T>

interface AnythingElse {
    interface SomethingInside
}

@Mock(Platform::class)
interface Platform<L> {
    fun Something.equals(): Int
    fun Something.doSomething(): Int
    fun SomethingElse<Any>.doSomethingElse(): List<Any>
    fun Platform<*>.mutabor(): Int
    fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T>
    fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T : Comparable<T>
    fun AnythingElse.SomethingInside.doInside(): Int

    fun iDo()
    fun mutabor(x: Platform<*>)
    fun doNothingElse(a: Any): Any
    fun doSomethingElse(x: SomethingElse<Any>)
}
