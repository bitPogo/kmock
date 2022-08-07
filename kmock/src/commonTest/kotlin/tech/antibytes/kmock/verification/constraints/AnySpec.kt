/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification.constraints

import kotlin.js.JsName
import kotlin.test.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.annotations.IgnoreJs
import tech.antibytes.util.test.annotations.JsOnly
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class AnySpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `any fulfils MatcherConstraint`() {
        any() fulfils KMockContract.ArgumentConstraint::class
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

    @Test
    @JsName("fn5")
    fun `Given toString is called and no Klass was specified it returns a AnyMatcher String`() {
        // When
        val actual = any().toString()

        // Then
        actual mustBe "(Any value)"
    }

    @Test
    @IgnoreJs
    @JsName("fn6")
    fun `Given toString is called and a Klass was specified it returns a AnyMatcher String for the KClass non Js`() {
        // When
        val actual = any(Boolean::class).toString()

        // Then
        actual mustBe "(Any value of kotlin.Boolean)"
    }

    @Test
    @JsOnly
    @JsName("fn7")
    fun `Given toString is called and a Klass was specified it returns a AnyMatcher String for the KClass Js`() {
        // When
        val actual = any(Boolean::class).toString()

        // Then
        actual mustBe "(Any value of ${Boolean::class})"
    }
}
