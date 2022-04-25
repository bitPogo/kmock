/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.mock.AsserterStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class VerificationSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given verify is called it fails if the covered mock does not contain any call`() {
        // Given
        val proxy = fixture.funProxyFixture()

        // When
        val error = assertFailsWith<AssertionError> {
            verify {
                Expectation(proxy, emptyList())
            }
        }

        error.message mustBe "Call not found."
    }

    @Test
    @JsName("fn1")
    fun `Given verify is called it fails if the covered mock does not have the minimum amount of calls`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 1
        val expectedCalls = 3

        // When
        val error = assertFailsWith<AssertionError> {
            verify(atLeast = expectedCalls) {
                Expectation(proxy, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at least $expectedCalls calls, but found only $givenCalls."
    }

    @Test
    @JsName("fn2")
    fun `Given verify is called it fails if the covered mock does exceeds the maximum amount of calls`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 3
        val expectedCalls = 1

        // When
        val error = assertFailsWith<AssertionError> {
            verify(atMost = expectedCalls) {
                Expectation(proxy, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at most $expectedCalls calls, but exceeded with $givenCalls."
    }

    @Test
    @JsName("fn3")
    fun `Given verify is called it fails if the covered mock does not have the exact minimum amount of calls`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 1
        val expectedCalls = 3

        // When
        val error = assertFailsWith<AssertionError> {
            verify(exactly = expectedCalls, atLeast = 0) {
                Expectation(proxy, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at least $expectedCalls calls, but found only $givenCalls."
    }

    @Test
    @JsName("fn4")
    fun `Given verify is called it fails if the covered mock does exceeds the exact maximum amount of calls`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 3
        val expectedCalls = 1

        // When
        val error = assertFailsWith<AssertionError> {
            verify(exactly = expectedCalls, atMost = 0) {
                Expectation(proxy, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at most $expectedCalls calls, but exceeded with $givenCalls."
    }

    @Test
    @JsName("fn5")
    fun `Given verify is called it passes if the covered mock matches the requirements`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val givenCalls = 3

        // When
        verify(exactly = givenCalls) {
            Expectation(proxy, fixture.listFixture(size = givenCalls))
        }
    }

    @Test
    @JsName("fn6")
    fun `Given assertOrder is called it uses a AssertionChain`() {
        // Given
        val verifier = AsserterStub(emptyList())

        // When
        verifier.assertOrder {
            // Then
            this fulfils AssertionChain::class
        }
    }

    @Test
    @JsName("fn7")
    fun `Given assertOrder is called it ensures all references had been evaluated`() {
        // Given
        val id: String = fixture.fixture()
        val verifier = AsserterStub(
            listOf(
                Reference(fixture.funProxyFixture(id = id), fixture.fixture())
            )
        )

        val actual = assertFailsWith<AssertionError> {
            // When
            verifier.assertOrder {}
        }

        actual.message mustBe "The given verification chain covers 1 items, but only 0 were expected ($id were referenced)."
    }

    @Test
    @JsName("fn8")
    fun `Given verifyStrictOrder is called it uses a AssertionChain`() {
        // Given
        val verifier = AsserterStub(emptyList())

        // When
        verifier.verifyStrictOrder {
            // Then
            this fulfils AssertionChain::class
        }
    }

    @Test
    @JsName("fn9")
    fun `Given verifyStrictOrder is called it ensures all references had been evaluated`() {
        // Given
        val id: String = fixture.fixture()
        val verifier = AsserterStub(
            listOf(
                Reference(fixture.funProxyFixture(id = id), fixture.fixture())
            )
        )

        val actual = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {}
        }

        actual.message mustBe "The given verification chain covers 1 items, but only 0 were expected ($id were referenced)."
    }

    @Test
    @JsName("fn10")
    fun `Given verifyOrder is called it uses a StrictVerificationChain`() {
        // Given
        val verifier = AsserterStub(emptyList())

        // When
        verifier.verifyOrder {
            // Then
            this fulfils VerificationChain::class
        }
    }
}
