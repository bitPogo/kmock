/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.error.MockError
import tech.antibytes.mock.VerificationChainStub
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.resolveMultiBlockCalls
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
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

class SyncFunProxySpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils FunProxy`() {
        SyncFunProxy<Unit, () -> Unit>(fixture.fixture()) fulfils KMockContract.FunProxy::class
    }

    @Test
    @JsName("fn0_a")
    fun `It fulfils SyncFunProxy`() {
        SyncFunProxy<Unit, () -> Unit>(fixture.fixture()) fulfils KMockContract.SyncFunProxy::class
    }

    @Test
    @JsName("fn0_b")
    fun `It is not ignorable for verfication by default`() {
        SyncFunProxy<Unit, () -> Unit>(fixture.fixture()).ignorableForVerification mustBe false
    }

    @Test
    @JsName("fn0_c")
    fun `It is can be ignored for verfication if told to`() {
        SyncFunProxy<Unit, () -> Unit>(
            fixture.fixture(),
            ignorableForVerification = true
        ).ignorableForVerification mustBe true
    }

    @Test
    @JsName("fn1")
    fun `Given a throws is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val error = RuntimeException(fixture.fixture<String>())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.throws = error
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.throws mustBe error
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2")
    fun `Given a returnValue is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValue = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.returnValue mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn3")
    fun `Given a returnValue is set with nullable value it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any?, () -> Any?>(fixture.fixture())
        val value: Any? = null

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValue = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.returnValue mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn4")
    fun `Given a returnValues is set with an emptyList it fails`() {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            proxy.returnValues = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn5")
    fun `Given a returnValues is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValues = values
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.returnValues mustBe values
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn6")
    fun `Given a sideEffect is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val effect: () -> Any = { fixture.fixture() }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = effect
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.sideEffect sameAs effect
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn7")
    fun `Given invoke is called it fails if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val proxy = SyncFunProxy<Any, () -> Any>(name)

        // When
        return runBlockingTestInContext(testScope1.coroutineContext) {
            // Then
            val error = assertFailsWith<MockError.MissingStub> {
                proxy.invoke()
            }

            error.message mustBe "Missing stub value for $name"
        }
    }

    @Test
    @JsName("fn7a")
    fun `Given invoke is called it uses the given Relaxer if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val proxy = SyncFunProxy<Any, () -> Any>(
            name,
            relaxer = { givenId ->
                capturedId.set(givenId)

                value
            }
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke()

            // Then
            actual mustBe value
            capturedId.value mustBe name
        }
    }

    @Test
    @JsName("fn7b")
    fun `Given invoke is called it uses the given UnitFunRelaxer if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val proxy = AsyncFunProxy<Any, suspend () -> Unit>(
            name,
            unitFunRelaxer = { givenId ->
                capturedId.set(givenId)

                value
            }
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke()

            // Then
            actual mustBe value
            capturedId.value mustBe name
        }
    }

    @Test
    @JsName("fn7c")
    fun `Given invoke is called it uses the given Implementation if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val implementation = Implementation<Any>()
        implementation.fun0 = { value }

        val proxy = SyncFunProxy<Any, () -> Any>(
            name,
            spyOn = implementation::fun0
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke()

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn7d")
    fun `Given invoke with 0 arguments is called it uses the given ParameterizedRelaxer if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedArgument = AtomicReference<Any?>(Any())
        val proxy = SyncFunProxy<Any, () -> Any>(
            name,
            buildInRelaxer = { givenArgument ->
                capturedArgument.set(givenArgument)

                value
            }
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke()

            // Then
            actual mustBe value
            capturedArgument.value mustBe null
        }
    }

    @Test
    @JsName("fn7e")
    fun `Given invoke with 1 arguments is called it uses the given ParameterizedRelaxer if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val argument: Any = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedArgument = AtomicReference<Any?>(null)
        val proxy = SyncFunProxy<Any, () -> Any>(
            name,
            buildInRelaxer = { givenArgument ->
                capturedArgument.set(givenArgument)

                value
            }
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke(argument)

            // Then
            actual mustBe value
            capturedArgument.value sameAs argument
        }
    }

    @Test
    @JsName("fn7f")
    fun `Given invoke with more then 1 arguments is called it uses the given ParameterizedRelaxer if no ReturnValue Provider with only the first argument is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val argument: Any = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedArgument = AtomicReference<Any?>(null)
        val proxy = SyncFunProxy<Any, () -> Any>(
            name,
            buildInRelaxer = { givenArgument ->
                capturedArgument.set(givenArgument)

                value
            }
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke(argument, fixture.fixture<Any>())

            // Then
            actual mustBe value
            capturedArgument.value sameAs argument
        }
    }

    @Test
    @JsName("fn8")
    fun `Given invoke is called it throws Throws threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val error = RuntimeException(fixture.fixture<String>())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.throws = error
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = assertFailsWith<RuntimeException> {
                proxy.invoke()
            }

            // Then
            actual mustBe error
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn9")
    fun `Given invoke is called it returns the ReturnValue threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val value: String = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValue = value
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke()

            // Then
            actual mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn10")
    fun `Given invoke is called it returns the ReturnValues threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            values.forEach { value ->
                val actual = proxy.invoke()

                // Then
                actual mustBe value
            }
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn11")
    fun `Given invoke is called it returns the last ReturnValue if the given List is down to one value threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 1)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            for (x in 0 until 10) {
                val actual = proxy.invoke()

                // Then
                actual mustBe values.first()
            }
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn12")
    fun `Given invoke is called it calls the given SideEffect and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke(argument0, argument1)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn13")
    fun `Given invoke is called it calls the given SideEffectChain and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = Channel<String>()
        val actualArgument1 = Channel<Int>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1 ->
                testScope1.launch {
                    actualArgument0.send(givenArg0)
                    actualArgument1.send(givenArg1)
                }

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke(argument0, argument1)

            // Then
            actual mustBe expected
            actualArgument0.receive() mustBe argument0
            actualArgument1.receive() mustBe argument1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn14")
    fun `Given invoke is called it uses ReturnValue over Throws`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val error = RuntimeException(fixture.fixture<String>())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValue = value
            proxy.throws = error
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke()

            // Then
            actual mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn15")
    fun `Given invoke is called it uses ReturnValues over ReturnValue`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValue = value
            proxy.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke()

            // Then
            actual mustBe values.first()
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn16")
    fun `Given invoke is called it uses SideEffect over ReturnValues`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val expected: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { expected }
            proxy.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke()

            // Then
            actual mustBe expected
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn17")
    fun `Given invoke is called it uses SideEffects over SideEffect`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val expected: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { expected }
            proxy.sideEffect = { values }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke()

            // Then
            actual mustBe expected
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn18")
    fun `Given invoke is called it fails if no Arguments were captured`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = SyncFunProxy<Any, suspend () -> Any>(id)

        // When
        val error = assertFailsWith<MockError.MissingCall> {
            proxy.getArgumentsForCall(0)
        }

        // Then
        error.message mustBe "0 was not found for $id!"
    }

    @Test
    @JsName("fn19")
    fun `Given invoke is called it captures Arguments threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String) -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)
        val argument: String = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.invoke(argument)
        }

        runBlockingTest {
            val actual = proxy.getArgumentsForCall(0)

            actual.size mustBe 1
            actual[0] mustBe argument
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn20")
    fun `Given invoke is called it captures void Arguments threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.invoke()
        }

        runBlockingTest {
            val actual = proxy.getArgumentsForCall(0)

            actual.toList() mustBe arrayOf<Any>().toList()
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn21")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = SyncFunProxy<Any, () -> Any>(name).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn22")
    fun `Its default call count is 0`() {
        SyncFunProxy<Any, () -> Any>(fixture.fixture()).calls mustBe 0
    }

    @Test
    @JsName("fn23")
    fun `Given invoke is called it increments the call counter threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.invoke()
        }

        runBlockingTest {
            proxy.calls mustBe 1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn24")
    fun `Given the Proxy has a Collector and invoke is called it calls the Collect`(): AsyncTestReturnValue {
        // Given
        val values: List<Any> = fixture.listFixture(size = 5)

        val capturedMock = AtomicReference<KMockContract.Proxy<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture(), collector)

        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.invoke()
        }

        runBlockingTest {
            capturedMock.get()?.id mustBe proxy.id
            capturedCalledIdx.get() mustBe 0
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn25")
    fun `Given clear is called it clears the mock`() {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())

        val error = Throwable()
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: () -> Any = {
            fixture.fixture()
        }
        val sideEffectChained: () -> Any = {
            fixture.fixture()
        }

        proxy.throws = error
        proxy.returnValue = value
        proxy.returnValues = values
        proxy.sideEffect = sideEffect
        proxy.sideEffects.add(sideEffectChained)
        proxy.verificationChain = VerificationChainStub()

        // When
        proxy.invoke()

        proxy.clear()

        // Then
        assertFailsWith<NullPointerException> { proxy.throws }
        proxy.returnValue mustBe null

        assertFailsWith<IndexOutOfBoundsException> { proxy.returnValues[0] }
        assertFailsWith<NullPointerException> { proxy.sideEffect }
        assertFailsWith<Throwable> { (proxy.sideEffects as SideEffectChain).next() }

        proxy.calls mustBe 0
        proxy.verificationChain mustBe null

        assertFailsWith<MockError.MissingCall> { proxy.getArgumentsForCall(0) }
    }

    @Test
    @JsName("fn26")
    fun `Given clear is called it clears the mock while leave the spy intact`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val error = Throwable()
        val valueImpl: Any = fixture.fixture()
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: () -> Any = {
            fixture.fixture()
        }
        val sideEffectChain: () -> Any = {
            fixture.fixture()
        }

        val proxy = SyncFunProxy<Any, () -> Any>(
            fixture.fixture(),
            spyOn = implementation::fun0
        )

        implementation.fun0 = { valueImpl }

        proxy.throws = error
        proxy.returnValue = value
        proxy.returnValues = values
        proxy.sideEffect = sideEffect
        proxy.sideEffects.add(sideEffectChain)
        proxy.verificationChain = VerificationChainStub()

        return runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.invoke()

            proxy.clear()

            val actual = proxy.invoke()

            actual mustBe valueImpl
        }
    }

    private class Implementation<T>(
        @JsName("fun0a")
        var fun0: (() -> T)? = null,
    ) {
        fun fun0(): T {
            return fun0?.invoke() ?: throw RuntimeException("Missing sideeffect fun0")
        }
    }
}
