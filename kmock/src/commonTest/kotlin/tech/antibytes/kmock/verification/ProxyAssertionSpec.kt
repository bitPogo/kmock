/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.mock.FunProxyStub
import tech.antibytes.mock.PropertyProxyStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ProxyAssertionSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given assertHasBeenCalled fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 5)

        proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertHasBeenCalled(exactly = 1)
        }
    }

    @Test
    @JsName("fn1")
    fun `Given assertHasBeenCalled accepts if the amount of calls match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { values }

        // When
        proxy.assertHasBeenCalled(exactly = 1)
    }

    @Test
    @JsName("fn2")
    fun `Given assertHasBeenCalledWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertHasBeenCalledWith(
                exactly = 1,
                arguments = fixture.listFixture<String>().toTypedArray()
            )
        }
    }

    @Test
    @JsName("fn3")
    fun `Given assertHasBeenCalledWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { values }
        // When
        proxy.assertHasBeenCalledWith(
            exactly = 1,
            values[0]
        )
    }

    @Test
    @JsName("fn4")
    fun `Given assertHasBeenCalledStrictlyWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertHasBeenCalledStrictlyWith(
                exactly = 1,
                values[0]
            )
        }
    }

    @Test
    @JsName("fn5")
    fun `Given assertHasBeenCalledStrictlyWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { values }

        // When
        proxy.assertHasBeenCalledStrictlyWith(
            exactly = 1,
            arguments = values
        )
    }

    @Test
    @JsName("fn6")
    fun `Given assertHadNotBeenCalled fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertHasNotBeenCalled()
        }
    }

    @Test
    @JsName("fn7")
    fun `Given assertHadNotBeenCalled accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 0)

        proxy.getArgumentsForCall = { values }

        // When
        proxy.assertHasNotBeenCalled()
    }

    @Test
    @JsName("fn8")
    fun `Given assertHadNotBeenCalledWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertHasBeenCalledStrictlyWithout(values[0])
        }
    }

    @Test
    @JsName("fn9")
    fun `Given assertHadNotBeenCalledWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { values }

        // When
        proxy.assertHasBeenCalledStrictlyWithout(fixture.fixture())
    }

    @Test
    @JsName("fn10")
    fun `Given assertHasBeenCalledWithout fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 2)

        proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertHasBeenCalledWithout(values[0])
        }
    }

    @Test
    @JsName("fn11")
    fun `Given assertHasBeenCalledWithout accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val proxy = FunProxyStub(fixture.fixture(), 2)

        proxy.getArgumentsForCall = { values }

        // When
        proxy.assertHasBeenCalledWithout(fixture.fixture())
    }

    @Test
    @JsName("fn12")
    fun `Given assertWasGotten fails if the amount of calls does not match the criteria`() {
        // Given
        val proxy = PropertyProxyStub(fixture.fixture(), 2)

        proxy.getArgumentsForCall = { KMockContract.GetOrSet.Get }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertWasGotten(1)
        }
    }

    @Test
    @JsName("fn13")
    fun `Given assertWasGotten accepts if the amount of calls does match the criteria`() {
        // Given
        val proxy = PropertyProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { KMockContract.GetOrSet.Get }

        // When
        proxy.assertWasGotten(1)
    }

    @Test
    @JsName("fn14")
    fun `Given assertWasSet fails if the amount of calls does not match the criteria`() {
        // Given
        val proxy = PropertyProxyStub(fixture.fixture(), 2)

        proxy.getArgumentsForCall = { KMockContract.GetOrSet.Set(null) }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertWasSet(1)
        }
    }

    @Test
    @JsName("fn15")
    fun `Given assertWasSet accepts if the amount of calls does match the criteria`() {
        // Given
        val proxy = PropertyProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { KMockContract.GetOrSet.Set(null) }

        // When
        proxy.assertWasSet(1)
    }

    @Test
    @JsName("fn16")
    fun `Given assertWasSetTo fails if the amount of calls does not match the criteria`() {
        // Given
        val proxy = PropertyProxyStub(fixture.fixture(), 2)

        proxy.getArgumentsForCall = { KMockContract.GetOrSet.Set(fixture.fixture<Any>()) }

        // Then
        assertFailsWith<AssertionError> {
            // When
            proxy.assertWasSetTo(1, fixture.fixture<Any>())
        }
    }

    @Test
    @JsName("fn17")
    fun `Given assertWasSetTo accepts if the amount of calls does match the criteria`() {
        // Given
        val value: Any = fixture.fixture()
        val proxy = PropertyProxyStub(fixture.fixture(), 1)

        proxy.getArgumentsForCall = { KMockContract.GetOrSet.Set(value) }

        // When
        proxy.assertWasSetTo(1, value)
    }
}
