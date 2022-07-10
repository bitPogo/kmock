/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import kotlin.js.JsName
import kotlin.test.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.verification.constraints.eq
import tech.antibytes.kmock.verification.constraints.isNot
import tech.antibytes.kmock.verification.constraints.isNotSame
import tech.antibytes.kmock.verification.constraints.isSame
import tech.antibytes.util.test.mustBe

class ArgumentMatcherSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given hasBeenCalledWithVoid is called it returns true if the Array is null`() {
        // Given
        val array: Array<out Any?> = arrayOf()

        // When
        val actual = array.hasBeenCalledWithVoid()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn1")
    fun `Given hasBeenCalledWithVoid is called it returns false if the contains values`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithVoid()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn2")
    fun `Given hasBeenCalledWith is called with an Argument it returns true if the Array is null and the Arguments are empty`() {
        // Given
        val array: Array<out Any?> = arrayOf()

        // When
        val actual = array.hasBeenCalledWith()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn3")
    fun `Given hasBeenCalledWith is called with an Argument it returns false if the Array contains not the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn4")
    fun `Given hasBeenCalledWith is called with an Argument it returns true if the Array contains the given Argument`() {
        // Given
        val array = fixture.listFixture<String>(size = 5).toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(array.first(), array[2])

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn5")
    fun `Given hasBeenCalledWith is called with an Argument it returns true if the Array contains the given Argument while respecting the order`() {
        // Given
        val array = fixture.listFixture<String>(size = 5).toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(array[2], array.first())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn6")
    fun `Given hasBeenCalledWith is called without Arguments void it returns true if the Array contains Arguments`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWith()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn7")
    fun `Given hasBeenCalledWith is called without Arguments void it returns true if the Array is null`() {
        // Given
        val array: Array<out Any?> = arrayOf()

        // When
        val actual = array.hasBeenCalledWith()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn8")
    fun `Given hasBeenCalledWith is called with Arguments it returns false if the Array is null`() {
        // Given
        val array: Array<out Any?> = arrayOf()

        // When
        val actual = array.hasBeenCalledWith(fixture.listFixture<String>(), fixture.listFixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn9")
    fun `Given hasBeenCalledWith is called with Arguments it returns false if the Array contains not all of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(array[0], array[1], array[2], fixture.fixture<String>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn9a")
    fun `Given hasBeenCalledWith is called with Arguments it returns false if the Array contains not all of given Arguments while propagating the contraint index`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()
        var capturedIdx: Int? = null
        var capturedArgument: Any? = fixture.fixture()

        // When
        val actual = array.hasBeenCalledWith(
            array[0],
            array[1],
            array[2],
            fixture.fixture<String>(),
            onFail = { givenArgument, givenIdx ->
                capturedIdx = givenIdx
                capturedArgument = givenArgument
            },
        )

        // Then
        actual mustBe false
        capturedIdx mustBe 3
        capturedArgument mustBe null
    }

    @Test
    @JsName("fn10")
    fun `Given hasBeenCalledWith is called with Arguments it returns true if the Array contains all of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(*array)

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn11")
    fun `Given hasBeenCalledWith is called with Arguments it returns true while using Constraints`() {
        // Given
        val array = fixture.listFixture<Any>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenCalledWith(
            *(array.map { value -> isSame(value) }.toTypedArray()),
        )

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn12")
    fun `Given hasBeenStrictlyCalledWith is called with an Argument it returns true if the Array is null and no values were given`() {
        // Given
        val array: Array<out Any?> = arrayOf()

        // When
        val actual = array.hasBeenStrictlyCalledWith()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn13")
    fun `Given hasBeenStrictlyCalledWith is called with an Argument it returns false if the Array is null and values were given`() {
        // Given
        val array: Array<out Any?> = arrayOf()

        // When
        val actual = array.hasBeenStrictlyCalledWith(fixture.fixture<Any>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn14")
    fun `Given hasBeenStrictlyCalledWith is called with an Argument it returns false if the Array and Value size does not match`() {
        // Given
        val array = fixture.listFixture<Any>(size = 5).toTypedArray()

        // When
        val actual = array.hasBeenStrictlyCalledWith(fixture.fixture<Any>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn15")
    fun `Given hasBeenStrictlyCalledWith is called with an Argument it returns false if the Array and Value do not match`() {
        // Given
        val array = fixture.listFixture<Any>(size = 5).toTypedArray()

        // When
        val actual = array.hasBeenStrictlyCalledWith(*fixture.listFixture<Any>(size = 5).toTypedArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn15a")
    fun `Given hasBeenStrictlyCalledWith is called with an Argument it returns false if the Array and Value do not match while propagating the value to a given closure`() {
        // Given
        val array = fixture.listFixture<Any>(size = 5).toTypedArray()
        val expected = fixture.listFixture<Any>(size = 5).toTypedArray()
        var capturedArgument: Any? = null
        var capturedIndex: Int? = null

        // When
        val actual = array.hasBeenStrictlyCalledWith(
            *expected,
            onFail = { argument, idx ->
                capturedArgument = argument
                capturedIndex = idx
            },
        )

        // Then
        actual mustBe false
        capturedArgument mustBe array.first()
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn16")
    fun `Given hasBeenStrictlyCalledWith is called with an Argument it returns true if the Array and Value do match`() {
        // Given
        val array = fixture.listFixture<Int>(size = 5)

        // When
        val actual = array.toTypedArray().hasBeenStrictlyCalledWith(*array.toTypedArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn17")
    fun `Given hasBeenStrictlyCalledWith is called with Arguments it returns true while using Constraints`() {
        // Given
        val array = fixture.listFixture<Any>(size = 5).toTypedArray()

        // When
        val actual = array.hasBeenStrictlyCalledWith(
            *(array.map { value -> isSame(value) }.toTypedArray()),
        )

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn18")
    fun `Given hasBeenCalledWithout is called with an Argument it returns false if the Array contains no Argument`() {
        // Given
        val array: Array<out Any?> = arrayOf()

        // When
        val actual = array.hasBeenCalledWithout()

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn19")
    fun `Given hasBeenCalledWithout is called with an Argument it returns true if the Array contains Arguments`() {
        // Given
        val array: Array<out Any?> = arrayOf()

        // When
        val actual = array.hasBeenCalledWithout(fixture.listFixture<String>().toTypedArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn20")
    fun `Given hasBeenCalledWithout is called with an Argument it returns true if the Array contains not the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout(fixture.fixture<String>())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn21")
    fun `Given hasBeenCalledWithout is called with an Argument it returns false if the Array contains the given Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout(array.first())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn22")
    fun `Given hasBeenCalledWithout is called with an Argument it returns false if the Array contains the given Argument while it propagates the error to a given closure`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()
        var capturedArgument: Any? = null
        var capturedIndex: Int? = null

        // When
        val actual = array.hasBeenCalledWithout(
            array.first(),
            onFail = { argument, idx ->
                capturedArgument = argument
                capturedIndex = idx
            },
        )

        // Then
        actual mustBe false
        capturedArgument mustBe array.first()
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn23")
    fun `Given hasBeenCalledWithout is called without Argument it returns true if the Array contains Argument`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn24")
    fun `Given hasBeenCalledWithout is called with Arguments it returns false if the Array is null`() {
        // Given
        val array = fixture.listFixture<String>().toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout()

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn25")
    fun `Given hasBeenCalledWithout is called with Arguments it returns true if the Array none of given Arguments matches`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual =
            array.hasBeenCalledWithout(fixture.fixture<String>(), fixture.fixture<String>(), fixture.fixture<String>())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn26")
    fun `Given hasBeenCalledWithout is called with Arguments it returns false if the Array matches at least one of given Arguments`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout(
            array[0],
            fixture.fixture<String>(),
            fixture.fixture<String>(),
            fixture.fixture<String>(),
        )

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn27")
    fun `Given hasBeenCalledWithout is called with Arguments it returns false while using Constraints`() {
        // Given
        val array = fixture.listFixture<String>(size = 8).toTypedArray()

        // When
        val actual = array.hasBeenCalledWithout(
            isNot(fixture.fixture<String>()),
            isNot(fixture.fixture<String>()),
            isNot(fixture.fixture<String>()),
            eq(array[0]),
        )

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn28")
    fun `Given wasGotten is called it returns false if it is attacht to Set`() {
        KMockContract.GetOrSet.Set(null).wasGotten() mustBe false
    }

    @Test
    @JsName("fn29")
    fun `Given wasGotten is called it returns true if it is attacht to Get`() {
        KMockContract.GetOrSet.Get.wasGotten() mustBe true
    }

    @Test
    @JsName("fn30")
    fun `Given wasSet is called it returns false if it is attacht to Get`() {
        KMockContract.GetOrSet.Get.wasSet() mustBe false
    }

    @Test
    @JsName("fn31")
    fun `Given wasGotten is called it returns true if it is attacht to Set`() {
        KMockContract.GetOrSet.Set(null).wasSet() mustBe true
    }

    @Test
    @JsName("fn32")
    fun `Given wasSetTo is called it returns false if it is attacht to Get`() {
        KMockContract.GetOrSet.Get.wasSetTo(null) mustBe false
    }

    @Test
    @JsName("fn33")
    fun `Given wasSetTo is called it returns false if the values do not match`() {
        KMockContract.GetOrSet.Set(fixture.fixture()).wasSetTo(fixture.fixture()) mustBe false
    }

    @Test
    @JsName("fn34")
    fun `Given wasSetTo is called it returns true if the values do match`() {
        val value: Any = fixture.fixture()
        KMockContract.GetOrSet.Set(value).wasSetTo(value) mustBe true
    }

    @Test
    @JsName("fn35")
    fun `Given wasSetTo is called it returns false if the values do match while using Constraints`() {
        val value: Any = fixture.fixture()
        KMockContract.GetOrSet.Set(value).wasSetTo(isNotSame(value)) mustBe false
    }
}
