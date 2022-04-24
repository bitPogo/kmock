/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.test.Test

class ArgumentConstraintWrapperSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils ArgumentConstraintWrapper`() {
        ArgumentConstraintWrapper fulfils KMockContract.ArgumentConstraintWrapper::class
    }

    @Test
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
    fun `Given wrapValue is called it ignores Constraints`() {
        // Given
        val value = KMockContract.ArgumentConstraint { true }

        // When
        val actual = ArgumentConstraintWrapper.wrapValue(value)

        // Then
        actual sameAs value
    }

    @Test
    fun `Given wrapValues is called it wraps a arbitrary values with a eq-Constraint`() {
        // Given
        val values: List<Any> = fixture.listFixture(size = 3)

        // When
        val actual = ArgumentConstraintWrapper.wrapValues(values.toTypedArray())

        // Then
        actual.forEachIndexed { idx, wrapped ->
            wrapped fulfils eq::class
            wrapped.matches(values[idx]) mustBe true
        }
    }

    @Test
    fun `Given wrapValues is called it ignores Constraints`() {
        // Given
        val values = listOf(
            KMockContract.ArgumentConstraint { true },
            KMockContract.ArgumentConstraint { true },
            KMockContract.ArgumentConstraint { false }
        )

        // When
        val actual = ArgumentConstraintWrapper.wrapValues(values.toTypedArray())

        // Then
        actual.forEachIndexed { idx, wrapped ->
            wrapped sameAs values[idx]
        }
    }

    @Test
    fun `Given wrapNegatedValues is called it wraps a arbitrary values with a eq-Constraint, while enclosing it with a not`() {
        // Given
        val values: List<Any> = fixture.listFixture(size = 3)

        // When
        val actual = ArgumentConstraintWrapper.wrapNegatedValues(values.toTypedArray())

        // Then
        actual.forEachIndexed { idx, wrapped ->
            wrapped fulfils not::class
            wrapped.matches(values[idx]) mustBe false
        }
    }

    @Test
    fun `Given wrapNegatedValues is called it ignores Constraints, while enclosing it with a not`() {
        // Given
        val values = listOf(
            KMockContract.ArgumentConstraint { true },
            KMockContract.ArgumentConstraint { true },
            KMockContract.ArgumentConstraint { false }
        )

        // When
        val actual = ArgumentConstraintWrapper.wrapNegatedValues(values.toTypedArray())

        // Then
        actual.forEachIndexed { idx, wrapped ->
            wrapped fulfils not::class
            wrapped.matches(Unit) mustBe !values[idx].matches(Unit)
        }
    }
}
