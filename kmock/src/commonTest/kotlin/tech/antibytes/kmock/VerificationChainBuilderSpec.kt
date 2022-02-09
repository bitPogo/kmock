/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.mock.FunMockeryStub
import tech.antibytes.util.test.fixture.PublicApi
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class VerificationChainBuilderSpec {
    private val fixture = kotlinFixture()

    private fun PublicApi.Fixture.handleFixtures(size: Int): List<KMockContract.VerificationHandle> {
        val fixtures = mutableListOf<KMockContract.VerificationHandle>()

        for (x in 0 until size) {
            fixtures.add(
                VerificationHandle(
                    fixture.fixture(),
                    fixture.listFixture()
                )
            )
        }

        return fixtures
    }

    @Test
    @JsName("fn0")
    fun `It fulfils HandleContainer`() {
        VerificationChainBuilder() fulfils KMockContract.VerificationHandleContainer::class
    }

    @Test
    @JsName("fn1")
    fun `Given add and toList is called it adds the given Handles and return them as List`() {
        // Given
        val size = 5
        val handles = fixture.handleFixtures(size)

        // When
        val container = VerificationChainBuilder()
        handles.forEach { handle ->
            container.add(handle)
        }

        val actual = container.toList()

        // Then
        actual mustBe handles
    }

    @Test
    @JsName("fn2")
    fun `Given withArguments is called it uses the extension of FunMockery`() {
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
        val container = VerificationChainBuilder()
        container.withArguments(mock, *(values.sorted()).toTypedArray())

        // Then
        val actual = container.toList()[0]
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn3")
    fun `Given withSameArguments is called it uses the extension of FunMockery`() {
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
        val container = VerificationChainBuilder()
        container.withSameArguments(mock, *values)

        // Then
        val actual = container.toList()[0]
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn4")
    fun `Given withoutArguments is called it uses the extension of FunMockery`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val container = VerificationChainBuilder()
        container.withoutArguments(mock, fixture.fixture<String>())

        // Then
        val actual = container.toList()[0]
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }
}
