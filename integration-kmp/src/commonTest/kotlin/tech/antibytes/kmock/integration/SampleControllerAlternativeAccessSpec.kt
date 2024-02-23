/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration

import co.touchlab.stately.concurrency.AtomicReference
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.KMock
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.KMockMulti
import tech.antibytes.kmock.hint
import tech.antibytes.kmock.integration.contract.ExampleContract.DecoderFactory
import tech.antibytes.kmock.integration.contract.ExampleContract.Generic
import tech.antibytes.kmock.integration.contract.ExampleContract.GenericSampleDomainObject
import tech.antibytes.kmock.integration.contract.ExampleContract.SampleLocalRepository
import tech.antibytes.kmock.integration.contract.ExampleContract.SampleRemoteRepository
import tech.antibytes.kmock.integration.contract.ExampleContract.SampleUselessObject
import tech.antibytes.kmock.integration.contract.GenericSampleDomainObjectMock
import tech.antibytes.kmock.integration.contract.SampleDomainObjectMock
import tech.antibytes.kmock.integration.contract.SampleLocalRepositoryMock
import tech.antibytes.kmock.integration.contract.SampleRemoteRepositoryMock
import tech.antibytes.kmock.integration.contract.SampleUselessObjectMock
import tech.antibytes.kmock.verification.Asserter
import tech.antibytes.kmock.verification.assertOrder
import tech.antibytes.kmock.verification.asyncAssertOrder
import tech.antibytes.kmock.verification.asyncVerify
import tech.antibytes.kmock.verification.asyncVerifyOrder
import tech.antibytes.kmock.verification.verify
import tech.antibytes.kmock.verification.verifyOrder
import tech.antibytes.util.test.annotations.IgnoreJs
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.defaultScheduler
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

@OptIn(KMockExperimental::class)
@KMock(
    SampleRemoteRepository::class,
    SampleLocalRepository::class,
    GenericSampleDomainObject::class,
    DecoderFactory::class,
    SampleUselessObject::class,
)
@KMockMulti(
    "MergedGeneric",
    SampleLocalRepository::class,
    GenericSampleDomainObject::class,
)
class SampleControllerAlternativeAccessSpec {
    private val fixture = kotlinFixture()
    private val collector = Asserter()
    private val local: SampleLocalRepositoryMock = kmock(collector, relaxed = true)
    private val remote: SampleRemoteRepositoryMock = kmock(collector, relaxed = true)
    private val domainObject: SampleDomainObjectMock = kmock(collector, relaxed = true)
    private val genericDomainObject: GenericSampleDomainObjectMock<String, Int> = kmock(
        collector,
        relaxed = true,
        templateType = GenericSampleDomainObject::class,
    )
    private val uselessObject: SampleUselessObjectMock = kmock(
        collector,
        relaxed = true,
    )
    private val mergedGenericMock: MergedGenericMock<String, Int, *> = kmock(
        templateType0 = SampleLocalRepository::class,
        templateType1 = GenericSampleDomainObject::class,
    )

