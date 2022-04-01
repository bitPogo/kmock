/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.NonIntrusivePropertyConfiguration
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
        NonIntrusivePropertyConfigurator<Any>() fulfils KMockContract.NonIntrusiveConfigurator::class
    }

    @Test
    @JsName("fn0")
    fun `It fulfils NonIntrusivePropertyConfigurator`() {
        NonIntrusivePropertyConfigurator<Any>() fulfils KMockContract.NonIntrusivePropertyConfigurator::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils NonIntrusiveConfigurationReceiver`() {
        NonIntrusivePropertyConfigurator<Any>() fulfils KMockContract.NonIntrusiveConfigurationReceiver::class
    }

    @Test
    @JsName("fn2")
    fun `Given getConfiguration is called while nothing else was invoked it returns the default configuration`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusivePropertyConfiguration(
            relaxer = null,
            spyOnGet = null,
            spyOnSet = null,
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
        actual mustBe NonIntrusivePropertyConfiguration(
            relaxer = null,
            spyOnGet = null,
            spyOnSet = null,
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
        actual mustBe NonIntrusivePropertyConfiguration(
            relaxer = null,
            spyOnGet = null,
            spyOnSet = null,
        )
    }

    @Test
    @JsName("fn6")
    fun `Given getConfiguration is called after useSpyOnGetIf while its spy is null it returns a configuration without spyOnGet`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useSpyOnGetIf(null) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusivePropertyConfiguration(
            relaxer = null,
            spyOnGet = null,
            spyOnSet = null,
        )
    }

    @Test
    @JsName("fn7")
    fun `Given getConfiguration is called after useSpyOnGetIf while its spy is not null it returns a configuration with spyOnGet`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()
        val expected: Int = fixture.fixture()
        val subjectToSpyOn = MockOfMocks(
            getterSpiedOn = { expected }
        )

        // When
        configurator.useSpyOnGetIf(Any(), subjectToSpyOn::property::get)
        val actual = configurator.getConfiguration().spyOnGet!!.invoke()

        // Then
        actual mustBe expected
    }

    @Test
    @JsName("fn8")
    fun `Given getConfiguration is called after useSpyOnGetIf while its spy is null after it was not it returns a configuration without spyOnGet`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useSpyOnGetIf(Any()) { fixture.fixture() }
        configurator.useSpyOnGetIf(null) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusivePropertyConfiguration(
            relaxer = null,
            spyOnGet = null,
            spyOnSet = null,
        )
    }

    @Test
    @JsName("fn9")
    fun `Given getConfiguration is called after useSpyOnSetIf while its spy is null it returns a configuration without spyOnSet`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useSpyOnSetIf(null) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusivePropertyConfiguration(
            relaxer = null,
            spyOnGet = null,
            spyOnSet = null,
        )
    }

    @Test
    @JsName("fn10")
    fun `Given getConfiguration is called after useSpyOnSetIf while its spy is not null it returns a configuration with spyOnSet`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()
        val expected: Int = fixture.fixture()
        var actual: Int? = null
        val subjectToSpyOn = MockOfMocks(
            setterSpiedOn = { givenValue ->
                actual = givenValue
            }
        )

        // When
        configurator.useSpyOnSetIf(Any(), subjectToSpyOn::property::set)
        configurator.getConfiguration().spyOnSet!!.invoke(expected)

        // Then
        actual mustBe expected
    }

    @Test
    @JsName("fn11")
    fun `Given getConfiguration is called after useSpyOnSetIf while its spy is null after it was not it returns a configuration without spyOnSet`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useSpyOnSetIf(Any()) { fixture.fixture() }
        configurator.useSpyOnSetIf(null) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual mustBe NonIntrusivePropertyConfiguration(
            relaxer = null,
            spyOnGet = null,
            spyOnSet = null,
        )
    }

    @Test
    @JsName("fn12")
    fun `Given a Relaxer and SpyOnGet is set the SpyOnGet wipes the Relaxer`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useRelaxerIf(true) { fixture.fixture() }
        configurator.useSpyOnGetIf(Any()) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual.relaxer mustBe null
        actual.spyOnGet isNot null
    }

    @Test
    @JsName("fn14")
    fun `Given a Relaxer and SpyOnSet is set the SpyOnGet wipes the Relaxer`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useRelaxerIf(true) { fixture.fixture() }
        configurator.useSpyOnSetIf(Any()) { fixture.fixture() }
        val actual = configurator.getConfiguration()

        // Then
        actual.relaxer mustBe null
        actual.spyOnSet isNot null
    }

    private class MockOfMocks(
        val getterSpiedOn: (() -> Int)? = null,
        val setterSpiedOn: ((Int) -> Unit)? = null
    ) {
        var property: Int
            get() = getterSpiedOn?.invoke() ?: throw RuntimeException("Missing SideEffect getterSpiedOn.")
            set(value) = setterSpiedOn?.invoke(value) ?: throw RuntimeException("Missing SideEffect setterSpiedOn.")
    }
}
