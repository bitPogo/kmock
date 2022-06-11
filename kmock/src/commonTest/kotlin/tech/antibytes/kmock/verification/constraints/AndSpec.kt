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
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AndSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `or fulfils MatcherConstraint`() {
        or(fixture.fixture()) fulfils KMockContract.ArgumentConstraint::class
    }

    @Test
    @JsName("fn1")
    fun `Given and is initialized without any values it fails`() {
        // Then
        val error = assertFailsWith<IllegalArgumentException> {
            // When
            and()
        }

        error.message mustBe "and should not be empty!"
    }

    @Test
    @JsName("fn2")
    fun `Given and is initialized wit any values and matches is called it returns false if actual is not equal to an expected values`() {
        // Given
        val values = fixture.listFixture<Any>(size = 2).toTypedArray()
        val constraint = and(*values)

        // When
        val actual = constraint.matches(fixture.fixture())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn3")
    fun `Given and is initialized wit any values and matches is called it returns true if actual is equal to all expected values`() {
        // Given
        val values = fixture.listFixture<Any>(size = 1).toTypedArray()
        val constraint = and(*values)

        // When
        val actual = constraint.matches(values[0])

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn4")
    fun `Given and is initialized wit any constraints and matches is called it returns false if actual is not matching to an expected constraint`() {
        // Given
        val values = arrayOf(
            MockConstraint(false),
            MockConstraint(false)
        )
        val constraint = and(*values)

        // When
        val actual = constraint.matches(fixture.fixture())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn5")
    fun `Given and is initialized wit any constraints and matches is called it returns true if actual matches to all expected constraint`() {
        // Given
        val values = arrayOf(
            MockConstraint(true),
            MockConstraint(true)
        )
        val constraint = and(*values)

        // When
        val actual = constraint.matches(fixture.fixture())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn6")
    fun `Given toString is called it returns a custom identifier string`() {
        // Given
        val id1: String = fixture.fixture()
        val id2: String = fixture.fixture()
        val values = arrayOf(
            MockConstraint(false, id1),
            MockConstraint(true, id2)
        )
        val constraint = and(*values)

        // When
        val actual = constraint.toString()

        // Then
        actual mustBe "and[$id1, $id2]"
    }

    private class MockConstraint(
        val matches: Boolean,
        val _toString: String = ""
    ) : KMockContract.ArgumentConstraint {
        override fun matches(actual: Any?): Boolean = matches
        override fun toString(): String = _toString
    }
}
