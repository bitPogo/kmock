/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.GetOrSet
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.kmock.fixture.propertyProxyFixture
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class VerificationChainSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils AssertionChain`() {
        VerificationChain(emptyList()) fulfils KMockContract.AssertionChain::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils ChainedAssertion`() {
        VerificationChain(emptyList()) fulfils KMockContract.ChainedAssertion::class
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

        val chain = VerificationChain(references)

        // When
        chain.ensureAllReferencesAreEvaluated()
    }

    @Test
    @JsName("fn3")
    fun `Given ensureVerification it fails if the given Proxy is not part of it`() {
        // Given
        val proxy = fixture.funProxyFixture()

        // When
        val container = VerificationChain(emptyList())

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
        val container = VerificationChain(
            listOf(
                Reference(proxy, 0)
            )
        )

        // When
        container.ensureVerificationOf(proxy)
    }

    private fun invoke(
        chain: VerificationChain,
        action: VerificationChain.() -> Unit
    ) = action(chain)

    @Test
    @JsName("fn5")
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
    @JsName("fn7")
    fun `Given hasBeenCalled is called in a Chain it delegates the call to the given Assertions`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        expectedProxy.getArgumentsForCall = { arrayOf() }

        val container = VerificationChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
        }
    }

    @Test
    @JsName("fn8")
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
    @JsName("fn10")
    fun `Given hasBeenCalledWithVoid is called in a Chain it fails if the given proxy is not void`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        expectedProxy.getArgumentsForCall = { arrayOf(fixture.fixture()) }

        val container = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithVoid()
            }
        }

        error.message mustBe "Expected ${expectedProxy.id} to be void, but the invocation contains Arguments."
    }

    @Test
    @JsName("fn11")
    fun `Given hasBeenCalledWithVoid is called in a Chain it accepts if the given proxy is void`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        expectedProxy.getArgumentsForCall = { arrayOf() }

        val container = VerificationChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithVoid()
        }
    }

    @Test
    @JsName("fn12")
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
    @JsName("fn13")
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
    @JsName("fn15")
    fun `Given hasBeenCalledWith is called in a Chain it fails if the arguments are not matching`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()
        val actualValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(actualValue) }

        val container = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWith(expectedValue)
            }
        }

        error.message mustBe "Expected ${expectedProxy.id} to be invoked with [$expectedValue], but no matching call was found."
    }

    @Test
    @JsName("fn16")
    fun `Given hasBeenCalledWith is called in a Chain it accepts`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(fixture.fixture(), expectedValue) }

        val container = VerificationChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWith(expectedValue)
        }
    }

    @Test
    @JsName("fn17")
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
    @JsName("fn18")
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
    @JsName("fn19")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails the arguments do not match`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()
        val actualValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(actualValue) }

        val container = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenStrictlyCalledWith(expectedValue)
            }
        }

        error.message mustBe "Expected ${expectedProxy.id} to be invoked with [$expectedValue], but no matching call was found."
    }

    @Test
    @JsName("fn20")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it fails the arguments are not in linear order`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()
        val actualValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(actualValue, expectedValue) }

        val container = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasBeenStrictlyCalledWith(expectedValue)
            }
        }

        error.message mustBe "Expected ${expectedProxy.id} to be invoked with [$expectedValue], but no matching call was found."
    }

    @Test
    @JsName("fn21")
    fun `Given hasBeenStrictlyCalledWith is called in a Chain it accepts the arguments match`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(expectedValue) }

        val container = VerificationChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenStrictlyCalledWith(expectedValue)
        }
    }

    @Test
    @JsName("fn22")
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
    @JsName("fn23")
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
    @JsName("fn24")
    fun `Given hasBeenCalledWithout is called in a Chain it fails if the a argument matches`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(expectedValue) }

        val container = VerificationChain(references)

        val error = assertFailsWith<AssertionError> {
            // Then
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithout(expectedValue)
            }
        }

        error.message mustBe "Illegal value <[$expectedValue]> detected."
    }

    @Test
    @JsName("fn25")
    fun `Given hasBeenCalledWithout is called in a Chain it accepts if the a the agruments do not match`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.funProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { arrayOf(fixture.fixture()) }

        val container = VerificationChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithout(expectedValue)
        }
    }

    @Test
    @JsName("fn26")
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
    @JsName("fn27")
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
    @JsName("fn28")
    fun `Given wasGotten is called in a Chain it fails if the was not a Getter`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        expectedProxy.getArgumentsForCall = { GetOrSet.Set(null) }

        val container = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasGotten()
            }
        }

        error.message mustBe "Expected a getter and got a setter."
    }

    @Test
    @JsName("fn29")
    fun `Given wasGotten is called in a Chain it accepts if it was a Getter`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        expectedProxy.getArgumentsForCall = { GetOrSet.Get }

        val container = VerificationChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasGotten()
        }
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
    fun `Given wasSet is called in a Chain it fails if the was not a Setter`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        expectedProxy.getArgumentsForCall = { GetOrSet.Get }

        val container = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSet()
            }
        }

        error.message mustBe "Expected a setter and got a getter."
    }

    @Test
    @JsName("fn33")
    fun `Given wasSet is called in a Chain it accepts if it was a Setter`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        expectedProxy.getArgumentsForCall = { GetOrSet.Set(null) }

        val container = VerificationChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSet()
        }
    }

    @Test
    @JsName("fn34")
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
    @JsName("fn35")
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
    @JsName("fn36")
    fun `Given wasSetTo is called in a Chain it fails if the a Setter with the given value was not found`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { GetOrSet.Get }

        val container = VerificationChain(references)

        // Then
        val error = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.wasSetTo(expectedValue)
            }
        }

        error.message mustBe "Expected ${expectedProxy.id} to be invoked with $expectedValue, but no matching call was found."
    }

    @Test
    @JsName("fn37")
    fun `Given wasSetTo is called in a Chain it accepts if it was a Setter with the given value`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
        val callIdx: Int = fixture.fixture()
        val references = listOf(
            Reference(expectedProxy, callIdx),
        )

        val expectedValue: String = fixture.fixture()

        expectedProxy.getArgumentsForCall = { GetOrSet.Set(expectedValue) }

        val container = VerificationChain(references)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSetTo(expectedValue)
        }
    }

    @Test
    @JsName("fn38")
    fun `It respects the pratial order of the chain`() {
        // Given
        val proxy1 = fixture.funProxyFixture()
        val proxy2 = fixture.funProxyFixture()
        val proxy3 = fixture.propertyProxyFixture()

        val references = listOf(
            Reference(proxy3, 0),
            Reference(proxy1, 0),
            Reference(proxy3, 1),
            Reference(proxy2, 0),
            Reference(proxy2, 1),
            Reference(proxy1, 1),
            Reference(proxy1, 2),
            Reference(proxy3, 2),
            Reference(proxy1, 3),
            Reference(proxy1, 4),
            Reference(proxy2, 2),
            Reference(proxy3, 3),
            Reference(proxy3, 4),
            Reference(proxy1, 5),
        )

        val expectedProxy1Value1: Any = fixture.fixture()
        val expectedProxy1Value2: Any = fixture.fixture()

        val expectedProxy3Value: Any = fixture.fixture()

        proxy1.getArgumentsForCall = { idx ->
            when (idx) {
                2 -> arrayOf(expectedProxy1Value1)
                4 -> arrayOf(expectedProxy1Value2)
                else -> arrayOf(fixture.fixture())
            }
        }

        proxy2.getArgumentsForCall = { arrayOf() }

        proxy3.getArgumentsForCall = { idx ->
            when (idx) {
                2 -> GetOrSet.Set(fixture.fixture<Any>())
                3 -> GetOrSet.Set(expectedProxy3Value)
                else -> GetOrSet.Get
            }
        }

        val chain = VerificationChain(references)

        // When
        invoke(chain) {
            proxy3.wasGotten()
            proxy2.hasBeenCalledWithVoid()
            proxy1.hasBeenStrictlyCalledWith(expectedProxy1Value1)
            proxy3.wasSet()
            proxy1.hasBeenCalledWith(expectedProxy1Value2)
            proxy2.hasBeenCalledWithout(fixture.fixture())
            proxy3.wasSetTo(expectedProxy3Value)
            proxy3.wasGotten()
            proxy1.hasBeenCalled()
        }
    }
}
