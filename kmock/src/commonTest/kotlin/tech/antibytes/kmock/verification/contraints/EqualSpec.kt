/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification.contraints

import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class EqualSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `eq fulfils MatcherConstraint`() {
        eq(fixture.fixture()) fulfils KMockContract.MatcherConstraint::class
    }

    @Test
    @JsName("fn1")
    fun `Given eq is called it returns false if the given Value and the call Value are not the matching`() {
        // Given
        val value: Int = fixture.fixture()

        // When
        val actual = eq(value).matches(fixture.fixture<Int>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn2")
    fun `Given eq is called it returns true if the given Value and the call Value are the same`() {
        // Given
        val value: Int = fixture.fixture()

        // When
        val actual = eq(value).matches(value)

        // Then
        actual mustBe true
    }
}
