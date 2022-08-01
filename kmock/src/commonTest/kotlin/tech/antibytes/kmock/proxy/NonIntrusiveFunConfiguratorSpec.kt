/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import kotlin.js.JsName
import kotlin.test.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe

class NonIntrusiveFunConfiguratorSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils NonIntrusiveFunConfigurator`() {
        NonIntrusiveFunConfigurator<Unit, () -> Unit>() fulfils KMockContract.NonIntrusiveFunConfigurator::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils NonIntrusivePropertyTarget`() {
        NonIntrusiveFunConfigurator<Unit, () -> Unit>() fulfils KMockContract.NonIntrusiveFunTarget::class
    }

    @Test
    @JsName("fn2")
    fun `Given unwrapRelaxer is called while nothing else was invoked it returns null`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        val actual = configurator.unwrapRelaxer()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn3")
    fun `Given isRelaxable is called while nothing else was invoked it returns false`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        val actual = configurator.isRelaxable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn4")
    fun `Given unwrapRelaxer is called after relaxUnitFunIf while its condition is falsum it returns null`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useUnitFunRelaxerIf(false)
        val actual = configurator.unwrapRelaxer()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn5")
    fun `Given isRelaxable is called after relaxUnitFunIf while its condition is falsum it returns false`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useUnitFunRelaxerIf(false)
        val actual = configurator.isRelaxable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn6")
    fun `Given unwrapRelaxer is called after relaxUnitFunIf while its condition is verum it returns the unitFunRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useUnitFunRelaxerIf(true)
        val actual = configurator.unwrapRelaxer()

        // Then
        actual isNot null
        actual!!.relax("") mustBe Unit
    }

    @Test
    @JsName("fn7")
    fun `Given isRelaxable is called after relaxUnitFunIf while its condition is verum it returns true`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useUnitFunRelaxerIf(true)
        val actual = configurator.isRelaxable()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn8")
    fun `Given unwrapRelaxer is called after useRelaxerIf while its condition is falsum it returns null`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Any, () -> Any>()

        // When
        configurator.useRelaxerIf(false) { fixture.fixture() }
        val actual = configurator.unwrapRelaxer()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn9")
    fun `Given isRelaxable is called after useRelaxerIf while its condition is falsum it returns false`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useRelaxerIf(false) { fixture.fixture() }
        val actual = configurator.isRelaxable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn10")
    fun `Given unwrapRelaxer is called after useRelaxerIf while its condition is true it returns the relaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Any, () -> Any>()
        val expected: Any = fixture.fixture()

        // When
        configurator.useRelaxerIf(true) { expected }
        val actual = configurator.unwrapRelaxer()

        // Then
        actual!!.relax(fixture.fixture()) mustBe expected
    }

    @Test
    @JsName("fn11")
    fun `Given isRelaxable is called after useRelaxerIf while its condition is true it returns true`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Any, () -> Any>()
        val expected: Any = fixture.fixture()

        // When
        configurator.useRelaxerIf(true) { expected }
        val actual = configurator.isRelaxable()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn12")
    fun `Given unwrapSpy is called it returns null`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        val actual = spyTarget.unwrapSpy()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn13")
    fun `Given unwrapSpy is called after useSpyIf while its target is null it returns null`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()

        // When
        spyTarget.useSpyIf(null) { fixture.fixture() }
        val actual = spyTarget.unwrapSpy()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn14")
    fun `Given unwrapSpy is called after useSpyIf while its target is null it returns the SpyTarget`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()
        val expected: Boolean = fixture.fixture()

        // When
        spyTarget.useSpyIf(Any()) { expected }
        val actual = spyTarget.unwrapSpy()

        // Then
        actual isNot null
        actual!!.invoke() mustBe expected
    }

    @Test
    @JsName("fn15")
    fun `Given unwrapSpy is called after useSpyOnEqualsIf while its target is null it returns null`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()

        // When
        spyTarget.useSpyIf(null) { fixture.fixture() }
        val actual = spyTarget.unwrapSpy()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn16")
    fun `Given unwrapSpy is called after useSpyOnEqualIf while its spy is not null it returns the spyTarget which uses the subjectToSpyOn`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()
        val expected: Boolean = fixture.fixture()
        var spyWasCalled = false
        val subjectToSpyOn = MockOfMocks(
            equals = {
                spyWasCalled = true
                expected
            },
        )

        // When
        spyTarget.useSpyOnEqualsIf(
            subjectToSpyOn,
            Any(),
            { fixture.fixture() },
            MockOfMocks::class,
        )
        val actual = spyTarget.unwrapSpy()

        // Then
        actual isNot null
        actual!!.invoke() mustBe expected
        spyWasCalled mustBe true
    }

    @Test
    @JsName("fn17")
    fun `Given unwrap is called after useSpyOnEqualIf while its spy is not null it returns a configuration with spyOn which uses its parent when invoked with another mock of the same Class`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()
        val subjectToSpyOn: Any = fixture.fixture()
        val expected: Boolean = fixture.fixture()
        var parentWasCalled = false
        val parent: Any = MockOfMocks(
            equals = {
                parentWasCalled = true
                expected
            },
        )
        val otherMock = MockOfMocks { !expected }

        // When
        spyTarget.useSpyOnEqualsIf(
            subjectToSpyOn,
            otherMock,
            parent::equals,
            MockOfMocks::class,
        )
        val actual = spyTarget.unwrapSpy()

        // Then
        actual isNot null
        actual?.invoke() mustBe expected
        parentWasCalled mustBe true
    }

    @Test
    @JsName("fn18")
    fun `Given isSpyable is called it returns false`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()

        // When
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn19")
    fun `Given isSpyable is called after useSpyIf while its target is null it returns false`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()

        // When
        spyTarget.useSpyIf(null) { fixture.fixture() }
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn20")
    fun `Given isSpyable is called after useSpyIf while its target is not null it returns true`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()

        // When
        spyTarget.useSpyIf(Any()) { fixture.fixture() }
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn21")
    fun `Given isSpyable is called after useSpyOnEqualsIf while it uses the given target it returns true`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()
        val subjectToSpyOn = MockOfMocks(
            equals = { fixture.fixture() },
        )

        // When
        spyTarget.useSpyOnEqualsIf(
            subjectToSpyOn,
            Any(),
            { fixture.fixture() },
            MockOfMocks::class,
        )
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn22")
    fun `Given isSpyable is called after useSpyIf while it uses the given parent it returns true`() {
        // Given
        val spyTarget = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()
        val subjectToSpyOn = MockOfMocks(
            equals = { fixture.fixture() },
        )
        val parent: Any = MockOfMocks(
            equals = { fixture.fixture() },
        )
        val otherMock = MockOfMocks { fixture.fixture() }

        // When
        spyTarget.useSpyOnEqualsIf(
            subjectToSpyOn,
            otherMock,
            parent::equals,
            MockOfMocks::class,
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
