/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.concurrency.AtomicReference
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import tech.antibytes.kmock.KMockContract.Collector
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

class SyncFunMockerySpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @Test
    @JsName("fn0")
    fun `It fulfils FunMockery`() {
        SyncFunMockery<Unit, () -> Unit>(fixture.fixture()) fulfils KMockContract.FunMockery::class
    }

    @Test
    @JsName("fn0_a")
    fun `It fulfils SyncFunMockery`() {
        SyncFunMockery<Unit, () -> Unit>(fixture.fixture()) fulfils KMockContract.SyncFunMockery::class
    }

    @Test
    @JsName("fn1")
    fun `Given a returnValue is set it is threadsafe retrievable`() {
        // Given
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()

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
        val mockery = SyncFunMockery<Any?, () -> Any?>(fixture.fixture())
        val value: Any? = null

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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())

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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture()

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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val effect: () -> Any = { fixture.fixture() }

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
    fun `Given invoke is called it fails if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val mockery = SyncFunMockery<Any, () -> Any>(name)

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
        val mockery = SyncFunMockery<Any, () -> Any>(name)

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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 1)

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
        val mockery = SyncFunMockery<Any, (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val expected: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { expected }
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
        val mockery = SyncFunMockery<Any, (String) -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)
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
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

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
        val actual = SyncFunMockery<Any, () -> Any>(name).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn16")
    fun `Its default call count is 0`() {
        SyncFunMockery<Any, () -> Any>(fixture.fixture()).calls mustBe 0
    }

    @Test
    @JsName("fn17")
    fun `Given invoke is called it increments the call counter threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

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

    @Test
    @JsName("fn18")
    fun `Given the mockery has a Collector and invoke is called it calls the Collect`() {
        // Given
        val values: List<Any> = fixture.listFixture(size = 5)

        val capturedMock = AtomicReference<KMockContract.Mockery<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture(), collector)

        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.invoke()
        }

        runBlockingTest {
            capturedMock.get()?.id mustBe mockery.id
            capturedCalledIdx.get() mustBe 0
        }
    }

    // Functions
    @Test
    @JsName("fn19")
    fun `Given invoke is called it calls the given SideEffect with 0 Arguments and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, () -> Any>(fixture.fixture())
        val expected: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = {
                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = mockery.invoke()

            // Then
            actual mustBe expected
            mockery.getArgumentsForCall(0) mustBe null
        }
    }

    @Test
    @JsName("fn20")
    fun `Given invoke is called it calls the given SideEffect with 1 Argument and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, (String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0

            val arguments = mockery.getArgumentsForCall(0)
            arguments?.size mustBe 1
            arguments!![0] mustBe argument0
        }
    }

    @Test
    @JsName("fn21")
    fun `Given invoke is called it calls the given SideEffect with 2 Arguments and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 2
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
        }
    }

    @Test
    @JsName("fn22")
    fun `Given invoke is called it calls the given SideEffect with 3 Arguments and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, (String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 3
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
        }
    }

    @Test
    @JsName("fn23")
    fun `Given invoke is called it calls the given SideEffect with 4 Arguments and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, (String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2, argument3)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 4
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
        }
    }

    @Test
    @JsName("fn24")
    fun `Given invoke is called it calls the given SideEffect with 5 Arguments and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, (String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2, argument3, argument4)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 5
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
        }
    }

    @Test
    @JsName("fn25")
    fun `Given invoke is called it calls the given SideEffect with 6 Arguments and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, (String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()
        val actualArgument5 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                    actualArgument5.send(givenArg5)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2, argument3, argument4, argument5)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4
            actualArgument5.receive() mustBe argument5

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 6
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
        }
    }

    @Test
    @JsName("fn27")
    fun `Given invoke is called it calls the given SideEffect with 7 Arguments and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, (String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()
        val actualArgument5 = Channel<Int>()
        val actualArgument6 = Channel<String>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                    actualArgument5.send(givenArg5)
                    actualArgument6.send(givenArg6)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2, argument3, argument4, argument5, argument6)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4
            actualArgument5.receive() mustBe argument5
            actualArgument6.receive() mustBe argument6

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 7
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
        }
    }

    @Test
    @JsName("fn28")
    fun `Given invoke is called it calls the given SideEffect with 8 Arguments and delegates values threadsafe`() {
        // Given
        val mockery = SyncFunMockery<Any, (String, Int, String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()
        val actualArgument5 = Channel<Int>()
        val actualArgument6 = Channel<String>()
        val actualArgument7 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                    actualArgument5.send(givenArg5)
                    actualArgument6.send(givenArg6)
                    actualArgument7.send(givenArg7)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual =
                mockery.invoke(argument0, argument1, argument2, argument3, argument4, argument5, argument6, argument7)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4
            actualArgument5.receive() mustBe argument5
            actualArgument6.receive() mustBe argument6
            actualArgument7.receive() mustBe argument7

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 8
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
        }
    }

    @Test
    @JsName("fn29")
    fun `Given invoke is called it calls the given SideEffect with 9 Arguments and delegates values threadsafe`() {
        // Given
        val mockery =
            SyncFunMockery<Any, (String, Int, String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()
        val actualArgument5 = Channel<Int>()
        val actualArgument6 = Channel<String>()
        val actualArgument7 = Channel<Int>()
        val actualArgument8 = Channel<String>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                    actualArgument5.send(givenArg5)
                    actualArgument6.send(givenArg6)
                    actualArgument7.send(givenArg7)
                    actualArgument8.send(givenArg8)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8
            )

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4
            actualArgument5.receive() mustBe argument5
            actualArgument6.receive() mustBe argument6
            actualArgument7.receive() mustBe argument7
            actualArgument8.receive() mustBe argument8

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 9
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
        }
    }

    @Test
    @JsName("fn30")
    fun `Given invoke is called it calls the given SideEffect with 10 Arguments and delegates values threadsafe`() {
        // Given
        val mockery =
            SyncFunMockery<Any, (String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()
        val actualArgument5 = Channel<Int>()
        val actualArgument6 = Channel<String>()
        val actualArgument7 = Channel<Int>()
        val actualArgument8 = Channel<String>()
        val actualArgument9 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                    actualArgument5.send(givenArg5)
                    actualArgument6.send(givenArg6)
                    actualArgument7.send(givenArg7)
                    actualArgument8.send(givenArg8)
                    actualArgument9.send(givenArg9)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9
            )

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4
            actualArgument5.receive() mustBe argument5
            actualArgument6.receive() mustBe argument6
            actualArgument7.receive() mustBe argument7
            actualArgument8.receive() mustBe argument8
            actualArgument9.receive() mustBe argument9

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 10
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
        }
    }

    @Test
    @JsName("fn31")
    fun `Given invoke is called it calls the given SideEffect with 11 Arguments and delegates values threadsafe`() {
        // Given
        val mockery =
            SyncFunMockery<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()
        val actualArgument5 = Channel<Int>()
        val actualArgument6 = Channel<String>()
        val actualArgument7 = Channel<Int>()
        val actualArgument8 = Channel<String>()
        val actualArgument9 = Channel<Int>()
        val actualArgument10 = Channel<String>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                    actualArgument5.send(givenArg5)
                    actualArgument6.send(givenArg6)
                    actualArgument7.send(givenArg7)
                    actualArgument8.send(givenArg8)
                    actualArgument9.send(givenArg9)
                    actualArgument10.send(givenArg10)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When

            val actual = mockery.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10
            )

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4
            actualArgument5.receive() mustBe argument5
            actualArgument6.receive() mustBe argument6
            actualArgument7.receive() mustBe argument7
            actualArgument8.receive() mustBe argument8
            actualArgument9.receive() mustBe argument9
            actualArgument10.receive() mustBe argument10

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 11
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
        }
    }

    @Test
    @JsName("fn32")
    fun `Given invoke is called it calls the given SideEffect with 12 Arguments and delegates values threadsafe`() {
        // Given
        val mockery =
            SyncFunMockery<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(
                fixture.fixture()
            )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()
        val argument11: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()
        val actualArgument5 = Channel<Int>()
        val actualArgument6 = Channel<String>()
        val actualArgument7 = Channel<Int>()
        val actualArgument8 = Channel<String>()
        val actualArgument9 = Channel<Int>()
        val actualArgument10 = Channel<String>()
        val actualArgument11 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                    actualArgument5.send(givenArg5)
                    actualArgument6.send(givenArg6)
                    actualArgument7.send(givenArg7)
                    actualArgument8.send(givenArg8)
                    actualArgument9.send(givenArg9)
                    actualArgument10.send(givenArg10)
                    actualArgument11.send(givenArg11)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
                argument11
            )

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4
            actualArgument5.receive() mustBe argument5
            actualArgument6.receive() mustBe argument6
            actualArgument7.receive() mustBe argument7
            actualArgument8.receive() mustBe argument8
            actualArgument9.receive() mustBe argument9
            actualArgument10.receive() mustBe argument10
            actualArgument11.receive() mustBe argument11

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 12
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
            arguments[11] mustBe argument11
        }
    }

    @Test
    @JsName("fn33")
    fun `Given invoke is called it calls the given SideEffect with 13 Arguments and delegates values threadsafe`() {
        // Given
        val mockery =
            SyncFunMockery<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(
                fixture.fixture()
            )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()
        val argument11: Int = fixture.fixture()
        val argument12: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()
        val actualArgument2 = Channel<String>()
        val actualArgument3 = Channel<Int>()
        val actualArgument4 = Channel<String>()
        val actualArgument5 = Channel<Int>()
        val actualArgument6 = Channel<String>()
        val actualArgument7 = Channel<Int>()
        val actualArgument8 = Channel<String>()
        val actualArgument9 = Channel<Int>()
        val actualArgument10 = Channel<String>()
        val actualArgument11 = Channel<Int>()
        val actualArgument12 = Channel<String>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11, givenArg12 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                    actualArgument2.send(givenArg2)
                    actualArgument3.send(givenArg3)
                    actualArgument4.send(givenArg4)
                    actualArgument5.send(givenArg5)
                    actualArgument6.send(givenArg6)
                    actualArgument7.send(givenArg7)
                    actualArgument8.send(givenArg8)
                    actualArgument9.send(givenArg9)
                    actualArgument10.send(givenArg10)
                    actualArgument11.send(givenArg11)
                    actualArgument12.send(givenArg12)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
                argument11,
                argument12
            )

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
            actualArgument2.receive() mustBe argument2
            actualArgument3.receive() mustBe argument3
            actualArgument4.receive() mustBe argument4
            actualArgument5.receive() mustBe argument5
            actualArgument6.receive() mustBe argument6
            actualArgument7.receive() mustBe argument7
            actualArgument8.receive() mustBe argument8
            actualArgument9.receive() mustBe argument9
            actualArgument10.receive() mustBe argument10
            actualArgument11.receive() mustBe argument11
            actualArgument12.receive() mustBe argument12

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 13
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
            arguments[11] mustBe argument11
            arguments[12] mustBe argument12
        }
    }
}
