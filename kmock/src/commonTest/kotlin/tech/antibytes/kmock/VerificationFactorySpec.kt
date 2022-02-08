/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.mock.FunMockeryStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class VerificationFactorySpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given withArguments is called with a FunMockery, it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 0)

        // When
        val actual = mock.withArguments()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
    }

    @Test
    @JsName("fn1")
    fun `Given withArguments is called with a FunMockery, it returns a VerficationHandle which contains no matches if nothing matches, while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = mock.withArguments(fixture.fixture<String>())

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn2")
    fun `Given withArguments is called with a FunMockery, it returns a VerficationHandle which contains matches if something matches, while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)
        val values = fixture.listFixture<String>().toTypedArray()

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = mock.withArguments(*(values.sorted()).toTypedArray())

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn4")
    fun `Given withSameArguments is called with a FunMockery, it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 0)

        // When
        val actual = mock.withSameArguments()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
    }

    @Test
    @JsName("fn5")
    fun `Given withSameArguments is called with a FunMockery, it returns a VerficationHandle which contains no matches if nothing matches, while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = mock.withSameArguments(fixture.fixture<String>())

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn6")
    fun `Given withSameArguments is called with a FunMockery, it returns a VerficationHandle which contains matches if something matches, while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)
        val values = fixture.listFixture<String>().toTypedArray()

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = mock.withSameArguments(*values)

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn7")
    fun `Given withoutArguments is called with a FunMockery, it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 0)

        // When
        val actual = mock.withoutArguments()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
    }

    @Test
    @JsName("fn8")
    fun `Given withoutArguments is called with a FunMockery, it returns a VerficationHandle which contains no matches if nothing matches, while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)
        val values = fixture.listFixture<String>().toTypedArray()

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = mock.withoutArguments(*values)

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn9")
    fun `Given withoutArguments is called with a FunMockery, it returns a VerficationHandle which contains matches if something matches, while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = mock.withoutArguments(fixture.fixture<String>())

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }
}
