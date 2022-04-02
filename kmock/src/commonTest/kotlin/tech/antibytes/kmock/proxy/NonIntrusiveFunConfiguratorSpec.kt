/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.NonIntrusiveFunConfiguration
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class NonIntrusiveFunConfiguratorSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0a")
    fun `It fulfils NonIntrusiveConfigurator`() {
        NonIntrusiveFunConfigurator<Unit, () -> Unit>() fulfils KMockContract.NonIntrusiveConfigurator::class
    }

    @Test
    @JsName("fn0")
    fun `It fulfils NonIntrusiveFunConfigurator`() {
        NonIntrusiveFunConfigurator<Unit, () -> Unit>() fulfils KMockContract.NonIntrusiveFunConfigurator::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils NonIntrusiveConfigurationReceiver`() {
        NonIntrusiveFunConfigurator<Unit, () -> Unit>() fulfils KMockContract.NonIntrusiveConfigurationExtractor::class
    }

    @Test
    @JsName("fn2")
    fun `Given getConfiguration is called while nothing else was invoked it returns the default configuration`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusiveFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
            spyOn = null
        )
    }

    @Test
    @JsName("fn3")
    fun `Given getConfiguration is called after relaxUnitFunIf while its condition is falsum it returns a configuration without the unitFunRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useUnitFunRelaxerIf(false)
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusiveFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
            spyOn = null
        )
    }

    @Test
    @JsName("fn4")
    fun `Given getConfiguration is called after relaxUnitFunIf while its condition is verum it returns a configuration with the unitFunRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useUnitFunRelaxerIf(true)
        val actual = configurator.getConfiguration()

        // Then
        actual.unitFunRelaxer isNot null
        actual.unitFunRelaxer!!.relax("") mustBe Unit
    }

    @Test
    @JsName("fn5")
    fun `Given getConfiguration is called after relaxUnitFunIf while its condition falsum after a verum it returns a configuration without unitFunRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useUnitFunRelaxerIf(true)
        configurator.useUnitFunRelaxerIf(false)
        val actual = configurator.getConfiguration()

        // Then
        actual.unitFunRelaxer mustBe null
    }

    @Test
    @JsName("fn6")
    fun `Given getConfiguration is called after useToStringRelaxer is called with a subject it returns a configuration with a buildInRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<String, () -> String>()

        // When
        val subject: Any = fixture.fixture()

        configurator.useToStringRelaxer(subject::toString)
        val actual = configurator.getConfiguration()

        // Then
        actual.buildInRelaxer isNot null
        actual.buildInRelaxer?.invoke(null) mustBe subject.toString()
    }

    @Test
    @JsName("fn7")
    fun `Given getConfiguration is called after useHashCodeRelaxer is called with a subject it returns a configuration with a buildInRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Int, () -> Int>()

        // When
        val subject: Any = fixture.fixture()

        configurator.useHashCodeRelaxer(subject::hashCode)
        val actual = configurator.getConfiguration()

        // Then
        actual.buildInRelaxer isNot null
        actual.buildInRelaxer?.invoke(null) mustBe subject.hashCode()
    }

    @Test
    @JsName("fn8")
    fun `Given getConfiguration is called after useEqualsRelaxer is called with a subject it returns a configuration with a buildInRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Boolean, () -> Boolean>()

        // When
        val subject: Any = fixture.fixture()

        configurator.useEqualsRelaxer(subject::equals)
        val actual = configurator.getConfiguration()

        // Then
        actual.buildInRelaxer isNot null
        actual.buildInRelaxer?.invoke(subject) mustBe true
        actual.buildInRelaxer?.invoke(fixture.fixture()) mustBe false
    }

    @Test
    @JsName("fn9")
    fun `Given getConfiguration is called after useRelaxerIf while its condition is falsum it returns a configuration without the relaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Int, () -> Int>()

        // When
        configurator.useRelaxerIf(false) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusiveFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
            spyOn = null
        )
    }

    @Test
    @JsName("fn10")
    fun `Given getConfiguration is called after useRelaxerIf while its condition is verum it returns a configuration without the relaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Int, () -> Int>()
        val expected: Int = fixture.fixture()

        // When
        configurator.useRelaxerIf(true) { expected }
        val actual = configurator.getConfiguration()

        // Then
        actual.relaxer isNot null
        actual.relaxer!!.relax(fixture.fixture()) mustBe expected
    }

    @Test
    @JsName("fn11")
    fun `Given getConfiguration is called after useRelaxerIf while its condition falsum after a verum it returns a configuration without the relaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        configurator.useRelaxerIf(true) { fixture.fixture() }
        configurator.useRelaxerIf(false) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual.relaxer mustBe null
    }

    @Test
    @JsName("fn12")
    fun `Given getConfiguration is called after useSpyOnEqualIf while its spy is null it returns a configuration without spyOn`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Boolean, (Any?) -> Boolean>()

        // When
        configurator.useSpyOnEqualsIf(
            null,
            { fixture.fixture() },
            MockOfMocks::class
        )
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusiveFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
            spyOn = null
        )
    }

    @Test
    @JsName("fn13")
    fun `Given getConfiguration is called after useSpyOnEqualIf while its spy is not null it returns a configuration with spyOn which uses the subjectToSpyOn`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Boolean, (Any?) -> Boolean>()
        val expected: Boolean = fixture.fixture()
        var spyWasCalled = false
        val subjectToSpyOn = MockOfMocks(
            equals = {
                spyWasCalled = true
                expected
            }
        )

        // When
        configurator.useSpyOnEqualsIf(
            subjectToSpyOn,
            { fixture.fixture() },
            MockOfMocks::class
        )
        val actual = configurator.getConfiguration()

        // Then
        actual.spyOn isNot null
        actual.spyOn?.invoke(fixture.fixture()) mustBe expected
        spyWasCalled mustBe true
    }

    @Test
    @JsName("fn14")
    fun `Given getConfiguration is called after useSpyOnEqualIf while its spy is not null it returns a configuration with spyOn which uses its parent when invoked with another mock of the same Class`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Boolean, (Any?) -> Boolean>()
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
        configurator.useSpyOnEqualsIf(
            subjectToSpyOn,
            parent::equals,
            MockOfMocks::class
        )
        val actual = configurator.getConfiguration()

        // Then
        actual.spyOn isNot null
        actual.spyOn?.invoke(otherMock) mustBe expected
        parentWasCalled mustBe true
    }

    @Test
    @JsName("fn15")
    fun `Given getConfiguration is called after useSpyIf while its spy is null it returns a configuration without spyOn`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Boolean, (Any?) -> Boolean>()

        // When
        configurator.useSpyIf(null) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusiveFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
            spyOn = null
        )
    }

    @Test
    @JsName("fn16")
    fun `Given getConfiguration is called after useSpyIf while its spy is not null it returns a configuration with spyOn`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Any, (Any) -> Any>()
        var capturedValue: Any? = null
        val expected: Any = fixture.fixture()
        val expectedValue: Any = fixture.fixture()
        val subjectToSpyOn = MockOfMocks(
            methodToSpiedOn = { payload ->
                capturedValue = payload
                expected
            }
        )

        // When
        configurator.useSpyIf(Any()) { payload: Any -> subjectToSpyOn.methodToSpiedOn(payload) }
        val actual = configurator.getConfiguration().spyOn!!.invoke(expectedValue)

        // Then
        actual mustBe expected
        capturedValue mustBe expectedValue
    }

    @Test
    @JsName("fn17")
    fun `Given getConfiguration is called after useSpyIf while its spy is null after it was not it returns a configuration without spyOn`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Boolean, (Any?) -> Boolean>()

        // When
        configurator.useSpyIf(Any()) { fixture.fixture() }
        configurator.useSpyIf(null) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusiveFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
            spyOn = null
        )
    }

    @Test
    @JsName("fn18")
    fun `Given a Relaxer and UnitFunRelaxer is set the UnitFunRelaxer wipes the Relaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, (Any) -> Unit>()

        // When
        configurator.useUnitFunRelaxerIf(true)
        configurator.useRelaxerIf(true) { fixture.fixture() }

        val actual = configurator.getConfiguration()

        // Then
        actual.unitFunRelaxer isNot null
        actual.relaxer mustBe null
    }

    @Test
    @JsName("fn19")
    fun `Given a Relaxer and BuildInRelaxer is set the BuildInRelaxer wipes the Relaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Boolean, (Any?) -> Boolean>()

        // When
        configurator.useEqualsRelaxer { fixture.fixture() }
        configurator.useRelaxerIf(true) { fixture.fixture() }

        val actual = configurator.getConfiguration()

        // Then
        actual.buildInRelaxer isNot null
        actual.relaxer mustBe null
    }

    @Test
    @JsName("fn20")
    fun `Given a Relaxer and Spy is set the Spy wipes the Relaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Any, (Any) -> Any>()

        // When
        configurator.useRelaxerIf(fixture.fixture()) { fixture.fixture() }
        configurator.useSpyIf(fixture.fixture<Any>()) { fixture.fixture() }

        val actual = configurator.getConfiguration()

        // Then
        actual.spyOn isNot null
        actual.relaxer mustBe null
    }

    @Test
    @JsName("fn21")
    fun `Given a UnitFunRelaxer and Spy is set the Spy wipes the UnitFunRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Any, (Any) -> Any>()

        // When
        configurator.useUnitFunRelaxerIf(true)
        configurator.useSpyIf(fixture.fixture<Any>()) { fixture.fixture() }

        val actual = configurator.getConfiguration()

        // Then
        actual.spyOn isNot null
        actual.unitFunRelaxer mustBe null
    }

    @Test
    @JsName("fn22")
    fun `Given a BuildInRelaxer and Spy is set the Spy wipes the BuildInRelaxer`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Boolean, (Any?) -> Boolean>()

        // When
        configurator.useEqualsRelaxer { fixture.fixture() }
        configurator.useSpyIf(fixture.fixture<Any>()) { fixture.fixture() }

        val actual = configurator.getConfiguration()

        // Then
        actual.buildInRelaxer isNot null
        actual.unitFunRelaxer mustBe null
    }

    private class MockOfMocks(
        @JsName("fn0")
        val equals: (() -> Boolean)? = null,
        val methodToSpiedOn: ((Any) -> Any)? = null
    ) {
        override fun equals(other: Any?): Boolean {
            return equals?.invoke()
                ?: throw RuntimeException("Missing SideEffect equals.")
        }

        override fun hashCode(): Int = TODO()

        fun methodToSpiedOn(parameter: Any): Any {
            return methodToSpiedOn?.invoke(parameter)
                ?: throw RuntimeException("Missing SideEffect methodToSpiedOn.")
        }
    }
}