/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.mock

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

class PropertyMockeryUnfrozenSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn1")
    fun `Given a get is set it is retrievable`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        mockery.get = value

        // Then
        mockery.get mustBe value
    }

    @Test
    @JsName("fn2")
    fun `Given a get is set with nullable value it is retrievable`() {
        // Given
        val mockery = PropertyMockery<Any?>(
            fixture.fixture(),
        )
        val value: Any? = null

        // When
        mockery.get = value

        // Then
        mockery.get mustBe value
    }

    @Test
    @JsName("fn3")
    fun `Given a getMany is set with an emptyList it fails`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            mockery.getMany = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn4")
    fun `Given a getMany is set it is retrievable`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture()

        // When
        mockery.getMany = values

        // Then
        mockery.getMany mustBe values
    }

    @Test
    @JsName("fn5")
    fun `Given a getSideEffect is set it is retrievable`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val sideEffect = { fixture.fixture<Any>() }

        // When
        mockery.getSideEffect = sideEffect

        // Then
        mockery.getSideEffect mustBe sideEffect
    }

    @Test
    @JsName("fn6")
    fun `Given a set is set it is retrievable`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val effect: (Any) -> Unit = { }

        // When
        mockery.set = effect

        // Then
        mockery.set sameAs effect
    }

    @Test
    @JsName("fn7")
    fun `Given onGet is called it fails if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val mockery = PropertyMockery<Any>(name, freeze = false)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            // When
            mockery.onGet()
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
        val mockery = PropertyMockery<Any>(
            name,
            relaxer = { givenId ->
                capturedId.set(givenId)

                value
            },
            freeze = false
        )

        // When
        val actual = mockery.onGet()

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
        val mockery = PropertyMockery(
            name,
            spyOnGet = implementation::foo::get,
            freeze = false
        )

        implementation.fooProp = value

        // When
        val actual = mockery.onGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn8")
    fun `Given onGet is called it returns the Get Value`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value: String = fixture.fixture()

        // When
        mockery.get = value

        val actual = mockery.onGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn9")
    fun `Given onGet is called it returns the GetMany Value`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val values: List<String> = fixture.listFixture()

        // When
        mockery.getMany = values

        values.forEach { value ->
            val actual = mockery.onGet()

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn10")
    fun `Given onGet is called it returns the last GetMany Value if the given List is down to one value`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 1)

        // When
        mockery.getMany = values.toList()

        for (x in 0 until 10) {
            val actual = mockery.onGet()

            // Then
            actual mustBe values.first()
        }
    }

    @Test
    @JsName("fn11")
    fun `Given onGet is called it returns the GetSideEffect Value`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value = fixture.fixture<Any>()
        val sideEffect = { value }

        // When
        mockery.getSideEffect = sideEffect

        val actual = mockery.onGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn12")
    fun `Given onGet is called it uses GetMany over Get`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        mockery.getMany = values
        mockery.get = value

        val actual = mockery.onGet()

        // Then
        actual mustBe values.first()
    }

    @Test
    @JsName("fn13")
    fun `Given onGet is called it uses GetSideEffect over GetMany`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)
        val sideEffect = { value }

        // When
        mockery.getSideEffect = sideEffect
        mockery.getMany = values

        val actual = mockery.onGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn14")
    fun `Given onSet is called it calls the given SideEffect and delegates values`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val newValue: Any = fixture.fixture()

        val actualNew = AtomicReference<Any?>(null)

        // When
        mockery.set = { givenNew ->
            actualNew.set(givenNew)
        }

        // When
        val actual = mockery.onSet(newValue)

        // Then
        actual mustBe Unit
        actualNew.value mustBe newValue
    }

    @Test
    @JsName("fn15")
    fun `Given onGet is called it sets an Arguments to capture the call`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        mockery.getMany = values.toList()

        mockery.onGet()

        val actual = mockery.getArgumentsForCall(0)

        actual fulfils KMockContract.GetOrSet.Get::class
        actual.value mustBe null
    }

    @Test
    @JsName("fn16")
    fun `Given onSet is called it sets an Arguments to capture the call`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        mockery.onSet(value)

        val actual = mockery.getArgumentsForCall(0)

        actual fulfils KMockContract.GetOrSet.Set::class
        actual.value mustBe value
    }

    @Test
    @JsName("fn17")
    fun `Given onSet is called it uses the given Spy`() {
        // Given
        val implementation = Implementation<Any>()

        val mockery = PropertyMockery(
            fixture.fixture(),
            spyOnSet = implementation::bar::set,
            freeze = false
        )
        val value: Any = fixture.fixture()

        // When
        mockery.onSet(value)

        implementation.barProp.value mustBe value
    }

    @Test
    @JsName("fn18")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = PropertyMockery<Any>(name, freeze = false).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn19")
    fun `Its default call count is 0`() {
        PropertyMockery<Any>(fixture.fixture(), freeze = false).calls mustBe 0
    }

    @Test
    @JsName("fn20")
    fun `Given onGet is called it increments the call counter`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        mockery.getMany = values

        mockery.onGet()

        mockery.calls mustBe 1
    }

    @Test
    @JsName("fn21")
    fun `Given onSet is called it increments the call counter`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        mockery.onSet(value)

        mockery.calls mustBe 1
    }

    @Test
    @JsName("fn22")
    fun `Given the mockery has a Collector and onGet is called it calls the Collect`() {
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

        mockery.getMany = values

        mockery.onGet()

        capturedMock.get()?.id mustBe mockery.id
        capturedCalledIdx.get() mustBe 0
    }

    @Test
    @JsName("fn23")
    fun `Given the mockery has a Collector and onSet is called it calls the Collect`() {
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

        mockery.onSet(value)

        capturedMock.get()?.id mustBe mockery.id
        capturedCalledIdx.get() mustBe 0
    }

    @Test
    @IgnoreJs
    @JsName("fn24")
    fun `Given clear is called it clears the mock`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: (Any) -> Unit = { }

        mockery.get = value
        mockery.getMany = values
        mockery.getSideEffect = { value }
        mockery.set = sideEffect

        mockery.onGet()
        mockery.onSet(fixture.fixture())

        // When
        mockery.clear()

        // Then
        mockery.get mustBe null

        try {
            mockery.getMany
        } catch (error: Throwable) {
            (error is NullPointerException) mustBe true
        }

        try {
            mockery.getSideEffect
        } catch (error: Throwable) {
            (error is NullPointerException) mustBe true
        }

        mockery.set isNot sideEffect

        mockery.calls mustBe 0

        try {
            mockery.getArgumentsForCall(0)
        } catch (error: Throwable) {
            (error is IndexOutOfBoundsException) mustBe true
        }
    }

    @Test
    @JsOnly
    @JsName("fn25")
    fun `Given clear is called it clears the mock for Js`() {
        // Given
        val mockery = PropertyMockery<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: (Any) -> Unit = { }

        mockery.get = value
        mockery.getMany = values
        mockery.getSideEffect = { value }
        mockery.set = sideEffect

        mockery.onGet()
        mockery.onSet(fixture.fixture())

        // When

        mockery.clear()

        // Then
        mockery.get mustBe null

        try {
            mockery.getMany
        } catch (error: Throwable) {
            (error is ClassCastException) mustBe true
        }

        try {
            mockery.getSideEffect
        } catch (error: Throwable) {
            (error is ClassCastException) mustBe true
        }

        mockery.set isNot sideEffect

        mockery.calls mustBe 0

        try {
            mockery.getArgumentsForCall(0)
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
        val mockery = PropertyMockery(
            fixture.fixture(),
            spyOnGet = implementation::foo::get,
            freeze = false
        )
        mockery.get = value
        mockery.getMany = values
        mockery.getSideEffect = { value }
        mockery.set = sideEffect

        mockery.onGet()
        mockery.onSet(fixture.fixture())

        mockery.clear()

        // Then
        mockery.onGet() mustBe valueImp
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
