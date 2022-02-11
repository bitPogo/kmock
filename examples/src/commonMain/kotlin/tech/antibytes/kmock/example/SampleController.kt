/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example

class SampleController(
    private val localRepository: ExampleContract.SampleLocalRepository,
    private val remoteRepository: ExampleContract.SampleRemoteRepository
) : ExampleContract.SampleController {
    override suspend fun fetchAndStore(url: String): ExampleContract.SampleDomainObject {
        val domainObject = remoteRepository.fetch(url)

        return localRepository.store(domainObject.id, domainObject.value)
    }

    override fun find(id: String): ExampleContract.SampleDomainObject {
        TODO("Not yet implemented")
    }
}
