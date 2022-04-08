/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class PropertySpyTargetInvocationSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils PropertySpyTargetInvocation`() {
        PropertySpyTargetInvocation<Unit>() fulfils KMockContract.PropertySpyTargetInvocation::class
    }

    @Test
    @JsName("fn1")
    fun `Given unwrap is called it returns null`() {
        // Given
        val spyTarget = PropertySpyTargetInvocation<Unit>()

        // When
        val actual = spyTarget.unwrap()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn2")
    fun `Given unwrap is called after useSpyIf while its target is null it returns null`() {
        // Given
        val spyTarget = PropertySpyTargetInvocation<Boolean>()

        // When
        spyTarget.useSpyIf(null) { fixture.fixture() }
        val actual = spyTarget.unwrap()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn3")
    fun `Given unwrap is called after useSpyIf while its target is null it returns the SpyTarget`() {
        // Given
        val spyTarget = PropertySpyTargetInvocation<Boolean>()
        val expected: Boolean = fixture.fixture()

        // When
        spyTarget.useSpyIf(Any()) { expected }
        val actual = spyTarget.unwrap()

        // Then
        actual isNot null
        actual!!.invoke() mustBe expected
    }
}
