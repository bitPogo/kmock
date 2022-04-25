/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.fixture.fixtureVerificationHandle
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.kmock.fixture.propertyProxyFixture
import tech.antibytes.mock.AssertionsStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AssertionChainSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils AssertionChain`() {
        AssertionChain(emptyList()) fulfils KMockContract.AssertionChain::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils Assertion`() {
        AssertionChain(emptyList()) fulfils KMockContract.Assertion::class
    }

    @Test
    @JsName("fn2")
    fun `Given propagate is called and the references are exceeded it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle()

        val chain = AssertionChain(emptyList())

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

        val chain = AssertionChain(references)

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

        val chain = AssertionChain(references)

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

        val chain = AssertionChain(references)

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

        val chain = AssertionChain(references)

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

        val chain = AssertionChain(references)

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

        val chain = AssertionChain(references)

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

        val chain = AssertionChain(references)

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

        val chain = AssertionChain(references)

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
        val container = AssertionChain(emptyList())

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
        val container = AssertionChain(emptyList())

        proxy.assertionChain = container

        // When
        container.ensureVerificationOf(proxy)
    }

    private fun invoke(
        chain: AssertionChain,
        action: AssertionChain.() -> Unit
    ) = action(chain)

    @Test
    @JsName("fn13")
    fun `Given hasBeenCalled is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = AssertionChain(emptyList())

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

        val container = AssertionChain(references)

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
    fun `Given hasBeenCalled is called in a Chain it delegates the call to the given Assertions`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )
        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null

        val assertions = AssertionsStub(
            hasBeenCalledAtIndex = { givenProxy, givenIdx ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
            }
        )

        val container = AssertionChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn16")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = AssertionChain(emptyList())

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

        val container = AssertionChain(references)

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
    fun `Given hasBeenCalledWithVoid is called in a Chain it the delegates the call to the Assertions`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )
        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null

        val assertions = AssertionsStub(
            hasBeenCalledWithVoidAtIndex = { givenProxy, givenIdx ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
            }
        )

        val container = AssertionChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithVoid()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn19")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = AssertionChain(emptyList())

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
    @JsName("fn20")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = AssertionChain(references)

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
    fun `Given hasBeenCalledWith is called in a Chain it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null
        var capturedArguments: Array<out Any?>? = null

        val assertions = AssertionsStub(
            hasBeenCalledWithAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
                capturedArguments = givenArguments
            }
        )

        val container = AssertionChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWith(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
        capturedArguments.contentDeepEquals(arrayOf(expectedValue)) mustBe true
    }

    @Test
    @JsName("fn22")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = AssertionChain(emptyList())

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
    @JsName("fn23")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = AssertionChain(references)

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
    @JsName("fn24")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null
        var capturedArguments: Array<out Any?>? = null

        val assertions = AssertionsStub(
            hasBeenStrictlyCalledWithAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
                capturedArguments = givenArguments
            }
        )

        val container = AssertionChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenStrictlyCalledWith(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
        capturedArguments.contentDeepEquals(arrayOf(expectedValue)) mustBe true
    }

    @Test
    @JsName("fn25")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = AssertionChain(emptyList())

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
    @JsName("fn26")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = AssertionChain(references)

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
    @JsName("fn27")
    fun `Given hasBeenCalledWithout is called in a Chain it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null
        var capturedArguments: Array<out Any?>? = null

        val assertions = AssertionsStub(
            hasBeenCalledWithoutAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
                capturedArguments = givenArguments
            }
        )

        val container = AssertionChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithout(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
        capturedArguments.contentDeepEquals(arrayOf(expectedValue)) mustBe true
    }

    @Test
    @JsName("fn28")
    fun `Given wasGotten is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = AssertionChain(emptyList())

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
    @JsName("fn29")
    fun `Given wasGotten is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = AssertionChain(references)

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
    @JsName("fn30")
    fun `Given wasGotten is called in a Chain it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null

        val assertions = AssertionsStub(
            wasGottenAtIndex = { givenProxy, givenIdx ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
            }
        )

        val container = AssertionChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasGotten()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn31")
    fun `Given wasSet is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = AssertionChain(emptyList())

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
    @JsName("fn32")
    fun `Given wasSet is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = AssertionChain(references)

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
    @JsName("fn33")
    fun `Given wasSet is called in a Chain it it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null

        val assertions = AssertionsStub(
            wasSetAtIndex = { givenProxy, givenIdx ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
            }
        )

        val container = AssertionChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSet()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn34")
    fun `Given wasSetTo is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = AssertionChain(emptyList())

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
    @JsName("fn35")
    fun `Given wasSetTo is called in a Chain it fails if the current Reference and the Expectation Proxies are not the same`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = AssertionChain(references)

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
    fun `Given wasSetTo is called in a Chain it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: Any? = fixture.fixture()

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null
        var capturedValue: Any? = null

        val assertions = AssertionsStub(
            wasSetToAtIndex = { givenProxy, givenIdx, argument ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
                capturedValue = argument
            }
        )

        val container = AssertionChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSetTo(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
        capturedValue sameAs expectedValue
    }
}
