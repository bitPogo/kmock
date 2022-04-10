/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.scoped

import tech.antibytes.kmock.Mock

interface Something

interface SomethingElse<T>

interface AnythingElse {
    interface SomethingInside
}

@Mock(Platform::class)
interface Platform {
    var Something.thing: Int
    val SomethingElse<Any>.things: List<Any>
    var Platform.extension: Int
    val <T> T.nothing: Unit where T : Something, T : Comparable<T>
    val myThing: String
    val AnythingElse.SomethingInside.inside: Int

    fun Something.equals(): Int
    fun Something.doSomething(): Int
    fun SomethingElse<Any>.doSomethingElse(): List<Any>
    fun Platform.mutabor(): Int
    fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T>
    fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T : Comparable<T>
    fun AnythingElse.SomethingInside.doInside(): Int
    fun iDo()
}
