/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.fixture.fixtureVerificationHandle
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NonStrictVerificationChainSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils VerificationChain`() {
        NonStrictVerificationChain(emptyList()) fulfils KMockContract.VerificationChain::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils VerificationInsurance`() {
        NonStrictVerificationChain(emptyList()) fulfils KMockContract.VerificationInsurance::class
    }

    @Test
    @JsName("fn2")
    fun `Given propagate is called and the references are exceeded, it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle()

        val chain = NonStrictVerificationChain(emptyList())

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected ${handle.proxy.id} to be invoked, but no call was captured with the given arguments."
    }

    @Test
    @JsName("fn3")
    fun `Given propagate is called and actual refers same Proxy as expected, but the invocations are exceeded, it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(calls = 0)
        val references = listOf(
            Reference(handle.proxy, fixture.fixture())
        )

        val chain = NonStrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected call of ${handle.proxy.id} was not made."
    }

    @Test
    @JsName("fn4")
    fun `Given propagate is called and actual refers same Proxy as expected, but the invocations are exceeded over multible Handles, it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(
            callIndices = listOf(3)
        )
        val references = listOf(
            Reference(handle.proxy, 0),
            Reference(handle.proxy, 1),
            Reference(handle.proxy, 2),
        )

        val chain = NonStrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected call of ${handle.proxy.id} was not made."
    }

    @Test
    @JsName("fn5")
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

        val chain = NonStrictVerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle1)
            chain.propagate(handle2)
            chain.propagate(handle1)
            chain.propagate(handle2)
        }

        error.message mustBe "Expected ${handle2.proxy.id} to be invoked, but no call was captured with the given arguments."
    }

    @Test
    @JsName("fn6")
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

        val chain = NonStrictVerificationChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.propagate(handle1)
        chain.propagate(handle2)
    }

    @Test
    @JsName("fn7")
    fun `Given propagate is called and actual refers to the same Proxy as expected and the invocation match, it accepts for incomplete calls`() {
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

        val chain = NonStrictVerificationChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.propagate(handle1)
    }

    @Test
    @JsName("fn8")
    fun `Given ensureAllReferencesAreEvaluated is calledit accepts allways`() {
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

        val chain = NonStrictVerificationChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.propagate(handle1)

        chain.ensureAllReferencesAreEvaluated()
    }
}
