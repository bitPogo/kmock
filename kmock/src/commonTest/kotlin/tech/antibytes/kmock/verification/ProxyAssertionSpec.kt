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
    fun `Given assertHadBeenCalled fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 5)

        Proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertHasBeenCalled(exactly = 1)
        }
    }

    @Test
    @JsName("fn1")
    fun `Given assertHadBeenCalled accepts if the amount of calls match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { values }

        // When
        Proxy.assertHasBeenCalled(exactly = 1)
    }

    @Test
    @JsName("fn2")
    fun `Given assertHadBeenCalledWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertHasBeenCalledWith(
                exactly = 1,
                arguments = fixture.listFixture<String>().toTypedArray()
            )
        }
    }

    @Test
    @JsName("fn3")
    fun `Given assertHadBeenCalledWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { values }
        // When
        Proxy.assertHasBeenCalledWith(
            exactly = 1,
            values[0]
        )
    }

    @Test
    @JsName("fn4")
    fun `Given assertHadBeenCalledStrictlyWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertHasBeenCalledStrictlyWith(
                exactly = 1,
                values[0]
            )
        }
    }

    @Test
    @JsName("fn5")
    fun `Given assertHadBeenCalledStrictlyWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { values }

        // When
        Proxy.assertHasBeenCalledStrictlyWith(
            exactly = 1,
            arguments = values
        )
    }

    @Test
    @JsName("fn6")
    fun `Given assertHadNotBeenCalled fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertHasNotBeenCalled()
        }
    }

    @Test
    @JsName("fn7")
    fun `Given assertHadNotBeenCalled accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 0)

        Proxy.getArgumentsForCall = { values }

        // When
        Proxy.assertHasNotBeenCalled()
    }

    @Test
    @JsName("fn8")
    fun `Given assertHadNotBeenCalledWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertHasNotBeenCalledWith(values[0])
        }
    }

    @Test
    @JsName("fn9")
    fun `Given assertHadNotBeenCalledWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { values }

        // When
        Proxy.assertHasNotBeenCalledWith(fixture.fixture())
    }

    @Test
    @JsName("fn10")
    fun `Given assertHadBeenCalledWithout fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 2)

        Proxy.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertHadBeenCalledWithout(values[0])
        }
    }

    @Test
    @JsName("fn11")
    fun `Given assertHadBeenCalledWithout accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val Proxy = FunProxyStub(fixture.fixture(), 2)

        Proxy.getArgumentsForCall = { values }

        // When
        Proxy.assertHadBeenCalledWithout(fixture.fixture())
    }

    @Test
    @JsName("fn12")
    fun `Given assertWasGotten fails if the amount of calls does not match the criteria`() {
        // Given
        val Proxy = PropertyProxyStub(fixture.fixture(), 2)

        Proxy.getArgumentsForCall = { KMockContract.GetOrSet.Get }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertWasGotten(1)
        }
    }

    @Test
    @JsName("fn13")
    fun `Given assertWasGotten accepts if the amount of calls does match the criteria`() {
        // Given
        val Proxy = PropertyProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { KMockContract.GetOrSet.Get }

        // When
        Proxy.assertWasGotten(1)
    }

    @Test
    @JsName("fn14")
    fun `Given assertWasSet fails if the amount of calls does not match the criteria`() {
        // Given
        val Proxy = PropertyProxyStub(fixture.fixture(), 2)

        Proxy.getArgumentsForCall = { KMockContract.GetOrSet.Set(null) }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertWasSet(1)
        }
    }

    @Test
    @JsName("fn15")
    fun `Given assertWasSet accepts if the amount of calls does match the criteria`() {
        // Given
        val Proxy = PropertyProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { KMockContract.GetOrSet.Set(null) }

        // When
        Proxy.assertWasSet(1)
    }

    @Test
    @JsName("fn16")
    fun `Given assertWasSetTo fails if the amount of calls does not match the criteria`() {
        // Given
        val Proxy = PropertyProxyStub(fixture.fixture(), 2)

        Proxy.getArgumentsForCall = { KMockContract.GetOrSet.Set(fixture.fixture<Any>()) }

        // Then
        assertFailsWith<AssertionError> {
            // When
            Proxy.assertWasSetTo(1, fixture.fixture<Any>())
        }
    }

    @Test
    @JsName("fn17")
    fun `Given assertWasSetTo accepts if the amount of calls does match the criteria`() {
        // Given
        val value: Any = fixture.fixture()
        val Proxy = PropertyProxyStub(fixture.fixture(), 1)

        Proxy.getArgumentsForCall = { KMockContract.GetOrSet.Set(value) }

        // When
        Proxy.assertWasSetTo(1, value)
    }
}
