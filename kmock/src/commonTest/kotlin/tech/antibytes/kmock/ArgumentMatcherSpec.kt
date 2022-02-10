/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class ArgumentMatcherSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given withArguments is called with an Argument it returns false if the Array contains not the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.withArguments(fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn1")
    fun `Given withArguments is called with an Argument it returns false if the Array contains the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.withArguments(array.first())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn2")
    fun `Given withArguments is called without Arguments void it returns true if the Array contains Arguments`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.withArguments()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn3")
    fun `Given withArguments is called without Arguments void it returns true if the Array is null`() {
        // Given
        val array = null

        // When
        val actual = array.withArguments()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn4")
    fun `Given withArguments is called with Arguments it returns false if the Array is null`() {
        // Given
        val array = null

        // When
        val actual = array.withArguments(fixture.listFixture<String>(), fixture.listFixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn5")
    fun `Given withArguments is called with Arguments it returns false if the Array contains not all of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.withArguments(array[0], array[1], array[2], fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn6")
    fun `Given withArguments is called with Arguments it returns true if the Array contains all of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.withArguments(array[0], array[1], array[2], array[3])

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn7")
    fun `Given withSameArguments is called with an Argument it returns false if the Array matches not the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.withSameArguments(fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn8")
    fun `Given withSameArguments is called with an Argument it returns true if the Array matches the given Argument`() {
        // Given
        val array = fixture.listFixture<String>(size = 1).toTypedArray()

        // When
        val actual = array.withSameArguments(array.first())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn9")
    fun `Given withSameArguments is called without Arguments void it returns false if the Array has Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 1).toTypedArray()

        // When
        val actual = array.withSameArguments()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn10")
    fun `Given withSameArguments is called witg Arguments void it returns false if the Array is null`() {
        // Given
        val array = null

        // When
        val actual = array.withSameArguments(fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn11")
    fun `Given withSameArguments is called without Arguments void it returns false if the Array is null`() {
        // Given
        val array = null

        // When
        val actual = array.withSameArguments()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn12")
    fun `Given withSameArguments is called with Arguments it returns false if the Array matches not exactly the given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.withSameArguments(array[0], array[1], array[2], array[3])

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn13")
    fun `Given withSameArguments is called with Arguments it returns true if the Array matches exactly the given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.withSameArguments(*array)

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn14")
    fun `Given withoutArgument is called with an Argument it returns true if the Array contains not the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.withoutArguments(fixture.fixture<String>())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn15")
    fun `Given withoutArgument is called with an Argument it returns false if the Array contains the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.withoutArguments(array.first())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn16")
    fun `Given withoutArgument is called without Argument it returns true if the Array contains Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.withoutArguments()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn17")
    fun `Given withoutArgument is called with Arguments it returns false if the Array is null`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.withoutArguments()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn18")
    fun `Given withoutArgument is called with Arguments it returns true if the Array none of given Arguments matches`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual =
            array.withoutArguments(fixture.fixture<String>(), fixture.fixture<String>(), fixture.fixture<String>())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn19")
    fun `Given withoutArgument is called with Arguments it returns false if the Array matches at least one of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.withoutArguments(
            array[0],
            fixture.fixture<String>(),
            fixture.fixture<String>(),
            fixture.fixture<String>()
        )

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn20")
    fun `Given wasGotten is called it returns false if it is attacht to Set`() {
        KMockContract.GetOrSet.Set(null).wasGotten() mustBe false
    }

    @Test
    @JsName("fn21")
    fun `Given wasGotten is called it returns true if it is attacht to Get`() {
        KMockContract.GetOrSet.Get().wasGotten() mustBe true
    }

    @Test
    @JsName("fn22")
    fun `Given wasSet is called it returns false if it is attacht to Get`() {
        KMockContract.GetOrSet.Get().wasSet() mustBe false
    }

    @Test
    @JsName("fn23")
    fun `Given wasGotten is called it returns true if it is attacht to Set`() {
        KMockContract.GetOrSet.Set(null).wasSet() mustBe true
    }

    @Test
    @JsName("fn24")
    fun `Given wasSetTo is called it returns false if it is attacht to Get`() {
        KMockContract.GetOrSet.Get().wasSetTo(null) mustBe false
    }

    @Test
    @JsName("fn25")
    fun `Given wasSetTo is called it returns false if the values do not match`() {
        KMockContract.GetOrSet.Set(fixture.fixture()).wasSetTo(fixture.fixture()) mustBe false
    }

    @Test
    @JsName("fn26")
    fun `Given wasSetTo is called it returns true if the values do math`() {
        val value: Any = fixture.fixture()
        KMockContract.GetOrSet.Set(value).wasSetTo(value) mustBe true
    }
}
