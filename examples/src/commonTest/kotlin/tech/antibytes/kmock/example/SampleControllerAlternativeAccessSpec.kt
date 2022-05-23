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
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.example.contract.ExampleContract.DecoderFactory
import tech.antibytes.kmock.example.contract.ExampleContract.GenericSampleDomainObject
import tech.antibytes.kmock.example.contract.ExampleContract.SampleLocalRepository
import tech.antibytes.kmock.example.contract.ExampleContract.SampleRemoteRepository
import tech.antibytes.kmock.example.contract.ExampleContract.SampleUselessObject
import tech.antibytes.kmock.example.contract.GenericSampleDomainObjectMock
import tech.antibytes.kmock.example.contract.SampleDomainObjectMock
import tech.antibytes.kmock.example.contract.SampleLocalRepositoryMock
import tech.antibytes.kmock.example.contract.SampleRemoteRepositoryMock
import tech.antibytes.kmock.example.contract.SampleUselessObjectMock
import tech.antibytes.kmock.hint
import tech.antibytes.kmock.verification.Asserter
import tech.antibytes.kmock.verification.assertOrder
import tech.antibytes.kmock.verification.asyncAssertOrder
import tech.antibytes.kmock.verification.asyncVerify
import tech.antibytes.kmock.verification.asyncVerifyOrder
import tech.antibytes.kmock.verification.verify
import tech.antibytes.kmock.verification.verifyOrder
import tech.antibytes.util.test.annotations.IgnoreJs
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

@MockCommon(
    SampleRemoteRepository::class,
    SampleLocalRepository::class,
    GenericSampleDomainObject::class,
    DecoderFactory::class,
    SampleUselessObject::class,
)
@OptIn(KMockExperimental::class)
class SampleControllerAlternativeAccessSpec {
    private val fixture = kotlinFixture()
    private val verifier = Asserter()
    private val local: SampleLocalRepositoryMock = kmock(verifier, relaxed = true)
    private val remote: SampleRemoteRepositoryMock = kmock(verifier, relaxed = true)
    private val domainObject: SampleDomainObjectMock = kmock(verifier, relaxed = true)
    private val genericDomainObject: GenericSampleDomainObjectMock<String, Int> = kmock(
        verifier,
        relaxed = true,
        templateType = GenericSampleDomainObject::class
    )
    private val uselessObjectObject: SampleUselessObjectMock = kmock(
        verifier,
        relaxed = true,
    )

