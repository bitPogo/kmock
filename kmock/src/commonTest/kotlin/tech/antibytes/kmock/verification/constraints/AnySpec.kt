/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class AnySpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `any fulfils MatcherConstraint`() {
        any() fulfils KMockContract.VerificationConstraint::class
    }

    @Test
    @JsName("fn1")
    fun `Given any is called it returns true regardless of the value`() {
        // When
        val actual = any().matches(fixture.fixture())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn2")
    fun `Given any is called it returns false if the given KClass and the call KClass are not matching`() {
        // Given
        val value = true

        // When
        val actual = any(Any::class).matches(value)

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn3")
    fun `Given any is called it returns false if the given KClass and the call was null`() {
        // When
        val actual = any(Any::class).matches(null)

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn4")
    fun `Given any is called it returns true if the given KClass and the call KClass are matching`() {
        // Given
        val value = true

        // When
        val actual = any(Boolean::class).matches(value)

        // Then
        actual mustBe true
    }
}
