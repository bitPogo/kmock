/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlin.js.JsName
import kotlin.math.absoluteValue
import kotlin.test.BeforeTest
import kotlin.test.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.mock.AsyncFunProxyStub
import tech.antibytes.mock.PropertyProxyStub
import tech.antibytes.mock.SyncFunProxyStub
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.resolveMultiBlockCalls
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs

class AsserterSpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils Asserter`() {
        Asserter() fulfils KMockContract.Asserter::class
    }

    @Test
    @JsName("fn0a")
    fun `It typealiases Verifier`() {
        Verifier() fulfils KMockContract.Asserter::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils Collector`() {
        Asserter() fulfils KMockContract.Collector::class
    }

    @Test
    @JsName("fn2")
    fun `It has a emptyMap of references by default`() {
        Asserter().references mustBe emptyList()
    }

    @Test
    @JsName("fn3")
    fun `Given addReference is called it adds a refrenence entry threadsafe`(): AsyncTestReturnValue {
        // Given
        val index: Int = fixture.fixture<Int>().absoluteValue
        val proxy = SyncFunProxyStub(fixture.fixture(), fixture.fixture())

        val asserter = Asserter()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            asserter.addReference(proxy, index)
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            asserter.references.first().proxy sameAs proxy
            asserter.references.first().callIndex mustBe index
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn4")
    fun `Given addReference is called it always adds PropertyProxies`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxyStub(fixture.fixture(), fixture.fixture())

        val asserter = Asserter()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            asserter.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            asserter.references.first().proxy sameAs proxy
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn5")
    fun `Given addReference is called it always adds AsyncFunProxies`(): AsyncTestReturnValue {
        // Given
        val proxy = AsyncFunProxyStub(fixture.fixture(), fixture.fixture())

        val asserter = Asserter()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            asserter.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            asserter.references.first().proxy sameAs proxy
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn6")
    fun `Given addReference is called it adds SyncFunProxies if they are not marked for ignoring`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            fixture.fixture(),
        )

        val asserter = Asserter()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            asserter.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            asserter.references.first().proxy sameAs proxy
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
            ignorableForVerification = true,
        )

        val asserter = Asserter()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            asserter.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            asserter.references.firstOrNull() mustBe null
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn8")
    fun `Given addReference is called it adds SyncFunProxies if they are marked for ignoring but are overruled by the Asserter`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            fixture.fixture(),
            ignorableForVerification = true,
        )

        val asserter = Asserter(coverAllInvocations = true)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            asserter.addReference(proxy, fixture.fixture())
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            asserter.references.first().proxy mustBe proxy
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn9")
    fun `Given clear is called it clears the Asserter`() {
        // Given
        val proxy = SyncFunProxyStub(fixture.fixture(), fixture.fixture())
        val asserter = Asserter()

        // When
        asserter.addReference(proxy, fixture.fixture())

        asserter.clear()

        // Then
        asserter.references mustBe emptyList()
    }
}
