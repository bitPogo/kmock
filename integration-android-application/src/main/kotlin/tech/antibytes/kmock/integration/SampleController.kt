/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration

import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tech.antibytes.kmock.integration.contract.ExampleContract

class SampleController(
    private val localRepository: ExampleContract.SampleLocalRepository,
    private val remoteRepository: ExampleContract.SampleRemoteRepository,
) : ExampleContract.SampleController {
    override suspend fun fetchAndStore(url: String): ExampleContract.SampleDomainObject {
        val domainObject = remoteRepository.fetch(url)
        domainObject.id
        domainObject.id = "42"

        return localRepository.store(domainObject.id, domainObject.value)
    }

    override fun find(id: String): SharedFlow<ExampleContract.SampleDomainObject> {
        val ref = AtomicReference(id)
        val flow = MutableSharedFlow<ExampleContract.SampleDomainObject>()

        CoroutineScope(Dispatchers.Default).launch {
            localRepository.contains(ref.get())

            val objc = remoteRepository.find(ref.get())

            localRepository.fetch(objc.id)

            objc.id = "23"

            flow.emit(objc)
        }

        return flow
    }
}
