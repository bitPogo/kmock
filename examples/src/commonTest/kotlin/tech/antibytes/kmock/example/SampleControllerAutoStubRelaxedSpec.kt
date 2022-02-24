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
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.Relaxer
import tech.antibytes.kmock.Verifier
import tech.antibytes.kmock.example.contract.ExampleContract
import tech.antibytes.kmock.example.contract.ExampleContract.SampleDomainObject
import tech.antibytes.kmock.example.contract.ExampleContract.SampleLocalRepository
import tech.antibytes.kmock.example.contract.ExampleContract.SampleRemoteRepository
import tech.antibytes.kmock.example.contract.SampleDomainObjectMock
import tech.antibytes.kmock.example.contract.SampleLocalRepositoryMock
import tech.antibytes.kmock.example.contract.SampleRemoteRepositoryMock
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
import tech.antibytes.util.test.fixture.PublicApi
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.native.concurrent.ThreadLocal
import kotlin.test.BeforeTest
import kotlin.test.Test

@ThreadLocal
object Fixture {
    var fixture: PublicApi.Fixture? = null
}

@Relaxer
internal inline fun <reified T> relax(id: String): T {
    if (Fixture.fixture == null) {
        Fixture.fixture = kotlinFixture()
    }

    return Fixture.fixture!!.fixture()
}

@MockCommon(
    SampleRemoteRepository::class,
    SampleLocalRepository::class,
    SampleDomainObject::class,
    ExampleContract.DecoderFactory::class
)
class SampleControllerAutoStubRelaxedSpec {
    private val fixture = kotlinFixture()
    private var verifier = Verifier()
    private var local = SampleLocalRepositoryMock(verifier, relaxed = true)
    private var remote = SampleRemoteRepositoryMock(verifier, relaxed = true)
    private var domainObject = SampleDomainObjectMock(verifier, relaxed = true)

    @BeforeTest
    fun setUp() {
        verifier.clear()
        local.clearMock()
        remote.clearMock()
        domainObject.clearMock()
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils SampleController`() {
        SampleController(local, remote) fulfils ExampleContract.SampleController::class
    }

    @Test
    @JsName("fn1")
    fun `Given fetchAndStore it fetches and stores DomainObjects`(): AsyncTestReturnValue {
        // Given
        val url = fixture.fixture<String>()
        val id = fixture.listFixture<String>(size = 2)

        domainObject.idProp.getMany = id

        remote.fetchFun.returnValue = domainObject
        local.storeFun.returnValue = domainObject

        // When
        val controller = SampleController(local, remote)
        return runBlockingTestWithTimeout {
            val actual = controller.fetchAndStore(url)

            // Then
            actual mustBe domainObject

            verify(exactly = 1) { remote.fetchFun.hasBeenStrictlyCalledWith(url) }
            verify(exactly = 1) { local.storeFun.hasBeenCalledWith(id[1]) }

            verifier.verifyStrictOrder {
                remote.fetchFun.hasBeenStrictlyCalledWith(url)
                domainObject.idProp.wasGotten()
                domainObject.idProp.wasSet()
                domainObject.idProp.wasGotten()
                domainObject.valueProp.wasGotten()
                local.storeFun.hasBeenCalledWith(id[1])
            }

            verifier.verifyOrder {
                remote.fetchFun.hasBeenCalledWith(url)
                domainObject.idProp.wasSetTo("42")
                local.storeFun.hasBeenCalledWith(id[1])
            }
        }
    }

    @Test
    @JsName("fn2")
    fun `Given find it fetches a DomainObjects`(): AsyncTestReturnValue {
        // Given
        val idOrg = fixture.fixture<String>()
        val id = fixture.fixture<String>()

        domainObject.idProp.get = id

        remote.findFun.returnValue = domainObject
        local.containsFun.sideEffect = { true }
        local.fetchFun.returnValue = domainObject

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

            verify(exactly = 1) { local.containsFun.hasBeenStrictlyCalledWith(idOrg) }
            verify(exactly = 1) { local.fetchFun.hasBeenStrictlyCalledWith(id) }
            verify(exactly = 1) { remote.findFun.hasBeenStrictlyCalledWith(idOrg) }

            verifier.verifyStrictOrder {
                local.containsFun.hasBeenStrictlyCalledWith(idOrg)
                remote.findFun.hasBeenStrictlyCalledWith(idOrg)
                domainObject.idProp.wasGotten()
                local.fetchFun.hasBeenStrictlyCalledWith(id)
                domainObject.idProp.wasSet()
            }

            verifier.verifyOrder {
                local.containsFun.hasBeenCalledWithout("abc")
            }
        }
    }
}
