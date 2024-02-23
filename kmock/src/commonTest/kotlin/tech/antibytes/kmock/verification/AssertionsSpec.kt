/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.kmock.fixture.propertyProxyFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class AssertionsSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils Assertions`() {
        Assertions fulfils KMockContract.Assertions::class
    }

    @Test
    @JsName("fn2")
    fun `Given hasBeenCalledAtIndex is called it fails if the invocations are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        proxy.getArgumentsForCall = { throw NullPointerException() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected 1th call of $id was not made."
    }

    @Test
    @JsName("fn3")
    fun `Given hasBeenCalledAtIndex is called it accepts if the invocations are not exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        proxy.getArgumentsForCall = { arrayOf() }

        // When
        Assertions.hasBeenCalledAtIndex(proxy, 0)
    }

    @Test
    @JsName("fn4")
    fun `Given hasBeenCalledWithVoidAtIndex is called it fails if the invocations are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        proxy.getArgumentsForCall = { throw NullPointerException() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledWithVoidAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected 1th call of $id was not made."
    }

    @Test
    @JsName("fn5")
    fun `Given hasBeenCalledWithVoidAtIndex is called it fails if the proxy contains values`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        proxy.getArgumentsForCall = { fixture.listFixture<Any>(size = 2).toTypedArray() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledWithVoidAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected $id to be void, but the invocation contains Arguments."
    }

    @Test
    @JsName("fn6")
    fun `Given hasBeenCalledWithVoidAtIndex is called it accepts if the proxy contains no values`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)
        proxy.getArgumentsForCall = { arrayOf() }

        // When
        Assertions.hasBeenCalledWithVoidAtIndex(proxy, 0)
    }

    @Test
    @JsName("fn7")
    fun `Given hasBeenCalledWithAtIndex is called it fails if the invocations are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        proxy.getArgumentsForCall = { throw NullPointerException() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledWithAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected 1th call of $id was not made."
    }

    @Test
    @JsName("fn8")
    fun `Given hasBeenCalledWithAtIndex is called in a Chain it fails if the invocation does not match the expected values`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()
        val actualValue: Any = fixture.fixture()
        var capturedIdx: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIdx = givenIdx

            arrayOf(actualValue)
        }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledWithAtIndex(proxy, 0, expectedValue)
        }

        actual.message mustBe "Expected <$expectedValue> got actual <$actualValue>."
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn9")
    fun `Given hasBeenCalledWithAtIndex is called it fails if the invocation is out of scope`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()

        proxy.getArgumentsForCall = { arrayOf() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledWithAtIndex(proxy, 0, expectedValue)
        }

        actual.message mustBe "The given matcher $expectedValue has not been found."
    }

    @Test
    @JsName("fn10")
    fun `Given hasBeenCalledWithAtIndex is called it accepts if the invocation if the expected and actual values match without beeing in linear order`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()

        proxy.getArgumentsForCall = { arrayOf(fixture.fixture(), expectedValue) }

        // When
        Assertions.hasBeenCalledWithAtIndex(proxy, 0, expectedValue)
    }

    @Test
    @JsName("fn11")
    fun `Given hasBeenStrictlyCalledWithAtIndex is called it fails if the invocations are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        proxy.getArgumentsForCall = { throw NullPointerException() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenStrictlyCalledWithAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected 1th call of $id was not made."
    }

    @Test
    @JsName("fn12")
    fun `Given hasBeenStrictlyCalledWithAtIndex is called in a Chain it fails if the invocation does not match the expected values`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()
        val actualValue: Any = fixture.fixture()
        var capturedIdx: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIdx = givenIdx

            arrayOf(actualValue)
        }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenStrictlyCalledWithAtIndex(proxy, 0, expectedValue)
        }

        actual.message mustBe "Expected <$expectedValue> got actual <$actualValue>."
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn13")
    fun `Given hasBeenStrictlyCalledWithAtIndex is called it fails if the invocation is out of scope`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()

        proxy.getArgumentsForCall = { arrayOf() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenStrictlyCalledWithAtIndex(proxy, 0, expectedValue)
        }

        actual.message mustBe "Expected <1> arguments got actual <0>."
    }

    @Test
    @JsName("fn14")
    fun `Given hasBeenStrictlyCalledWithAtIndex is called it accepts if the invocation if the expected and actual values match`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()

        proxy.getArgumentsForCall = { arrayOf(expectedValue) }

        // When
        Assertions.hasBeenStrictlyCalledWithAtIndex(proxy, 0, expectedValue)
    }

    @Test
    @JsName("fn15")
    fun `Given hasBeenCalledWithoutAtIndex is called it fails if the invocations are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        proxy.getArgumentsForCall = { throw NullPointerException() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledWithoutAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected 1th call of $id was not made."
    }

    @Test
    @JsName("fn16")
    fun `Given hasBeenCalledWithoutAtIndex is called it fails if the invocation match`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()
        var capturedIdx: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIdx = givenIdx
            arrayOf(expectedValue)
        }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledWithoutAtIndex(proxy, 0, expectedValue)
        }

        actual.message mustBe "Illegal value <$expectedValue> detected."
        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn17")
    fun `Given hasBeenCalledWithoutAtIndex is called it fails if the invocation is out of scope`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()

        proxy.getArgumentsForCall = { arrayOf(expectedValue) }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.hasBeenCalledWithoutAtIndex(proxy, 0, expectedValue)
        }

        actual.message mustBe "Illegal value <$expectedValue> detected."
    }

    @Test
    @JsName("fn18")
    fun `Given hasBeenCalledWithoutAtIndex is called it fails if the invocation if the expected and actual values do not match`() {
        val id: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = id)

        val expectedValue: String = fixture.fixture()
        val actualValue: Any = fixture.fixture()
        var capturedIdx: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIdx = givenIdx

            arrayOf(actualValue)
        }

        // When
        Assertions.hasBeenCalledWithoutAtIndex(proxy, 0, expectedValue)

        capturedIdx mustBe 0
    }

    @Test
    @JsName("fn19")
    fun `Given wasGottenAtIndex is called it fails if the invocations are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)

        proxy.getArgumentsForCall = { throw NullPointerException() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.wasGottenAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected 1th call of $id was not made."
    }

    @Test
    @JsName("fn20")
    fun `Given wasGottenAtIndex it fails if the invocation was not a getter`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        var capturedIndex: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Set(null)
        }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.wasGottenAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected a getter and got a setter."
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn21")
    fun `Given wasGottenAtIndex it accepts if the invocation was a getter`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        var capturedIndex: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Get
        }

        // When
        Assertions.wasGottenAtIndex(proxy, 0)

        // Then
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn22")
    fun `Given wasSetAtIndex is called it fails if the invocations are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)

        proxy.getArgumentsForCall = { throw NullPointerException() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.wasSetAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected 1th call of $id was not made."
    }

    @Test
    @JsName("fn23")
    fun `Given wasSetAtIndex it fails if the invocation was not a getter`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        var capturedIndex: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Get
        }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.wasSetAtIndex(proxy, 0)
        }

        actual.message mustBe "Expected a setter and got a getter."
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn24")
    fun `Given wasSetAtIndex it accepts if the invocation was a setter`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        var capturedIndex: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Set(null)
        }

        // When
        Assertions.wasSetAtIndex(proxy, 0)

        // Then
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn25")
    fun `Given wasSetToAtIndex is called it fails if the invocations are exhausted`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)

        proxy.getArgumentsForCall = { throw NullPointerException() }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.wasSetToAtIndex(proxy, 0, fixture.fixture())
        }

        actual.message mustBe "Expected 1th call of $id was not made."
    }

    @Test
    @JsName("fn26")
    fun `Given wasSetToAtIndex it fails if the invocation was not a setter`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        var capturedIndex: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Get
        }

        // Then
        val actual = assertFailsWith<AssertionError> {
            // When
            Assertions.wasSetToAtIndex(proxy, 0, fixture.fixture())
        }

        actual.message mustBe "Expected a setter and got a getter."
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn27")
    fun `Given wasSetToAtIndex it fails if the invocation does not match the expected value`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val expectedValue: Any = fixture.fixture()
        val actualValue: Any? = fixture.fixture()
        var capturedIndex: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Set(actualValue)
        }

        // Then
        val actual = assertFailsWith<AssertionError> {
            Assertions.wasSetToAtIndex(proxy, 0, expectedValue)
        }

        actual.message mustBe "Expected <$expectedValue> got actual <$actualValue>."
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn28")
    fun `Given wasSetToAtIndex it accepts if the invocation does match the expected value`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = fixture.propertyProxyFixture(id = id)
        val expectedValue: Any = fixture.fixture()
        var capturedIndex: Int? = null

        proxy.getArgumentsForCall = { givenIdx ->
            capturedIndex = givenIdx

            KMockContract.GetOrSet.Set(expectedValue)
        }

        // When
        Assertions.wasSetToAtIndex(proxy, 0, expectedValue)

        // Then
        capturedIndex mustBe 0
    }
}
