/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.fixture.funProxyFixture
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
    fun `Given verifyStrictOrder is called it fails if the amount captured calls does not match the given Order`() {
        // Given
        val shared = fixture.funProxyFixture()
        val verifierLower = VerifierStub(emptyList())
        val verifierUpper = VerifierStub(
            listOf(
                Reference(shared, 0),
                Reference(fixture.funProxyFixture(), fixture.fixture())
            )
        )
        val handle = VerificationHandle(shared, listOf(0))

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
    @JsName("fn1")
    fun `Given verifyStrictOrder is called it fails if the referenced Functions do not match`() {
        // Given
        val handleProxy = fixture.funProxyFixture()
        val referenceProxy = fixture.funProxyFixture()

        val handle = VerificationHandle(
            handleProxy,
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

        error.message mustBe "Excepted '${handleProxy.id}', but got '${referenceProxy.id}'."
    }

    @Test
    @JsName("fn2")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 0
        val actualCallIdx = 1
        val referenceProxy = fixture.funProxyFixture(name)

        val handle = VerificationHandle(
            referenceProxy,
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

        error.message mustBe "Excepted the $expectedCallIdx call of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn3")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 1
        val actualCallIdx = 2
        val referenceProxy = fixture.funProxyFixture(name)

        val handle = VerificationHandle(
            referenceProxy,
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

        error.message mustBe "Excepted the $expectedCallIdx call of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn4")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values with various length`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 1
        val actualCallIdx = 2
        val referenceProxy = fixture.funProxyFixture(name)

        val handle1 = VerificationHandle(
            referenceProxy,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            referenceProxy,
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

        error.message mustBe "Excepted the $expectedCallIdx call of $name, but the $actualCallIdx was referenced."
    }

    @Test
    @JsName("fn5")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values with various length while exceeding the range`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 2
        val referenceProxy = fixture.funProxyFixture(name)

        val handle1 = VerificationHandle(
            referenceProxy,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            referenceProxy,
            listOf(
                expectedCallIdx + 1,
            )
        )

        val handle3 = VerificationHandle(
            referenceProxy,
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
    @JsName("fn6")
    fun `Given verifyStrictOrder is called it fails if the referenced CallIndicies do not match on multiple values with mixed References`() {
        // Given
        val name1: String = fixture.fixture()
        val name2: String = fixture.fixture()
        val expectedCallIdx = 2
        val referenceProxy1 = fixture.funProxyFixture(name1)
        val referenceProxy2 = fixture.funProxyFixture(name2)

        val handle1 = VerificationHandle(
            referenceProxy1,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            referenceProxy2,
            listOf(
                0,
            )
        )

        val handle3 = VerificationHandle(
            referenceProxy1,
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
    @JsName("fn7")
    fun `Given verifyStrictOrder is called it passes if the referenced CallIndicies match on multiple values with mixed References`() {
        // Given
        val name1: String = fixture.fixture()
        val name2: String = fixture.fixture()
        val expectedCallIdx = 1
        val referenceProxy1 = fixture.funProxyFixture(name1)
        val referenceProxy2 = fixture.funProxyFixture(name2)

        val handle1 = VerificationHandle(
            referenceProxy1,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            referenceProxy2,
            listOf(
                0,
            )
        )

        val handle3 = VerificationHandle(
            referenceProxy1,
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
    @JsName("fn8")
    fun `Given verifyOrder is called it fails if the amount captured calls is smaller than the given Order`() {
        // Given
        val verifier = VerifierStub(emptyList())
        val handle = VerificationHandle(fixture.funProxyFixture(), fixture.listFixture())

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
    @JsName("fn9")
    fun `Given verifyOrder is called it fails if the captured calls does not contain the mentioned function call`() {
        val handleProxy = fixture.funProxyFixture()
        val referenceProxy = fixture.funProxyFixture()

        val handle = VerificationHandle(
            handleProxy,
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

        error.message mustBe "Last referred invocation of ${handleProxy.id} was not found."
    }

    @Test
    @JsName("fn10")
    fun `Given verifyOrder is called it passes if the referenced CallIndicies was found`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx = 0
        val referenceProxy = fixture.funProxyFixture(name)

        val handle = VerificationHandle(
            referenceProxy,
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
    @JsName("fn11")
    fun `Given verifyOrder is called it fails if the referenced CallIndicies was not found for multiple calls`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx1 = 1
        val expectedCallIdx2 = 0
        val referenceProxy = fixture.funProxyFixture(name)

        val handle1 = VerificationHandle(
            referenceProxy,
            listOf(expectedCallIdx1)
        )

        val handle2 = VerificationHandle(
            referenceProxy,
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
    @JsName("fn12")
    fun `Given verifyOrder is called it passes if the referenced CallIndicies was found for multiple calls`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx1 = 0
        val expectedCallIdx2 = 1
        val referenceProxy = fixture.funProxyFixture(name)

        val handle1 = VerificationHandle(
            referenceProxy,
            listOf(expectedCallIdx1)
        )

        val handle2 = VerificationHandle(
            referenceProxy,
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
    @JsName("fn13")
    fun `Given verifyOrder is called it passes if the referenced CallIndicies was found for multiple calls with various length`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx1 = 2
        val expectedCallIdx2 = 3

        val referenceProxy = fixture.funProxyFixture(name)

        val handle1 = VerificationHandle(
            referenceProxy,
            listOf(
                0,
                1,
                expectedCallIdx1
            )
        )

        val handle2 = VerificationHandle(
            referenceProxy,
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
                Reference(referenceProxy, expectedCallIdx2),
                Reference(referenceProxy, 4),
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
    @JsName("fn14")
    fun `Given verifyOrder is called it fails if the referenced CallIndicies was not found for multiple calls with various length`() {
        // Given
        val name: String = fixture.fixture()
        val expectedCallIdx1 = 3
        val expectedCallIdx2 = 2
        val referenceProxy = fixture.funProxyFixture(name)

        val handle1 = VerificationHandle(
            referenceProxy,
            listOf(
                expectedCallIdx1
            )
        )

        val handle2 = VerificationHandle(
            referenceProxy,
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
    @JsName("fn15")
    fun `Given verifyOrder is called it passes if the referenced CallIndicies match on multiple values with mixed References`() {
        // Given
        val name1: String = fixture.fixture()
        val name2: String = fixture.fixture()
        val expectedCallIdx = 2
        val referenceProxy1 = fixture.funProxyFixture(name1)
        val referenceProxy2 = fixture.funProxyFixture(name2)

        val handle1 = VerificationHandle(
            referenceProxy1,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            referenceProxy2,
            listOf(
                0,
            )
        )

        val handle3 = VerificationHandle(
            referenceProxy1,
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
    @JsName("fn16")
    fun `Given verifyOrder is called it fails if the referenced CallIndicies does not match on multiple values with mixed References`() {
        // Given
        val name1: String = fixture.fixture()
        val name2: String = fixture.fixture()
        val expectedCallIdx = 2
        val referenceProxy1 = fixture.funProxyFixture(name1)
        val referenceProxy2 = fixture.funProxyFixture(name2)

        val handle1 = VerificationHandle(
            referenceProxy1,
            listOf(
                0,
            )
        )

        val handle2 = VerificationHandle(
            referenceProxy2,
            listOf(
                0,
            )
        )

        val handle3 = VerificationHandle(
            referenceProxy1,
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
