/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.fixture.fixtureVerificationHandle
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class StrictVerificationChainSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils VerificationChain`() {
        StrictVerificationChain(emptyList()) fulfils KMockContract.VerificationChain::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils VerificationInsurance`() {
        StrictVerificationChain(emptyList()) fulfils KMockContract.VerificationInsurance::class
    }

    @Test
    @JsName("fn2")
    fun `Given propagate is called and the references are exceeded, it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle()

        val chain = StrictVerificationChain(emptyList())

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected ${handle.proxy.id} to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn3")
    fun `Given propagate is called and actual refers not to the same Proxy, it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle()
        val references = listOf(
            Reference(fixture.funProxyFixture(), fixture.fixture())
        )

        val chain = StrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected ${handle.proxy.id} to be invoked, but ${references[0].proxy.id} was called."
    }

    @Test
    @JsName("fn4")
    fun `Given propagate is called and actual refers to the same Proxy as expected, but the invocations are exceeded, it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(calls = 0)
        val references = listOf(
            Reference(handle.proxy, fixture.fixture())
        )

        val chain = StrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected 1th call of ${handle.proxy.id} was not made."
    }

    @Test
    @JsName("fn5")
    fun `Given propagate is called and actual refers to the same Proxy as expected, but the invocation do not match, it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(calls = 1)
        val references = listOf(
            Reference(handle.proxy, fixture.fixture())
        )

        val chain = StrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected 1th call of ${handle.proxy.id}, but it refers to the ${references[0].callIndex + 1}th call."
    }

    @Test
    @JsName("fn6")
    fun `Given propagate is called and actual refers to the same Proxy as expected, but the invocation do not match, while working on several handels, it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(
            callIndices = listOf(0, 1)
        )
        val references = listOf(
            Reference(handle.proxy, 0),
            Reference(handle.proxy, 2)
        )

        val chain = StrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
            chain.propagate(handle)
        }

        error.message mustBe "Expected 2th call of ${handle.proxy.id}, but it refers to the ${references[1].callIndex + 1}th call."
    }

    @Test
    @JsName("fn7")
    fun `Given propagate is called and actual refers to the same Proxy as expected, but the invocation do not match, while working on several handels and references, it fails`() {
        // Given
        val handle1 = fixture.fixtureVerificationHandle(
            callIndices = listOf(0, 1)
        )
        val handle2 = fixture.fixtureVerificationHandle(
            callIndices = listOf(0, 1)
        )
        val references = listOf(
            Reference(handle1.proxy, 0),
            Reference(handle2.proxy, 0),
            Reference(handle1.proxy, 1),
            Reference(handle2.proxy, 2),
        )

        val chain = StrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle1)
            chain.propagate(handle2)
            chain.propagate(handle1)
            chain.propagate(handle2)
        }

        error.message mustBe "Expected 2th call of ${handle2.proxy.id}, but it refers to the ${references[3].callIndex + 1}th call."
    }

    @Test
    @JsName("fn8")
    fun `Given propagate is called and actual refers to the same Proxy as expected and the invocation match, it accepts`() {
        // Given
        val handle1 = fixture.fixtureVerificationHandle(
            callIndices = listOf(0, 1)
        )
        val handle2 = fixture.fixtureVerificationHandle(
            callIndices = listOf(0, 1)
        )
        val references = listOf(
            Reference(handle1.proxy, 0),
            Reference(handle2.proxy, 0),
            Reference(handle1.proxy, 1),
            Reference(handle2.proxy, 1),
        )

        val chain = StrictVerificationChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.propagate(handle1)
        chain.propagate(handle2)
    }

    @Test
    @JsName("fn9")
    fun `Given ensureAllReferencesAreEvaluated is called, it fails if not all References had been evaluated`() {
        // Given
        val references = listOf(
            Reference(fixture.funProxyFixture(), 0),
            Reference(fixture.funProxyFixture(), 0),
        )

        val chain = StrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.ensureAllReferencesAreEvaluated()
        }

        error.message mustBe "The given verification chain covers 2 items, but only 0 were expected (${references[0].proxy.id}, ${references[1].proxy.id} were referenced)."
    }

    @Test
    @JsName("fn10")
    fun `Given ensureAllReferencesAreEvaluated is called, it accepts if all References had been evaluated`() {
        // Given
        val handle1 = fixture.fixtureVerificationHandle(
            callIndices = listOf(0)
        )
        val handle2 = fixture.fixtureVerificationHandle(
            callIndices = listOf(0)
        )
        val references = listOf(
            Reference(handle1.proxy, 0),
            Reference(handle2.proxy, 0),
        )

        val chain = StrictVerificationChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.ensureAllReferencesAreEvaluated()
    }
}
