/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.mock.FunProxyStub
import tech.antibytes.mock.VerifierStub
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
        val proxy = FunProxyStub(fixture.fixture(), fixture.fixture())

        // When
        val error = assertFailsWith<AssertionError> {
            verify {
                VerificationHandle(proxy.id, emptyList())
            }
        }

        error.message mustBe "Call not found."
    }

    @Test
    @JsName("fn1")
    fun `Given verify is called it fails if the covered mock does not have the minimum amount of calls`() {
        // Given
        val proxy = FunProxyStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 1
        val expectedCalls = 3

        // When
        val error = assertFailsWith<AssertionError> {
            verify(atLeast = expectedCalls) {
                VerificationHandle(proxy.id, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at least $expectedCalls calls, but found only $givenCalls."
    }

    @Test
    @JsName("fn2")
    fun `Given verify is called it fails if the covered mock does exceeds the maximum amount of calls`() {
        // Given
        val proxy = FunProxyStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 3
        val expectedCalls = 1

        // When
        val error = assertFailsWith<AssertionError> {
            verify(atMost = expectedCalls) {
                VerificationHandle(proxy.id, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at most $expectedCalls calls, but exceeded with $givenCalls."
    }

    @Test
    @JsName("fn3")
    fun `Given verify is called it fails if the covered mock does not have the exact minimum amount of calls`() {
        // Given
        val proxy = FunProxyStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 1
        val expectedCalls = 3

        // When
        val error = assertFailsWith<AssertionError> {
            verify(exactly = expectedCalls, atLeast = 0) {
                VerificationHandle(proxy.id, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at least $expectedCalls calls, but found only $givenCalls."
    }

    @Test
    @JsName("fn4")
    fun `Given verify is called it fails if the covered mock does exceeds the exact maximum amount of calls`() {
        // Given
        val proxy = FunProxyStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 3
        val expectedCalls = 1

        // When
        val error = assertFailsWith<AssertionError> {
            verify(exactly = expectedCalls, atMost = 0) {
                VerificationHandle(proxy.id, fixture.listFixture(size = givenCalls))
            }
        }

        error.message mustBe "Expected at most $expectedCalls calls, but exceeded with $givenCalls."
    }

    @Test
    @JsName("fn5")
    fun `Given verify is called it passes if the covered mock matches the requirements`() {
        // Given
        val proxy = FunProxyStub(fixture.fixture(), fixture.fixture())
        val givenCalls = 3

        // When
        verify(exactly = givenCalls) {
            VerificationHandle(proxy.id, fixture.listFixture(size = givenCalls))
        }
    }

    @Test
    @JsName("fn6")
    fun `Given verifyStrictOrder is called it fails if the amount captured calls does not match the given Order`() {
        // Given
        val verifierLower = VerifierStub(emptyList())
        val verifierUpper = VerifierStub(
            listOf(
                Reference(FunProxyStub(fixture.fixture(), fixture.fixture()), fixture.fixture()),
                Reference(FunProxyStub(fixture.fixture(), fixture.fixture()), fixture.fixture())
            )
        )
        val handle = VerificationHandle(fixture.fixture(), fixture.listFixture())

        // Then
        val errorLowerBound = assertFailsWith<AssertionError> {
            // When
            verifierLower.verifyStrictOrder {
                this as VerificationChainBuilder

                add(handle)
            }
        }

        errorLowerBound.message mustBe "The given verification chain (has 1 items) does not match the captured calls (${verifierLower.references.size} were captured)."

        // Then
        val errorUpperBound = assertFailsWith<AssertionError> {
            // When
            verifierUpper.verifyStrictOrder {
                this as VerificationChainBuilder

                add(handle)
            }
        }

        errorUpperBound.message mustBe "The given verification chain (has 1 items) does not match the captured calls (${verifierUpper.references.size} were captured)."
    }

    @Test
    @JsName("fn7")
    fun `Given verifyStrictOrder is called it fails if the referenced Functions do not match`() {
        // Given
        val handleproxy = FunProxyStub(
            fixture.fixture(),
            calls = fixture.fixture()
        )
        val referenceProxy = FunProxyStub(
            fixture.fixture(),
            calls = fixture.fixture()
        )

        val handle = VerificationHandle(
            handleproxy.id,
            listOf(1)
        )

        val verifier = VerifierStub(
            listOf(Reference(referenceProxy, 0))
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                this as VerificationChainBuilder

                add(handle)
            }
        }

        error.message mustBe "Excepted '${handleproxy.id}', but got '${referenceProxy.id}'."
    }

    @Test
    @JsName("fn8")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 0
        val actualCallIdx = 1
        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
        )

        val handle = VerificationHandle(
            name,
            listOf(expectedCallIdx)
        )

        val verifier = VerifierStub(
            listOf(Reference(referenceProxy, actualCallIdx))
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                this as VerificationChainBuilder

                add(handle)
            }
        }

        error.message mustBe "Excepted the $expectedCallIdx of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn9")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 1
        val actualCallIdx = 2
        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
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
                Reference(referenceProxy, 0),
                Reference(referenceProxy, actualCallIdx)
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                this as VerificationChainBuilder

                add(handle)
                add(handle)
            }
        }

        error.message mustBe "Excepted the $expectedCallIdx of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn10")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values with various length`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 1
        val actualCallIdx = 2
        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
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
                Reference(referenceProxy, 0),
                Reference(referenceProxy, actualCallIdx)
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                this as VerificationChainBuilder

                add(handle1)
                add(handle2)
            }
        }

        error.message mustBe "Excepted the $expectedCallIdx of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn11")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values with various length while exceeding the range`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 2
        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
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
                Reference(referenceProxy, 0),
                Reference(referenceProxy, expectedCallIdx - 1),
                Reference(referenceProxy, expectedCallIdx),
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                this as VerificationChainBuilder

                add(handle1)
                add(handle2)
                add(handle3)
            }
        }

        error.message mustBe "The captured calls of $name exceeds the captured calls."
    }

    @Test
    @JsName("fn12")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values with mixed References`() {
        // Given
        val name1: String = fixture.fixture()
        val name2: String = fixture.fixture()
        val expectedCallIdx = 2
        val referenceProxy1 = FunProxyStub(
            name1,
            calls = fixture.fixture()
        )
        val referenceProxy2 = FunProxyStub(
            name2,
            calls = fixture.fixture()
        )

        val handle1 = VerificationHandle(
            name1,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            name2,
            listOf(
                0,
            )
        )

        val handle3 = VerificationHandle(
            name1,
            listOf(
                expectedCallIdx,
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceProxy1, 0),
                Reference(referenceProxy2, 0),
                Reference(referenceProxy1, expectedCallIdx),
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyStrictOrder {
                this as VerificationChainBuilder

                add(handle1)
                add(handle2)
                add(handle3)
            }
        }

        error.message mustBe "The captured calls of $name1 exceeds the captured calls."
    }

    @Test
    @JsName("fn13")
    fun `Given verifyStrictOrder is called it passes if the referenced CallIndicies match on multiple values with mixed References`() {
        // Given
        val name1: String = fixture.fixture()
        val name2: String = fixture.fixture()
        val expectedCallIdx = 1
        val referenceProxy1 = FunProxyStub(
            name1,
            calls = fixture.fixture()
        )
        val referenceProxy2 = FunProxyStub(
            name2,
            calls = fixture.fixture()
        )

        val handle1 = VerificationHandle(
            name1,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            name2,
            listOf(
                0,
            )
        )

        val handle3 = VerificationHandle(
            name1,
            listOf(
                expectedCallIdx,
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceProxy1, 0),
                Reference(referenceProxy2, 0),
                Reference(referenceProxy1, expectedCallIdx),
            )
        )

        // When
        verifier.verifyStrictOrder {
            this as VerificationChainBuilder

            add(handle1)
            add(handle2)
            add(handle3)
        }
    }

    @Test
    @JsName("fn14")
    fun `Given verifyOrder is called it fails if the amount captured calls is smaller than the given Order`() {
        // Given
        val verifier = VerifierStub(emptyList())
        val handle = VerificationHandle(fixture.fixture(), fixture.listFixture())

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyOrder {
                this as VerificationChainBuilder

                add(handle)
            }
        }

        error.message mustBe "The given verification chain (has ${verifier.references.size} items) is exceeding the captured calls (1 were captured)."
    }

    @Test
    @JsName("fn15")
    fun `Given verifyOrder is called it fails if the captured calls does not contain the mentioned function call`() {
        val handleproxy = FunProxyStub(
            fixture.fixture(),
            calls = fixture.fixture()
        )
        val referenceProxy = FunProxyStub(
            fixture.fixture(),
            calls = fixture.fixture()
        )

        val handle = VerificationHandle(
            handleproxy.id,
            listOf(1)
        )

        val verifier = VerifierStub(
            listOf(Reference(referenceProxy, 0))
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyOrder {
                this as VerificationChainBuilder

                add(handle)
            }
        }

        error.message mustBe "Last referred invocation of ${handleproxy.id} was not found."
    }

    @Test
    @JsName("fn16")
    fun `Given verifyOrder is called it passes if the referenced CallIndicies was found`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 0
        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
        )

        val handle = VerificationHandle(
            name,
            listOf(expectedCallIdx)
        )

        val verifier = VerifierStub(
            listOf(Reference(referenceProxy, expectedCallIdx))
        )

        // When
        verifier.verifyStrictOrder {
            this as VerificationChainBuilder

            add(handle)
        }
    }

    @Test
    @JsName("fn17")
    fun `Given verifyOrder is called it fails if the referenced CallIndicies was not found for multiple calls`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx1 = 1
        val expectedCallIdx2 = 0
        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
        )

        val handle1 = VerificationHandle(
            name,
            listOf(expectedCallIdx1)
        )

        val handle2 = VerificationHandle(
            name,
            listOf(expectedCallIdx2)
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceProxy, expectedCallIdx1),
                Reference(referenceProxy, expectedCallIdx2)
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyOrder {
                this as VerificationChainBuilder

                add(handle1)
                add(handle2)
            }
        }

        error.message mustBe "Last referred invocation of $name was not found."
    }

    @Test
    @JsName("fn18")
    fun `Given verifyOrder is called it passes if the referenced CallIndicies was found for multiple calls`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx1 = 0
        val expectedCallIdx2 = 1
        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
        )

        val handle1 = VerificationHandle(
            name,
            listOf(expectedCallIdx1)
        )

        val handle2 = VerificationHandle(
            name,
            listOf(expectedCallIdx2)
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceProxy, expectedCallIdx1),
                Reference(referenceProxy, expectedCallIdx2)
            )
        )

        // When
        verifier.verifyOrder {
            this as VerificationChainBuilder

            add(handle1)
            add(handle2)
        }
    }

    @Test
    @JsName("fn19")
    fun `Given verifyOrder is called it passes if the referenced CallIndicies was found for multiple calls with various length`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx1 = 2
        val expectedCallIdx2 = 3

        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
        )

        val handle1 = VerificationHandle(
            name,
            listOf(
                0,
                1,
                expectedCallIdx1
            )
        )

        val handle2 = VerificationHandle(
            name,
            listOf(
                0,
                expectedCallIdx2
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceProxy, 0),
                Reference(referenceProxy, 1),
                Reference(referenceProxy, expectedCallIdx1),
                Reference(referenceProxy, expectedCallIdx2)
            )
        )

        // When
        verifier.verifyOrder {
            this as VerificationChainBuilder

            add(handle1)
            add(handle2)
        }
    }

    @Test
    @JsName("fn20")
    fun `Given verifyOrder is called it fails if the referenced CallIndicies was not found for multiple calls with various length`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx1 = 3
        val expectedCallIdx2 = 2
        val referenceProxy = FunProxyStub(
            name,
            calls = fixture.fixture()
        )

        val handle1 = VerificationHandle(
            name,
            listOf(
                expectedCallIdx1
            )
        )

        val handle2 = VerificationHandle(
            name,
            listOf(
                expectedCallIdx2
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceProxy, expectedCallIdx1),
                Reference(referenceProxy, expectedCallIdx2)
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyOrder {
                this as VerificationChainBuilder

                add(handle1)
                add(handle2)
            }
        }

        error.message mustBe "Last referred invocation of $name was not found."
    }

    @Test
    @JsName("fn21")
    fun `Given verifyOrder is called it passes if the referenced CallIndicies match on multiple values with mixed References`() {
        // Given
        val name1: String = fixture.fixture()
        val name2: String = fixture.fixture()
        val expectedCallIdx = 2
        val referenceProxy1 = FunProxyStub(
            name1,
            calls = fixture.fixture()
        )
        val referenceProxy2 = FunProxyStub(
            name2,
            calls = fixture.fixture()
        )

        val handle1 = VerificationHandle(
            name1,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            name2,
            listOf(
                0,
            )
        )

        val handle3 = VerificationHandle(
            name1,
            listOf(
                expectedCallIdx,
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceProxy1, 0),
                Reference(referenceProxy2, 0),
                Reference(referenceProxy1, expectedCallIdx),
            )
        )

        // When
        verifier.verifyOrder {
            this as VerificationChainBuilder

            add(handle1)
            add(handle2)
            add(handle3)
        }
    }

    @Test
    @JsName("fn22")
    fun `Given verifyOrder is called it fails if the referenced CallIndicies does not match on multiple values with mixed References`() {
        // Given
        val name1: String = fixture.fixture()
        val name2: String = fixture.fixture()
        val expectedCallIdx = 2
        val referenceProxy1 = FunProxyStub(
            name1,
            calls = fixture.fixture()
        )
        val referenceProxy2 = FunProxyStub(
            name2,
            calls = fixture.fixture()
        )

        val handle1 = VerificationHandle(
            name1,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            name2,
            listOf(
                0,
            )
        )

        val handle3 = VerificationHandle(
            name1,
            listOf(
                expectedCallIdx + 1,
            )
        )

        val verifier = VerifierStub(
            listOf(
                Reference(referenceProxy1, 0),
                Reference(referenceProxy2, 0),
                Reference(referenceProxy1, expectedCallIdx),
            )
        )

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            verifier.verifyOrder {
                this as VerificationChainBuilder

                add(handle1)
                add(handle2)
                add(handle3)
            }
        }

        error.message mustBe "Last referred invocation of $name1 was not found."
    }
}
