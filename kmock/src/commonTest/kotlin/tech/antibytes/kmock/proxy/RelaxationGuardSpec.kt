/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class RelaxationGuardSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given a negative condtion and guardRelaxer is called with a function it returns null`() {
        // When
        val actual = false.guardRelaxer { fixture.fixture<Any>() }

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn1")
    fun `Given a positive condtion and guardRelaxer is called with a function it returns a Relaxer with the given function`() {
        // Given
        val expected: Any = fixture.fixture()

        // When
        val actual = true.guardRelaxer { expected }

        // Then
        actual!! fulfils Relaxer::class
        actual.relax("") mustBe expected
    }

    @Test
    @JsName("fn2")
    fun `Given a null and guardSpy is called with a function it returns null`() {
        // When
        val actual = (null as Any?).guardSpy { fixture.fixture<Any>() }

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn3")
    fun `Given Anything and guardSpy is called with a function it returns the given SideEffect`() {
        // Given
        val expected: Any = fixture.fixture()

        // When
        val actual = Any().guardSpy { expected }

        // Then
        actual isNot null
        actual!!.invoke() mustBe expected
    }
}
