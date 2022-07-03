/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.kmock.fixture.propertyProxyFixture
import tech.antibytes.mock.AssertionsStub
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs

class AssertionChainSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils AssertionChain`() {
        AssertionChain(emptyList()) fulfils KMockContract.AssertionChain::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils ChainedAssertion`() {
        AssertionChain(emptyList()) fulfils KMockContract.ChainedAssertion::class
    }

    private fun invoke(
        chain: AssertionChain,
        action: AssertionChain.() -> Unit,
    ) = action(chain)

    @Test
    @JsName("fn2")
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
    @JsName("fn3")
    fun `Given ensureAllReferencesAreEvaluated is called it accepts if all References had been evaluated`() {
        // Given
        val proxy1 = fixture.funProxyFixture()
        val proxy2 = fixture.funProxyFixture()
        val references = listOf(
            Reference(proxy1, 0),
            Reference(proxy2, 0),
        )

        val assertions = AssertionsStub(
            hasBeenCalledAtIndex = { _, _ -> Unit },
        )

        val chain = AssertionChain(references, assertions)

        // When
        invoke(chain) {
            proxy1.hasBeenCalled()
            proxy2.hasBeenCalled()
        }

        chain.ensureAllReferencesAreEvaluated()
    }

    @Test
    @JsName("fn4")
    fun `Given ensureVerification it fails if the given mock is not part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()

        // When
        val container = AssertionChain(emptyList())

        val error = assertFailsWith<IllegalStateException> {
            container.ensureVerificationOf(proxy)
        }

        // Then
        error.message mustBe "The given proxy ${proxy.id} is not part of this chain."
    }

    @Test
    @JsName("fn5")
    fun `Given ensureVerification it accepts if the given Proxy is part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()
        val container = AssertionChain(
            listOf(
                Reference(proxy, 0),
            ),
        )

        // When
        container.ensureVerificationOf(proxy)
    }

    @Test
    @JsName("fn6")
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
    @JsName("fn7")
    fun `Given hasBeenCalled is called in a Chain it fails if the current Reference and the expected Proxies are not the same`() {
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
    @JsName("fn8")
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
            },
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
    @JsName("fn9")
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
    @JsName("fn10")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the current Reference and the expected Proxies are not the same`() {
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
    @JsName("fn11")
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
            },
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
    @JsName("fn12")
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
    @JsName("fn13")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the current Reference and the expected Proxies are not the same`() {
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
    @JsName("fn14")
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
            },
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
    @JsName("fn15")
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
    @JsName("fn16")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails if the current Reference and the expected Proxies are not the same`() {
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
    @JsName("fn17")
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
            },
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
    @JsName("fn18")
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
    @JsName("fn19")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the current Reference and the expected Proxies are not the same`() {
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
    @JsName("fn20")
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
            },
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
    @JsName("fn21")
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
    @JsName("fn22")
    fun `Given wasGotten is called in a Chain it fails if the current Reference and the expected Proxies are not the same`() {
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
    @JsName("fn23")
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
            },
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
    @JsName("fn24")
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
    @JsName("fn25")
    fun `Given wasSet is called in a Chain it fails if the current Reference and the expected Proxies are not the same`() {
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
    @JsName("fn26")
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
            },
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
    @JsName("fn27")
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
    @JsName("fn28")
    fun `Given wasSetTo is called in a Chain it fails if the current Reference and the expected Proxies are not the same`() {
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
    @JsName("fn29")
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
            },
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

    @Test
    @JsName("fn30")
    fun `It respects the order of the chain`() {
        // Given
        val proxy1 = fixture.funProxyFixture()
        val proxy2 = fixture.funProxyFixture()
        val proxy3 = fixture.propertyProxyFixture()
        val references = listOf(
            Reference(proxy1, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy2, fixture.fixture()),
            Reference(proxy1, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy2, fixture.fixture()),
            Reference(proxy3, fixture.fixture()),
            Reference(proxy1, fixture.fixture()),
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
            },
        )

        val chain = AssertionChain(references, assertions)

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
        capturedProxies mustBe references.map { it.proxy }
        capturedCallIdx mustBe references.map { it.callIndex }
    }
}
