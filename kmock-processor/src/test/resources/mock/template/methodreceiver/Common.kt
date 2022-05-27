/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.methodreceiver

import tech.antibytes.kmock.MockCommon

interface Something

interface SomethingElse<T>

interface AnythingElse {
    interface SomethingInside
}

@MockCommon(Common::class)
interface Common<L> {
    fun Something.equals(): Int
    fun Something.doSomething(): Int
    fun SomethingElse<Any>.doSomethingElse(): List<Any>
    fun Common<*>.mutabor(): Int
    fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T>
    fun <T> T.doNothing(): Unit where T : Any
    fun <T, R : Any, X : Comparable<X>> T.doNothingElse(a: R, b: X): Unit where T : Something, T : Comparable<T>
    fun AnythingElse.SomethingInside.doInside(): Int

    fun iDo()
    fun mutabor(x: Common<*>)
    fun doNothingElse(a: Any): Any
    fun doSomethingElse(x: SomethingElse<Any>)
}
