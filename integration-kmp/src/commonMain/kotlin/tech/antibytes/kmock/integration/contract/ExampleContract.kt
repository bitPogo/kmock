/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration.contract

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

    data class Generic<T>(
        val value: T,
    )

    interface SampleUselessObject {
        fun doSomething(): Int

        fun <T> doSomething(arg: T): Int

        fun <T> doSomething(arg: T): Int where T : Comparable<T>, T : CharSequence

        fun <T> doSomething(arg0: T, arg1: String): String

        fun <T> doSomething(arg0: T, arg1: Int): String

        fun doSomethingElse(): Int

        fun <T> doSomethingElse(arg: T): Int

        fun <T> doSomethingElse(arg: T): Int where T : Comparable<T>, T : CharSequence

        fun <T : Map<String, B<K>>, K> doSomethingElse(arg0: T, arg1: String): String

        fun <T> doSomethingElse(arg0: T, arg1: Int): String

        fun <K : A<T>, T> doAnything(arg: K): Int where T : Comparable<T>, T : CharSequence

        fun <T> run(arg: T): Int

        fun <T : () -> String> withFun(arg: T): String

        fun <T : () -> Unit> withOtherFun(arg: T): Int
    }
}

typealias A<T> = ExampleContract.Generic<T>
typealias B<T> = A<T>
