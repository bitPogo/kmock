/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class OperationSpec {
    private val fixture = kotlinFixture()

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn0")
    fun `Given union is called with different VerificationHandles fails if they refence 2 different Proxies both`() {
        // Given
        val values1: List<Int> = fixture.listFixture()
        val values2: List<Int> = fixture.listFixture()

        val handle1 = Expectation(fixture.funProxyFixture(), values1)
        val handle2 = Expectation(fixture.funProxyFixture(), values2)

        // When
        val unionError = assertFailsWith<IllegalArgumentException> {
            handle1 union handle2
        }
        val orError = assertFailsWith<IllegalArgumentException> {
            handle1 or handle2
        }

        // Then
        unionError.message mustBe orError.message

        unionError.message mustBe "union cannot be applied to handles which refer to different proxies."
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn1")
    fun `Given union is called with different VerificationHandles it merges both`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = name)
        val values1: List<Int> = fixture.listFixture()
        val values2: List<Int> = fixture.listFixture()

        val handle1 = Expectation(proxy, values1)
        val handle2 = Expectation(proxy, values2)

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

        actualUnion.proxy mustBe handle1.proxy
        actualUnion.callIndices.forEachIndexed { idx, value ->
            value mustBe expected[idx]
        }
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn2")
    fun `Given intersect is called with different VerificationHandles fails if they refence 2 different Proxies both`() {
        // Given
        val values1: List<Int> = fixture.listFixture()
        val values2: List<Int> = fixture.listFixture()

        val handle1 = Expectation(fixture.funProxyFixture(), values1)
        val handle2 = Expectation(fixture.funProxyFixture(), values2)

        // When
        val intersectError = assertFailsWith<IllegalArgumentException> {
            handle1 intersection handle2
        }
        val andError = assertFailsWith<IllegalArgumentException> {
            handle1 and handle2
        }

        // Then
        intersectError.message mustBe andError.message

        intersectError.message mustBe "intersection cannot be applied to handles which refer to different proxies."
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn3")
    fun `Given intersect is called with different VerificationHandles it makes the intersection of both`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = name)

        val base: List<Int> = fixture.listFixture()
        val values1: List<Int> = fixture.listFixture<Int>()
            .toMutableList()
            .also { it.addAll(base) }
            .also { it.shuffle() }
        val values2: List<Int> = fixture.listFixture<Int>()
            .toMutableList()
            .also { it.addAll(base) }
            .also { it.shuffle() }

        val handle1 = Expectation(proxy, values1)
        val handle2 = Expectation(proxy, values2)

        // When
        val actualIntersect = handle1 intersection handle2
        val actualAnd = handle1 and handle2

        // Then
        val expected = base.sorted()

        actualAnd mustBe actualIntersect

        actualIntersect.proxy mustBe handle1.proxy
        actualIntersect.callIndices.forEachIndexed { idx, value ->
            value mustBe expected[idx]
        }
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn4")
    fun `Given diff is called with different VerificationHandles fails if they refence 2 different Proxies both`() {
        // Given
        val values1: List<Int> = fixture.listFixture()
        val values2: List<Int> = fixture.listFixture()

        val handle1 = Expectation(fixture.funProxyFixture(), values1)
        val handle2 = Expectation(fixture.funProxyFixture(), values2)

        // When
        val diffError = assertFailsWith<IllegalArgumentException> {
            handle1 diff handle2
        }
        val xorError = assertFailsWith<IllegalArgumentException> {
            handle1 xor handle2
        }

        // Then
        diffError.message mustBe xorError.message

        diffError.message mustBe "diff cannot be applied to handles which refer to different proxies."
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn5")
    fun `Given diff is called with different VerificationHandles it makes the difference of both`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(id = name)

        val base: List<Int> = fixture.listFixture()
        val values1: List<Int> = fixture.listFixture<Int>()
            .toMutableList()
            .also { it.addAll(base) }
            .also { it.shuffle() }
        val values2: List<Int> = fixture.listFixture<Int>()
            .toMutableList()
            .also { it.addAll(base) }
            .also { it.shuffle() }

        val handle1 = Expectation(proxy, values1)
        val handle2 = Expectation(proxy, values2)

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

        actualDiff.proxy mustBe handle1.proxy
        actualDiff.callIndices.forEachIndexed { idx, value ->
            value mustBe expected[idx]
        }
    }
}
