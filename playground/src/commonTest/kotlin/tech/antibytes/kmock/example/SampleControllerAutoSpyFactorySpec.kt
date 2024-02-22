/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example

import co.touchlab.stately.concurrency.AtomicReference
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MultiMockCommon
import tech.antibytes.kmock.example.contract.ExampleContract
import tech.antibytes.kmock.example.contract.ExampleContract.SampleDomainObject
import tech.antibytes.kmock.example.contract.ExampleContract.SampleLocalRepository
import tech.antibytes.kmock.example.contract.ExampleContract.SampleRemoteRepository
import tech.antibytes.kmock.example.contract.SampleDomainObjectMock
import tech.antibytes.kmock.example.contract.SampleLocalRepositoryMock
import tech.antibytes.kmock.example.contract.SampleRemoteRepositoryMock
import tech.antibytes.kmock.verification.Asserter
import tech.antibytes.kmock.verification.NonFreezingAsserter
import tech.antibytes.kmock.verification.assertOrder
import tech.antibytes.kmock.verification.verify
import tech.antibytes.kmock.verification.verifyOrder
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.defaultScheduler
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.mustBe

@MockCommon(
    SampleRemoteRepository::class,
    SampleLocalRepository::class,
    SampleDomainObject::class,
    ExampleContract.DecoderFactory::class,
)
@MultiMockCommon(
    "MergedCommon",
    SampleDomainObject::class,
    ExampleContract.DecoderFactory::class,
)
class SampleControllerAutoSpyFactorySpec {
    private val fixture = kotlinFixture()
    private val collector = Asserter()
    private val local: SampleLocalRepositoryMock = kmock(collector, relaxed = true)
    private val remote: SampleRemoteRepositoryMock = kmock(collector, relaxed = true)
    private val domainObjectInstance = DomainObject("test", 21)
    private val domainObject: SampleDomainObjectMock = kspy(
        domainObjectInstance,
        collector,
    )

    @BeforeTest
    fun setUp() {
        collector.clear()
        local._clearMock()
        remote._clearMock()
        domainObject._clearMock()
        clearBlockingTest()
    }

    @Test
    @JsName("fn1")
    fun `Given fetchAndStore it fetches and stores DomainObjects`() = runTest {
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

        val actual = controller.fetchAndStore(url)

        // Then
        actual mustBe domainObject

        assertTrue((domainObject as Any) == actual) // will pass
        assertTrue((actual as Any) == domainObjectInstance) // will pass
        assertFalse((domainObjectInstance as Any) == actual) // will fail

        verify(exactly = 1) { remote._fetch.hasBeenStrictlyCalledWith(url) }
        verify(exactly = 1) { local._store.hasBeenCalledWith() }

        collector.assertOrder {
            remote._fetch.hasBeenStrictlyCalledWith(url)
            domainObject._id.wasGotten()
            domainObject._id.wasSet()
            domainObject._id.wasGotten()
            domainObject._value.wasGotten()
            local._store.hasBeenCalledWith()
        }

        collector.verifyOrder {
            remote._fetch.hasBeenCalledWith(url)
            domainObject._id.wasSetTo("42")
            local._store.hasBeenCalledWith()
        }
    }

    @Test
    @JsName("fn2")
    fun `Given find it fetches a DomainObjects`(): AsyncTestReturnValue {
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
                .onEach { actual -> actual.hashCode() mustBe doRef.get().hashCode() }
                .launchIn(CoroutineScope(contextRef.get()))

            delay(20)

            verify(exactly = 1) { local._contains.hasBeenStrictlyCalledWith(idOrg) }
            verify(exactly = 2) { local._fetch.hasBeenCalledWith() }
            verify(exactly = 2) { remote._find.hasBeenStrictlyCalledWith(idOrg) }

            collector.assertOrder {
                local._contains.hasBeenStrictlyCalledWith(idOrg)
                remote._find.hasBeenStrictlyCalledWith(idOrg)
                domainObject._id.wasGotten()
                local._fetch.hasBeenCalled()
                domainObject._id.wasGotten()
                local._fetch.hasBeenCalled()
                remote._find.hasBeenStrictlyCalledWith(idOrg)
                domainObject._id.wasSet()
            }

            collector.verifyOrder {
                local._contains.hasBeenCalledWithout("abc")
            }
        }
    }

    @Test
    @JsName("fn3")
    fun `Given find it fetches blocking a DomainObjects`() {
        // Given
        val idOrg = fixture.fixture<String>()
        val instance = DomainObject2("test", 21)
        val collector = NonFreezingAsserter()
        val local: SampleLocalRepositoryMock = kmock(collector, relaxed = true, freeze = false)
        val remote: SampleRemoteRepositoryMock = kmock(collector, relaxed = true, freeze = false)

        val domainObject: SampleDomainObjectMock = kspy(
            instance,
            collector,
            freeze = false,
        )

        remote._find.returnValue = domainObject
        local._contains run { true }
        local._fetch.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)

        // When
        controller.findBlocking(idOrg)

        verify(exactly = 1) { local._contains.hasBeenStrictlyCalledWith(idOrg) }
        verify(exactly = 1) { local._fetch.hasBeenCalledWith() }
        verify(exactly = 1) { remote._find.hasBeenStrictlyCalledWith(idOrg) }

        collector.assertOrder {
            local._contains.hasBeenStrictlyCalledWith(idOrg)
            remote._find.hasBeenStrictlyCalledWith(idOrg)
            domainObject._id.wasGotten()
            local._fetch.hasBeenCalledWith()
            domainObject._id.wasSet()
        }

        collector.verifyOrder {
            local._contains.hasBeenCalledWithout("abc")
        }
    }

    @Test
    @JsName("fn4")
    fun `Given a multi interface mock it runs`() {
        // Given
        val mock: MergedCommonMock<*> = kmock()

        // When
        mock._id.getValue = "23"

        // Then
        mock.id mustBe "23"
    }
}

private class DomainObject(
    id: String,
    value: Int,
) : SampleDomainObject {
    private val _id: AtomicRef<String> = atomic(id)
    private val _value: AtomicInt = atomic(value)

    override var id: String by _id
    override val value: Int by _value
}

private class DomainObject2(
    override var id: String,
    override val value: Int,
) : SampleDomainObject
