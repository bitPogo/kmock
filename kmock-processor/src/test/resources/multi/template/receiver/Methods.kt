/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.receiver

interface Methods<L> {
    fun Something.equals(): Int
    fun Something.doSomething(): Int
    fun SomethingElse<Any>.doSomethingElse(): List<Any>
    fun Methods<*>.mutabor(): Int
    fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T>
    fun <T> T.doNothing(): Unit where T : Any
    fun <T> T.doSomethingElse(): L
    fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T : Comparable<T>
    fun AnythingElse.SomethingInside.doInside(): Int

    fun iDo()
    fun mutabor(x: Methods<*>)
    fun doNothingElse(a: Any): Any
    fun doSomethingElse(x: SomethingElse<Any>)
}
