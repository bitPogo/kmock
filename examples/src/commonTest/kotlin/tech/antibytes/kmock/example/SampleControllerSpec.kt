/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example

import co.touchlab.stately.concurrency.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tech.antibytes.kmock.AsyncFunMockery
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.PropertyMockery
import tech.antibytes.kmock.SyncFunMockery
import tech.antibytes.kmock.Verifier
import tech.antibytes.kmock.assertHasBeenCalledStrictlyWith
import tech.antibytes.kmock.example.contract.ExampleContract
import tech.antibytes.kmock.example.contract.ExampleContract.SampleDomainObject
import tech.antibytes.kmock.example.contract.ExampleContract.SampleLocalRepository
import tech.antibytes.kmock.example.contract.ExampleContract.SampleRemoteRepository
import tech.antibytes.kmock.hasBeenCalledWith
import tech.antibytes.kmock.hasBeenCalledWithout
import tech.antibytes.kmock.hasBeenStrictlyCalledWith
import tech.antibytes.kmock.verify
import tech.antibytes.kmock.verifyOrder
import tech.antibytes.kmock.verifyStrictOrder
import tech.antibytes.kmock.wasGotten
import tech.antibytes.kmock.wasSet
import tech.antibytes.kmock.wasSetTo
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.defaultTestContext
import tech.antibytes.util.test.coroutine.runBlockingTestWithTimeout
import tech.antibytes.util.test.coroutine.runBlockingTestWithTimeoutInScope
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test

class SampleControllerSpec {
    private val fixture = kotlinFixture()

    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils SampleController`() {
        SampleController(
            SampleLocalRepositoryManualStub(),
            SampleRemoteRepositoryManualStub()
        ) fulfils ExampleContract.SampleController::class
    }

    @Test
    @JsName("fn1")
    fun `Given fetchAndStore it fetches and stores DomainObjects`(): AsyncTestReturnValue {
        // Given
        val verifier = Verifier()

        val url = fixture.fixture<String>()
        val id = fixture.listFixture<String>(size = 2)
        val number = fixture.fixture<Int>()

        val local = SampleLocalRepositoryManualStub(verifier)
        val remote = SampleRemoteRepositoryManualStub(verifier)
        val domainObject = SampleDomainObjectManualStub(verifier)

        domainObject.propId.getMany = id
        domainObject.propValue.get = number

        remote.fetch.returnValue = domainObject
        local.store.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        return runBlockingTestWithTimeout {
            val actual = controller.fetchAndStore(url)

            // Then
            actual mustBe domainObject

            verify(exactly = 1) { remote.fetch.hasBeenStrictlyCalledWith(url) }
            verify(exactly = 1) { local.store.hasBeenStrictlyCalledWith(id[1], number) }

            verifier.verifyStrictOrder {
                remote.fetch.hasBeenStrictlyCalledWith(url)
                domainObject.propId.wasGotten()
                domainObject.propId.wasSet()
                domainObject.propId.wasGotten()
                domainObject.propValue.wasGotten()
                local.store.hasBeenCalledWith(id[1])
            }

            verifier.verifyOrder {
                remote.fetch.hasBeenCalledWith(url)
                domainObject.propId.wasSetTo("42")
                local.store.hasBeenCalledWith(id[1])
            }
        }
    }

    @Test
    @JsName("fn2")
    fun `Given find it fetches a DomainObjects`(): AsyncTestReturnValue {
        // Given
        val verifier = Verifier()

        val idOrg = fixture.fixture<String>()
        val id = fixture.fixture<String>()
        val number = fixture.fixture<Int>()

        val local = SampleLocalRepositoryManualStub(verifier)
        val remote = SampleRemoteRepositoryManualStub(verifier)
        val domainObject = SampleDomainObjectManualStub(verifier)

        domainObject.propId.get = id
        domainObject.propValue.get = number

        remote.find.returnValue = domainObject
        local.contains.sideEffect = { true }
        local.fetch.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        val doRef = AtomicReference(domainObject)
        val contextRef = AtomicReference(defaultTestContext)

        return runBlockingTestWithTimeoutInScope(defaultTestContext) {
            // When
            controller.find(idOrg)
                .onEach { actual -> actual mustBe doRef.get() }
                .launchIn(CoroutineScope(contextRef.get()))

            delay(20)

            local.contains.assertHasBeenCalledStrictlyWith(1, idOrg)
            local.fetch.assertHasBeenCalledStrictlyWith(1, id)
            remote.find.assertHasBeenCalledStrictlyWith(1, idOrg)

            verifier.verifyStrictOrder {
                local.contains.hasBeenStrictlyCalledWith(idOrg)
                remote.find.hasBeenStrictlyCalledWith(idOrg)
                domainObject.propId.wasGotten()
                local.fetch.hasBeenStrictlyCalledWith(id)
                domainObject.propId.wasSet()
            }

            verifier.verifyOrder {
                local.contains.hasBeenCalledWithout("abc")
            }
        }
    }
}

private class SampleDomainObjectManualStub(
    verifier: Collector = Collector { _, _ -> Unit }
) : SampleDomainObject {
    val propId = PropertyMockery<String>("do#id", verifier)
    val propValue = PropertyMockery<Int>("do#value", verifier)

    override var id: String
        get() = propId.onGet()
        set(value) = propId.onSet(value)

    override val value: Int
        get() = propValue.onGet()
}

private class SampleRemoteRepositoryManualStub(
    verifier: Collector = Collector { _, _ -> Unit }
) : SampleRemoteRepository {
    val fetch = AsyncFunMockery<SampleDomainObject, suspend (String) -> SampleDomainObject>("remote#fetch", verifier)
    val find = SyncFunMockery<SampleDomainObject, (String) -> SampleDomainObject>("remote#find", verifier)

    override suspend fun fetch(url: String): SampleDomainObject = fetch.invoke(url)

    override fun find(id: String): SampleDomainObject = find.invoke(id)
}

private class SampleLocalRepositoryManualStub(
    verifier: Collector = Collector { _, _ -> Unit }
) : SampleLocalRepository {
    val store = AsyncFunMockery<SampleDomainObject, suspend (String, Int) -> SampleDomainObject>("local#store", verifier)
    val contains = SyncFunMockery<Boolean, (String) -> Boolean>("local#contains", verifier)
    val fetch = SyncFunMockery<SampleDomainObject, (String) -> SampleDomainObject>("local#fetch", verifier)

    override suspend fun store(id: String, value: Int): SampleDomainObject = store.invoke(id, value)

    override fun contains(id: String): Boolean = contains.invoke(id)

    override fun fetch(id: String): SampleDomainObject = fetch.invoke(id)
}
