/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.propertyreceiver

import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.Relaxer

interface Something

interface SomethingElse<T>

interface AnythingElse {
    interface SomethingInside
}

@Relaxer
internal inline fun <reified T> relaxed(id: String): T {
    return id as T
}

@Mock(Relaxed::class)
interface Relaxed<L> {
    var Something.thing: Int
    val SomethingElse<Any>.things: List<Any>
    var Relaxed<*>.extension: Int
    var <T> T.nothing: T where T : Something, T : Comparable<T>
    var L.otherThing: String
    var L.nextThing: L
    var <T : L> T.nothing: L
    var <T : L> T.otherThing: T
    val myThing: String
    val AnythingElse.SomethingInside.inside: Int

    fun getOtherThing()
}