    @BeforeTest
    fun setUp() {
        verifier.clear()
        local._clearMock()
        remote._clearMock()
        domainObject._clearMock()
        genericDomainObject._clearMock()
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils SampleController`() {
        SampleController(local, remote) fulfils SampleController::class
    }

    @Test
    @JsName("fn1")
    fun `Given fetchAndStore it fetches and stores DomainObjects`(): AsyncTestReturnValue {
        // Given
        val url = fixture.fixture<String>()
        val id = fixture.listFixture<String>(size = 2)

        domainObject.propertyProxyOf(domainObject::id).getMany = id

        remote.asyncFunProxyOf(remote::fetch).returnValue = domainObject
        local.asyncFunProxyOf(local::store).returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        return runBlockingTestWithTimeout {
            val actual = controller.fetchAndStore(url)

            // Then
            actual mustBe domainObject

            asyncVerify(exactly = 1) { remote.asyncFunProxyOf(remote::fetch).hasBeenStrictlyCalledWith(url) }
            asyncVerify(exactly = 1) { local.asyncFunProxyOf(local::store).hasBeenCalledWith(id[1]) }

            verifier.asyncAssertOrder {
                remote.asyncFunProxyOf(remote::fetch).hasBeenStrictlyCalledWith(url)
                domainObject.propertyProxyOf(domainObject::id).wasGotten()
                domainObject.propertyProxyOf(domainObject::id).wasSet()
                domainObject.propertyProxyOf(domainObject::id).wasGotten()
                domainObject.propertyProxyOf(domainObject::value).wasGotten()
                local.asyncFunProxyOf(local::store).hasBeenCalledWith(id[1])
            }

            verifier.asyncVerifyOrder {
                remote.asyncFunProxyOf(remote::fetch).hasBeenCalledWith(url)
                domainObject.propertyProxyOf(domainObject::id).wasSetTo("42")
                local.asyncFunProxyOf(local::store).hasBeenCalledWith(id[1])
            }
        }
    }

    @Test
    @JsName("fn2")
    fun `Given find it fetches a DomainObjects`(): AsyncTestReturnValue {
        // Given
        val idOrg = fixture.fixture<String>()
        val id = fixture.fixture<String>()

        domainObject.propertyProxyOf(domainObject::id).get = id

        remote.syncFunProxyOf(remote::find).returnValue = domainObject
        local.syncFunProxyOf(local::contains).sideEffect = { true }
        local.syncFunProxyOf(local::fetch).returnValue = domainObject

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

            // Then
            verify(exactly = 1) { local.syncFunProxyOf(local::contains).hasBeenStrictlyCalledWith(idOrg) }
            verify(exactly = 2) { local.syncFunProxyOf(local::fetch).hasBeenStrictlyCalledWith(id) }
            verify(exactly = 2) { remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg) }

            verifier.assertOrder {
                local.syncFunProxyOf(local::contains).hasBeenStrictlyCalledWith(idOrg)
                remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg)
                domainObject.propertyProxyOf(domainObject::id).wasGotten()
                local.syncFunProxyOf(local::fetch).hasBeenStrictlyCalledWith(id)
                domainObject.propertyProxyOf(domainObject::id).wasGotten()
                local.syncFunProxyOf(local::fetch).hasBeenStrictlyCalledWith(id)
                remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg)
                domainObject.propertyProxyOf(domainObject::id).wasSet()
            }

            verifier.verifyOrder {
                local.syncFunProxyOf(local::contains).hasBeenCalledWithout("abc")
                remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg)
                remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg)
            }
        }
    }

    @Test
    @JsName("fnx3")
    fun `Given a mocked DomainObject it does strange things`() {
        // Given
        val id = fixture.fixture<String>()

        domainObject._toString.returnValue = id

        // When
        val actual = domainObject.toString()

        // Then
        actual mustBe id
    }

    @Test
    @JsName("fnx4")
    fun `Given a mocked generic DomainObject it does strange things`() {
        // Given
        val id = fixture.fixture<String>()

        genericDomainObject.propertyProxyOf(genericDomainObject::id).get = id

        // When
        val actual = genericDomainObject.id

        // Then
        actual mustBe id
    }

    @Test
    @JsName("fnx5")
    @IgnoreJs
    fun `Given a mocked SampleUselessThing it does strange things`() {
        // Given
        uselessObjectObject.syncFunProxyOf<Any?>(uselessObjectObject::doSomething, hint<Any>()).returnValue = 23
        uselessObjectObject.syncFunProxyOf(uselessObjectObject::doSomething, hint<String>()).returnValue = 42

        uselessObjectObject.syncFunProxyOf<Any?>(uselessObjectObject::doSomethingElse, hint<Any>()).returnValue = 107
        uselessObjectObject.syncFunProxyOf(uselessObjectObject::doSomethingElse, hint<String>()).returnValue = 31

        uselessObjectObject.syncFunProxyOf<Any>(uselessObjectObject::doSomething, hint<Any, String>()).returnValue = "works!"
        uselessObjectObject.syncFunProxyOf<Any>(uselessObjectObject::doSomething, hint<Any, Int>()).returnValue = "It"

        uselessObjectObject.syncFunProxyOf<Any>(uselessObjectObject::doSomethingElse, hint<Any, String>()).returnValue = "works!"
        uselessObjectObject.syncFunProxyOf<Any>(uselessObjectObject::doSomethingElse, hint<Any, Int>()).returnValue = "It"

        // When & Then
        uselessObjectObject.doSomething(null) mustBe 23
        uselessObjectObject.doSomething("null") mustBe 42
        uselessObjectObject.doSomething(Any(), 21) mustBe "It"
        uselessObjectObject.doSomething(Any(), "argggg") mustBe "works!"

        uselessObjectObject.doSomethingElse("null") mustBe 31
        uselessObjectObject.doSomethingElse(null) mustBe 107
        uselessObjectObject.doSomethingElse(Any(), 21) mustBe "It"
        uselessObjectObject.doSomethingElse(Any(), "argggg") mustBe "works!"
    }
}
