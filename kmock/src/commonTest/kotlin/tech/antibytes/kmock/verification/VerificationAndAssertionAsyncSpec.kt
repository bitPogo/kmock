/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.mock.AsserterStub
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(KMockExperimental::class)
class VerificationAndAssertionAsyncSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given asyncVerify is called it fails if the covered mock does not contain any call`() = runBlockingTest {
        // Given
        val proxy = fixture.funProxyFixture()

        // When
        val error = assertFailsWith<AssertionError> {
            asyncVerify {
                Expectation(proxy, emptyList())
            }
        }

        error.message mustBe "Call not found."
    }

    @Test
    @JsName("fn1")
    fun `Given asyncVerify is called it fails if the covered mock does not have the minimum amount of calls`() = runBlockingTest {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 1
        val expectedCalls = 3

        // When
        val error = assertFailsWith<AssertionError> {
            asyncVerify(atLeast = expectedCalls) {
                Expectation(proxy, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at least $expectedCalls calls, but found only $givenCalls."
    }

    @Test
    @JsName("fn2")
    fun `Given asyncVerify is called it fails if the covered mock does exceeds the maximum amount of calls`() = runBlockingTest {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 3
        val expectedCalls = 1

        // When
        val error = assertFailsWith<AssertionError> {
            asyncVerify(atMost = expectedCalls) {
                Expectation(proxy, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at most $expectedCalls calls, but exceeded with $givenCalls."
    }

    @Test
    @JsName("fn3")
    fun `Given asyncVerify is called it fails if the covered mock does not have the exact minimum amount of calls`() = runBlockingTest {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 1
        val expectedCalls = 3

        // When
        val error = assertFailsWith<AssertionError> {
            asyncVerify(exactly = expectedCalls, atLeast = 0) {
                Expectation(proxy, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at least $expectedCalls calls, but found only $givenCalls."
    }

    @Test
    @JsName("fn4")
    fun `Given asyncVerify is called it fails if the covered mock does exceeds the exact maximum amount of calls`() = runBlockingTest {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 3
        val expectedCalls = 1

        // When
        val error = assertFailsWith<AssertionError> {
            asyncVerify(exactly = expectedCalls, atMost = 0) {
                Expectation(proxy, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at most $expectedCalls calls, but exceeded with $givenCalls."
    }

    @Test
    @JsName("fn5")
    fun `Given asyncVerify is called it passes if the covered mock matches the requirements`() = runBlockingTest {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 3

        // When
        asyncVerify(exactly = givenCalls) {
            Expectation(proxy, fixture.listFixture(size = givenCalls))
        }
    }

    @Test
    @JsName("fn6")
    fun `Given asyncAssertProxy is called it uses a UnchainedAssertion`() = runBlockingTest {
        // When
        asyncAssertProxy {
            // Then
            this fulfils UnchainedAssertion::class
        }
    }

    @Test
    @JsName("fn7")
    fun `Given asyncAssertOrder is called it uses a AssertionChain`() = runBlockingTest {
        // Given
        val verifier = AsserterStub(emptyList())

        // When
        verifier.asyncAssertOrder {
            // Then
            this fulfils AssertionChain::class
        }
    }

    @Test
    @JsName("fn8")
    fun `Given asyncAssertOrder is called it ensures all references had been evaluated`() = runBlockingTest {
        // Given
        val id: String = fixture.fixture()
        val verifier = AsserterStub(
            listOf(
                Reference(fixture.funProxyFixture(id = id), fixture.fixture())
            )
        )

        val actual = assertFailsWith<AssertionError> {
            // When
            verifier.asyncAssertOrder {}
        }

        actual.message mustBe "The given verification chain covers 1 items, but only 0 were expected ($id were referenced)."
    }

    @Test
    @JsName("fn9")
    fun `Given asyncVerifyStrictOrder is called it uses a StrictVerificationChain`() = runBlockingTest {
        // Given
        val verifier = AsserterStub(emptyList())

        // When
        verifier.asyncVerifyStrictOrder {
            // Then
            this fulfils StrictVerificationChain::class
        }
    }

    @Test
    @JsName("fn10")
    fun `Given asyncVerifyOrder is called it uses a VerificationChain`() = runBlockingTest {
        // Given
        val verifier = AsserterStub(emptyList())

        // When
        verifier.asyncVerifyOrder {
            // Then
            this fulfils VerificationChain::class
        }
    }
}
