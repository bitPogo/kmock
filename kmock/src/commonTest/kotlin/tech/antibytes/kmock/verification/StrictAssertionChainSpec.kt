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
import tech.antibytes.kmock.fixture.propertyProxyFixture
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class StrictAssertionChainSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils AssertionChain`() {
        StrictAssertionChain(emptyList()) fulfils KMockContract.AssertionChain::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils Assertion`() {
        StrictAssertionChain(emptyList()) fulfils KMockContract.Assertion::class
    }

    @Test
    @JsName("fn2")
    fun `Given propagate is called and the references are exceeded it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle()

        val chain = StrictAssertionChain(emptyList())

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected ${handle.proxy.id} to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn3")
    fun `Given propagate is called and actual refers not to the same Proxy it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle()
        val references = listOf(
            Reference(fixture.funProxyFixture(), fixture.fixture())
        )

        val chain = StrictAssertionChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected ${handle.proxy.id} to be invoked, but ${references[0].proxy.id} was called."
    }

    @Test
    @JsName("fn4")
    fun `Given propagate is called and actual refers to the same Proxy as expected but the invocations are exceeded it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(calls = 0)
        val references = listOf(
            Reference(handle.proxy, fixture.fixture())
        )

        val chain = StrictAssertionChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected 1th call of ${handle.proxy.id} was not made."
    }

    @Test
    @JsName("fn5")
    fun `Given propagate is called and actual refers to the same Proxy as expected but the invocation do not match it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(calls = 1)
        val references = listOf(
            Reference(handle.proxy, fixture.fixture())
        )

        val chain = StrictAssertionChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected 1th call of ${handle.proxy.id} was not made."
    }

    @Test
    @JsName("fn6")
    fun `Given propagate is called and actual refers to the same Proxy as expected but the invocation do not match while working on several handels it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(
            callIndices = listOf(0, 1)
        )
        val references = listOf(
            Reference(handle.proxy, 0),
            Reference(handle.proxy, 2)
        )

        val chain = StrictAssertionChain(references)

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
    fun `Given propagate is called and actual refers to the same Proxy as expected but the invocation do not match while working on several handels and references it fails`() {
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

        val chain = StrictAssertionChain(references)

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
    fun `Given propagate is called and actual refers to the same Proxy as expected and the invocation match it accepts`() {
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

        val chain = StrictAssertionChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.propagate(handle1)
        chain.propagate(handle2)
    }

    @Test
    @JsName("fn9")
    fun `Given ensureAllReferencesAreEvaluated is called it fails if not all References had been evaluated`() {
        // Given
        val references = listOf(
            Reference(fixture.funProxyFixture(), 0),
            Reference(fixture.funProxyFixture(), 0),
        )

        val chain = StrictAssertionChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.ensureAllReferencesAreEvaluated()
        }

        error.message mustBe "The given verification chain covers 2 items, but only 0 were expected (${references[0].proxy.id}, ${references[1].proxy.id} were referenced)."
    }

    @Test
    @JsName("fn10")
    fun `Given ensureAllReferencesAreEvaluated is called it accepts if all References had been evaluated`() {
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

        val chain = StrictAssertionChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.ensureAllReferencesAreEvaluated()
    }

    @Test
    @JsName("fn11")
    fun `Given ensureVerification it fails if the given mock is not part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()

        // When
        val container = StrictAssertionChain(emptyList())

        val error = assertFailsWith<IllegalStateException> {
            container.ensureVerificationOf(proxy)
        }

        // Then
        error.message mustBe "The given proxy ${proxy.id} is not part of this AssertionChain."
    }

    @Test
    @JsName("fn12")
    fun `Given ensureVerification it accepts if the given Proxy is part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val container = StrictAssertionChain(emptyList())

        proxy.assertionChain = container

        // When
        container.ensureVerificationOf(proxy)
    }

    private fun invoke(
        chain: StrictAssertionChain,
        action: StrictAssertionChain.() -> Unit
    ) = action(chain)

    @Test
    @JsName("fn13")
    fun `Given hasBeenCalled is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictAssertionChain(emptyList())

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                proxy.hasBeenCalled()
            }
        }

        actual.message mustBe "Expected $id to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn14")
    fun `Given hasBeenCalled is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalled()
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but $id2 was called."
    }

    @Test
    @JsName("fn15")
    fun `Given hasBeenCalled is called in a Chain it accepts if the current Reference and the Expectation Proxies are the same`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
        }
    }

    @Test
    @JsName("fn16")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictAssertionChain(emptyList())

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                proxy.hasBeenCalledWithVoid()
            }
        }

        actual.message mustBe "Expected $id to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn17")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithVoid()
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but $id2 was called."
    }

    @Test
    @JsName("fn18")
    fun `Given hasBeenCalledWithVoid is called in a Chain it accepts if the current Reference and the Expectation Proxies are the same, but the proxy contains values`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        expectedProxy.getArgumentsForCall = { fixture.listFixture<Any>(size = 2).toTypedArray() }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithVoid()
            }
        }

        actual.message mustBe "Expected a void function invocation, but the invocation contains Arguments."
    }

    @Test
    @JsName("fn19")
    fun `Given hasBeenCalledWithVoid is called in a Chain it accepts if the current Reference and the Expectation Proxies are the same`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        expectedProxy.getArgumentsForCall = { arrayOf() }

        val container = StrictAssertionChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
        }
    }

    @Test
    @JsName("fn20")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictAssertionChain(emptyList())

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                proxy.hasBeenCalledWith(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn21")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWith(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but $id2 was called."
    }

    @Test
    @JsName("fn21")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the invocation does not end up`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()
        val actualValue: Any = fixture.fixture()
        var capturedIdx: Int? = null

        expectedProxy.getArgumentsForCall = { givenIdx ->
            capturedIdx = givenIdx

            arrayOf(actualValue)
        }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWith(expectedValue)
            }
        }

        actual.message mustBe "Expected <$expectedValue> got actual <$actualValue>."
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn22")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the invocation is out of scope`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf() }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWith(expectedValue)
            }
        }

        actual.message mustBe "The given matcher $expectedValue has not been found."
    }

    @Test
    @JsName("fn23")
    fun `Given hasBeenCalledWith is called in a Chain it accepts if the Proxy and Expectation end up`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(expectedValue) }

        val container = StrictAssertionChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWith(expectedValue)
        }
    }

    @Test
    @JsName("fn24")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictAssertionChain(emptyList())

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                proxy.hasBeenStrictlyCalledWith(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn25")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenStrictlyCalledWith(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but $id2 was called."
    }

    @Test
    @JsName("fn26")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the invocation does not end up`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()
        val actualValue: Any = fixture.fixture()
        var capturedIdx: Int? = null

        expectedProxy.getArgumentsForCall = { givenIdx ->
            capturedIdx = givenIdx
            arrayOf(actualValue)
        }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenStrictlyCalledWith(expectedValue)
            }
        }

        actual.message mustBe "Expected <$expectedValue> got actual <$actualValue>."
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn26")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the invocation is out of scope`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf() }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenStrictlyCalledWith(expectedValue)
            }
        }

        actual.message mustBe "Expected <1> arguments got actual <0>."
    }

    @Test
    @JsName("fn27")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it accepts if the Proxy and Expectation end up`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(expectedValue) }

        val container = StrictAssertionChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenStrictlyCalledWith(expectedValue)
        }
    }

    @Test
    @JsName("fn28")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictAssertionChain(emptyList())

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                proxy.hasBeenCalledWithout(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn29")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithout(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but $id2 was called."
    }

    @Test
    @JsName("fn30")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the invocation do end up`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()
        var capturedIdx: Int? = null

        expectedProxy.getArgumentsForCall = { givenIdx ->
            capturedIdx = givenIdx
            arrayOf(expectedValue)
        }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithout(expectedValue)
            }
        }

        actual.message mustBe "Illegal value <$expectedValue> detected."
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn31")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the invocation is out of scope`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(expectedValue) }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithout(expectedValue)
            }
        }

        actual.message mustBe "Illegal value <$expectedValue> detected."
    }

    @Test
    @JsName("fn32")
    fun `Given hasBeenCalledWithout is called in a Chain it accepts if the Proxy matches but Expectation do not end up`() {
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        val expectedValue: String = fixture.fixture()
        val actualValue: Any = fixture.fixture()
        var capturedIdx: Int? = null

        expectedProxy.getArgumentsForCall = { givenIdx ->
            capturedIdx = givenIdx

            arrayOf(actualValue)
        }

        val container = StrictAssertionChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithout(expectedValue)
        }

        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn33")
    fun `Given wasGotten is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = StrictAssertionChain(emptyList())

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                proxy.wasGotten()
            }
        }

        actual.message mustBe "Expected $id to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn34")
    fun `Given wasGotten is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasGotten()
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but $id2 was called."
    }

    @Test
    @JsName("fn35")
    fun `Given wasGotten is called in a Chain it fails if the invocation Proxies was not a get`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )
        var capturedIndex: Int? = null

        expectedProxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Set(null)
        }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasGotten()
            }
        }

        actual.message mustBe "Expected a getter and got a setter."
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn36")
    fun `Given wasGotten is called in a Chain it accepts if the current Reference and the Expectation Proxies are the same`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture()
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        expectedProxy.getArgumentsForCall = {
            KMockContract.GetOrSet.Get
        }

        val container = StrictAssertionChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasGotten()
        }
    }

    @Test
    @JsName("fn37")
    fun `Given wasSet is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = StrictAssertionChain(emptyList())

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                proxy.wasSet()
            }
        }

        actual.message mustBe "Expected $id to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn38")
    fun `Given wasSet is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSet()
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but $id2 was called."
    }

    @Test
    @JsName("fn39")
    fun `Given wasSet is called in a Chain it fails if the invocation Proxies was not a set`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val references = listOf(
            Reference(expectedProxy, 0),
        )
        var capturedIndex: Int? = null

        expectedProxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Get
        }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSet()
            }
        }

        actual.message mustBe "Expected a setter and got a getter."
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn40")
    fun `Given wasSet is called in a Chain it accepts if the current Reference and the Expectation Proxies are the same`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture()
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        expectedProxy.getArgumentsForCall = {
            KMockContract.GetOrSet.Set(null)
        }

        val container = StrictAssertionChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSet()
        }
    }

    @Test
    @JsName("fn41")
    fun `Given wasSetTo is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = StrictAssertionChain(emptyList())

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                proxy.wasSetTo(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn42")
    fun `Given wasSetTo is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSetTo(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but $id2 was called."
    }

    @Test
    @JsName("fn43")
    fun `Given wasSetTo is called in a Chain it fails if the invocation Proxies does not contain a setter`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val expectedValue: Any = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, 0),
        )
        var capturedIndex: Int? = null

        expectedProxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Get
        }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSetTo(expectedValue)
            }
        }

        actual.message mustBe "Expected a setter and got a getter."
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn44")
    fun `Given wasSetTo is called in a Chain it fails if the invocation Proxies does not contain the expexted value`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val expectedValue: Any = fixture.fixture()
        val actualValue: Any? = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, 0),
        )
        var capturedIndex: Int? = null

        expectedProxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Set(actualValue)
        }

        val container = StrictAssertionChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSetTo(expectedValue)
            }
        }

        actual.message mustBe "Expected <$expectedValue> got actual <$actualValue>."
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn45")
    fun `Given wasSetTo is called in a Chain it accepts if the current Reference and the Expectation Proxies are the same`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture()
        val expectedValue: Any = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, 0),
        )

        expectedProxy.getArgumentsForCall = {
            KMockContract.GetOrSet.Set(expectedValue)
        }

        val container = StrictAssertionChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSetTo(expectedValue)
        }
    }
}
