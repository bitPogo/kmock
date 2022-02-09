/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

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
    @JsName("fn0")
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
}
