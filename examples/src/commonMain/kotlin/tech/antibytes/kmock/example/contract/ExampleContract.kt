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
}
