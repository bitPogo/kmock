/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class EqualSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `eq fulfils MatcherConstraint`() {
        eq(fixture.fixture()) fulfils KMockContract.VerificationConstraint::class
    }

    @Test
    @JsName("fn1")
    fun `Given eq is called it returns false if the given Value and the call Value are not the matching`() {
        // Given
        val value: Int = fixture.fixture()

        // When
        val actual = eq(value).matches(fixture.fixture<Int>())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn2")
    fun `Given eq is called it returns true if the given Value and the call Value are matching`() {
        // Given
        val value: Int = fixture.fixture()

        // When
        val actual = eq(value).matches(value)

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn3")
    fun `Given eq is called it returns false if the given Value is an Array and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Int>().toTypedArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<Int>().toTypedArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn4")
    fun `Given eq is called it returns true if the given Value is an Array and the call Value are matching`() {
        // Given
        val value = fixture.listFixture<Int>()

        // When
        val actual = eq(value.toTypedArray()).matches(value.toTypedArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn5")
    fun `Given eq is called it returns false if the given Value is an ByteArray and the call Value are not matching`() {
        // Given
        val value = fixture.fixture<String>().encodeToByteArray()

        // When
        val actual = eq(value).matches(fixture.fixture<String>().encodeToByteArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn6")
    fun `Given eq is called it returns true if the given Value is an ByteArray and the call Value are not matching`() {
        // Given
        val value = fixture.fixture<String>()

        // When
        val actual = eq(value.encodeToByteArray()).matches(value.encodeToByteArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn7")
    fun `Given eq is called it returns false if the given Value is an ShortArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Short>().toTypedArray().toShortArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<Short>().toTypedArray().toShortArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn8")
    fun `Given eq is called it returns true if the given Value is an ShortArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Short>()

        // When
        val actual = eq(value.toTypedArray().toShortArray()).matches(value.toTypedArray().toShortArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn9")
    fun `Given eq is called it returns false if the given Value is an IntArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Int>().toTypedArray().toIntArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<Int>().toTypedArray().toIntArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn10")
    fun `Given eq is called it returns true if the given Value is an IntArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Int>()

        // When
        val actual = eq(value.toTypedArray().toIntArray()).matches(value.toTypedArray().toIntArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn11")
    fun `Given eq is called it returns false if the given Value is an LongArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Long>().toTypedArray().toLongArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<Long>().toTypedArray().toLongArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn12")
    fun `Given eq is called it returns true if the given Value is an LongArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Long>()

        // When
        val actual = eq(value.toTypedArray().toLongArray()).matches(value.toTypedArray().toLongArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn13")
    fun `Given eq is called it returns false if the given Value is an FloatArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Float>().toTypedArray().toFloatArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<Float>().toTypedArray().toFloatArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn14")
    fun `Given eq is called it returns true if the given Value is an FloatArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Float>()

        // When
        val actual = eq(value.toTypedArray().toFloatArray()).matches(value.toTypedArray().toFloatArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn15")
    fun `Given eq is called it returns false if the given Value is an DoubleArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Double>().toTypedArray().toDoubleArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<Double>().toTypedArray().toDoubleArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn16")
    fun `Given eq is called it returns true if the given Value is an DoubleArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Double>()

        // When
        val actual = eq(value.toTypedArray().toDoubleArray()).matches(value.toTypedArray().toDoubleArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn17")
    fun `Given eq is called it returns false if the given Value is an CharArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Char>().toTypedArray().toCharArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<Char>().toTypedArray().toCharArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn18")
    fun `Given eq is called it returns true if the given Value is an CharArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Char>()

        // When
        val actual = eq(value.toTypedArray().toCharArray()).matches(value.toTypedArray().toCharArray())

        // Then
        actual mustBe true
    }

    @Test
    @JsName("fn19")
    fun `Given eq is called it returns false if the given Value is an BooleanArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Boolean>().toTypedArray().toBooleanArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<Boolean>().toTypedArray().toBooleanArray())

        // Then
        actual mustBe false
    }

    @Test
    @JsName("fn20")
    fun `Given eq is called it returns true if the given Value is an BooleanArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<Boolean>()

        // When
        val actual = eq(value.toTypedArray().toBooleanArray()).matches(value.toTypedArray().toBooleanArray())

        // Then
        actual mustBe true
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    @JsName("fn21")
    fun `Given eq is called it returns false if the given Value is an UByteArray and the call Value are not matching`() {
        // Given
        val value = fixture.fixture<UByteArray>()

        // When
        val actual = eq(value).matches(fixture.fixture<UByteArray>())

        // Then
        actual mustBe false
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    @JsName("fn22")
    fun `Given eq is called it returns true if the given Value is an UByteArray and the call Value are not matching`() {
        // Given
        val value = fixture.fixture<UByteArray>()

        // When
        val actual = eq(value).matches(value)

        // Then
        actual mustBe true
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    @JsName("fn23")
    fun `Given eq is called it returns false if the given Value is an UShortArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<UShort>().toTypedArray().toUShortArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<UShort>().toTypedArray().toUShortArray())

        // Then
        actual mustBe false
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    @JsName("fn24")
    fun `Given eq is called it returns true if the given Value is an UShortArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<UShort>()

        // When
        val actual = eq(value.toTypedArray().toUShortArray()).matches(value.toTypedArray().toUShortArray())

        // Then
        actual mustBe true
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    @JsName("fn25")
    fun `Given eq is called it returns false if the given Value is an UIntArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<UInt>().toTypedArray().toUIntArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<UInt>().toTypedArray().toUIntArray())

        // Then
        actual mustBe false
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    @JsName("fn26")
    fun `Given eq is called it returns true if the given Value is an UIntArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<UInt>()

        // When
        val actual = eq(value.toTypedArray().toUIntArray()).matches(value.toTypedArray().toUIntArray())

        // Then
        actual mustBe true
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    @JsName("fn27")
    fun `Given eq is called it returns false if the given Value is an ULongArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<ULong>().toTypedArray().toULongArray()

        // When
        val actual = eq(value).matches(fixture.listFixture<ULong>().toTypedArray().toULongArray())

        // Then
        actual mustBe false
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    @JsName("fn28")
    fun `Given eq is called it returns true if the given Value is an ULongArray and the call Value are not matching`() {
        // Given
        val value = fixture.listFixture<ULong>()

        // When
        val actual = eq(value.toTypedArray().toULongArray()).matches(value.toTypedArray().toULongArray())

        // Then
        actual mustBe true
    }
}
