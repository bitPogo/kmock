/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.mock.FunMockeryStub
import tech.antibytes.mock.VerifierStub
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
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
        val mockery = FunMockeryStub(fixture.fixture(), fixture.fixture())

        // When
        val error = assertFailsWith<AssertionError> {
            verify {
                VerificationHandle(mockery.id, emptyList())
            }
        }

        error.message mustBe "Call not found."
    }

    @Test
    @JsName("fn1")
    fun `Given verify is called it fails if the covered mock does not have the minimum amount of calls`() {
        // Given
        val mockery = FunMockeryStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 1
        val expectedCalls = 3

        // When
        val error = assertFailsWith<AssertionError> {
            verify(atLeast = expectedCalls) {
                VerificationHandle(mockery.id, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at least $expectedCalls calls, but found only $givenCalls."
    }

    @Test
    @JsName("fn2")
    fun `Given verify is called it fails if the covered mock does exceeds the maximum amount of calls`() {
        // Given
        val mockery = FunMockeryStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 3
        val expectedCalls = 1

        // When
        val error = assertFailsWith<AssertionError> {
            verify(atMost = expectedCalls) {
                VerificationHandle(mockery.id, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at most $expectedCalls calls, but exceeded with $givenCalls."
    }

    @Test
    @JsName("fn3")
    fun `Given verify is called it fails if the covered mock does not have the exact minimum amount of calls`() {
        // Given
        val mockery = FunMockeryStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 1
        val expectedCalls = 3

        // When
        val error = assertFailsWith<AssertionError> {
            verify(exactly = expectedCalls, atLeast = 0) {
                VerificationHandle(mockery.id, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at least $expectedCalls calls, but found only $givenCalls."
    }

    @Test
    @JsName("fn4")
    fun `Given verify is called it fails if the covered mock does exceeds the exact maximum amount of calls`() {
        // Given
        val mockery = FunMockeryStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 3
        val expectedCalls = 1

        // When
        val error = assertFailsWith<AssertionError> {
            verify(exactly = expectedCalls) {
                VerificationHandle(mockery.id, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at most $expectedCalls calls, but exceeded with $givenCalls."
    }

    @Test
    @JsName("fn5")
    fun `Given verifyStrictOrder is called it fails if the amount captured calls does not match the given Order`() {
        // Given
        val verifierLower = VerifierStub(emptyList())
        val verifierUpper = VerifierStub(
            listOf(
                Reference(FunMockeryStub(fixture.fixture(), fixture.fixture()), fixture.fixture()),
                Reference(FunMockeryStub(fixture.fixture(), fixture.fixture()), fixture.fixture())
            )
        )
        val handle = VerificationHandle(fixture.fixture(), fixture.listFixture())

        // Then
        val errorLowerBound = assertFailsWith<AssertionError> {
            // When
            verifierLower.verifyStrictOrder {
                add(handle)
            }
        }

        errorLowerBound.message mustBe "The given verification chain (has ${verifierLower.references.size} items) does not match the captured calls (1 were captured)."

        // Then
        val errorUpperBound = assertFailsWith<AssertionError> {
            // When
            verifierUpper.verifyStrictOrder {
                add(handle)
            }
        }

        errorUpperBound.message mustBe "The given verification chain (has ${verifierUpper.references.size} items) does not match the captured calls (1 were captured)."
    }

    @Test
    @JsName("fn6")
    fun `Given verifyStrictOrder is called it fails if the referenced Functions do not match`() {
        // Given
        val handleMockery = FunMockeryStub(
            fixture.fixture(),
            calls = 3
        )
        val referenceMockery = FunMockeryStub(
            fixture.fixture(),
            calls = 3
        )

        val handle = VerificationHandle(
            handleMockery.id,
            listOf(1)
        )

        val verifier = VerifierStub(
            listOf(Reference(referenceMockery, 0))
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                add(handle)
            }
        }

        error.message mustBe "Excepted ${handleMockery.id}, but got ${referenceMockery.id}."
    }

    @Test
    @JsName("fn7")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 0
        val actualCallIdx = 1
        val referenceMockery = FunMockeryStub(
            name,
            calls = 3
        )

        val handle = VerificationHandle(
            name,
            listOf(expectedCallIdx)
        )

        val verifier = VerifierStub(
            listOf(Reference(referenceMockery, actualCallIdx))
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                add(handle)
            }
        }

        error.message mustBe "Excepted the $expectedCallIdx of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn8")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 1
        val actualCallIdx = 2
        val referenceMockery = FunMockeryStub(
            name,
            calls = 3
        )

        val handle = VerificationHandle(
            name,
            listOf(
                0,
                expectedCallIdx
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceMockery, 0),
                Reference(referenceMockery, actualCallIdx)
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                add(handle)
                add(handle)
            }
        }

        error.message mustBe "Excepted the $expectedCallIdx of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn9")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values with various length`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 1
        val actualCallIdx = 2
        val referenceMockery = FunMockeryStub(
            name,
            calls = 3
        )

        val handle1 = VerificationHandle(
            name,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            name,
            listOf(
                expectedCallIdx,
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceMockery, 0),
                Reference(referenceMockery, actualCallIdx)
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                add(handle1)
                add(handle2)
            }
        }

        error.message mustBe "Excepted the $expectedCallIdx of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn10")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values with various length while exceeding the range`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 2
        val actualCallIdx = 1
        val referenceMockery = FunMockeryStub(
            name,
            calls = 3
        )

        val handle1 = VerificationHandle(
            name,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            name,
            listOf(
                expectedCallIdx + 1,
            )
        )

        val handle3 = VerificationHandle(
            name,
            listOf(
                expectedCallIdx,
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceMockery, 0),
                Reference(referenceMockery, expectedCallIdx + 1),
                Reference(referenceMockery, expectedCallIdx + 2)
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                add(handle1)
                add(handle2)
                add(handle3)
            }
        }

        error.message mustBe "The captured calls of $name exceeds the captured calls."
    }

    @Test
    @JsName("fn11")
    fun `Given verifyOrder is called it fails if the amount captured calls is smaller than the given Order`() {
        // Given
        val verifier = VerifierStub(emptyList())
        val handle = VerificationHandle(fixture.fixture(), fixture.listFixture())

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyOrder {
                add(handle)
            }
        }

        error.message mustBe "The given verification chain (has ${verifier.references.size} items) is exceeding the captured calls (1 were captured)."
    }
}
