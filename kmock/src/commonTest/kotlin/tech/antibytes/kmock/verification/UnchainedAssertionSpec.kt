/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import co.touchlab.stately.isFrozen
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract.CloseableAssertionContext
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.kmock.fixture.propertyProxyFixture
import tech.antibytes.mock.AssertionsStub
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UnchainedAssertionSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils AssertionContext`() {
        UnchainedAssertion() fulfils CloseableAssertionContext::class
    }

    private fun invoke(
        chain: UnchainedAssertion,
        action: UnchainedAssertion.() -> Unit
    ) = action(chain)

    @Test
    @JsName("fn1")
    fun `Given hasBeenCalled is called it delegates the call to the given Assertions`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null

        val assertions = AssertionsStub(
            hasBeenCalledAtIndex = { givenProxy, givenIdx ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn2")
    fun `Given hasBeenCalled is called it delegates the call to the given Assertions while respecting the order`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
            expectedProxy.hasBeenCalled()
        }

        capturedProxies[0] sameAs expectedProxy
        capturedProxies[1] sameAs expectedProxy

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 1
    }

    @Test
    @JsName("fn3")
    fun `Given hasBeenCalled is called it delegates the call to the given Assertions while respecting the order and multiple Proxies`() {
        // Given
        val expectedProxy1 = fixture.funProxyFixture()
        val expectedProxy2 = fixture.funProxyFixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy1.hasBeenCalled()
            expectedProxy2.hasBeenCalled()
            expectedProxy1.hasBeenCalled()
            expectedProxy2.hasBeenCalled()
        }

        capturedProxies[0] sameAs expectedProxy1
        capturedProxies[1] sameAs expectedProxy2
        capturedProxies[2] sameAs expectedProxy1
        capturedProxies[3] sameAs expectedProxy2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 0
        capturedIndices[2] mustBe 1
        capturedIndices[3] mustBe 1
    }

    @Test
    @JsName("fn4")
    fun `Given hasBeenCalled is called it delegates the call to the given Assertions while respecting the frozen memory model`(): AsyncTestReturnValue {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = true)

        val assertions = AssertionsStub(
            hasBeenCalledAtIndex = { _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        return runBlockingTest {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalled()
                expectedProxy.hasBeenCalled()
            }
        }
    }

    @Test
    @JsName("fn5")
    fun `Given hasBeenCalled is called it delegates the call to the given Assertions while respecting the unfrozen memory model`() {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = false)

        val assertions = AssertionsStub(
            hasBeenCalledAtIndex = { _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalled()
            expectedProxy.hasBeenCalled()
        }

        expectedProxy.isFrozen mustBe false
    }

    @Test
    @JsName("fn6")
    fun `Given hasBeenCalledWithVoid is called it the delegates the call to the Assertions`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null

        val assertions = AssertionsStub(
            hasBeenCalledWithVoidAtIndex = { givenProxy, givenIdx ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithVoid()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn7")
    fun `Given hasBeenCalledWithVoid is called it the delegates the call to the Assertions while respecting the order`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledWithVoidAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithVoid()
            expectedProxy.hasBeenCalledWithVoid()
        }

        capturedProxies[0] sameAs expectedProxy
        capturedProxies[1] sameAs expectedProxy

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 1
    }

    @Test
    @JsName("fn8")
    fun `Given hasBeenCalledWithVoid is called it delegates the call to the given Assertions while respecting the order and multiple Proxies`() {
        // Given
        val expectedProxy1 = fixture.funProxyFixture()
        val expectedProxy2 = fixture.funProxyFixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledWithVoidAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy1.hasBeenCalledWithVoid()
            expectedProxy2.hasBeenCalledWithVoid()
            expectedProxy1.hasBeenCalledWithVoid()
            expectedProxy2.hasBeenCalledWithVoid()
        }

        capturedProxies[0] sameAs expectedProxy1
        capturedProxies[1] sameAs expectedProxy2
        capturedProxies[2] sameAs expectedProxy1
        capturedProxies[3] sameAs expectedProxy2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 0
        capturedIndices[2] mustBe 1
        capturedIndices[3] mustBe 1
    }

    @Test
    @JsName("fn9")
    fun `Given hasBeenCalledWithVoid is called it the delegates the call to the Assertions  while respecting the frozen memory model`(): AsyncTestReturnValue {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = true)

        val assertions = AssertionsStub(
            hasBeenCalledWithVoidAtIndex = { _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        return runBlockingTest {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithVoid()
            }
        }
    }

    @Test
    @JsName("fn10")
    fun `Given hasBeenCalledWithVoid is called it the delegates the call to the Assertions  while respecting the unfrozen memory model`() {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = false)

        val assertions = AssertionsStub(
            hasBeenCalledWithVoidAtIndex = { _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithVoid()
        }

        expectedProxy.isFrozen mustBe false
    }

    @Test
    @JsName("fn11")
    fun `Given hasBeenCalledWith is called it delegates the call to the assertions`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

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

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWith(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe 0
        capturedArguments.contentDeepEquals(arrayOf(expectedValue)) mustBe true
    }

    @Test
    @JsName("fn12")
    fun `Given hasBeenCalledWith is called it delegates the call to the assertions while respecting the order`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

        val expectedValue1: String = fixture.fixture()
        val expectedValue2: String = fixture.fixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()
        val capturedArguments: MutableList<Array<out Any?>> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledWithAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
                capturedArguments.add(givenArguments)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWith(expectedValue1)
            expectedProxy.hasBeenCalledWith(expectedValue2)
        }

        capturedProxies[0] sameAs expectedProxy
        capturedProxies[1] sameAs expectedProxy

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 1

        capturedArguments[0].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[1].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
    }

    @Test
    @JsName("fn13")
    fun `Given hasBeenCalledWith is called it delegates the call to the given Assertions while respecting the order and multiple Proxies`() {
        // Given
        val expectedProxy1 = fixture.funProxyFixture()
        val expectedProxy2 = fixture.funProxyFixture()

        val expectedValue1: String = fixture.fixture()
        val expectedValue2: String = fixture.fixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()
        val capturedArguments: MutableList<Array<out Any?>> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledWithAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
                capturedArguments.add(givenArguments)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy1.hasBeenCalledWith(expectedValue1)
            expectedProxy2.hasBeenCalledWith(expectedValue1)
            expectedProxy1.hasBeenCalledWith(expectedValue2)
            expectedProxy2.hasBeenCalledWith(expectedValue2)
        }

        capturedProxies[0] sameAs expectedProxy1
        capturedProxies[1] sameAs expectedProxy2
        capturedProxies[2] sameAs expectedProxy1
        capturedProxies[3] sameAs expectedProxy2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 0
        capturedIndices[2] mustBe 1
        capturedIndices[3] mustBe 1

        capturedArguments[0].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[1].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[2].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
        capturedArguments[3].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
    }

    @Test
    @JsName("fn14")
    fun `Given hasBeenCalledWith is called it delegates the call to the assertions while repecting the frozen memory model`(): AsyncTestReturnValue {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = true)

        val assertions = AssertionsStub(
            hasBeenCalledWithAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        return runBlockingTest {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWith(fixture.fixture())
            }
        }
    }

    @Test
    @JsName("fn15")
    fun `Given hasBeenCalledWith is called it delegates the call to the assertions while repecting the unfrozen memory model`() {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = false)

        val assertions = AssertionsStub(
            hasBeenCalledWithAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWith(fixture.fixture())
        }

        expectedProxy.isFrozen mustBe false
    }

    @Test
    @JsName("fn16")
    fun `Given hasBeenStrictlyCalledWith is called it delegates the call to the assertions`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

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

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenStrictlyCalledWith(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe 0
        capturedArguments.contentDeepEquals(arrayOf(expectedValue)) mustBe true
    }

    @Test
    @JsName("fn17")
    fun `Given hasBeenCalhasBeenStrictlyCalledWithledWith is called it delegates the call to the assertions while respecting the order`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

        val expectedValue1: String = fixture.fixture()
        val expectedValue2: String = fixture.fixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()
        val capturedArguments: MutableList<Array<out Any?>> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenStrictlyCalledWithAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
                capturedArguments.add(givenArguments)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenStrictlyCalledWith(expectedValue1)
            expectedProxy.hasBeenStrictlyCalledWith(expectedValue2)
        }

        capturedProxies[0] sameAs expectedProxy
        capturedProxies[1] sameAs expectedProxy

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 1

        capturedArguments[0].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[1].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
    }

    @Test
    @JsName("fn18")
    fun `Given hasBeenStrictlyCalledWith is called it delegates the call to the given Assertions while respecting the order and multiple Proxies`() {
        // Given
        val expectedProxy1 = fixture.funProxyFixture()
        val expectedProxy2 = fixture.funProxyFixture()

        val expectedValue1: String = fixture.fixture()
        val expectedValue2: String = fixture.fixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()
        val capturedArguments: MutableList<Array<out Any?>> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenStrictlyCalledWithAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
                capturedArguments.add(givenArguments)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy1.hasBeenStrictlyCalledWith(expectedValue1)
            expectedProxy2.hasBeenStrictlyCalledWith(expectedValue1)
            expectedProxy1.hasBeenStrictlyCalledWith(expectedValue2)
            expectedProxy2.hasBeenStrictlyCalledWith(expectedValue2)
        }

        capturedProxies[0] sameAs expectedProxy1
        capturedProxies[1] sameAs expectedProxy2
        capturedProxies[2] sameAs expectedProxy1
        capturedProxies[3] sameAs expectedProxy2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 0
        capturedIndices[2] mustBe 1
        capturedIndices[3] mustBe 1

        capturedArguments[0].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[1].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[2].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
        capturedArguments[3].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
    }

    @Test
    @JsName("fn19")
    fun `Given hasBeenStrictlyCalledWith is called it delegates the call to the assertions while repecting the frozen memory model`(): AsyncTestReturnValue {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = true)

        val assertions = AssertionsStub(
            hasBeenStrictlyCalledWithAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        return runBlockingTest {
            invoke(container) {
                // When
                expectedProxy.hasBeenStrictlyCalledWith(fixture.fixture())
            }
        }
    }

    @Test
    @JsName("fn20")
    fun `Given hasBeenStrictlyCalledWith is called it delegates the call to the assertions while repecting the unfrozen memory model`() {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = false)

        val assertions = AssertionsStub(
            hasBeenStrictlyCalledWithAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenStrictlyCalledWith(fixture.fixture())
        }

        expectedProxy.isFrozen mustBe false
    }

    @Test
    @JsName("fn21")
    fun `Given hasBeenCalledWithout is called it delegates the call to the assertions`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

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

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithout(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe 0
        capturedArguments.contentDeepEquals(arrayOf(expectedValue)) mustBe true
    }

    @Test
    @JsName("fn22")
    fun `Given hasBeenCalledWithout is called it delegates the call to the assertions while respecting the order`() {
        // Given
        val expectedProxy = fixture.funProxyFixture()

        val expectedValue1: String = fixture.fixture()
        val expectedValue2: String = fixture.fixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()
        val capturedArguments: MutableList<Array<out Any?>> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledWithoutAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
                capturedArguments.add(givenArguments)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithout(expectedValue1)
            expectedProxy.hasBeenCalledWithout(expectedValue2)
        }

        capturedProxies[0] sameAs expectedProxy
        capturedProxies[1] sameAs expectedProxy

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 1

        capturedArguments[0].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[1].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
    }

    @Test
    @JsName("fn23")
    fun `Given hasBeenCalledWithout is called it delegates the call to the given Assertions while respecting the order and multiple Proxies`() {
        // Given
        val expectedProxy1 = fixture.funProxyFixture()
        val expectedProxy2 = fixture.funProxyFixture()

        val expectedValue1: String = fixture.fixture()
        val expectedValue2: String = fixture.fixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()
        val capturedArguments: MutableList<Array<out Any?>> = mutableListOf()

        val assertions = AssertionsStub(
            hasBeenCalledWithoutAtIndex = { givenProxy, givenIdx, givenArguments ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
                capturedArguments.add(givenArguments)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy1.hasBeenCalledWithout(expectedValue1)
            expectedProxy2.hasBeenCalledWithout(expectedValue1)
            expectedProxy1.hasBeenCalledWithout(expectedValue2)
            expectedProxy2.hasBeenCalledWithout(expectedValue2)
        }

        capturedProxies[0] sameAs expectedProxy1
        capturedProxies[1] sameAs expectedProxy2
        capturedProxies[2] sameAs expectedProxy1
        capturedProxies[3] sameAs expectedProxy2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 0
        capturedIndices[2] mustBe 1
        capturedIndices[3] mustBe 1

        capturedArguments[0].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[1].contentDeepEquals(arrayOf(expectedValue1)) mustBe true
        capturedArguments[2].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
        capturedArguments[3].contentDeepEquals(arrayOf(expectedValue2)) mustBe true
    }

    @Test
    @JsName("fn24")
    fun `Given hasBeenCalledWithout is called it delegates the call to the assertions while repecting the frozen memory model`(): AsyncTestReturnValue {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = true)

        val assertions = AssertionsStub(
            hasBeenCalledWithoutAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        return runBlockingTest {
            invoke(container) {
                // When
                expectedProxy.hasBeenCalledWithout(fixture.fixture())
            }
        }
    }

    @Test
    @JsName("fn25")
    fun `Given hasBeenCalledWithout is called it delegates the call to the assertions while repecting the unfrozen memory model`() {
        // Given
        val expectedProxy = fixture.funProxyFixture(freeze = false)

        val assertions = AssertionsStub(
            hasBeenCalledWithoutAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.hasBeenCalledWithout(fixture.fixture())
        }

        expectedProxy.isFrozen mustBe false
    }

    @Test
    @JsName("fn26")
    fun `Given wasGotten is called it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null

        val assertions = AssertionsStub(
            wasGottenAtIndex = { givenProxy, givenIdx ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasGotten()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn27")
    fun `Given wasGotten is called it the delegates the call to the Assertions while respecting the order`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            wasGottenAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasGotten()
            expectedProxy.wasGotten()
        }

        capturedProxies[0] sameAs expectedProxy
        capturedProxies[1] sameAs expectedProxy

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 1
    }

    @Test
    @JsName("fn28")
    fun `Given wasGotten is called it delegates the call to the given Assertions while respecting the order and multiple Proxies`() {
        // Given
        val expectedProxy1 = fixture.propertyProxyFixture()
        val expectedProxy2 = fixture.propertyProxyFixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            wasGottenAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy1.wasGotten()
            expectedProxy2.wasGotten()
            expectedProxy1.wasGotten()
            expectedProxy2.wasGotten()
        }

        capturedProxies[0] sameAs expectedProxy1
        capturedProxies[1] sameAs expectedProxy2
        capturedProxies[2] sameAs expectedProxy1
        capturedProxies[3] sameAs expectedProxy2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 0
        capturedIndices[2] mustBe 1
        capturedIndices[3] mustBe 1
    }

    @Test
    @JsName("fn29")
    fun `Given wasGotten is called it the delegates the call to the Assertions  while respecting the frozen memory model`(): AsyncTestReturnValue {
        // Given
        val expectedProxy = fixture.propertyProxyFixture(freeze = true)

        val assertions = AssertionsStub(
            wasGottenAtIndex = { _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        return runBlockingTest {
            invoke(container) {
                // When
                expectedProxy.wasGotten()
            }
        }
    }

    @Test
    @JsName("fn30")
    fun `Given wasGotten is called it the delegates the call to the Assertions  while respecting the unfrozen memory model`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture(freeze = false)

        val assertions = AssertionsStub(
            wasGottenAtIndex = { _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasGotten()
        }

        expectedProxy.isFrozen mustBe false
    }

    @Test
    @JsName("fn31")
    fun `Given wasSet is called it it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)

        var capturedProxy: Proxy<*, *>? = null
        var capturedIdx: Int? = null

        val assertions = AssertionsStub(
            wasSetAtIndex = { givenProxy, givenIdx ->
                capturedProxy = givenProxy
                capturedIdx = givenIdx
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSet()
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn32")
    fun `Given wasSet is called it the delegates the call to the Assertions while respecting the order`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            wasSetAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSet()
            expectedProxy.wasSet()
        }

        capturedProxies[0] sameAs expectedProxy
        capturedProxies[1] sameAs expectedProxy

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 1
    }

    @Test
    @JsName("fn33")
    fun `Given wasSet is called it delegates the call to the given Assertions while respecting the order and multiple Proxies`() {
        // Given
        val expectedProxy1 = fixture.propertyProxyFixture()
        val expectedProxy2 = fixture.propertyProxyFixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()

        val assertions = AssertionsStub(
            wasSetAtIndex = { givenProxy, givenIdx ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy1.wasSet()
            expectedProxy2.wasSet()
            expectedProxy1.wasSet()
            expectedProxy2.wasSet()
        }

        capturedProxies[0] sameAs expectedProxy1
        capturedProxies[1] sameAs expectedProxy2
        capturedProxies[2] sameAs expectedProxy1
        capturedProxies[3] sameAs expectedProxy2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 0
        capturedIndices[2] mustBe 1
        capturedIndices[3] mustBe 1
    }

    @Test
    @JsName("fn34")
    fun `Given wasSet is called it the delegates the call to the Assertions  while respecting the frozen memory model`(): AsyncTestReturnValue {
        // Given
        val expectedProxy = fixture.propertyProxyFixture(freeze = true)

        val assertions = AssertionsStub(
            wasSetAtIndex = { _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        return runBlockingTest {
            invoke(container) {
                // When
                expectedProxy.wasSet()
            }
        }
    }

    @Test
    @JsName("fn35")
    fun `Given wasSet is called it the delegates the call to the Assertions  while respecting the unfrozen memory model`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture(freeze = false)

        val assertions = AssertionsStub(
            wasSetAtIndex = { _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSet()
        }

        expectedProxy.isFrozen mustBe false
    }

    @Test
    @JsName("fn36")
    fun `Given wasSetTo is called it delegates the call to the assertions`() {
        // Given
        val id: String = fixture.fixture()
        val expectedProxy = fixture.propertyProxyFixture(id = id)
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

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSetTo(expectedValue)
        }

        capturedProxy sameAs expectedProxy
        capturedIdx mustBe 0
        capturedValue sameAs expectedValue
    }

    @Test
    @JsName("fn37")
    fun `Given wasSetTo is called it the delegates the call to the Assertions while respecting the order`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture()

        val expectedValue1: Any? = fixture.fixture()
        val expectedValue2: Any? = fixture.fixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()
        val capturedValues: MutableList<Any?> = mutableListOf()

        val assertions = AssertionsStub(
            wasSetToAtIndex = { givenProxy, givenIdx, argument ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
                capturedValues.add(argument)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSetTo(expectedValue1)
            expectedProxy.wasSetTo(expectedValue2)
        }

        capturedProxies[0] sameAs expectedProxy
        capturedProxies[1] sameAs expectedProxy

        capturedValues[0] sameAs expectedValue1
        capturedValues[1] sameAs expectedValue2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 1
    }

    @Test
    @JsName("fn38")
    fun `Given wasSetTo is called it delegates the call to the given Assertions while respecting the order and multiple Proxies`() {
        // Given
        val expectedProxy1 = fixture.propertyProxyFixture()
        val expectedProxy2 = fixture.propertyProxyFixture()

        val expectedValue1: Any? = fixture.fixture()
        val expectedValue2: Any? = fixture.fixture()

        val capturedProxies: MutableList<Proxy<*, *>> = mutableListOf()
        val capturedIndices: MutableList<Int> = mutableListOf()
        val capturedValues: MutableList<Any?> = mutableListOf()

        val assertions = AssertionsStub(
            wasSetToAtIndex = { givenProxy, givenIdx, argument ->
                capturedProxies.add(givenProxy)
                capturedIndices.add(givenIdx)
                capturedValues.add(argument)
            }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy1.wasSetTo(expectedValue1)
            expectedProxy2.wasSetTo(expectedValue1)
            expectedProxy1.wasSetTo(expectedValue2)
            expectedProxy2.wasSetTo(expectedValue2)
        }

        capturedProxies[0] sameAs expectedProxy1
        capturedProxies[1] sameAs expectedProxy2
        capturedProxies[2] sameAs expectedProxy1
        capturedProxies[3] sameAs expectedProxy2

        capturedIndices[0] mustBe 0
        capturedIndices[1] mustBe 0
        capturedIndices[2] mustBe 1
        capturedIndices[3] mustBe 1
    }

    @Test
    @JsName("fn39")
    fun `Given wasSetTo is called it the delegates the call to the Assertions  while respecting the frozen memory model`(): AsyncTestReturnValue {
        // Given
        val expectedProxy = fixture.propertyProxyFixture(freeze = true)

        val assertions = AssertionsStub(
            wasSetToAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        return runBlockingTest {
            invoke(container) {
                // When
                expectedProxy.wasSetTo(fixture.fixture())
            }
        }
    }

    @Test
    @JsName("fn40")
    fun `Given wasSetTo is called it the delegates the call to the Assertions  while respecting the unfrozen memory model`() {
        // Given
        val expectedProxy = fixture.propertyProxyFixture(freeze = false)

        val assertions = AssertionsStub(
            wasSetToAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSetTo(fixture.fixture())
        }

        expectedProxy.isFrozen mustBe false
    }

    @Test
    @JsName("fn41")
    fun `Given hasNoFurtherInvocations is called it fails if the given Proxy has still coverageble invocations left`() {
        // Given
        val calls = 23
        val expectedProxy = fixture.propertyProxyFixture(calls = calls)

        val container = UnchainedAssertion()

        // Then
        val error = assertFailsWith<AssertionError> {
            invoke(container) {
                // When
                expectedProxy.hasNoFurtherInvocations()
            }
        }

        error.message mustBe "Only 0 of $calls invocations had been asserted."
    }

    @Test
    @JsName("fn42")
    fun `Given hasNoFurtherInvocations is called it accepts if the given Proxy has no coverageble invocations left`() {
        // Given
        val calls = 1
        val expectedProxy = fixture.propertyProxyFixture(calls = calls)

        val assertions = AssertionsStub(
            wasSetToAtIndex = { _, _, _ -> }
        )

        val container = UnchainedAssertion(assertions)

        // Then
        invoke(container) {
            // When
            expectedProxy.wasSetTo(fixture.fixture())
            expectedProxy.hasNoFurtherInvocations()
        }
    }
}
