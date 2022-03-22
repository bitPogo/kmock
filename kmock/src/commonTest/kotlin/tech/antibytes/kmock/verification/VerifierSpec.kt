/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.mock.AsyncFunProxyStub
import tech.antibytes.mock.PropertyProxyStub
import tech.antibytes.mock.SyncFunProxyStub
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.resolveMultiBlockCalls
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.math.absoluteValue
import kotlin.test.BeforeTest
import kotlin.test.Test

class VerifierSpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils Verifier`() {
        Verifier() fulfils KMockContract.Verifier::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils Collector`() {
        Verifier() fulfils KMockContract.Collector::class
    }

    @Test
    @JsName("fn2")
    fun `It has a emptyMap of references by default`() {
        Verifier().references mustBe emptyList()
    }

    @Test
    @JsName("fn3")
    fun `Given addReference is called it adds a refrenence entry threadsafe`(): AsyncTestReturnValue {
        // Given
        val index: Int = fixture.fixture<Int>().absoluteValue
        val proxy = SyncFunProxyStub(fixture.fixture(), fixture.fixture())

        val verifier = Verifier()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            verifier.addReference(proxy, index)
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            verifier.references.first().proxy sameAs proxy
            verifier.references.first().callIndex mustBe index
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn4")
    fun `Given addReference is called it always adds PropertyProxies`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxyStub(fixture.fixture(), fixture.fixture())

        val verifier = Verifier()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            verifier.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            verifier.references.first().proxy sameAs proxy
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn5")
    fun `Given addReference is called it always adds AsyncFunProxies`(): AsyncTestReturnValue {
        // Given
        val proxy = AsyncFunProxyStub(fixture.fixture(), fixture.fixture())

        val verifier = Verifier()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            verifier.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            verifier.references.first().proxy sameAs proxy
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn6")
    fun `Given addReference is called it adds SyncFunProxies if they are not marked for ignoring`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            fixture.fixture()
        )

        val verifier = Verifier()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            verifier.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            verifier.references.first().proxy sameAs proxy
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn7")
    fun `Given addReference is called it ignores SyncFunProxies if they are marked for ignoring`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            fixture.fixture(),
            ignorableForVerification = true
        )

        val verifier = Verifier()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            verifier.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            verifier.references.firstOrNull() mustBe null
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn8")
    fun `Given addReference is called it adds SyncFunProxies if they are marked for ignoring but are overruled by the Verifier`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            fixture.fixture(),
            ignorableForVerification = true
        )

        val verifier = Verifier(coverAllInvocations = true)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            verifier.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            verifier.references.first().proxy mustBe proxy
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn9")
    fun `Given clear is called it clears the verifier`() {
        // Given
        val proxy = SyncFunProxyStub(fixture.fixture(), fixture.fixture())
        val verifier = Verifier()

        // When
        verifier.addReference(proxy, fixture.fixture())

        verifier.clear()

        // Then
        verifier.references mustBe emptyList()
    }
}
