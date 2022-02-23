/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
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
    @JsName("fn6a")
    fun `Given invoke is called it uses the given Relaxer if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val mockery = AsyncFunMockery<Any, suspend () -> Any>(
            name,
            relaxer = { givenId ->
                capturedId.set(givenId)

                value
            }
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = mockery.invoke()

            // Then
            actual mustBe value
            capturedId.value mustBe name
        }
    }

    @Test
    @JsName("fn6b")
    fun `Given invoke is called it uses the given Implementation if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val implementation = Implementation<Any>()
        implementation.fun0 = { value }

        val mockery = AsyncFunMockery<Any, suspend () -> Any>(
            name,
            spyOn = implementation::fun0
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = mockery.invoke()

            // Then
            actual mustBe value
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
    @IgnoreJs
    @JsName("fn19")
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
    @JsName("fn20")
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

    private class Implementation<T>(
        var fun0: (suspend () -> T)? = null,
        var fun1: (suspend (Any) -> T)? = null
    ) {
        suspend fun fun0(): T {
            return fun0?.invoke() ?: throw RuntimeException("Missing sideeffect fun0")
        }
    }
}
