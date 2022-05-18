/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.receiver

import tech.antibytes.kmock.MockShared

interface Something

interface SomethingElse<T>

interface AnythingElse {
    interface SomethingInside
}

@MockShared(
    "sharedTest",
    Shared::class
)
interface Shared {
    fun Something.doSomething(): Int
    fun SomethingElse<Any>.doSomethingElse(): List<Any>
    fun SomethingElse<Any>.hashCode(): String
    fun Shared.mutabor(): Int
    fun <T> T.doNothing(): Unit where T : Something, T : Comparable<T>
    fun <T, R : Any> T.doNothingElse(a: R): Unit where T : Something, T : Comparable<T>
    fun AnythingElse.SomethingInside.doInside(): Int
    fun iDo()
}
