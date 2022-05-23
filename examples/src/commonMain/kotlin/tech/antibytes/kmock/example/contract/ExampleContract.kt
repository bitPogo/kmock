/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example.contract

import kotlinx.coroutines.flow.SharedFlow

interface ExampleContract {
    interface SampleDomainObject {
        var id: String
        val value: Int
    }

    interface GenericSampleDomainObject<Id, Value> where Id : CharSequence, Id : Comparable<Id> {
        var id: Id
        val value: Value
        fun toSampleDomainObject()
    }

    interface SampleRemoteRepository {
        suspend fun fetch(url: String): SampleDomainObject
        fun find(id: String): SampleDomainObject
        suspend fun doSomething()
    }

    interface SampleLocalRepository {
        suspend fun store(id: String, value: Int): SampleDomainObject
        fun contains(id: String): Boolean
        fun fetch(id: String): SampleDomainObject
    }

    interface SampleController {
        suspend fun fetchAndStore(url: String): SampleDomainObject
        fun find(id: String): SharedFlow<SampleDomainObject>
        fun findBlocking(id: String): SampleDomainObject
    }

    interface DecoderFactory {
        fun createDecoder(): PlatformDecoder?
    }

    interface SampleUselessObject {
        fun doSomething(): Int

        fun <T> doSomething(arg: T): Int

        fun <T> doSomething(arg: T): Int where T : Comparable<T>, T : CharSequence

        fun <T> doSomething(arg0: T, arg1: String): String

        fun <T> doSomething(arg0: T, arg1: Int): String

        fun doSomethingElse(): Int

        fun <T> doSomethingElse(arg: T): Int

        fun <T> doSomethingElse(arg: T): Int where T : Comparable<T>, T : CharSequence

        fun <T> doSomethingElse(arg0: T, arg1: String): String

        fun <T> doSomethingElse(arg0: T, arg1: Int): String
    }
}
