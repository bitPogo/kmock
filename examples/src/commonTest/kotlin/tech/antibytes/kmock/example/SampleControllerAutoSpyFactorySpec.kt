/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example

import co.touchlab.stately.concurrency.AtomicReference
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.example.contract.ExampleContract
import tech.antibytes.kmock.example.contract.ExampleContract.SampleDomainObject
import tech.antibytes.kmock.example.contract.ExampleContract.SampleLocalRepository
import tech.antibytes.kmock.example.contract.ExampleContract.SampleRemoteRepository
import tech.antibytes.kmock.example.contract.SampleDomainObjectMock
import tech.antibytes.kmock.example.contract.SampleLocalRepositoryMock
import tech.antibytes.kmock.example.contract.SampleRemoteRepositoryMock
import tech.antibytes.kmock.verification.NonfreezingVerifier
import tech.antibytes.kmock.verification.Verifier
import tech.antibytes.kmock.verification.hasBeenCalledWith
import tech.antibytes.kmock.verification.hasBeenCalledWithout
import tech.antibytes.kmock.verification.hasBeenStrictlyCalledWith
import tech.antibytes.kmock.verification.verify
import tech.antibytes.kmock.verification.verifyOrder
import tech.antibytes.kmock.verification.verifyStrictOrder
import tech.antibytes.kmock.verification.wasGotten
import tech.antibytes.kmock.verification.wasSet
import tech.antibytes.kmock.verification.wasSetTo
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.defaultTestContext
import tech.antibytes.util.test.coroutine.runBlockingTestWithTimeout
import tech.antibytes.util.test.coroutine.runBlockingTestWithTimeoutInScope
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test

@MockCommon(
    SampleRemoteRepository::class,
    SampleLocalRepository::class,
    SampleDomainObject::class,
    ExampleContract.DecoderFactory::class
)
class SampleControllerAutoSpyFactorySpec {
    private val fixture = kotlinFixture()
    private val verifier = Verifier()
    private val local: SampleLocalRepositoryMock = kmock(verifier, relaxed = true)
    private val remote: SampleRemoteRepositoryMock = kmock(verifier, relaxed = true)
    private val domainObjectInstance = DomainObject("test", 21)
    private val domainObject: SampleDomainObjectMock = kspy(
        domainObjectInstance,
        verifier,
    )

    @BeforeTest
    fun setUp() {
        verifier.clear()
        local._clearMock()
        remote._clearMock()
        domainObject._clearMock()
        clearBlockingTest()
    }

    @Test
    @JsName("fn1")
    fun `Given fetchAndStore it fetches and stores DomainObjects`(): AsyncTestReturnValue {
        // Given
        val url = fixture.fixture<String>()
        val id = fixture.listFixture<String>(size = 2)
        val number = fixture.fixture<Int>()

        domainObject._id.getMany = id
        domainObject._value.get = number

        remote._fetch.returnValue = domainObject
        local._store.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        return runBlockingTestWithTimeout {
            val actual = controller.fetchAndStore(url)

            // Then
            actual mustBe domainObject

            verify(exactly = 1) { remote._fetch.hasBeenStrictlyCalledWith(url) }
            verify(exactly = 1) { local._store.hasBeenCalledWith() }

            verifier.verifyStrictOrder {
                remote._fetch.hasBeenStrictlyCalledWith(url)
                domainObject._id.wasGotten()
                domainObject._id.wasSet()
                domainObject._id.wasGotten()
                domainObject._value.wasGotten()
                local._store.hasBeenCalledWith()
            }

            verifier.verifyOrder {
                remote._fetch.hasBeenCalledWith(url)
                domainObject._id.wasSetTo("42")
                local._store.hasBeenCalledWith()
            }
        }
    }

    @Test
    @JsName("fn2")
    fun `Given find it fetches a DomainObjects`(): AsyncTestReturnValue {
        // Given
        val idOrg = fixture.fixture<String>()
        val id = fixture.fixture<String>()
        val number = fixture.fixture<Int>()

        domainObject._id.get = id
        domainObject._value.get = number

        remote._find.returnValue = domainObject
        local._contains.sideEffect = { true }
        local._fetch.returnValue = domainObject

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

            verify(exactly = 1) { local._contains.hasBeenStrictlyCalledWith(idOrg) }
            verify(exactly = 1) { local._fetch.hasBeenCalledWith() }
            verify(exactly = 1) { remote._find.hasBeenStrictlyCalledWith(idOrg) }

            verifier.verifyStrictOrder {
                local._contains.hasBeenStrictlyCalledWith(idOrg)
                remote._find.hasBeenStrictlyCalledWith(idOrg)
                domainObject._id.wasGotten()
                local._fetch.hasBeenCalledWith()
                domainObject._id.wasSet()
            }

            verifier.verifyOrder {
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
        val verifier = NonfreezingVerifier()
        val local: SampleLocalRepositoryMock = kmock(verifier, relaxed = true, freeze = false)
        val remote: SampleRemoteRepositoryMock = kmock(verifier, relaxed = true, freeze = false)

        val domainObject: SampleDomainObjectMock = kspy(
            instance,
            verifier,
            freeze = false
        )

        remote._find.returnValue = domainObject
        local._contains.sideEffect = { true }
        local._fetch.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)

        // When
        controller.findBlocking(idOrg)

        verify(exactly = 1) { local._contains.hasBeenStrictlyCalledWith(idOrg) }
        verify(exactly = 1) { local._fetch.hasBeenCalledWith() }
        verify(exactly = 1) { remote._find.hasBeenStrictlyCalledWith(idOrg) }

        verifier.verifyStrictOrder {
            local._contains.hasBeenStrictlyCalledWith(idOrg)
            remote._find.hasBeenStrictlyCalledWith(idOrg)
            domainObject._id.wasGotten()
            local._fetch.hasBeenCalledWith()
            domainObject._id.wasSet()
        }

        verifier.verifyOrder {
            local._contains.hasBeenCalledWithout("abc")
        }
    }
}

private class DomainObject(
    id: String,
    value: Int
) : SampleDomainObject {
    private val _id: AtomicRef<String> = atomic(id)
    private val _value: AtomicInt = atomic(value)

    override var id: String by _id
    override val value: Int by _value
}

private class DomainObject2(
    override var id: String,
    override val value: Int
) : SampleDomainObject