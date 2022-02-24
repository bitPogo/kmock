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
    fun `Given hadBeenCalledWith is called with an Argument it returns false if the Array contains not the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn1")
    fun `Given hadBeenCalledWith is called with an Argument it returns false if the Array contains the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(array.first())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn2")
    fun `Given hadBeenCalledWith is called without Arguments void it returns true if the Array contains Arguments`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWith()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn3")
    fun `Given hadBeenCalledWith is called without Arguments void it returns true if the Array is null`() {
        // Given
        val array = null

        // When
        val actual = array.hasBeenCalledWith()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn4")
    fun `Given hadBeenCalledWith is called with Arguments it returns false if the Array is null`() {
        // Given
        val array = null

        // When
        val actual = array.hasBeenCalledWith(fixture.listFixture<String>(), fixture.listFixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn5")
    fun `Given hadBeenCalledWith is called with Arguments it returns false if the Array contains not all of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(array[0], array[1], array[2], fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn6")
    fun `Given hadBeenCalledWith is called with Arguments it returns true if the Array contains all of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(array[0], array[1], array[2], array[3])

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn7")
    fun `Given hadBeenStrictlyCalledWith is called with an Argument it returns false if the Array matches not the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenStrictlyCalledWith(fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn8")
    fun `Given hadBeenStrictlyCalledWith is called with an Argument it returns true if the Array matches the given Argument`() {
        // Given
        val array = fixture.listFixture<String>(size = 1).toTypedArray()

        // When
        val actual = array.hasBeenStrictlyCalledWith(array.first())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn9")
    fun `Given hadBeenStrictlyCalledWith is called without Arguments void it returns false if the Array has Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 1).toTypedArray()

        // When
        val actual = array.hasBeenStrictlyCalledWith()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn10")
    fun `Given hadBeenStrictlyCalledWith is called witg Arguments void it returns false if the Array is null`() {
        // Given
        val array = null

        // When
        val actual = array.hasBeenStrictlyCalledWith(fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn11")
    fun `Given hadBeenStrictlyCalledWith is called without Arguments void it returns false if the Array is null`() {
        // Given
        val array = null

        // When
        val actual = array.hasBeenStrictlyCalledWith()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn12")
    fun `Given hadBeenStrictlyCalledWith is called with Arguments it returns false if the Array matches not exactly the given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenStrictlyCalledWith(array[0], array[1], array[2], array[3])

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn13")
    fun `Given hadBeenStrictlyCalledWith is called with Arguments it returns true if the Array matches exactly the given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenStrictlyCalledWith(*array)

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn14")
    fun `Given hadBeenCalledWithout is called with an Argument it returns false if the Array contains no Argument`() {
        // Given
        val array = null

        // When
        val actual = array.hasBeenCalledWithout()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn15")
    fun `Given hadBeenCalledWithout is called with an Argument it returns true if the Array contains Arguments`() {
        // Given
        val array = null

        // When
        val actual = array.hasBeenCalledWithout(fixture.listFixture<String>().toTypedArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn16")
    fun `Given hadBeenCalledWithout is called with an Argument it returns true if the Array contains not the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout(fixture.fixture<String>())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn17")
    fun `Given hadBeenCalledWithout is called with an Argument it returns false if the Array contains the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout(array.first())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn18")
    fun `Given hadBeenCalledWithout is called without Argument it returns true if the Array contains Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn19")
    fun `Given hadBeenCalledWithout is called with Arguments it returns false if the Array is null`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn20")
    fun `Given hadBeenCalledWithout is called with Arguments it returns true if the Array none of given Arguments matches`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual =
            array.hasBeenCalledWithout(fixture.fixture<String>(), fixture.fixture<String>(), fixture.fixture<String>())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn21")
    fun `Given hadBeenCalledWithout is called with Arguments it returns false if the Array matches at least one of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout(
            array[0],
            fixture.fixture<String>(),
            fixture.fixture<String>(),
            fixture.fixture<String>()
        )

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn22")
    fun `Given wasGotten is called it returns false if it is attacht to Set`() {
        KMockContract.GetOrSet.Set(null).wasGotten() mustBe false
    }

    @Test
    @JsName("fn23")
    fun `Given wasGotten is called it returns true if it is attacht to Get`() {
        KMockContract.GetOrSet.Get.wasGotten() mustBe true
    }

    @Test
    @JsName("fn24")
    fun `Given wasSet is called it returns false if it is attacht to Get`() {
        KMockContract.GetOrSet.Get.wasSet() mustBe false
    }

    @Test
    @JsName("fn25")
    fun `Given wasGotten is called it returns true if it is attacht to Set`() {
        KMockContract.GetOrSet.Set(null).wasSet() mustBe true
    }

    @Test
    @JsName("fn26")
    fun `Given wasSetTo is called it returns false if it is attacht to Get`() {
        KMockContract.GetOrSet.Get.wasSetTo(null) mustBe false
    }

    @Test
    @JsName("fn27")
    fun `Given wasSetTo is called it returns false if the values do not match`() {
        KMockContract.GetOrSet.Set(fixture.fixture()).wasSetTo(fixture.fixture()) mustBe false
    }

    @Test
    @JsName("fn28")
    fun `Given wasSetTo is called it returns true if the values do math`() {
        val value: Any = fixture.fixture()
        KMockContract.GetOrSet.Set(value).wasSetTo(value) mustBe true
    }
}
