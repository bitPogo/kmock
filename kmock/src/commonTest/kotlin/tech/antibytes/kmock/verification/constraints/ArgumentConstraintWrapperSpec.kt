/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test

class ArgumentConstraintWrapperSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils ArgumentConstraintWrapper`() {
        ArgumentConstraintWrapper fulfils KMockContract.ArgumentConstraintWrapper::class
    }

    @Test
    @JsName("fn1")
    fun `Given wrapValue is called it wraps a arbitrary value with a eq-Constraint`() {
        // Given
        val value: Any = fixture.fixture()

        // When
        val actual = ArgumentConstraintWrapper.wrapValue(value)

        // Then
        actual fulfils eq::class
        actual.matches(value) mustBe true
    }

    @Test
    @JsName("fn2")
    fun `Given wrapValue is called it ignores Constraints`() {
        // Given
        val value = KMockContract.ArgumentConstraint { true }

        // When
        val actual = ArgumentConstraintWrapper.wrapValue(value)

        // Then
        actual sameAs value
    }

    @Test
    @JsName("fn3")
    fun `Given wrapNegatedValue is called it wraps a arbitrary values with a eq-Constraint while enclosing it with a not`() {
        // Given
        val value: Any = fixture.fixture()

        // When
        val actual = ArgumentConstraintWrapper.wrapNegatedValue(value)

        // Then
        actual fulfils not::class
        actual.matches(value) mustBe false
    }

    @Test
    @JsName("fn4")
    fun `Given wrapNegatedValue is called it ignores Constraints while enclosing it with a not`() {
        // Given
        val value = KMockContract.ArgumentConstraint { true }

        // When
        val actual = ArgumentConstraintWrapper.wrapNegatedValue(value)

        // Then
        actual fulfils not::class
        actual.matches(Unit) mustBe !value.matches(Unit)
    }
}
