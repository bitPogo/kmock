/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class OperationSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given union is called with different VerificationHandles it merges both`() {
        // Given
        val name: String = fixture.fixture()
        val values1: List<Int> = fixture.listFixture()
        val values2: List<Int> = fixture.listFixture()

        val handle1 = VerificationHandle(name, values1)
        val handle2 = VerificationHandle(name, values2)

        // When
        val actualUnion = handle1 union handle2
        val actualOr = handle1 or handle2

        // Then
        val expected = values1.toMutableList()
            .also { it.addAll(values2) }
            .toSet()
            .toList()
            .sorted()

        actualOr mustBe actualUnion

        actualUnion.id mustBe handle1.id
        actualUnion.callIndices.forEachIndexed { idx, value ->
            value mustBe expected[idx]
        }
    }

    @Test
    @JsName("fn1")
    fun `Given intersect is called with different VerificationHandles it makes the intersection of both`() {
        // Given
        val name: String = fixture.fixture()
        val base: List<Int> = fixture.listFixture()
        val values1: List<Int> = fixture.listFixture<Int>()
            .toMutableList()
            .also { it.addAll(base) }
            .also { it.shuffle() }
        val values2: List<Int> = fixture.listFixture<Int>()
            .toMutableList()
            .also { it.addAll(base) }
            .also { it.shuffle() }

        val handle1 = VerificationHandle(name, values1)
        val handle2 = VerificationHandle(name, values2)

        // When
        val actualIntersect = handle1 intersect handle2
        val actualAnd = handle1 and handle2

        // Then
        val expected = base.sorted()

        actualAnd mustBe actualIntersect

        actualIntersect.id mustBe handle1.id
        actualIntersect.callIndices.forEachIndexed { idx, value ->
            value mustBe expected[idx]
        }
    }

    @Test
    @JsName("fn2")
    fun `Given diif is called with different VerificationHandles it makes the difference of both`() {
        // Given
        val name: String = fixture.fixture()
        val base: List<Int> = fixture.listFixture()
        val values1: List<Int> = fixture.listFixture<Int>()
            .toMutableList()
            .also { it.addAll(base) }
            .also { it.shuffle() }
        val values2: List<Int> = fixture.listFixture<Int>()
            .toMutableList()
            .also { it.addAll(base) }
            .also { it.shuffle() }

        val handle1 = VerificationHandle(name, values1)
        val handle2 = VerificationHandle(name, values2)

        // When
        val actualDiff = handle1 diff handle2
        val actualXOr = handle1 xor handle2

        // Then
        val expected = values1
            .toMutableSet()
            .also { it.addAll(values2) }
            .filterNot { value -> base.contains(value) }
            .sorted()

        actualXOr mustBe actualDiff

        actualDiff.id mustBe handle1.id
        actualDiff.callIndices.forEachIndexed { idx, value ->
            value mustBe expected[idx]
        }
    }
}
