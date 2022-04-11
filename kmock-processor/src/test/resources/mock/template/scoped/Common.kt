/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.scoped

import tech.antibytes.kmock.MockCommon

interface Something

interface SomethingElse<T>

interface AnythingElse {
    interface SomethingInside
}

@MockCommon(Common::class)
interface Common {
    var Something.thing: Int
    val SomethingElse<Any>.things: List<Any>
    var Common.extension: Int
    val <T> T.nothing: Unit where T : Something, T : Comparable<T>
    val myThing: String
    val AnythingElse.SomethingInside.inside: Int

    fun Something.doSomething(): Int
    fun SomethingElse<Any>.doSomethingElse(): List<Any>
    fun Common.mutabor(): Int
    fun AnythingElse.SomethingInside.toString(): Int
    fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T>
    fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T : Comparable<T>
    fun AnythingElse.SomethingInside.doInside(): Int
    fun iDo()
}
