/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.error.MockError
import tech.antibytes.util.test.annotations.IgnoreJs
import tech.antibytes.util.test.annotations.JsOnly
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PropertyProxyUnfrozenSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn1")
    fun `Given a get is set it is retrievable`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        Proxy.get = value

        // Then
        Proxy.get mustBe value
    }

    @Test
    @JsName("fn2")
    fun `Given a get is set with nullable value it is retrievable`() {
        // Given
        val Proxy = PropertyProxy<Any?>(
            fixture.fixture(),
        )
        val value: Any? = null

        // When
        Proxy.get = value

        // Then
        Proxy.get mustBe value
    }

    @Test
    @JsName("fn3")
    fun `Given a getMany is set with an emptyList it fails`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            Proxy.getMany = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn4")
    fun `Given a getMany is set it is retrievable`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture()

        // When
        Proxy.getMany = values

        // Then
        Proxy.getMany mustBe values
    }

    @Test
    @JsName("fn5")
    fun `Given a getSideEffect is set it is retrievable`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val sideEffect = { fixture.fixture<Any>() }

        // When
        Proxy.getSideEffect = sideEffect

        // Then
        Proxy.getSideEffect mustBe sideEffect
    }

    @Test
    @JsName("fn6")
    fun `Given a set is set it is retrievable`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val effect: (Any) -> Unit = { }

        // When
        Proxy.set = effect

        // Then
        Proxy.set sameAs effect
    }

    @Test
    @JsName("fn7")
    fun `Given onGet is called it fails if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val Proxy = PropertyProxy<Any>(name, freeze = false)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            // When
            Proxy.onGet()
        }

        error.message mustBe "Missing stub value for $name"
    }

    @Test
    @JsName("fn7a")
    fun `Given onGet is called it uses the given Relaxer if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val Proxy = PropertyProxy<Any>(
            name,
            relaxer = { givenId ->
                capturedId.set(givenId)

                value
            },
            freeze = false
        )

        // When
        val actual = Proxy.onGet()

        // Then
        actual mustBe value
        capturedId.value mustBe name
    }

    @Test
    @JsName("fn7b")
    fun `Given onGet is called it uses the given Spy if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val value: Any = fixture.fixture()

        val implementation = Implementation<Any>()
        val Proxy = PropertyProxy(
            name,
            spyOnGet = implementation::foo::get,
            freeze = false
        )

        implementation.fooProp = value

        // When
        val actual = Proxy.onGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn8")
    fun `Given onGet is called it returns the Get Value`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: String = fixture.fixture()

        // When
        Proxy.get = value

        val actual = Proxy.onGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn9")
    fun `Given onGet is called it returns the GetMany Value`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<String> = fixture.listFixture()

        // When
        Proxy.getMany = values

        values.forEach { value ->
            val actual = Proxy.onGet()

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn10")
    fun `Given onGet is called it returns the last GetMany Value if the given List is down to one value`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 1)

        // When
        Proxy.getMany = values.toList()

        for (x in 0 until 10) {
            val actual = Proxy.onGet()

            // Then
            actual mustBe values.first()
        }
    }

    @Test
    @JsName("fn11")
    fun `Given onGet is called it returns the GetSideEffect Value`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value = fixture.fixture<Any>()
        val sideEffect = { value }

        // When
        Proxy.getSideEffect = sideEffect

        val actual = Proxy.onGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn12")
    fun `Given onGet is called it uses GetMany over Get`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        Proxy.getMany = values
        Proxy.get = value

        val actual = Proxy.onGet()

        // Then
        actual mustBe values.first()
    }

    @Test
    @JsName("fn13")
    fun `Given onGet is called it uses GetSideEffect over GetMany`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)
        val sideEffect = { value }

        // When
        Proxy.getSideEffect = sideEffect
        Proxy.getMany = values

        val actual = Proxy.onGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn14")
    fun `Given onSet is called it calls the given SideEffect and delegates values`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val newValue: Any = fixture.fixture()

        val actualNew = AtomicReference<Any?>(null)

        // When
        Proxy.set = { givenNew ->
            actualNew.set(givenNew)
        }

        // When
        val actual = Proxy.onSet(newValue)

        // Then
        actual mustBe Unit
        actualNew.value mustBe newValue
    }

    @Test
    @JsName("fn15")
    fun `Given onGet is called it sets an Arguments to capture the call`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        Proxy.getMany = values.toList()

        Proxy.onGet()

        val actual = Proxy.getArgumentsForCall(0)

        actual fulfils KMockContract.GetOrSet.Get::class
        actual.value mustBe null
    }

    @Test
    @JsName("fn16")
    fun `Given onSet is called it sets an Arguments to capture the call`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        Proxy.onSet(value)

        val actual = Proxy.getArgumentsForCall(0)

        actual fulfils KMockContract.GetOrSet.Set::class
        actual.value mustBe value
    }

    @Test
    @JsName("fn17")
    fun `Given onSet is called it uses the given Spy`() {
        // Given
        val implementation = Implementation<Any>()

        val Proxy = PropertyProxy(
            fixture.fixture(),
            spyOnSet = implementation::bar::set,
            freeze = false
        )
        val value: Any = fixture.fixture()

        // When
        Proxy.onSet(value)

        implementation.barProp.value mustBe value
    }

    @Test
    @JsName("fn18")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = PropertyProxy<Any>(name, freeze = false).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn19")
    fun `Its default call count is 0`() {
        PropertyProxy<Any>(fixture.fixture(), freeze = false).calls mustBe 0
    }

    @Test
    @JsName("fn20")
    fun `Given onGet is called it increments the call counter`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        Proxy.getMany = values

        Proxy.onGet()

        Proxy.calls mustBe 1
    }

    @Test
    @JsName("fn21")
    fun `Given onSet is called it increments the call counter`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        Proxy.onSet(value)

        Proxy.calls mustBe 1
    }

    @Test
    @JsName("fn22")
    fun `Given the Proxy has a Collector and onGet is called it calls the Collect`() {
        // Given
        val values: List<Any> = fixture.listFixture(size = 5)

        val capturedMock = AtomicReference<KMockContract.Proxy<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = KMockContract.Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val Proxy = PropertyProxy<Any>(fixture.fixture(), collector)

        Proxy.getMany = values

        Proxy.onGet()

        capturedMock.get()?.id mustBe Proxy.id
        capturedCalledIdx.get() mustBe 0
    }

    @Test
    @JsName("fn23")
    fun `Given the Proxy has a Collector and onSet is called it calls the Collect`() {
        // Given
        val value: Any = fixture.fixture()

        val capturedMock = AtomicReference<KMockContract.Proxy<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = KMockContract.Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val Proxy = PropertyProxy<Any>(fixture.fixture(), collector)

        Proxy.onSet(value)

        capturedMock.get()?.id mustBe Proxy.id
        capturedCalledIdx.get() mustBe 0
    }

    @Test
    @IgnoreJs
    @JsName("fn24")
    fun `Given clear is called it clears the mock`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: (Any) -> Unit = { }

        Proxy.get = value
        Proxy.getMany = values
        Proxy.getSideEffect = { value }
        Proxy.set = sideEffect

        Proxy.onGet()
        Proxy.onSet(fixture.fixture())

        // When
        Proxy.clear()

        // Then
        Proxy.get mustBe null

        try {
            Proxy.getMany
        } catch (error: Throwable) {
            (error is NullPointerException) mustBe true
        }

        try {
            Proxy.getSideEffect
        } catch (error: Throwable) {
            (error is NullPointerException) mustBe true
        }

        Proxy.set isNot sideEffect

        Proxy.calls mustBe 0

        try {
            Proxy.getArgumentsForCall(0)
        } catch (error: Throwable) {
            (error is IndexOutOfBoundsException) mustBe true
        }
    }

    @Test
    @JsOnly
    @JsName("fn25")
    fun `Given clear is called it clears the mock for Js`() {
        // Given
        val Proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: (Any) -> Unit = { }

        Proxy.get = value
        Proxy.getMany = values
        Proxy.getSideEffect = { value }
        Proxy.set = sideEffect

        Proxy.onGet()
        Proxy.onSet(fixture.fixture())

        // When

        Proxy.clear()

        // Then
        Proxy.get mustBe null

        try {
            Proxy.getMany
        } catch (error: Throwable) {
            (error is ClassCastException) mustBe true
        }

        try {
            Proxy.getSideEffect
        } catch (error: Throwable) {
            (error is ClassCastException) mustBe true
        }

        Proxy.set isNot sideEffect

        Proxy.calls mustBe 0

        try {
            Proxy.getArgumentsForCall(0)
        } catch (error: Throwable) {
            (error is IndexOutOfBoundsException) mustBe true
        }
    }

    @Test
    @JsName("fn26")
    fun `Given clear is called it clears the mock while repecting Spyies`() {
        // Given
        val implementation = Implementation<Any>()

        val value: Any = fixture.fixture()
        val valueImp: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: (Any) -> Unit = { }

        implementation.fooProp = valueImp

        // When
        val Proxy = PropertyProxy(
            fixture.fixture(),
            spyOnGet = implementation::foo::get,
            freeze = false
        )
        Proxy.get = value
        Proxy.getMany = values
        Proxy.getSideEffect = { value }
        Proxy.set = sideEffect

        Proxy.onGet()
        Proxy.onSet(fixture.fixture())

        Proxy.clear()

        // Then
        Proxy.onGet() mustBe valueImp
    }

    private class Implementation<T>(
        var fooProp: T? = null,
        var barProp: AtomicReference<T?> = AtomicReference(null)
    ) {
        val foo: T
            get() {
                return fooProp ?: throw MockError.MissingStub("Missing Sideeffect foo")
            }
        var bar: T
            get() {
                return if (barProp.value == null) {
                    throw MockError.MissingStub("Missing Sideeffect bar")
                } else {
                    barProp.value!!
                }
            }
            set(value) {
                barProp.set(value)
            }
    }
}
