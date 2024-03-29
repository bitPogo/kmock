/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification.constraints

import kotlin.js.JsName
import kotlin.test.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class IsSameSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `isSame fulfils MatcherConstraint`() {
        isSame(fixture.fixture()) fulfils KMockContract.ArgumentConstraint::class
    }

    @Test
    @JsName("fn1")
    fun `Given isSame is called it returns false if the given Value and the call Value are not the same`() {
        // Given
        val value: Any = fixture.fixture()

        // When
        val actual = isSame(value).matches(fixture.fixture())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn2")
    fun `Given isSame is called it returns true if the given Value and the call Value are the same`() {
        // Given
        val value: Any? = fixture.fixture()

        // When
        val actual = isSame(value).matches(value)

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn3")
    fun `Given toString is called it returns a String which contains a marker for that Constraint`() {
        // Given
        val value: Any? = fixture.fixture()

        // When
        val actual = isSame(value).toString()

        // Then
        actual mustBe "same($value)"
    }
}
