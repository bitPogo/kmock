/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example

import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.example.ExampleContract.SampleLocalRepository
import tech.antibytes.kmock.example.ExampleContract.SampleRemoteRepository
import tech.antibytes.kmock.example.ExampleContract.SampleDomainObject
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.SyncFunMockery
import tech.antibytes.kmock.Verifier
import tech.antibytes.kmock.verify
import tech.antibytes.kmock.verifyOrder
import tech.antibytes.kmock.verifyStrictOrder
import tech.antibytes.kmock.wasGotten
import tech.antibytes.kmock.withArguments
import tech.antibytes.kmock.withSameArguments
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class SampleControllerSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils SampleController`() {
        SampleController(
            SampleLocalRepositoryStub(),
            SampleRemoteRepositoryStub()
        ) fulfils ExampleContract.SampleController::class
    }

    @Test
    @JsName("fn1")
    fun `Given fetchAndStore it fetches and stores DomainObjects`(): AsyncTestReturnValue {
        // Given
        val verifier = Verifier()

        val url = fixture.fixture<String>()
        val id = fixture.fixture<String>()
        val number = fixture.fixture<Int>()

        val local = SampleLocalRepositoryStub(verifier)
        val remote = SampleRemoteRepositoryStub(verifier)
        val domainObject = SampleDomainObjectStub(verifier)

        domainObject.propId.get = id
        domainObject.propValue.get = number

        remote.fetch.returnValue = domainObject
        local.store.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        return runBlockingTest {
            val actual = controller.fetchAndStore(url)

            // Then
            actual mustBe domainObject

            verify(exactly = 1) { remote.fetch.withSameArguments(url) }
            verify(exactly = 1) { local.store.withSameArguments(id, number) }

            verifier.verifyStrictOrder {
                withArguments(remote.fetch, url)
                add(domainObject.propId.wasGotten())
                add(domainObject.propValue.wasGotten())
                withSameArguments(local.store, id, number)
            }

            verifier.verifyOrder {
                withArguments(remote.fetch, url)
                withSameArguments(local.store, id, number)
            }
        }
    }
}


private class SampleDomainObjectStub(
    verifier: Collector = Collector { _, _ -> Unit }
) : SampleDomainObject {
    val propId = PropertyMockery<String>("id", verifier)
    val propValue = PropertyMockery<Int>("value", verifier)

    override var id: String
        get() = propId.onGet()
        set(value) = propId.onSet(value)

    override val value: Int
        get() = propValue.onGet()
}

private class SampleRemoteRepositoryStub(
    verifier: Collector = Collector { _, _ -> Unit }
) : SampleRemoteRepository {
    val fetch = AsyncFunMockery<SampleDomainObject, suspend (String) -> SampleDomainObject>("fetch", verifier)
    val find = SyncFunMockery<SampleDomainObject, (String) -> SampleDomainObject>("find", verifier)

    override suspend fun fetch(url: String): SampleDomainObject = fetch.invoke(url)

    override fun find(id: String): SampleDomainObject = find.invoke(id)
}

private class SampleLocalRepositoryStub(
   verifier: Collector = Collector { _, _ -> Unit }
): SampleLocalRepository {
    val store = AsyncFunMockery<SampleDomainObject, suspend (String, Int) -> SampleDomainObject>("store", verifier)
    val contains = SyncFunMockery<Boolean, (String) -> Boolean>("contains", verifier)
    val fetch = SyncFunMockery<SampleDomainObject, (String) -> SampleDomainObject>("fetch", verifier)

    override suspend fun store(id: String, value: Int): SampleDomainObject = store.invoke(id, value)

    override fun contains(id: String): Boolean = contains.invoke(id)

    override fun fetch(id: String): SampleDomainObject = fetch.invoke(id)
}
