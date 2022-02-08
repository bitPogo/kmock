/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import tech.antibytes.util.test.MockError
import tech.antibytes.util.test.annotations.IgnoreJs
import tech.antibytes.util.test.annotations.JsOnly
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FunMockerySpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @Test
    @JsName("fn0")
    fun `It fulfils FunMockery`() {
        FunMockery<Unit>(fixture.fixture()) fulfils KMockContract.FunMockery::class
    }

    @Test
    @JsName("fn1")
    fun `Given a returnValue is set it is threadsafe retrievable`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val value: String = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValue = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.returnValue mustBe value
        }
    }

    @Test
    @JsName("fn2")
    fun `Given a returnValue is set with nullable value it is threadsafe retrievable`() {
        // Given
        val mockery = FunMockery<String?>(fixture.fixture())
        val value: String? = null

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValue = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.returnValue mustBe value
        }
    }

    @Test
    @JsName("fn3")
    fun `Given a returnValues is set with an emptyList it fails`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            mockery.returnValues = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn4")
    fun `Given a returnValues is set it is threadsafe retrievable`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val values: List<String> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValues = values
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.returnValues mustBe values
        }
    }

    @Test
    @JsName("fn5")
    fun `Given a sideEffect is set it is threadsafe retrievable`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val effect: (Array<*>) -> String = { fixture.fixture() }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = effect
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.sideEffect sameAs effect
        }
    }

    @Test
    @JsName("fn6")
    @IgnoreJs
    fun `Given invoke is called it fails iif no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val mockery = FunMockery<String>(name)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            // When
            runBlockingTestInContext(testScope1.coroutineContext) {
                mockery.invoke()
            }
        }

        error.message mustBe "Missing stub value for $name"
    }

    @Test
    @JsName("fn6_a")
    @JsOnly
    fun `Given invoke is called it fails iif no ReturnValue Provider is set for JS`() {
        // Given
        val name: String = fixture.fixture()
        val mockery = FunMockery<String>(name)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            // When
            mockery.invoke()
        }

        error.message mustBe "Missing stub value for $name"
    }

    @Test
    @JsName("fn7")
    fun `Given invoke is called it returns the ReturnValue threadsafe`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val value: String = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValue = value
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = mockery.invoke()

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn8")
    fun `Given invoke is called it returns the ReturnValues threadsafe`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val values: List<String> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            values.forEach { value ->
                val actual = mockery.invoke()

                // Then
                actual mustBe value
            }
        }
    }

    @Test
    @JsName("fn9")
    fun `Given invoke is called it returns the last ReturnValue if the given List is down to one value threadsafe`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val values: List<String> = fixture.listFixture(size = 1)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            for (x in 0 until 10) {
                val actual = mockery.invoke()

                // Then
                actual mustBe values.first()
            }
        }
    }

    @Test
    @JsName("fn10")
    fun `Given invoke is called it calls the given SideEffect and delegates values threadsafe`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: String = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { arguments: Array<out Any?> ->
                testScope1.launch {
                    actualArgument0.send(arguments[0] as String)
                    actualArgument1.send(arguments[1] as Int)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = mockery.invoke(argument0, argument1)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
        }
    }

    @Test
    @JsName("fn11")
    fun `Given invoke is called it uses ReturnValues over ReturnValue`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val value: String = fixture.fixture()
        val values: List<String> = fixture.listFixture(size = 2)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValue = value
            mockery.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = mockery.invoke()

            // Then
            actual mustBe values.first()
        }
    }

    @Test
    @JsName("fn12")
    fun `Given invoke is called it uses SideEffect over ReturnValues`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val expected: String = fixture.fixture()
        val values: List<String> = fixture.listFixture(size = 2)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { _ -> expected }
            mockery.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = mockery.invoke()

            // Then
            actual mustBe expected
        }
    }

    @Test
    @JsName("fn13")
    fun `Given invoke is called it captures Arguments threadsafe`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val values: List<String> = fixture.listFixture(size = 5)
        val argument: String = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.invoke(argument)
        }

        runBlockingTest {
            val actual = mockery.getArgumentsForCall(0)

            actual!!.size mustBe 1
            actual[0] mustBe argument
        }
    }

    @Test
    @JsName("fn14")
    fun `Given invoke is called it captures void Arguments threadsafe`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val values: List<String> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.invoke()
        }

        runBlockingTest {
            val actual = mockery.getArgumentsForCall(0)

            actual mustBe null
        }
    }

    @Test
    @JsName("fn15")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = FunMockery<String>(name).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn16")
    fun `Its default call count is 0`() {
        FunMockery<String>(fixture.fixture()).calls mustBe 0
    }

    @Test
    @JsName("fn17")
    fun `Given invoke is called it increments the call counter threadsafe`() {
        // Given
        val mockery = FunMockery<String>(fixture.fixture())
        val values: List<String> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.invoke()
        }

        runBlockingTest {
            mockery.calls mustBe 1
        }
    }
}
