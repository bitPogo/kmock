/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.mock.FunMockeryStub
import tech.antibytes.mock.PropertyMockeryStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MockeryAssertionSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given assertWasCalled fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 5)

        mockery.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasCalled(exactly = 1)
        }
    }

    @Test
    @JsName("fn1")
    fun `Given assertWasCalled accepts if the amount of calls match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { values }

        // When
        mockery.assertWasCalled(exactly = 1)
    }

    @Test
    @JsName("fn2")
    fun `Given assertWasCalledWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasCalledWith(
                exactly = 1,
                arguments = fixture.listFixture<String>().toTypedArray()
            )
        }
    }

    @Test
    @JsName("fn3")
    fun `Given assertWasCalledWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { values }
        // When
        mockery.assertWasCalledWith(
            exactly = 1,
            values[0]
        )
    }

    @Test
    @JsName("fn4")
    fun `Given assertWasCalledStrictlyWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasCalledStrictlyWith(
                exactly = 1,
                values[0]
            )
        }
    }

    @Test
    @JsName("fn5")
    fun `Given assertWasCalledStrictlyWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { values }

        // When
        mockery.assertWasCalledStrictlyWith(
            exactly = 1,
            arguments = values
        )
    }

    @Test
    @JsName("fn6")
    fun `Given assertWasNotCalled fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasNotCalled()
        }
    }

    @Test
    @JsName("fn7")
    fun `Given assertWasNotCalled accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 0)

        mockery.getArgumentsForCall = { values }

        // When
        mockery.assertWasNotCalled()
    }

    @Test
    @JsName("fn8")
    fun `Given assertWasNotCalledWith fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasNotCalledWith(values[0])
        }
    }

    @Test
    @JsName("fn9")
    fun `Given assertWasNotCalledWith accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { values }

        // When
        mockery.assertWasNotCalledWith(fixture.fixture())
    }

    @Test
    @JsName("fn10")
    fun `Given assertWasCalledWithout fails if the amount of calls does not match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 2)

        mockery.getArgumentsForCall = { values }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasCalledWithout(values[0])
        }
    }

    @Test
    @JsName("fn11")
    fun `Given assertWasCalledWithout accepts if the amount of calls does match the criteria`() {
        // Given
        val values = fixture.listFixture<String>(size = 2).toTypedArray()
        val mockery = FunMockeryStub(fixture.fixture(), 2)

        mockery.getArgumentsForCall = { values }

        // When
        mockery.assertWasCalledWithout(fixture.fixture())
    }

    @Test
    @JsName("fn12")
    fun `Given assertWasGotten fails if the amount of calls does not match the criteria`() {
        // Given
        val mockery = PropertyMockeryStub(fixture.fixture(), 2)

        mockery.getArgumentsForCall = { KMockContract.GetOrSet.Get }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasGotten(1)
        }
    }

    @Test
    @JsName("fn13")
    fun `Given assertWasGotten accepts if the amount of calls does match the criteria`() {
        // Given
        val mockery = PropertyMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { KMockContract.GetOrSet.Get }

        // When
        mockery.assertWasGotten(1)
    }

    @Test
    @JsName("fn14")
    fun `Given assertWasSet fails if the amount of calls does not match the criteria`() {
        // Given
        val mockery = PropertyMockeryStub(fixture.fixture(), 2)

        mockery.getArgumentsForCall = { KMockContract.GetOrSet.Set(null) }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasSet(1)
        }
    }

    @Test
    @JsName("fn15")
    fun `Given assertWasSet accepts if the amount of calls does match the criteria`() {
        // Given
        val mockery = PropertyMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { KMockContract.GetOrSet.Set(null) }

        // When
        mockery.assertWasSet(1)
    }

    @Test
    @JsName("fn16")
    fun `Given assertWasSetTo fails if the amount of calls does not match the criteria`() {
        // Given
        val mockery = PropertyMockeryStub(fixture.fixture(), 2)

        mockery.getArgumentsForCall = { KMockContract.GetOrSet.Set(fixture.fixture<Any>()) }

        // Then
        assertFailsWith<AssertionError> {
            // When
            mockery.assertWasSetTo(1, fixture.fixture<Any>())
        }
    }

    @Test
    @JsName("fn17")
    fun `Given assertWasSetTo accepts if the amount of calls does match the criteria`() {
        // Given
        val value: Any = fixture.fixture()
        val mockery = PropertyMockeryStub(fixture.fixture(), 1)

        mockery.getArgumentsForCall = { KMockContract.GetOrSet.Set(value) }

        // When
        mockery.assertWasSetTo(1, value)
    }
}
