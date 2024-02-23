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

    interface SampleRemoteRepository {
        suspend fun fetch(url: String): SampleDomainObject
        fun find(id: String): SampleDomainObject
    }

    interface SampleLocalRepository {
        suspend fun store(id: String, value: Int): SampleDomainObject
        fun contains(id: String): Boolean
        fun fetch(id: String): SampleDomainObject
    }

    interface SampleController {
        suspend fun fetchAndStore(url: String): SampleDomainObject
        fun find(id: String): SharedFlow<SampleDomainObject>
    }
}
