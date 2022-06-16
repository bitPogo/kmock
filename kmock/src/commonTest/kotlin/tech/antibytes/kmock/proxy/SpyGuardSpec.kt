/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class SpyGuardSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given a null and guardSpy is called with a function it returns null`() {
        // When
        val actual = (null as Any?).guardSpy { fixture.fixture<Any>() }

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn1")
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
