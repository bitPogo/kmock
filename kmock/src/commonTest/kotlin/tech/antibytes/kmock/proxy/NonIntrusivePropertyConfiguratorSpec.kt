/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class NonIntrusivePropertyConfiguratorSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils NonIntrusiveConfigurator`() {
        NonIntrusivePropertyConfigurator<Any>() fulfils KMockContract.NonIntrusivePropertyConfigurator::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils NonIntrusivePropertyConfigurator`() {
        NonIntrusivePropertyConfigurator<Any>() fulfils KMockContract.NonIntrusivePropertyTarget::class
    }

    @Test
    @JsName("fn2")
    fun `Given unwrapRelaxer is called while nothing else was invoked it returns null`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        val actual = configurator.unwrapRelaxer()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn3")
    fun `Given isRelaxable is called while nothing else was invoked it returns false`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        val actual = configurator.isRelaxable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn4")
    fun `Given unwrapRelaxer is called after useRelaxerIf while its condition is falsum it returns null`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useRelaxerIf(false) { fixture.fixture() }
        val actual = configurator.unwrapRelaxer()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn5")
    fun `Given isRelaxable is called after useRelaxerIf while its condition is falsum it returns false`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useRelaxerIf(false) { fixture.fixture() }
        val actual = configurator.isRelaxable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn6")
    fun `Given unwrapRelaxer is called after useRelaxerIf while its condition is verum it returns a relaxer`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()
        val expected: Int = fixture.fixture()

        // When
        configurator.useRelaxerIf(true) { expected }
        val actual = configurator.unwrapRelaxer()

        // Then
        actual!!.relax(fixture.fixture()) mustBe expected
    }

    @Test
    @JsName("fn7")
    fun `Given isRelaxable is called after useRelaxerIf while its condition is verum it returns true`() {
        // Given
        val configurator = NonIntrusivePropertyConfigurator<Int>()

        // When
        configurator.useRelaxerIf(true) { fixture.fixture() }
        val actual = configurator.isRelaxable()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn8")
    fun `Given unwrapSpy is called it returns null`() {
        // Given
        val spyTarget = NonIntrusivePropertyConfigurator<Unit>()

        // When
        val actual = spyTarget.unwrapSpy()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn9")
    fun `Given unwrapSpy is called after useSpyIf while its target is null it returns null`() {
        // Given
        val spyTarget = NonIntrusivePropertyConfigurator<Boolean>()

        // When
        spyTarget.useSpyIf(null) { fixture.fixture() }
        val actual = spyTarget.unwrapSpy()

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn10")
    fun `Given unwrapSpy is called after useSpyIf while its target is null it returns the SpyTarget`() {
        // Given
        val spyTarget = NonIntrusivePropertyConfigurator<Boolean>()
        val expected: Boolean = fixture.fixture()

        // When
        spyTarget.useSpyIf(Any()) { expected }
        val actual = spyTarget.unwrapSpy()

        // Then
        actual isNot null
        actual!!.invoke() mustBe expected
    }

    @Test
    @JsName("fn11")
    fun `Given isSpyable is called it returns false`() {
        // Given
        val spyTarget = NonIntrusivePropertyConfigurator<Boolean>()

        // When
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn12")
    fun `Given isSpyable is called after useSpyIf while its target is null it returns false`() {
        // Given
        val spyTarget = NonIntrusivePropertyConfigurator<Boolean>()

        // When
        spyTarget.useSpyIf(null) { fixture.fixture() }
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn13")
    fun `Given isSpyable is called after useSpyIf while its target is not null it returns true`() {
        // Given
        val spyTarget = NonIntrusivePropertyConfigurator<Boolean>()

        // When
        spyTarget.useSpyIf(Any()) { fixture.fixture() }
        val actual = spyTarget.isSpyable()

        // Then
        actual mustBe true
    }
}
