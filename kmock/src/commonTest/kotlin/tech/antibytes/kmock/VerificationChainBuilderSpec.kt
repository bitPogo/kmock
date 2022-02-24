/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import VerificationChainBuilderStub
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
    fun `It fulfils VerificationChainBuilder`() {
        VerificationChainBuilder() fulfils KMockContract.VerificationChainBuilder::class
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
    fun `Given ensureVerification it hooks in itself into a Mockery`() {
        // Given
        val mock = FunMockeryStub(fixture.fixture(), fixture.fixture())

        // When
        val container = VerificationChainBuilder()

        container.ensureVerificationOf(mock)

        // Then
        mock.verificationBuilderReference mustBe container
    }

    @Test
    @JsName("fn3")
    fun `Given ensureVerification will not overwrite exiting hocks`() {
        // Given
        val builder = VerificationChainBuilderStub(mutableListOf())
        val mock = FunMockeryStub(
            fixture.fixture(),
            fixture.fixture(),
            verificationBuilderReference = builder
        )

        // When
        val container = VerificationChainBuilder()

        container.ensureVerificationOf(mock)

        // Then
        mock.verificationBuilderReference mustBe builder
    }

    @Test
    @JsName("fn3")
    fun `Given cleanEnsuredMocks it cleans itself from a Mock`() {
        // Given
        val mock = FunMockeryStub(
            fixture.fixture(),
            fixture.fixture(),
        )

        // When
        val container = VerificationChainBuilder()

        container.ensureVerificationOf(mock)
        container.cleanEnsuredMocks()

        // Then
        mock.verificationBuilderReference mustBe null
    }
}
