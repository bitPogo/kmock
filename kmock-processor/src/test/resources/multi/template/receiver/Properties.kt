/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package multi.template.receiver

import tech.antibytes.kmock.MultiMock

interface Something

interface SomethingElse<T>

interface AnythingElse {
    interface SomethingInside
}

@MultiMock(
    "ReceiverMulti",
    Properties::class,
    Methods::class
)
interface Properties<L> {
    var Something.thing: Int
    val SomethingElse<Any>.things: List<Any>
    var Properties<*>.extension: Int
    var <T> T.nothing: T where T : Something, T : Comparable<T>
    var L.otherThing: String
    var <T : L> T.nothing: L
    val myThing: String
    val AnythingElse.SomethingInside.inside: Int

    fun getOtherThing()
}
