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
    fun `It is not ignorable for verification by default`() {
        SyncFunProxy<Unit, () -> Unit>(fixture.fixture()).ignorableForVerification mustBe false
    }

    @Test
    @JsName("fn0_c")
    fun `It is can be ignored for verification if told to`() {
        SyncFunProxy<Unit, () -> Unit>(
            fixture.fixture(),
            ignorableForVerification = true
        ).ignorableForVerification mustBe true
    }

    @Test
    @JsName("fn1")
    fun `It is frozen by default`() {
        SyncFunProxy<Any, () -> Any>(fixture.fixture()).frozen mustBe true
    }

    @Test
    @JsName("fn2")
    fun `Given a error is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val error = RuntimeException(fixture.fixture<String>())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.error = error
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.error mustBe error
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2a")
    fun `throws is an alias setter of error`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val error = RuntimeException()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy throws error
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.error mustBe error
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2b")
    fun `Given errors is set with an emptyList it fails`() {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            proxy.errors = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn2c")
    fun `Given errors is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val errors = listOf(RuntimeException(), RuntimeException())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.errors = errors
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.errors mustBe errors
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2d")
    fun `throwMany is an alias setter of errors`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val errors = listOf(RuntimeException(), RuntimeException())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy throwsMany errors
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.errors mustBe errors
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2e")
    fun `throwMany is an alias of errors`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val errors = listOf(RuntimeException(), RuntimeException())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.throwsMany = errors
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.errors mustBe errors
            proxy.throwsMany mustBe errors
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn3")
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
    @JsName("fn3a")
    fun `returns is a alias to returnValue`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy returns value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.returnValue mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn3b")
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
    @JsName("fn5a")
    fun `returnsMany is an alias setter of returnValues`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy returnsMany values
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
        val proxy = SyncFunProxy<Any, () -> Any>(name)

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke {
                useRelaxerIf(true) { givenId ->
                    capturedId.set(givenId)

                    value
                }
            }

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
        val proxy = AsyncFunProxy<Any, suspend () -> Unit>(name)

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke {
                useUnitFunRelaxerIf(true)
            }

            // Then
            actual mustBe Unit
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
        )

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.invoke {
                useSpyIf(implementation) {
                    implementation.fun0()
                }
            }

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn8")
    fun `Given invoke is called it throws Error threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val error = RuntimeException(fixture.fixture<String>())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.error = error
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
    @JsName("fn8a")
    fun `Given invoke is called it throws the given Errors threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val errors = listOf(RuntimeException(), RuntimeException())

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.errors = errors
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            errors.forEach { error ->
                val actual = assertFailsWith<RuntimeException> {
                    proxy.invoke()
                }

                // Then
                actual mustBe error
            }
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
            repeat(10) {
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
    @JsName("fn12a")
    fun `Given run is called SideEffect it overrides SideEffect`() {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(fixture.fixture())
        val sideEffect0: (String, Int) -> Any = { _, _ -> fixture.fixture() }
        val sideEffect1: (String, Int) -> Any = { _, _ -> fixture.fixture() }

        // When
        proxy.sideEffect = sideEffect0
        proxy.run(sideEffect1)

        // Then
        proxy.sideEffect sameAs sideEffect1
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
    @JsName("fn13a")
    fun `Given invoke is called it calls the given runs and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()

        val expected0: Any = fixture.fixture()
        val expected1: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy runs { givenArg0, givenArg1 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)

                expected0
            } runs { givenArg0, givenArg1 ->
                actualArgument2.set(givenArg0)
                actualArgument3.set(givenArg1)

                expected1
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1)

            // Then
            actual mustBe expected0
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1)

            // Then
            actual mustBe expected1
            actualArgument2.get() mustBe argument0
            actualArgument3.get() mustBe argument1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn14")
    fun `Given invoke is called it uses Errors over Error`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val error = RuntimeException(fixture.fixture<String>())
        val errors = listOf(RuntimeException(fixture.fixture<String>()), RuntimeException(fixture.fixture<String>()))

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.error = error
            proxy.errors = errors
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = assertFailsWith<RuntimeException> {
                proxy.invoke()
            }

            // Then
            actual mustBe errors.first()
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn14a")
    fun `Given invoke is called it uses ReturnValue over Errors`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val error = listOf(RuntimeException(fixture.fixture<String>()))

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.returnValue = value
            proxy.errors = error
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
    @JsName("fn17a")
    fun `Given invoke is called it uses SideEffects which are delegated via runs`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val expected0: Any = fixture.fixture()
        val expected1: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.runs { expected0 }
            proxy.sideEffects.add { expected1 }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual0 = proxy.invoke()
            val actual1 = proxy.invoke()

            // Then
            actual0 mustBe expected0
            actual1 mustBe expected1
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
    @JsName("fn19a")
    fun `Given invoke is called it captures Arguments which can be accessed ArrayStyle`(): AsyncTestReturnValue {
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
            val actual = proxy[0]

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
        val errors = listOf(RuntimeException(fixture.fixture<String>()), RuntimeException(fixture.fixture<String>()))
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: () -> Any = {
            fixture.fixture()
        }
        val sideEffectChained: () -> Any = {
            fixture.fixture()
        }

        proxy.error = error
        proxy.errors = errors
        proxy.returnValue = value
        proxy.returnValues = values
        proxy.sideEffect = sideEffect
        proxy.sideEffects.add(sideEffectChained)
        proxy.runs(sideEffectChained)

        // When
        proxy.invoke()

        proxy.clear()

        // Then
        assertFailsWith<NullPointerException> { proxy.error mustBe null }
        proxy.errors mustBe emptyList()
        proxy.returnValue mustBe null

        assertFailsWith<IndexOutOfBoundsException> { proxy.returnValues[0] }
        assertFailsWith<NullPointerException> { proxy.sideEffect }
        assertFailsWith<Throwable> { (proxy.sideEffects as SideEffectChain).next() }

        proxy.calls mustBe 0

        assertFailsWith<MockError.MissingCall> { proxy.getArgumentsForCall(0) }
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
