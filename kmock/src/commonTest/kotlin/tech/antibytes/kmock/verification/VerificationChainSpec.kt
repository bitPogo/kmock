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

class VerificationChainSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils VerificationChain`() {
        VerificationChain(emptyList()) fulfils KMockContract.AssertionChain::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils Assert`() {
        VerificationChain(emptyList()) fulfils KMockContract.Assert::class
    }

    @Test
    @JsName("fn2")
    fun `Given propagate is called and the references are exceeded it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle()

        val chain = VerificationChain(emptyList())

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected ${handle.proxy.id} to be invoked, but no call was captured with the given arguments."
    }

    @Test
    @JsName("fn3")
    fun `Given propagate is called and actual refers same Proxy as expected but the invocations are exceeded it fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(calls = 0)
        val references = listOf(
            Reference(handle.proxy, fixture.fixture())
        )

        val chain = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected ${handle.proxy.id} to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn4")
    fun `Given propagate is called and actual refers same Proxy as expected but the invocations are exceeded over multible Handlesit fails`() {
        // Given
        val handle = fixture.fixtureVerificationHandle(
            callIndices = listOf(3)
        )
        val references = listOf(
            Reference(handle.proxy, 0),
            Reference(handle.proxy, 1),
            Reference(handle.proxy, 2),
        )

        val chain = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            // When
            chain.propagate(handle)
        }

        error.message mustBe "Expected ${handle.proxy.id} to be invoked, but no call was captured with the given arguments."
    }

    @Test
    @JsName("fn5")
    fun `Given propagate is called and actual refers to the same Proxy as expected but the invocation do not match while working on several handels and referencesit fails`() {
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

        val chain = VerificationChain(references)

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

        val chain = VerificationChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.propagate(handle1)
        chain.propagate(handle2)
    }

    @Test
    @JsName("fn7")
    fun `Given propagate is called and actual refers to the same Proxy as expected and the invocation match it accepts for incomplete calls`() {
        // Given
        val handle1 = fixture.fixtureVerificationHandle(
            callIndices = listOf(0, 2)
        )
        val handle2 = fixture.fixtureVerificationHandle(
            callIndices = listOf(1)
        )
        val references = listOf(
            Reference(handle1.proxy, 0),
            Reference(handle2.proxy, 0),
            Reference(handle1.proxy, 1),
            Reference(handle2.proxy, 1),
            Reference(handle1.proxy, 2),
        )

        val chain = VerificationChain(references)

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

        val chain = VerificationChain(references)

        // When
        chain.propagate(handle1)
        chain.propagate(handle2)
        chain.propagate(handle1)

        chain.ensureAllReferencesAreEvaluated()
    }

    @Test
    @JsName("fn9")
    fun `Given ensureVerification it fails if the given Proxy is not part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()

        // When
        val container = VerificationChain(emptyList())

        val error = assertFailsWith<IllegalStateException> {
            container.ensureVerificationOf(proxy)
        }

        // Then
        error.message mustBe "The given proxy ${proxy.id} is not part of this AssertionChain."
    }

    @Test
    @JsName("fn10")
    fun `Given ensureVerification it accepts if the given Proxy is part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val container = VerificationChain(emptyList())

        proxy.assertionChain = container

        // When
        container.ensureVerificationOf(proxy)
    }

    private fun invoke(
        chain: VerificationChain,
        action: VerificationChain.() -> Unit
    ) = action(chain)

    @Test
    @JsName("fn12")
    fun `Given hasBeenCalled is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = VerificationChain(emptyList())

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
    @JsName("fn13")
    fun `Given hasBeenCalled is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = VerificationChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalled()
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn14")
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

        val container = VerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn15")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = VerificationChain(emptyList())

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
    @JsName("fn16")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = VerificationChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithVoid()
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn17")
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

        val container = VerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithVoid()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn18")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = VerificationChain(emptyList())

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
    @JsName("fn19")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = VerificationChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWith(fixture.fixture())
            }
        }

        actual.message mustBe "Expected $id1 to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn20")
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

        val container = VerificationChain(references, assertions)

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
    @JsName("fn21")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = VerificationChain(emptyList())

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
    @JsName("fn22")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = VerificationChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenStrictlyCalledWith(fixture.fixture())
            }
        }

       actual.message mustBe "Expected $id1 to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn23")
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

        val container = VerificationChain(references, assertions)

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
    @JsName("fn24")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = VerificationChain(emptyList())

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
    @JsName("fn25")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = VerificationChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithout(fixture.fixture())
            }
        }

       actual.message mustBe "Expected $id1 to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn26")
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

        val container = VerificationChain(references, assertions)

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
    @JsName("fn27")
    fun `Given wasGotten is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = VerificationChain(emptyList())

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
    @JsName("fn28")
    fun `Given wasGotten is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = VerificationChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasGotten()
            }
        }

       actual.message mustBe "Expected $id1 to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn29")
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

        val container = VerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasGotten()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn30")
    fun `Given wasSet is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = VerificationChain(emptyList())

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
    @JsName("fn31")
    fun `Given wasSet is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = VerificationChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSet()
            }
        }

       actual.message mustBe "Expected $id1 to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn32")
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

        val container = VerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSet()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn33")
    fun `Given wasSetTo is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = VerificationChain(emptyList())

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
    @JsName("fn34")
    fun `Given wasSetTo is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = VerificationChain(references)

        // Then
        val actual = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSetTo(fixture.fixture())
            }
        }

       actual.message mustBe "Expected $id1 to be invoked, but no further calls were captured."
    }

    @Test
    @JsName("fn35")
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

        val container = VerificationChain(references, assertions)

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
