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

class MethodSpyTargetInvocationSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils MethodSpyTargetInvocation`() {
        MethodSpyTargetInvocation<Unit, () -> Unit>() fulfils KMockContract.MethodSpyTargetInvocation::class
    }

    @Test
    @JsName("fn1")
    fun `Given unwrap is called it returns null`() {
        // Given
        val spyTarget = MethodSpyTargetInvocation<Unit, () -> Unit>()

        // When
        val actual = spyTarget.unwrap()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn2")
    fun `Given unwrap is called after useSpyIf while its target is null it returns null`() {
        // Given
        val spyTarget = MethodSpyTargetInvocation<Boolean, () -> Boolean>()

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
        val spyTarget = MethodSpyTargetInvocation<Boolean, () -> Boolean>()
        val expected: Boolean = fixture.fixture()

        // When
        spyTarget.useSpyIf(Any()) { expected }
        val actual = spyTarget.unwrap()

        // Then
        actual isNot null
        actual!!.invoke() mustBe expected
    }

    @Test
    @JsName("fn4")
    fun `Given unwrap is called after useSpyOnEqualsIf while its target is null it returns null`() {
        // Given
        val spyTarget = MethodSpyTargetInvocation<Boolean, () -> Boolean>()

        // When
        spyTarget.useSpyIf(null) { fixture.fixture() }
        val actual = spyTarget.unwrap()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn5")
    fun `Given unwrap is called after useSpyOnEqualIf while its spy is not null it returns the spyTarget which uses the subjectToSpyOn`() {
        // Given
        val spyTarget = MethodSpyTargetInvocation<Boolean, () -> Boolean>()
        val expected: Boolean = fixture.fixture()
        var spyWasCalled = false
        val subjectToSpyOn = MockOfMocks(
            equals = {
                spyWasCalled = true
                expected
            }
        )

        // When
        spyTarget.useSpyOnEqualsIf(
            subjectToSpyOn,
            Any(),
            { fixture.fixture() },
            MockOfMocks::class
        )
        val actual = spyTarget.unwrap()

        // Then
        actual isNot null
        actual!!.invoke() mustBe expected
        spyWasCalled mustBe true
    }

    @Test
    @JsName("fn6")
    fun `Given unwrap is called after useSpyOnEqualIf while its spy is not null it returns a configuration with spyOn which uses its parent when invoked with another mock of the same Class`() {
        // Given
        val spyTarget = MethodSpyTargetInvocation<Boolean, () -> Boolean>()
        val subjectToSpyOn: Any = fixture.fixture()
        val expected: Boolean = fixture.fixture()
        var parentWasCalled = false
        val parent: Any = MockOfMocks(
            equals = {
                parentWasCalled = true
                expected
            }
        )
        val otherMock = MockOfMocks { !expected }

        // When
        spyTarget.useSpyOnEqualsIf(
            subjectToSpyOn,
            otherMock,
            parent::equals,
            MockOfMocks::class
        )
        val actual = spyTarget.unwrap()

        // Then
        actual isNot null
        actual?.invoke() mustBe expected
        parentWasCalled mustBe true
    }

    @Test
    @JsName("fn7")
    fun `Given isSpyable is called it returns false`() {
        // Given
        val spyTarget = PropertySpyTargetInvocation<Boolean>()

        // When
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn8")
    fun `Given isSpyable is called after useSpyIf while its target is null it returns false`() {
        // Given
        val spyTarget = PropertySpyTargetInvocation<Boolean>()

        // When
        spyTarget.useSpyIf(null) { fixture.fixture() }
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn9")
    fun `Given isSpyable is called after useSpyIf while its target is not null it returns true`() {
        // Given
        val spyTarget = PropertySpyTargetInvocation<Boolean>()

        // When
        spyTarget.useSpyIf(Any()) { fixture.fixture() }
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn10")
    fun `Given isSpyable is called after useSpyOnEqualsIf while it uses the given target it returns true`() {
        // Given
        val spyTarget = MethodSpyTargetInvocation<Boolean, () -> Boolean>()
        val subjectToSpyOn = MockOfMocks(
            equals = { fixture.fixture() }
        )

        // When
        spyTarget.useSpyOnEqualsIf(
            subjectToSpyOn,
            Any(),
            { fixture.fixture() },
            MockOfMocks::class
        )
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn11")
    fun `Given isSpyable is called after useSpyIf while it uses the given parent it returns true`() {
        // Given
        val spyTarget = MethodSpyTargetInvocation<Boolean, () -> Boolean>()
        val subjectToSpyOn = MockOfMocks(
            equals = { fixture.fixture() }
        )
        val parent: Any = MockOfMocks(
            equals = { fixture.fixture() }
        )
        val otherMock = MockOfMocks { fixture.fixture() }

        // When
        spyTarget.useSpyOnEqualsIf(
            subjectToSpyOn,
            otherMock,
            parent::equals,
            MockOfMocks::class
        )
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe true
    }

    private class MockOfMocks(
        @JsName("fn0")
        val equals: (() -> Boolean)? = null,
    ) {
        override fun equals(other: Any?): Boolean {
            return equals?.invoke()
                ?: throw RuntimeException("Missing SideEffect equals.")
        }

        override fun hashCode(): Int {
            return equals?.hashCode() ?: 0
        }
    }
}