    @BeforeTest
    fun setUp() {
        collector.clear()
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
    fun `Given fetchAndStore it fetches and stores DomainObjects`() = runTest {
        // Given
        val url = fixture.fixture<String>()
        val id = fixture.listFixture<String>(size = 2)

        domainObject.propertyProxyOf(domainObject::id).getValues = id

        remote.asyncFunProxyOf(remote::fetch).returnValue = domainObject
        local.asyncFunProxyOf(local::store).returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        val actual = controller.fetchAndStore(url)

        // Then
        actual mustBe domainObject

        asyncVerify(exactly = 1) { remote.asyncFunProxyOf(remote::fetch).hasBeenStrictlyCalledWith(url) }
        asyncVerify(exactly = 1) { local.asyncFunProxyOf(local::store).hasBeenCalledWith(id[1]) }

        collector.asyncAssertOrder {
            remote.asyncFunProxyOf(remote::fetch).hasBeenStrictlyCalledWith(url)
            domainObject.propertyProxyOf(domainObject::id).wasGotten()
            domainObject.propertyProxyOf(domainObject::id).wasSet()
            domainObject.propertyProxyOf(domainObject::id).wasGotten()
            domainObject.propertyProxyOf(domainObject::value).wasGotten()
            local.asyncFunProxyOf(local::store).hasBeenCalledWith(id[1])
        }

        collector.asyncVerifyOrder {
            remote.asyncFunProxyOf(remote::fetch).hasBeenCalledWith(url)
            domainObject.propertyProxyOf(domainObject::id).wasSetTo("42")
            local.asyncFunProxyOf(local::store).hasBeenCalledWith(id[1])
        }
    }

    @Test
    @JsName("fn2")
    fun `Given find it fetches a DomainObjects`() = runTest {
        // Given
        val idOrg = fixture.fixture<String>()
        val id = fixture.fixture<String>()

        domainObject.propertyProxyOf(domainObject::id).getValue = id

        remote.syncFunProxyOf(remote::find).returnValue = domainObject
        local.syncFunProxyOf(local::contains).sideEffect = { true }
        local.syncFunProxyOf(local::fetch).returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        val doRef = AtomicReference(domainObject)
        val contextRef = AtomicReference(defaultScheduler)

        // When
        controller.find(idOrg)
            .onEach { actual -> actual mustBe doRef.get() }
            .launchIn(CoroutineScope(contextRef.get()))

        delay(20)

        // Then
        verify(exactly = 1) { local.syncFunProxyOf(local::contains).hasBeenStrictlyCalledWith(idOrg) }
        verify(exactly = 2) { local.syncFunProxyOf(local::fetch).hasBeenStrictlyCalledWith(id) }
        verify(exactly = 2) { remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg) }

        collector.assertOrder {
            local.syncFunProxyOf(local::contains).hasBeenStrictlyCalledWith(idOrg)
            remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg)
            domainObject.propertyProxyOf(domainObject::id).wasGotten()
            local.syncFunProxyOf(local::fetch).hasBeenStrictlyCalledWith(id)
            domainObject.propertyProxyOf(domainObject::id).wasGotten()
            local.syncFunProxyOf(local::fetch).hasBeenStrictlyCalledWith(id)
            remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg)
            domainObject.propertyProxyOf(domainObject::id).wasSet()
        }

        collector.verifyOrder {
            local.syncFunProxyOf(local::contains).hasBeenCalledWithout("abc")
            remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg)
            remote.syncFunProxyOf(remote::find).hasBeenStrictlyCalledWith(idOrg)
        }
    }

    @Test
    @JsName("fnx3")
    fun `Given a mocked DomainObject it does strange things`() {
        // Given
        val id = fixture.fixture<String>()

        domainObject.syncFunProxyOf(domainObject::toString).returnValue = id

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

        genericDomainObject.propertyProxyOf(genericDomainObject::id).getValue = id

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
        uselessObject.syncFunProxyOf<Any?>(uselessObject::doSomething, hint<Any>()).returnValue = 23
        uselessObject.syncFunProxyOf(uselessObject::doSomething, hint<String>()).returnValue = 42

        uselessObject.syncFunProxyOf<Any?>(uselessObject::doSomethingElse, hint<Any>()).returnValue = 107
        uselessObject.syncFunProxyOf(uselessObject::doSomethingElse, hint<String>()).returnValue = 31

        uselessObject.syncFunProxyOf<Any>(uselessObject::doSomething, hint<Any, String>()).returnValue = "works!"
        uselessObject.syncFunProxyOf<Any>(uselessObject::doSomething, hint<Any, Int>()).returnValue = "It"

        uselessObject.syncFunProxyOf(
            uselessObject::doSomethingElse,
            hint<Map<String, Generic<String>>, String>(),
        ).returnValue = "works!"

        uselessObject.syncFunProxyOf<Any>(uselessObject::doSomethingElse, hint<Any, Int>()).returnValue = "It"
        uselessObject.syncFunProxyOf(
            uselessObject::doAnything,
            hint<Generic<String>>(),
        ).returnValue = 109

        uselessObject.syncFunProxyOf(
            uselessObject::withFun,
            hint<Function0<String>>(),
        ).returnValue = "still"

        uselessObject.syncFunProxyOf(
            uselessObject::withOtherFun,
            hint<Function0<Unit>>(),
        ).returnValue = 31

        uselessObject.syncFunProxyOf<Any>(
            uselessObject::run,
            hint<Any>(),
        ).returnValue = 91

        // When & Then
        uselessObject.doSomething(null) mustBe 23
        uselessObject.doSomething("null") mustBe 42
        uselessObject.doSomething(Any(), 21) mustBe "It"
        uselessObject.doAnything(Generic("Test")) mustBe 109
        uselessObject.withFun { "Something" } mustBe "still"
        uselessObject.withOtherFun { } mustBe 31
        uselessObject.doSomething(Any(), "argggg") mustBe "works!"

        uselessObject.doSomethingElse("null") mustBe 31
        uselessObject.doSomethingElse(null) mustBe 107
        uselessObject.doSomethingElse(Any(), 21) mustBe "It"
        uselessObject.run(Any()) mustBe 91
        uselessObject.doSomethingElse(mapOf("uff" to Generic("puff")), "argggg") mustBe "works!"
    }

    @Test
    @JsName("fnx6")
    @IgnoreJs
    fun `Given a mocked MergedGeneric it does strange things`() {
        // Given
        val id = fixture.fixture<String>()
        mergedGenericMock.propertyProxyOf(mergedGenericMock::id).getValue = id

        // When
        val actual = mergedGenericMock.id

        // Then
        actual mustBe id
    }
}
