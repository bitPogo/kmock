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
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class StrictVerificationChainSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils AssertionChain`() {
        StrictVerificationChain(emptyList()) fulfils KMockContract.AssertionChain::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils ChainedAssertion`() {
        StrictVerificationChain(emptyList()) fulfils KMockContract.ChainedAssertion::class
    }

    @Test
    @JsName("fn2")
    fun `Given ensureAllReferencesAreEvaluated is called it accepts allways`() {
        // Given
        val proxy1 = fixture.funProxyFixture()
        val proxy2 = fixture.funProxyFixture()
        val references = listOf(
            Reference(proxy1, fixture.fixture()),
            Reference(proxy2, fixture.fixture()),
            Reference(proxy1, fixture.fixture()),
            Reference(proxy2, fixture.fixture()),
        )

        val chain = StrictVerificationChain(references)

        // When
        chain.ensureAllReferencesAreEvaluated()
    }

    @Test
    @JsName("fn3")
    fun `Given ensureVerification it fails if the given Proxy is not part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()

        // When
        val container = StrictVerificationChain(emptyList())

        val error = assertFailsWith<IllegalStateException> {
            container.ensureVerificationOf(proxy)
        }

        // Then
        error.message mustBe "The given proxy ${proxy.id} is not part of this chain."
    }

    @Test
    @JsName("fn4")
    fun `Given ensureVerification it accepts if the given Proxy is part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val container = StrictVerificationChain(
            listOf(
                Reference(proxy, 0)
            )
        )

        // When
        container.ensureVerificationOf(proxy)
    }

    private fun invoke(
        chain: StrictVerificationChain,
        action: StrictVerificationChain.() -> Unit
    ) = action(chain)

    @Test
    @JsName("fn5")
    fun `Given hasBeenCalled is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictVerificationChain(emptyList())

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
    @JsName("fn6")
    fun `Given hasBeenCalled is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictVerificationChain(references)

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
    @JsName("fn7")
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

        val container = StrictVerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn8")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictVerificationChain(emptyList())

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
    @JsName("fn9")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictVerificationChain(references)

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
    @JsName("fn10")
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

        val container = StrictVerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithVoid()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn11")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictVerificationChain(emptyList())

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
    @JsName("fn12")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictVerificationChain(references)

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
    @JsName("fn13")
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

        val container = StrictVerificationChain(references, assertions)

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
    @JsName("fn14")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictVerificationChain(emptyList())

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
    @JsName("fn15")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictVerificationChain(references)

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
    @JsName("fn16")
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

        val container = StrictVerificationChain(references, assertions)

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
    @JsName("fn17")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        val container = StrictVerificationChain(emptyList())

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
    @JsName("fn18")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id1)
        val actualProxy = fixture.funProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictVerificationChain(references)

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
    @JsName("fn19")
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

        val container = StrictVerificationChain(references, assertions)

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
    @JsName("fn20")
    fun `Given wasGotten is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = StrictVerificationChain(emptyList())

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
    @JsName("fn21")
    fun `Given wasGotten is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictVerificationChain(references)

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
    @JsName("fn22")
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

        val container = StrictVerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasGotten()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn23")
    fun `Given wasSet is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = StrictVerificationChain(emptyList())

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
    @JsName("fn24")
    fun `Given wasSet is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictVerificationChain(references)

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
    @JsName("fn25")
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

        val container = StrictVerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSet()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
    }

    @Test
    @JsName("fn26")
    fun `Given wasSetTo is called in a Chain it fails if the current References are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val container = StrictVerificationChain(emptyList())

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
    @JsName("fn27")
    fun `Given wasSetTo is called in a Chain it fails if the expected Proxies was not found`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id1)
        val actualProxy = fixture.propertyProxyFixture(id = id2)
        val references = listOf(
            Reference(actualProxy, 0),
        )

        val container = StrictVerificationChain(references)

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
    @JsName("fn28")
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

        val container = StrictVerificationChain(references, assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSetTo(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe callIdx
        capturedValue sameAs expectedValue
    }

    @Test
    @JsName("fn29")
    fun `It respects the pratial order of the chain`() {
        // Given
        val proxy1 = fixture.funProxyFixture()
        val proxy2 = fixture.funProxyFixture()
        val proxy3 = fixture.propertyProxyFixture()

        val expectedProxies = listOf(
            proxy1,
            proxy3,
            proxy2,
            proxy1,
            proxy3,
            proxy2,
            proxy3,
            proxy1,
        )

        val expectedCallIndices: List<Int> = fixture.listFixture(size = 8)

        val references = listOf(
            Reference(proxy3, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy1, expectedCallIndices[0]),
            Reference(proxy3, expectedCallIndices[1]),
            Reference(proxy2, expectedCallIndices[2]),
            Reference(proxy2, fixture.fixture()),
            Reference(proxy2, fixture.fixture()),
            Reference(proxy1, expectedCallIndices[3]),
            Reference(proxy1, fixture.fixture()),
            Reference(proxy1, fixture.fixture()),
            Reference(proxy1, fixture.fixture()),
            Reference(proxy3, expectedCallIndices[4]),
            Reference(proxy2, expectedCallIndices[5]),
            Reference(proxy3, expectedCallIndices[6]),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy1, expectedCallIndices[7]),
        )

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedCallIdx: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedCallIdx.add(givenIdx)
            },
            hasBeenCalledWithVoidAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedCallIdx.add(givenIdx)
            },
            hasBeenCalledWithAtIndex = { givenProxy, givenIdx, _ ->
                capturedProxies.add(givenProxy)
                capturedCallIdx.add(givenIdx)
            },
            hasBeenStrictlyCalledWithAtIndex = { givenProxy, givenIdx, _ ->
                capturedProxies.add(givenProxy)
                capturedCallIdx.add(givenIdx)
            },
            hasBeenCalledWithoutAtIndex = { givenProxy, givenIdx, _ ->
                capturedProxies.add(givenProxy)
                capturedCallIdx.add(givenIdx)
            },
            wasGottenAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedCallIdx.add(givenIdx)
            },
            wasSetAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedCallIdx.add(givenIdx)
            },
            wasSetToAtIndex = { givenProxy, givenIdx, _ ->
                capturedProxies.add(givenProxy)
                capturedCallIdx.add(givenIdx)
            }
        )

        val chain = StrictVerificationChain(references, assertions)

        // When
        invoke(chain) {
            proxy1.hasBeenCalled()
            proxy3.wasGotten()
            proxy2.hasBeenCalledWithVoid()
            proxy1.hasBeenCalledWith(fixture.fixture())
            proxy3.wasSet()
            proxy2.hasBeenStrictlyCalledWith(fixture.fixture())
            proxy3.wasSetTo(fixture.fixture())
            proxy1.hasBeenCalledWithout(fixture.fixture())
        }

        // Then
        capturedProxies mustBe expectedProxies
        capturedCallIdx mustBe expectedCallIndices
    }
}
