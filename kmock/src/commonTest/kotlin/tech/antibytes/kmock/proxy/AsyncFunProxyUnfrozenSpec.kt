/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.error.MockError
import tech.antibytes.util.test.annotations.IgnoreJs
import tech.antibytes.util.test.annotations.JsOnly
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AsyncFunProxyUnfrozenSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils FunProxy`() {
        AsyncFunProxy<Unit, suspend () -> Unit>(fixture.fixture()) fulfils KMockContract.FunProxy::class
    }

    @Test
    @JsName("fn1")
    fun `Given a throws is set it is retrievable`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val error = RuntimeException(fixture.fixture<String>())

        // When
        Proxy.throws = error

        // Then
        Proxy.throws mustBe error
    }

    @Test
    @JsName("fn2")
    fun `Given a returnValue is set it is retrievable`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        Proxy.returnValue = value

        // Then
        Proxy.returnValue mustBe value
    }

    @Test
    @JsName("fn3")
    fun `Given a returnValue is set with nullable value it is retrievable`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any?, suspend () -> Any?>(fixture.fixture())
        val value: Any? = null

        // When
        Proxy.returnValue = value

        // Then
        Proxy.returnValue mustBe value
    }

    @Test
    @JsName("fn4")
    fun `Given a returnValues is set with an emptyList it fails`() {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            Proxy.returnValues = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn5")
    fun `Given a returnValues is set it is retrievable`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture()

        // When
        Proxy.returnValues = values

        // Then
        Proxy.returnValues mustBe values
    }

    @Test
    @JsName("fn6")
    fun `Given a sideEffect is set it is retrievable`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val effect: suspend () -> Any = { fixture.fixture() }

        // When
        Proxy.sideEffect = effect

        // Then
        Proxy.sideEffect sameAs effect
    }

    @Test
    @JsName("fn7")
    fun `Given invoke is called it fails if no ReturnValue Provider is set`() = runBlockingTest {
        // Given
        val name: String = fixture.fixture()
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(name)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            // When
            Proxy.invoke()
        }

        error.message mustBe "Missing stub value for $name"
    }

    @Test
    @JsName("fn7a")
    fun `Given invoke is called it uses the given Relaxer if no ReturnValue Provider is set`() = runBlockingTest {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(
            name,
            relaxer = { givenId ->
                capturedId.set(givenId)

                value
            },
            freeze = false
        )

        // When
        val actual = Proxy.invoke()

        // Then
        actual mustBe value
        capturedId.value mustBe name
    }

    @Test
    @JsName("fn7b")
    fun `Given invoke is called it uses the given UnitFunRelaxer if no ReturnValue Provider is set`(): AsyncTestReturnValue = runBlockingTest {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val Proxy = AsyncFunProxy<Any, suspend () -> Unit>(
            name,
            unitFunRelaxer = { givenId ->
                capturedId.set(givenId)

                value
            }
        )

        // When
        val actual = Proxy.invoke()

        // Then
        actual mustBe value
        capturedId.value mustBe name
    }

    @Test
    @JsName("fn7c")
    fun `Given invoke is called it uses the given Implementation if no ReturnValue Provider is set`() = runBlockingTest {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val implementation = Implementation<Any>()
        implementation.fun0 = { value }

        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(
            name,
            spyOn = implementation::fun0,
            freeze = false
        )

        // When
        val actual = Proxy.invoke()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn8")
    fun `Given invoke is called it throws the Throwable`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val error = RuntimeException(fixture.fixture<String>())

        // When
        Proxy.throws = error

        val actual = assertFailsWith<RuntimeException> { Proxy.invoke() }

        // Then
        actual mustBe error
    }

    @Test
    @JsName("fn9")
    fun `Given invoke is called it returns the ReturnValue`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val value: String = fixture.fixture()

        // When
        Proxy.returnValue = value

        val actual = Proxy.invoke()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn10")
    fun `Given invoke is called it returns the ReturnValues`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        Proxy.returnValues = values.toList()

        values.forEach { value ->
            val actual = Proxy.invoke()

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn11")
    fun `Given invoke is called it returns the last ReturnValue if the given List is down to one value`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 1)

        // When
        Proxy.returnValues = values.toList()

        for (x in 0 until 10) {
            val actual = Proxy.invoke()

            // Then
            actual mustBe values.first()
        }
    }

    @Test
    @JsName("fn12")
    fun `Given invoke is called it calls the given SideEffect and delegates values`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend (String, Int) -> Any>(fixture.fixture(), freeze = false)
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)

        // When
        Proxy.sideEffect = { givenArg0, givenArg1 ->
            actualArgument0.set(givenArg0)
            actualArgument1.set(givenArg1)

            expected
        }

        // When
        val actual = Proxy.invoke(argument0, argument1)

        // Then
        actual mustBe expected
        actualArgument0.get() mustBe argument0
        actualArgument1.get() mustBe argument1
    }

    @Test
    @JsName("fn13")
    fun `Given invoke is called it uses ReturnValue over Throws`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val error = RuntimeException(fixture.fixture<String>())

        // When
        Proxy.returnValue = value
        Proxy.throws = error

        val actual = Proxy.invoke()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn14")
    fun `Given invoke is called it uses ReturnValues over ReturnValue`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        Proxy.returnValue = value
        Proxy.returnValues = values

        val actual = Proxy.invoke()

        // Then
        actual mustBe values.first()
    }

    @Test
    @JsName("fn15")
    fun `Given invoke is called it uses SideEffect over ReturnValues`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val expected: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        Proxy.sideEffect = { expected }
        Proxy.returnValues = values

        val actual = Proxy.invoke()

        // Then
        actual mustBe expected
    }

    @Test
    @JsName("fn16")
    fun `Given invoke is called it captures Arguments`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, (String) -> Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)
        val argument: String = fixture.fixture()

        // When
        Proxy.returnValues = values.toList()

        Proxy.invoke(argument)

        val actual = Proxy.getArgumentsForCall(0)

        // Then
        actual!!.size mustBe 1
        actual[0] mustBe argument
    }

    @Test
    @JsName("fn17")
    fun `Given invoke is called it captures void Arguments`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        Proxy.returnValues = values

        Proxy.invoke()

        val actual = Proxy.getArgumentsForCall(0)

        // Then
        actual mustBe null
    }

    @Test
    @JsName("fn18")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = AsyncFunProxy<Any, suspend () -> Any>(name, freeze = false).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn19")
    fun `Its default call count is 0`() {
        AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false).calls mustBe 0
    }

    @Test
    @JsName("fn20")
    fun `Given invoke is called it increments the call counter`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        Proxy.returnValues = values

        Proxy.invoke()

        Proxy.calls mustBe 1
    }

    @Test
    @JsName("fn21")
    fun `Given the Proxy has a Collector and invoke is called it calls the Collect`() = runBlockingTest {
        // Given
        val values: List<Any> = fixture.listFixture(size = 5) // NOTE: These values get frozen

        var capturedMock: KMockContract.Proxy<*, *>? = null
        var capturedCalledIdx: Int? = null

        val collector = Collector { referredMock, referredCall ->
            capturedMock = referredMock
            capturedCalledIdx = referredCall
        }

        // When
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), collector, freeze = false)

        Proxy.returnValues = values

        Proxy.invoke()

        capturedMock?.id mustBe Proxy.id
        capturedCalledIdx mustBe 0
    }

    @Test
    @IgnoreJs
    @JsName("fn22")
    fun `Given clear is called it clears the mock`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: suspend () -> Any = {
            fixture.fixture()
        }

        Proxy.returnValue = value
        Proxy.returnValues = values
        Proxy.sideEffect = sideEffect

        Proxy.invoke()

        Proxy.clear()

        Proxy.returnValue mustBe null

        try {
            Proxy.returnValues
        } catch (error: Throwable) {
            (error is NullPointerException) mustBe true
        }

        try {
            Proxy.sideEffect mustBe null
        } catch (error: Throwable) {
            (error is NullPointerException) mustBe true
        }

        Proxy.calls mustBe 0

        try {
            Proxy.getArgumentsForCall(0)
        } catch (error: Throwable) {
            (error is IndexOutOfBoundsException) mustBe true
        }
    }

    @Test
    @JsOnly
    @JsName("fn23")
    fun `Given clear is called it clears the mock for Js`() = runBlockingTest {
        // Given
        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: suspend () -> Any = {
            fixture.fixture()
        }

        Proxy.returnValue = value
        Proxy.returnValues = values
        Proxy.sideEffect = sideEffect

        Proxy.invoke()

        Proxy.clear()

        Proxy.returnValue mustBe null

        try {
            Proxy.returnValues
        } catch (error: Throwable) {
            (error is ClassCastException) mustBe true
        }

        try {
            Proxy.sideEffect mustBe null
        } catch (error: Throwable) {
            (error is ClassCastException) mustBe true
        }

        Proxy.calls mustBe 0

        try {
            Proxy.getArgumentsForCall(0)
        } catch (error: Throwable) {
            (error is IndexOutOfBoundsException) mustBe true
        }
    }

    @Test
    @JsName("fn24")
    fun `Given clear is called it clears the mock while leave the spy intact`() = runBlockingTest {
        // Given
        val implementation = Implementation<Any>()

        val valueImpl: Any = fixture.fixture()
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: suspend () -> Any = {
            fixture.fixture()
        }

        val Proxy = AsyncFunProxy<Any, suspend () -> Any>(
            fixture.fixture(),
            spyOn = implementation::fun0
        )

        implementation.fun0 = { valueImpl }

        Proxy.returnValue = value
        Proxy.returnValues = values
        Proxy.sideEffect = sideEffect

        Proxy.invoke()

        Proxy.clear()

        val actual = Proxy.invoke()

        actual mustBe valueImpl
    }

    private class Implementation<T>(
        @JsName("fun0a")
        var fun0: (suspend () -> T)? = null,
    ) {
        suspend fun fun0(): T {
            return fun0?.invoke() ?: throw RuntimeException("Missing sideeffect fun0")
        }
    }
}