/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.mock.SyncFunProxyStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FunProxyArgumentRetrievalSpec {
    private val fixture = kotlinFixture()

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn0")
    fun `Given getArgumentsByType is called it progates errors`() {
        // Given
        val error = RuntimeException()
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            0,
            getArgumentsForCall = { throw error }
        )

        // Then
        val actual = assertFailsWith<Throwable> {
            // When
            proxy.getArgumentsByType<Any>(0)
        }

        actual mustBe error
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn1")
    fun `Given getArgumentsByType is called collects all Arguments with for the given Type at the given CallIndex`() {
        // Given
        var capturedIdx: Int? = null
        val idx: Int = fixture.fixture()
        val expectedSize = 3

        val expected = fixture.listFixture<String>(size = expectedSize)
        val noise = fixture.listFixture<Any>(size = 4)

        val arguments = mutableListOf<Any>().also {
            it.addAll(expected)
            it.addAll(noise)
            it.shuffle()
        }

        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            0,
            getArgumentsForCall = { givenIdx ->
                capturedIdx = givenIdx

                arguments.toTypedArray()
            }
        )

        // When
        val actual = proxy.getArgumentsByType<String>(idx)

        // Then
        capturedIdx mustBe idx
        actual.sorted() mustBe expected.sorted()
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn2")
    fun `Given getAllArgumentsByType is called it progates errors`() {
        // Given
        val error = RuntimeException()

        var counter = 2

        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            4,
            getArgumentsForCall = {
                if (counter == 0) {
                    throw error
                } else {
                    counter -= 1
                    arrayOf()
                }
            }
        )

        // Then
        val actual = assertFailsWith<Throwable> {
            // When
            proxy.getAllArgumentsByType<Any>()
        }

        actual mustBe error
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn3")
    fun `Given getAllArgumentsByType is called collects all Arguments with for the given Type at the given CallIndex`() {
        // Given
        val expectedSize = 3

        val expected = fixture.listFixture<String>(size = expectedSize)
        val noise = fixture.listFixture<Any>(size = 4)

        val arguments = mutableListOf<Any>().also {
            it.addAll(expected)
            it.addAll(noise)
            it.shuffle()
        }

        val allArgument = mutableListOf(
            arguments,
            arguments,
            arguments
        )

        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            3,
            getArgumentsForCall = {
                allArgument.removeFirst().toTypedArray()
            }
        )

        // When
        val actual = proxy.getAllArgumentsByType<String>()

        // Then
        actual.sorted() mustBe mutableListOf<String>().also {
            it.addAll(expected)
            it.addAll(expected)
            it.addAll(expected)
            it.sort()
        }
    }

    @OptIn(KMockExperimental::class)
    @Test
    @JsName("fn4")
    fun `Given getAllArgumentsBoxedByType is called it progates errors`() {
        // Given
        val error = RuntimeException()

        var counter = 2

        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            4,
            getArgumentsForCall = {
                if (counter == 0) {
                    throw error
                } else {
                    counter -= 1
                    arrayOf()
                }
            }
        )

        // Then
        val actual = assertFailsWith<Throwable> {
            // When
            proxy.getAllArgumentsBoxedByType<Any>()
        }

        actual mustBe error
    }

    @Test
    @JsName("fn5")
    fun `Given getAllArgumentsBoxedByType is called collects all Arguments with for the given Type at the given CallIndex`() {
        // Given
        val expectedSize = 3

        val expected = fixture.listFixture<String>(size = expectedSize)
        val noise = fixture.listFixture<Any>(size = 4)

        val arguments = mutableListOf<Any>().also {
            it.addAll(expected)
            it.addAll(noise)
            it.shuffle()
        }

        val allArgument = mutableListOf(
            arguments,
            arguments,
            arguments
        )

        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            3,
            getArgumentsForCall = {
                allArgument.removeFirst().toTypedArray()
            }
        )

        // When
        val actual = proxy.getAllArgumentsBoxedByType<String>()

        // Then
        actual.map { it.sorted() } mustBe mutableListOf<List<String>>().also {
            it.add(expected.sorted())
            it.add(expected.sorted())
            it.add(expected.sorted())
        }
    }
}
