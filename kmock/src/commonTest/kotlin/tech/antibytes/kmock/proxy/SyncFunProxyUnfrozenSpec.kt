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

class SyncFunProxyUnfrozenSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils FunProxy`() {
        SyncFunProxy<Unit, () -> Unit>(fixture.fixture(), freeze = false) fulfils KMockContract.FunProxy::class
    }

    @Test
    @JsName("fn0_a")
    fun `It fulfils SyncFunProxy`() {
        SyncFunProxy<Unit, () -> Unit>(fixture.fixture(), freeze = false) fulfils KMockContract.SyncFunProxy::class
    }

    @Test
    @JsName("fn0_b")
    fun `It is not ignorable for verfication by default`() {
        SyncFunProxy<Unit, () -> Unit>(fixture.fixture(), freeze = false).ignorableForVerification mustBe false
    }

    @Test
    @JsName("fn0_c")
    fun `It is can be ignored for verfication if told to`() {
        SyncFunProxy<Unit, () -> Unit>(
            fixture.fixture(),
            ignorableDuringVerification = true,
            freeze = false
        ).ignorableForVerification mustBe true
    }

    @Test
    @JsName("fn1")
    fun `Given a throws is set it is retrievable`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val error = RuntimeException(fixture.fixture<String>())

        // When
        proxy.throws = error

        // Then
        proxy.throws mustBe error
    }

    @Test
    @JsName("fn2")
    fun `Given a returnValue is set it is retrievable`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val value: Any = fixture.fixture()

        // When
        proxy.returnValue = value

        // Then
        proxy.returnValue mustBe value
    }

    @Test
    @JsName("fn3")
    fun `Given a returnValue is set with nullable value it is retrievable`() {
        // Given
        val proxy = SyncFunProxy<Any?, Function0<Any?>>(
            fixture.fixture(),
            freeze = false
        )
        val value: Any? = null

        // When
        proxy.returnValue = value

        // Then
        proxy.returnValue mustBe value
    }

    @Test
    @JsName("fn4")
    fun `Given a returnValues is set with an emptyList it fails`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            proxy.returnValues = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn5")
    fun `Given a returnValues is set it is retrievable`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val values: List<Any> = fixture.listFixture()

        // When
        proxy.returnValues = values

        // Then
        proxy.returnValues mustBe values
    }

    @Test
    @JsName("fn6")
    fun `Given a sideEffect is set it is retrievable`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val effect: () -> Any = { fixture.fixture() }

        // When
        proxy.sideEffect = effect

        // Then
        proxy.sideEffect sameAs effect
    }

    @Test
    @JsName("fn7")
    fun `Given invoke is called it fails if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = SyncFunProxy<Any, () -> Any>(name, freeze = false)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            // When
            proxy.invoke()
        }

        error.message mustBe "Missing stub value for $name"
    }

    @Test
    @JsName("fn7a")
    fun `Given invoke is called it uses the given Relaxer if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val proxy = SyncFunProxy<Any, () -> Any>(
            name,
            relaxer = { givenId ->
                capturedId.set(givenId)

                value
            },
            freeze = false
        )

        // When
        val actual = proxy.invoke()

        // Then
        actual mustBe value
        capturedId.value mustBe name
    }

    @Test
    @JsName("fn7b")
    fun `Given invoke is called it uses the given UnitFunRelaxer if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val proxy = SyncFunProxy<Any, () -> Unit>(
            name,
            unitFunRelaxer = { givenId ->
                capturedId.set(givenId)

                value
            },
            freeze = false
        )

        // When
        val actual = proxy.invoke()

        // Then
        actual mustBe value
        capturedId.value mustBe name
    }

    @Test
    @JsName("fn7c")
    fun `Given invoke is called it uses the given Implementation if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val implementation = Implementation<Any>()
        implementation.fun0 = { value }

        val proxy = SyncFunProxy<Any, () -> Any>(
            name,
            spyOn = implementation::fun0,
            freeze = false
        )

        // When
        val actual = proxy.invoke()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn7d")
    fun `Given invoke with 0 arguments is called it uses the given ParameterizedRelaxer if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedArgument = AtomicReference<Any?>(Any())
        val proxy = SyncFunProxy<Any, () -> Any>(
            name,
            buildInRelaxer = { givenArgument ->
                capturedArgument.set(givenArgument)

                value
            },
            freeze = false
        )

        // When
        val actual = proxy.invoke()

        // Then
        actual mustBe value
        capturedArgument.value mustBe null
    }

    @Test
    @JsName("fn7e")
    fun `Given invoke with arguments is called it uses the given ParameterizedRelaxer if no ReturnValue Provider is set`() {
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
            },
            freeze = false
        )

        // When
        val actual = proxy.invoke(argument)

        // Then
        actual mustBe value
        capturedArgument.value sameAs argument
    }

    @Test
    @JsName("fn7e")
    fun `Given invoke with more then 1 arguments is called it uses the given ParameterizedRelaxer if no ReturnValue Provider with only the first argument is set`() {
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
            },
            freeze = false
        )

        // When
        val actual = proxy.invoke(argument)

        // Then
        actual mustBe value
        capturedArgument.value sameAs argument
    }

    @Test
    @JsName("fn8")
    fun `Given invoke is called it throws Throws threadsafe`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val error = RuntimeException(fixture.fixture<String>())

        // When
        proxy.throws = error

        val actual = assertFailsWith<RuntimeException> {
            proxy.invoke()
        }

        // Then
        actual mustBe error
    }

    @Test
    @JsName("fn9")
    fun `Given invoke is called it returns the ReturnValue threadsafe`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val value: String = fixture.fixture()

        // When
        proxy.returnValue = value

        val actual = proxy.invoke()
        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn10")
    fun `Given invoke is called it returns the ReturnValues threadsafe`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        proxy.returnValues = values.toList()

        values.forEach { value ->
            val actual = proxy.invoke()

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn11")
    fun `Given invoke is called it returns the last ReturnValue if the given List is down to one value threadsafe`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val values: List<Any> = fixture.listFixture(size = 1)

        // When
        proxy.returnValues = values.toList()

        for (x in 0 until 10) {
            val actual = proxy.invoke()

            // Then
            actual mustBe values.first()
        }
    }

    @Test
    @JsName("fn12")
    fun `Given invoke is called it calls the given SideEffect and delegates values threadsafe`() {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        var actualArgument0: String? = null
        var actualArgument1: Int? = null

        // When

        proxy.sideEffect = { givenArg0, givenArg1 ->
            actualArgument0 = givenArg0
            actualArgument1 = givenArg1

            expected
        }

        val actual = proxy.invoke(argument0, argument1)

        // Then
        actual mustBe expected
        actualArgument0 mustBe argument0
        actualArgument1 mustBe argument1
    }

    @Test
    @JsName("fn13")
    fun `Given invoke is called it calls the given SideEffects and delegates values threadsafe`() {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(fixture.fixture(), freeze = false)
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        var actualArgument0: String? = null
        var actualArgument1: Int? = null

        // When

        proxy.sideEffects.add { givenArg0, givenArg1 ->
            actualArgument0 = givenArg0
            actualArgument1 = givenArg1

            expected
        }

        val actual = proxy.invoke(argument0, argument1)

        // Then
        actual mustBe expected
        actualArgument0 mustBe argument0
        actualArgument1 mustBe argument1
    }

    @Test
    @JsName("fn14")
    fun `Given invoke is called it uses ReturnValue over Throws`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val value: Any = fixture.fixture()
        val error = RuntimeException(fixture.fixture<String>())

        // When
        proxy.returnValue = value
        proxy.throws = error

        val actual = proxy.invoke()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn15")
    fun `Given invoke is called it uses ReturnValues over ReturnValue`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        proxy.returnValue = value
        proxy.returnValues = values

        val actual = proxy.invoke()

        // Then
        actual mustBe values.first()
    }

    @Test
    @JsName("fn16")
    fun `Given invoke is called it uses SideEffect over ReturnValues`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val expected: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        proxy.sideEffect = { expected }
        proxy.returnValues = values

        val actual = proxy.invoke()

        // Then
        actual mustBe expected
    }

    @Test
    @JsName("fn17")
    fun `Given invoke is called it uses SideEffects over SideEffect`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val expected: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        proxy.sideEffects.add { expected }
        proxy.sideEffect = { values }

        val actual = proxy.invoke()

        // Then
        actual mustBe expected
    }

    @Test
    @JsName("fn18")
    fun `Given invoke is called it fails if no Arguments were captured`() {
        // Given
        val id: String = fixture.fixture()
        val proxy = SyncFunProxy<Any, suspend () -> Any>(id, freeze = true)

        // When
        val error = assertFailsWith<MockError.MissingCall> {
            proxy.getArgumentsForCall(0)
        }

        // Then
        error.message mustBe "0 was not found for $id!"
    }

    @Test
    @JsName("fn19")
    fun `Given invoke is called it captures Arguments`() {
        // Given
        val proxy = SyncFunProxy<Any, (String) -> Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)
        val argument: String = fixture.fixture()

        // When
        proxy.returnValues = values.toList()

        proxy.invoke(argument)

        val actual = proxy.getArgumentsForCall(0)

        actual.size mustBe 1
        actual[0] mustBe argument
    }

    @Test
    @JsName("fn20")
    fun `Given invoke is called it captures void Arguments`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        proxy.returnValues = values
        proxy.invoke()

        val actual = proxy.getArgumentsForCall(0)

        actual.toList() mustBe arrayOf<Any>().toList()
    }

    @Test
    @JsName("fn21")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = SyncFunProxy<Any, () -> Any>(name, freeze = false).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn22")
    fun `Its default call count is 0`() {
        SyncFunProxy<Any, () -> Any>(fixture.fixture(), freeze = false).calls mustBe 0
    }

    @Test
    @JsName("fn23")
    fun `Given invoke is called it increments the call counter threadsafe`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        proxy.returnValues = values
        proxy.invoke()

        proxy.calls mustBe 1
    }

    @Test
    @JsName("fn24")
    fun `Given the Proxy has a Collector and invoke is called it calls the Collect`() {
        // Given
        val values: List<Any> = fixture.listFixture(size = 5)

        var capturedMock: KMockContract.Proxy<*, *>? = null
        var capturedCalledIdx: Int? = null

        val collector = Collector { referredMock, referredCall ->
            capturedMock = referredMock
            capturedCalledIdx = referredCall
        }

        // When
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture(), collector, freeze = false)

        proxy.returnValues = values
        proxy.invoke()

        // Then
        capturedMock?.id mustBe proxy.id
        capturedCalledIdx mustBe 0
    }

    @Test
    @IgnoreJs
    @JsName("fn25")
    fun `Given clear is called it clears the mock`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: () -> Any = {
            fixture.fixture()
        }
        val sideEffectChained: () -> Any = {
            fixture.fixture()
        }

        proxy.returnValue = value
        proxy.returnValues = values
        proxy.sideEffect = sideEffect
        proxy.sideEffects.add(sideEffectChained)

        // When
        proxy.invoke()

        proxy.clear()

        // Then
        proxy.returnValue mustBe null

        assertFailsWith<IndexOutOfBoundsException> { proxy.returnValues[0] }
        assertFailsWith<NullPointerException> { proxy.sideEffect }
        assertFailsWith<Throwable> { (proxy.sideEffects as SideEffectChain).next() }

        proxy.calls mustBe 0

        assertFailsWith<MockError.MissingCall> { proxy.getArgumentsForCall(0) }
    }

    @Test
    @JsOnly
    @JsName("fn26")
    fun `Given clear is called it clears the mock for Js`() {
        // Given
        val proxy = SyncFunProxy<Any, Function0<Any>>(
            fixture.fixture(),
            freeze = false
        )
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: () -> Any = {
            fixture.fixture()
        }
        val sideEffectChain: () -> Any = {
            fixture.fixture()
        }

        proxy.returnValue = value
        proxy.returnValues = values
        proxy.sideEffect = sideEffect
        proxy.sideEffects.add(sideEffectChain)

        // When
        proxy.invoke()

        proxy.clear()

        // Then
        proxy.returnValue mustBe null

        assertFailsWith<IndexOutOfBoundsException> { proxy.returnValues[0] }
        assertFailsWith<ClassCastException> { proxy.sideEffect }
        assertFailsWith<Throwable> { (proxy.sideEffects as SideEffectChain).next() }

        proxy.calls mustBe 0

        assertFailsWith<MockError.MissingCall> { proxy.getArgumentsForCall(0) }
    }

    @Test
    @JsName("fn27")
    fun `Given clear is called it clears the mock while leave the spy intact`() {
        // Given
        val implementation = Implementation<Any>()

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
            spyOn = implementation::fun0,
            freeze = false
        )

        implementation.fun0 = { valueImpl }

        proxy.returnValue = value
        proxy.returnValues = values
        proxy.sideEffect = sideEffect
        proxy.sideEffects.add(sideEffectChain)

        // When
        proxy.invoke()

        proxy.clear()

        // Then
        val actual = proxy.invoke()
        actual mustBe valueImpl
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
