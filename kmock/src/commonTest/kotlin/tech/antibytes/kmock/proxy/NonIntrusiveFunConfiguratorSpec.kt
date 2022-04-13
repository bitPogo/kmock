/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.RelaxationFunConfiguration
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
        NonIntrusiveFunConfigurator<Unit, () -> Unit>() fulfils KMockContract.RelaxationConfigurator::class
    }

    @Test
    @JsName("fn0")
    fun `It fulfils NonIntrusiveFunConfigurator`() {
        NonIntrusiveFunConfigurator<Unit, () -> Unit>() fulfils KMockContract.RelaxationFunConfigurator::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils NonIntrusiveConfigurationReceiver`() {
        NonIntrusiveFunConfigurator<Unit, () -> Unit>() fulfils KMockContract.RelaxationConfigurationExtractor::class
    }

    @Test
    @JsName("fn2")
    fun `Given getConfiguration is called while nothing else was invoked it returns the default configuration`() {
        // Given
        val configurator = NonIntrusiveFunConfigurator<Unit, () -> Unit>()

        // When
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe RelaxationFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
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
        actual mustBe RelaxationFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
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
        actual mustBe RelaxationFunConfiguration(
            unitFunRelaxer = null,
            buildInRelaxer = null,
            relaxer = null,
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
    @JsName("fn13")
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
}
