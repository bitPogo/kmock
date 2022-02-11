/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.concurrency.AtomicReference
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import tech.antibytes.util.test.MockError
import tech.antibytes.util.test.annotations.IgnoreJs
import tech.antibytes.util.test.annotations.JsOnly
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.asyncMultiBlock
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

class PropertyMockerySpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @Test
    @JsName("fn0")
    fun `It fulfils PropMockery`() {
        PropertyMockery<Unit>(fixture.fixture()) fulfils KMockContract.PropMockery::class
    }

    @Test
    @JsName("fn1")
    fun `Given a get is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.get = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.get mustBe value
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn2")
    fun `Given a get is set with nullable value it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any?>(fixture.fixture())
        val value: Any? = null

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.get = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.get mustBe value
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn3")
    fun `Given a getMany is set with an emptyList it fails`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            mockery.getMany = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn4")
    fun `Given a getMany is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.getMany = values
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.getMany mustBe values
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn5")
    fun `Given a set is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val effect: (Any) -> Unit = { }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.set = effect
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.set sameAs effect
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn6")
    fun `Given onGet is called it fails if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val mockery = PropertyMockery<Any>(name)

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // Then
            val error = assertFailsWith<MockError.MissingStub> {
                // When
                mockery.onGet()
            }

            error.message mustBe "Missing stub value for $name"
        }
    }

    @Test
    @JsName("fn7")
    fun `Given onGet is called it returns the Get Value threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val value: String = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.get = value
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = mockery.onGet()

            // Then
            actual mustBe value
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn8")
    fun `Given onGet is called it returns the GetMany Value threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val values: List<String> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.getMany = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            values.forEach { value ->
                val actual = mockery.onGet()

                // Then
                actual mustBe value
            }
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn9")
    fun `Given onGet is called it returns the last GetMany Value if the given List is down to one value threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 1)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.getMany = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            for (x in 0 until 10) {
                val actual = mockery.onGet()

                // Then
                actual mustBe values.first()
            }
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn10")
    fun `Given onGet is called it uses GetMany over Get`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.get = value
            mockery.getMany = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = mockery.onGet()

            // Then
            actual mustBe values.first()
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn11")
    fun `Given onSet is called it calls the given SideEffect and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val newValue: Any = fixture.fixture()

        val actualNew = Channel<Any>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.set = { givenNew ->
                testScope1.launch {
                    actualNew.send(givenNew)
                }
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = mockery.onSet(newValue)

            // Then
            actual mustBe Unit
            actualNew.receive() mustBe newValue
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn12")
    fun `Given onGet is called it sets an Arguments to capture the call threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.getMany = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.onGet()
        }

        runBlockingTest {
            val actual = mockery.getArgumentsForCall(0)

            actual fulfils KMockContract.GetOrSet.Get::class
            actual.value mustBe null
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn13")
    fun `Given onSet is called it sets an Arguments to capture the call threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.onSet(value)
        }

        runBlockingTest {
            val actual = mockery.getArgumentsForCall(0)

            actual fulfils KMockContract.GetOrSet.Set::class
            actual.value mustBe value
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn14")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = PropertyMockery<Any>(name).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn15")
    fun `Its default call count is 0`() {
        PropertyMockery<Any>(fixture.fixture()).calls mustBe 0
    }

    @Test
    @JsName("fn17")
    fun `Given onGet is called it increments the call counter threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.getMany = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.onGet()
        }

        runBlockingTest {
            mockery.calls mustBe 1
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn18")
    fun `Given onSet is called it increments the call counter threadsafe`(): AsyncTestReturnValue {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.onSet(value)
        }

        runBlockingTest {
            mockery.calls mustBe 1
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn19")
    fun `Given the mockery has a Collector and onGet is called it calls the Collect`(): AsyncTestReturnValue {
        // Given
        val values: List<Any> = fixture.listFixture(size = 5)

        val capturedMock = AtomicReference<KMockContract.Mockery<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = KMockContract.Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val mockery = PropertyMockery<Any>(fixture.fixture(), collector)

        runBlockingTestInContext(testScope1.coroutineContext) {
            mockery.getMany = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.onGet()
        }

        runBlockingTest {
            capturedMock.get()?.id mustBe mockery.id
            capturedCalledIdx.get() mustBe 0
        }

        return asyncMultiBlock
    }

    @Test
    @JsName("fn20")
    fun `Given the mockery has a Collector and onSet is called it calls the Collect`(): AsyncTestReturnValue {
        // Given
        val value: Any = fixture.fixture()

        val capturedMock = AtomicReference<KMockContract.Mockery<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = KMockContract.Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val mockery = PropertyMockery<Any>(fixture.fixture(), collector)

        runBlockingTestInContext(testScope2.coroutineContext) {
            mockery.onSet(value)
        }

        runBlockingTest {
            capturedMock.get()?.id mustBe mockery.id
            capturedCalledIdx.get() mustBe 0
        }

        return asyncMultiBlock
    }
}
