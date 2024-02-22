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
import org.junit.Before
import org.junit.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.example.contract.AndroidThing
import tech.antibytes.kmock.example.contract.AndroidThingMock
import tech.antibytes.kmock.example.contract.ExampleContract
import tech.antibytes.kmock.example.contract.ExampleContract.SampleDomainObject
import tech.antibytes.kmock.example.contract.ExampleContract.SampleLocalRepository
import tech.antibytes.kmock.example.contract.ExampleContract.SampleRemoteRepository
import tech.antibytes.kmock.example.contract.SampleDomainObjectMock
import tech.antibytes.kmock.example.contract.SampleLocalRepositoryMock
import tech.antibytes.kmock.example.contract.SampleRemoteRepositoryMock
import tech.antibytes.kmock.verification.Asserter
import tech.antibytes.kmock.verification.assertOrder
import tech.antibytes.kmock.verification.verify
import tech.antibytes.kmock.verification.verifyOrder
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.defaultScheduler
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

@Mock(
    SampleRemoteRepository::class,
    SampleLocalRepository::class,
    SampleDomainObject::class,
    ExampleContract.DecoderFactory::class,
    AndroidThing::class,
)
class AndroidSampleControllerAutoStubSpec {
    private val fixture = kotlinFixture()
    private val collector = Asserter()
    private val local = SampleLocalRepositoryMock(collector)
    private val remote = SampleRemoteRepositoryMock(collector)
    private val domainObject = SampleDomainObjectMock(collector)
    private val androidThing = AndroidThingMock()

    @Before
    fun setUp() {
        collector.clear()
        local._clearMock()
        remote._clearMock()
        domainObject._clearMock()
        clearBlockingTest()
    }

    @Test
    fun `It fulfils SampleController`() {
        SampleController(local, remote) fulfils ExampleContract.SampleController::class
    }

    @Test
    fun `Given fetchAndStore it fetches and stores DomainObjects`() {
        // Given
        val url = fixture.fixture<String>()
        val id = fixture.listFixture<String>(size = 2)
        val number = fixture.fixture<Int>()

        domainObject._id.getValues = id
        domainObject._value.getValue = number

        remote._fetch.returnValue = domainObject
        local._store.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        return runBlockingTestWithTimeout {
            val actual = controller.fetchAndStore(url)

            // Then
            actual mustBe domainObject

            verify(exactly = 1) { remote._fetch.hasBeenStrictlyCalledWith(url) }
            verify(exactly = 1) { local._store.hasBeenStrictlyCalledWith(id[1], number) }

            collector.assertOrder {
                remote._fetch.hasBeenStrictlyCalledWith(url)
                domainObject._id.wasGotten()
                domainObject._id.wasSet()
                domainObject._id.wasGotten()
                domainObject._value.wasGotten()
                local._store.hasBeenCalledWith(id[1])
            }

            collector.verifyOrder {
                remote._fetch.hasBeenCalledWith(url)
                domainObject._id.wasSetTo("42")
                local._store.hasBeenCalledWith(id[1])
            }
        }
    }

    @Test
    fun `Given find it fetches a DomainObjects`() {
        // Given
        val idOrg = fixture.fixture<String>()
        val id = fixture.fixture<String>()
        val number = fixture.fixture<Int>()

        domainObject._id.getValue = id
        domainObject._value.getValue = number

        remote._find.returnValue = domainObject
        local._contains.sideEffect = { true }
        local._fetch.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        val doRef = AtomicReference(domainObject)
        val contextRef = AtomicReference(defaultScheduler)

        return runBlockingTestInContext(defaultScheduler) {
            // When
            controller.find(idOrg)
                .onEach { actual -> actual mustBe doRef.get() }
                .launchIn(CoroutineScope(contextRef.get()))

            delay(20)

            verify(exactly = 1) { local._contains.hasBeenStrictlyCalledWith(idOrg) }
            verify(exactly = 2) { local._fetch.hasBeenStrictlyCalledWith(id) }
            verify(exactly = 2) { remote._find.hasBeenStrictlyCalledWith(idOrg) }

            collector.assertOrder {
                local._contains.hasBeenStrictlyCalledWith(idOrg)
                remote._find.hasBeenStrictlyCalledWith(idOrg)
                domainObject._id.wasGotten()
                local._fetch.hasBeenStrictlyCalledWith(id)
                domainObject._id.wasGotten()
                local._fetch.hasBeenStrictlyCalledWith(id)
                remote._find.hasBeenStrictlyCalledWith(idOrg)
                domainObject._id.wasSet()
            }

            collector.verifyOrder {
                local._contains.hasBeenCalledWithout("abc")
                remote._find.hasBeenStrictlyCalledWith(idOrg)
                remote._find.hasBeenStrictlyCalledWith(idOrg)
            }
        }
    }
}
