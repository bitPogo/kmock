/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.concurrency.AtomicReference
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.util.test.MockError
import tech.antibytes.util.test.annotations.IgnoreJs
import tech.antibytes.util.test.annotations.JsOnly
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.resolveMultiBlockCalls
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.coroutine.runBlockingTestWithTimeoutInScope
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AsyncFunMockerySpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils FunMockery`() {
        AsyncFunMockery<Unit, suspend () -> Unit>(fixture.fixture()) fulfils KMockContract.FunMockery::class
    }

    @Test
    @JsName("fn0_a")
    fun `It fulfils AsyncFunMockery`() {
        AsyncFunMockery<Unit, suspend () -> Unit>(fixture.fixture()) fulfils KMockContract.AsyncFunMockery::class
    }

    @Test
    @JsName("fn1")
    fun `Given a returnValue is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValue = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.returnValue mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2")
    fun `Given a returnValue is set with nullable value it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any?, suspend () -> Any?>(fixture.fixture())
        val value: Any? = null

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValue = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.returnValue mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn3")
    fun `Given a returnValues is set with an emptyList it fails`() {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            mockery.returnValues = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn4")
    fun `Given a returnValues is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.returnValues = values
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.returnValues mustBe values
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn5")
    fun `Given a sideEffect is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
        val effect: suspend () -> Any = { fixture.fixture() }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = effect
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.sideEffect sameAs effect
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn6")
    fun `Given invoke is called it fails if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(name)

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // Then
            val error = assertFailsWith<MockError.MissingStub> {
                // When
                mockery.invoke()
            }

            error.message mustBe "Missing stub value for $name"
        }
    }

    @Test
    @JsName("fn7")
    fun `Given invoke is called it returns the ReturnValue threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn8")
    fun `Given invoke is called it returns the ReturnValues threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn9")
    fun `Given invoke is called it returns the last ReturnValue if the given List is down to one value threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn10")
    fun `Given invoke is called it calls the given SideEffect and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn11")
    fun `Given invoke is called it uses ReturnValues over ReturnValue`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn12")
    fun `Given invoke is called it uses SideEffect over ReturnValues`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn13")
    fun `Given invoke is called it captures Arguments threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, (String) -> Any>(fixture.fixture())
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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn14")
    fun `Given invoke is called it captures void Arguments threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn15")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = AsyncFunMockery<Any, suspend () -> Any>(name).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn16")
    fun `Its default call count is 0`() {
        AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture()).calls mustBe 0
    }

    @Test
    @JsName("fn17")
    fun `Given invoke is called it increments the call counter threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn18")
    fun `Given the mockery has a Collector and invoke is called it calls the Collect`(): AsyncTestReturnValue {
        // Given
        val values: List<Any> = fixture.listFixture(size = 5)

        val capturedMock = AtomicReference<KMockContract.Mockery<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture(), collector)

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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn34")
    fun `Given invoke is called it calls the given SideEffect with 0 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
        val expected: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = {
                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
            val actual = mockery.invoke()

            // Then
            actual mustBe expected
            mockery.getArgumentsForCall(0) mustBe null
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn35")
    fun `Given invoke is called it calls the given SideEffect with 1 Argument and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0 ->
                actualArgument0.set(givenArg0)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0

            val arguments = mockery.getArgumentsForCall(0)
            arguments?.size mustBe 1
            arguments!![0] mustBe argument0
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn36")
    fun `Given invoke is called it calls the given SideEffect with 2 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1 ->

                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 2
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn37")
    fun `Given invoke is called it calls the given SideEffect with 3 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 3
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn38")
    fun `Given invoke is called it calls the given SideEffect with 4 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2, argument3)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 4
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn39")
    fun `Given invoke is called it calls the given SideEffect with 5 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)

        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)

                expected
            }
        }

        runBlockingTest {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2, argument3, argument4)
            actual mustBe expected

            // Then
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 5
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn40")
    fun `Given invoke is called it calls the given SideEffect with 6 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2, argument3, argument4, argument5)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5

            val arguments = mockery.getArgumentsForCall(0)
            arguments!!.size mustBe 6
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn41")
    fun `Given invoke is called it calls the given SideEffect with 7 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)

        // When
        runBlockingTestWithTimeoutInScope(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(argument0, argument1, argument2, argument3, argument4, argument5, argument6)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6

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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn42")
    fun `Given invoke is called it calls the given SideEffect with 8 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
            // When
            val actual = mockery.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7

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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn43")
    fun `Given invoke is called it calls the given SideEffect with 9 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
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

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
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
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8

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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn44")
    fun `Given invoke is called it calls the given SideEffect with 10 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
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

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
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
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9

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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn45")
    fun `Given invoke is called it calls the given SideEffect with 11 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
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

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
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
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10

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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn46")
    fun `Given invoke is called it calls the given SideEffect with 12 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
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

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)
        val actualArgument11 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)
                actualArgument11.set(givenArg11)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
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
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10
            actualArgument11.get() mustBe argument11

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

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn47")
    fun `Given invoke is called it calls the given SideEffect with 13 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
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

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)
        val actualArgument11 = AtomicReference<Int?>(null)
        val actualArgument12 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11, givenArg12 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)
                actualArgument11.set(givenArg11)
                actualArgument12.set(givenArg12)

                expected
            }
        }

        runBlockingTestWithTimeoutInScope(testScope2.coroutineContext) {
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
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10
            actualArgument11.get() mustBe argument11
            actualArgument12.get() mustBe argument12

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

        return resolveMultiBlockCalls()
    }

    @Test
    @IgnoreJs
    @JsName("fn48")
    fun `Given clear is called it clears the mock`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: suspend () -> Any = {
            fixture.fixture()
        }

        mockery.returnValue = value
        mockery.returnValues = values
        mockery.sideEffect = sideEffect

        return runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.invoke()

            mockery.clear()

            mockery.returnValue mustBe null

            try {
                mockery.returnValues
            } catch (error: Throwable) {
                (error is NullPointerException) mustBe true
            }

            try {
                mockery.sideEffect mustBe null
            } catch (error: Throwable) {
                (error is NullPointerException) mustBe true
            }

            mockery.calls mustBe 0

            try {
                mockery.getArgumentsForCall(0)
            } catch (error: Throwable) {
                (error is IndexOutOfBoundsException) mustBe true
            }
        }
    }

    @Test
    @JsOnly
    @JsName("fn49")
    fun `Given clear is called it clears the mock for Js`(): AsyncTestReturnValue {
        // Given
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: suspend () -> Any = {
            fixture.fixture()
        }

        mockery.returnValue = value
        mockery.returnValues = values
        mockery.sideEffect = sideEffect

        return runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.invoke()

            mockery.clear()

            mockery.returnValue mustBe null

            try {
                mockery.returnValues
            } catch (error: Throwable) {
                (error is ClassCastException) mustBe true
            }

            try {
                mockery.sideEffect mustBe null
            } catch (error: Throwable) {
                (error is ClassCastException) mustBe true
            }

            mockery.calls mustBe 0

            try {
                mockery.getArgumentsForCall(0)
            } catch (error: Throwable) {
                (error is IndexOutOfBoundsException) mustBe true
            }
        }
    }
}
