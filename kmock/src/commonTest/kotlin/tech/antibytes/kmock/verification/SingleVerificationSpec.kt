/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SingleVerificationSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given verify is called it fails if the covered mock does not contain any call`() {
        // Given
        val proxy = fixture.funProxyFixture()

        // When
        val error = assertFailsWith<AssertionError> {
            verify {
                VerificationHandle(proxy, emptyList())
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
                VerificationHandle(proxy, fixture.listFixture(size = givenCalls))
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
                VerificationHandle(proxy, fixture.listFixture(size = givenCalls))
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
                VerificationHandle(proxy, fixture.listFixture(size = givenCalls))
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
                VerificationHandle(proxy, fixture.listFixture(size = givenCalls))
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
            VerificationHandle(proxy, fixture.listFixture(size = givenCalls))
        }
    }
}
