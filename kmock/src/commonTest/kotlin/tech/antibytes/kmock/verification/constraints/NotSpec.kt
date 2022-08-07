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
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class NotSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `any fulfils MatcherConstraint`() {
        any() fulfils KMockContract.ArgumentConstraint::class
    }

    @Test
    @JsName("fn1")
    fun `Given any is called with a Contraint it delegates the given Argument and negates the output of the wrapped match`() {
        // Given
        val output: Boolean = fixture.fixture()
        var capturedArgument: Any? = null
        val constraint = KMockContract.ArgumentConstraint { argument ->
            capturedArgument = argument
            output
        }
        val argument: Any = fixture.fixture()

        // When
        val actual = not(constraint).matches(argument)

        // Then
        actual mustBe !output
        capturedArgument mustBe argument
    }

    @Test
    @JsName("fn2")
    fun `Given toString is called it uses the wrapped value`() {
        // Given
        val nested: String = fixture.fixture()
        val constraint = StubConstraint(nested)

        // When
        val actual = not(constraint).toString()

        // Then
        actual mustBe "not($nested)"
    }

    private class StubConstraint(
        val _toString: String,
    ) : KMockContract.ArgumentConstraint {
        override fun matches(actual: Any?): Boolean {
            TODO()
        }

        override fun toString(): String = _toString
    }
}
