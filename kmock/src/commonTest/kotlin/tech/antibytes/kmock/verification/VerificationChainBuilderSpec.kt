/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.mock.FunMockeryStub
import tech.antibytes.util.test.fixture.PublicApi
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

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
    @JsName("fn0a")
    fun `It fulfils VerificationReferenceBuilder`() {
        VerificationChainBuilder() fulfils KMockContract.VerificationReferenceBuilder::class
    }

    @Test
    @JsName("fn0b")
    fun `It fulfils VerificationReferenceCleaner`() {
        VerificationChainBuilder() fulfils KMockContract.VerificationReferenceCleaner::class
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
    fun `Given ensureVerification it fails if the given mock is not part of it`() {
        // Given
        val mock = FunMockeryStub(fixture.fixture(), fixture.fixture())

        // When
        val container = VerificationChainBuilder()

        val error = assertFailsWith<IllegalStateException> {
            container.ensureVerificationOf(mock)
        }

        // Then
        error.message mustBe "The given mock ${mock.id} is not part of this VerificationChain."
    }
}
