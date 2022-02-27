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

class IsNotSameSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `isNotSame fulfils MatcherConstraint`() {
        isNotSame(fixture.fixture()) fulfils KMockContract.MatcherConstraint::class
    }

    @Test
    @JsName("fn1")
    fun `Given isNotSame is called it returns false if the given Value and the call Value are not the same`() {
        // Given
        val value: Any = fixture.fixture()

        // When
        val actual = isNotSame(value).matches(fixture.fixture())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn2")
    fun `Given isNotSame is called it returns true if the given Value and the call Value are the same`() {
        // Given
        val value: Any? = fixture.fixture()

        // When
        val actual = isNotSame(value).matches(value)

        // Then
        actual mustBe false
    }
}
