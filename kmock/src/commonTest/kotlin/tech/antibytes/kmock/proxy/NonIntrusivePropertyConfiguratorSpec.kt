/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.RelaxationPropertyConfiguration
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class NonIntrusivePropertyConfiguratorSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0a")
    fun `It fulfils NonIntrusiveConfigurator`() {
        NonIntrusivePropertyConfigurator<Any>() fulfils KMockContract.RelaxationConfigurator::class
    }

    @Test
    @JsName("fn0")
    fun `It fulfils NonIntrusivePropertyConfigurator`() {
        NonIntrusivePropertyConfigurator<Any>() fulfils KMockContract.RelaxationPropertyConfigurator::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils NonIntrusiveConfigurationReceiver`() {
        NonIntrusivePropertyConfigurator<Any>() fulfils KMockContract.RelaxationConfigurationExtractor::class
    }

    @Test
    @JsName("fn2")
    fun `Given getConfiguration is called while nothing else was invoked it returns the default configuration`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe RelaxationPropertyConfiguration(
            relaxer = null,
        )
    }

    @Test
    @JsName("fn3")
    fun `Given getConfiguration is called after useRelaxerIf while its condition is falsum it returns a configuration without the relaxer`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useRelaxerIf(false) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe RelaxationPropertyConfiguration(
            relaxer = null,
        )
    }

    @Test
    @JsName("fn4")
    fun `Given getConfiguration is called after useRelaxerIf while its condition is verum it returns a configuration with the relaxer`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()
        val expected: Int = fixture.fixture()

        // When
        configurator.useRelaxerIf(true) { expected }
        val actual = configurator.getConfiguration()

        // Then
        actual.relaxer isNot null
        actual.relaxer!!.relax(fixture.fixture()) mustBe expected
    }

    @Test
    @JsName("fn5")
    fun `Given getConfiguration is called after useRelaxerIf while its condition falsum after a verum it returns a configuration without the relaxer`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useRelaxerIf(true) { fixture.fixture() }
        configurator.useRelaxerIf(false) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe RelaxationPropertyConfiguration(
            relaxer = null,
        )
    }
}
