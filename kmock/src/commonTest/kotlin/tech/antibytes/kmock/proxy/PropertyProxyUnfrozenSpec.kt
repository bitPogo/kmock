/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.error.MockError
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PropertyProxyUnfrozenSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils PropProxy`() {
        PropertyProxy<Unit>(fixture.fixture()) fulfils KMockContract.PropertyProxy::class
    }

    @Test
    @JsName("fn1")
    fun `It is not frozen if told so`() {
        PropertyProxy<Any>(fixture.fixture(), freeze = false).frozen mustBe false
    }

    @Test
    @JsName("fn2")
    fun `Given a get is set it is retrievable`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        proxy.getValue = value

        // Then
        proxy.getValue mustBe value
    }

    @Test
    @JsName("fn2a")
    fun `Given a GetValue is set with nullable value it is retrievable`() {
        // Given
        val proxy = PropertyProxy<Any?>(
            fixture.fixture(),
        )
        val value: Any? = null

        // When
        proxy.getValue = value

        // Then
        proxy.getValue mustBe value
    }

    @Test
    @JsName("fn3")
    fun `Given a GetValues is set with an emptyList it fails`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            proxy.getValues = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn4")
    fun `Given a GetValues is set it is retrievable`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture()

        // When
        proxy.getValues = values

        // Then
        proxy.getValues mustBe values
    }

    @Test
    @JsName("fn4a")
    fun `returnMany is an alias setter of getValues`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture()

        // When
        proxy returnsMany values

        // Then
        proxy.getValues mustBe values
    }

    @Test
    @JsName("fn4b")
    fun `getMany is an alias setter of getValues`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture()

        // When
        proxy.getMany = values

        // Then
        proxy.getValues mustBe values
        proxy.getMany mustBe values
    }

    @Test
    @JsName("fn5")
    fun `Given Get is set it is retrievable`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val sideEffect = { fixture.fixture<Any>() }

        // When
        proxy.get = sideEffect

        // Then
        proxy.get mustBe sideEffect
    }

    @Test
    @JsName("fn6")
    fun `Given a set is set it is retrievable`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val effect: (Any) -> Unit = { }

        // When
        proxy.set = effect

        // Then
        proxy.set sameAs effect
    }

    @Test
    @JsName("fn7")
    fun `Given executeOnGet is called it fails if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = PropertyProxy<Any>(name, freeze = false)

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            // When
            proxy.executeOnGet()
        }

        error.message mustBe "Missing stub value for $name"
    }

    @Test
    @JsName("fn7a")
    fun `Given executeOnGet is called it uses the given Relaxer if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val proxy = PropertyProxy<Any>(
            name,
            freeze = false
        )

        // When
        val actual = proxy.executeOnGet {
            useRelaxerIf(true) { givenId ->
                capturedId.set(givenId)

                value
            }
        }

        // Then
        actual mustBe value
        capturedId.value mustBe name
    }

    @Test
    @JsName("fn7b")
    fun `Given executeOnGet is called it uses the given Spy if no ReturnValue Provider is set`() {
        // Given
        val name: String = fixture.fixture()
        val value: Any = fixture.fixture()

        val implementation = Implementation<Any>()
        val proxy = PropertyProxy<Any>(
            name,
            freeze = false
        )

        implementation.fooProp = value

        // When
        val actual = proxy.executeOnGet { useSpyIf(implementation) { implementation.foo } }

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn8")
    fun `Given executeOnGet is called it returns the GetValue`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: String = fixture.fixture()

        // When
        proxy.getValue = value

        val actual = proxy.executeOnGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn9")
    fun `Given executeOnGet is called it returns the GetValues`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<String> = fixture.listFixture()

        // When
        proxy.getValues = values

        values.forEach { value ->
            val actual = proxy.executeOnGet()

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn10")
    fun `Given executeOnGet is called it returns the last GetValues if the given List is down to one value`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 1)

        // When
        proxy.getValues = values.toList()

        repeat(10) {
            val actual = proxy.executeOnGet()

            // Then
            actual mustBe values.first()
        }
    }

    @Test
    @JsName("fn11")
    fun `Given executeOnGet is called it returns the SideEffect of Get`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value = fixture.fixture<Any>()
        val sideEffect = { value }

        // When
        proxy.get = sideEffect

        val actual = proxy.executeOnGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn12")
    fun `Given executeOnGet is called it uses GetValues over GetValue`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        proxy.getValues = values
        proxy.getValue = value

        val actual = proxy.executeOnGet()

        // Then
        actual mustBe values.first()
    }

    @Test
    @JsName("fn13")
    fun `Given executeOnGet is called it uses Get over GetValues`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)
        val sideEffect = { value }

        // When
        proxy.get = sideEffect
        proxy.getValues = values

        val actual = proxy.executeOnGet()

        // Then
        actual mustBe value
    }

    @Test
    @JsName("fn14")
    fun `Given executeOnSet is called it calls the given SideEffect and delegates values`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val newValue: Any = fixture.fixture()

        val actualNew = AtomicReference<Any?>(null)

        // When
        proxy.set = { givenNew ->
            actualNew.set(givenNew)
        }

        // When
        val actual = proxy.executeOnSet(newValue)

        // Then
        actual mustBe Unit
        actualNew.value mustBe newValue
    }

    @Test
    @JsName("fn15")
    fun `Given executeOnGet is called it sets an Arguments to capture the call`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        proxy.getValues = values.toList()

        proxy.executeOnGet()

        val actual = proxy.getArgumentsForCall(0)

        actual fulfils KMockContract.GetOrSet.Get::class
        actual.value mustBe null
    }

    @Test
    @JsName("fn15a")
    fun `Given executeOnGet is called it captures Arguments which are accessable ArrayStyle`() {
        // / Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        proxy.getValues = values.toList()

        proxy.executeOnGet()

        val actual = proxy[0]

        actual fulfils KMockContract.GetOrSet.Get::class
        actual.value mustBe null
    }

    @Test
    @JsName("fn16")
    fun `Given executeOnSet is called it captures Arguments of the call`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        proxy.executeOnSet(value)

        val actual = proxy.getArgumentsForCall(0)

        actual fulfils KMockContract.GetOrSet.Set::class
        actual.value mustBe value
    }

    @Test
    @JsName("fn16a")
    fun `Given executeOnSet is called it captures Arguments which are accessable ArrayStyle`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        proxy.executeOnSet(value)

        val actual = proxy[0]

        actual fulfils KMockContract.GetOrSet.Set::class
        actual.value mustBe value
    }

    @Test
    @JsName("fn17")
    fun `Given executeOnSet is called it uses the given Spy`() {
        // Given
        val implementation = Implementation<Any>()

        val proxy = PropertyProxy<Any>(
            fixture.fixture(),
            freeze = false
        )
        val value: Any = fixture.fixture()

        // When
        proxy.executeOnSet(value) { useSpyIf(implementation) { implementation.bar = value } }

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
    fun `Given executeOnGet is called it increments the call counter`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        proxy.getValues = values

        proxy.executeOnGet()

        proxy.calls mustBe 1
    }

    @Test
    @JsName("fn21")
    fun `Given executeOnSet is called it increments the call counter`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()

        // When
        proxy.executeOnSet(value)

        proxy.calls mustBe 1
    }

    @Test
    @JsName("fn22")
    fun `Given the Proxy has a Collector and executeOnGet is called it calls the Collect`() {
        // Given
        val values: List<Any> = fixture.listFixture(size = 5)

        val capturedMock = AtomicReference<KMockContract.Proxy<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = KMockContract.Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val proxy = PropertyProxy<Any>(fixture.fixture(), collector)

        proxy.getValues = values

        proxy.executeOnGet()

        capturedMock.get()?.id mustBe proxy.id
        capturedCalledIdx.get() mustBe 0
    }

    @Test
    @JsName("fn23")
    fun `Given the Proxy has a Collector and executeOnSet is called it calls the Collect`() {
        // Given
        val value: Any = fixture.fixture()

        val capturedMock = AtomicReference<KMockContract.Proxy<*, *>?>(null)
        val capturedCalledIdx = AtomicReference<Int?>(null)

        val collector = KMockContract.Collector { referredMock, referredCall ->
            capturedMock.set(referredMock)
            capturedCalledIdx.set(referredCall)
        }

        // When
        val proxy = PropertyProxy<Any>(fixture.fixture(), collector)

        proxy.executeOnSet(value)

        capturedMock.get()?.id mustBe proxy.id
        capturedCalledIdx.get() mustBe 0
    }

    @Test
    @JsName("fn24")
    fun `Given clear is called it clears the mock`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture(), freeze = false)
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture()
        val sideEffect: (Any) -> Unit = { }

        proxy.getValue = value
        proxy.getValues = values
        proxy.get = { value }
        proxy.set = sideEffect

        proxy.executeOnGet()
        proxy.executeOnSet(fixture.fixture())

        // When
        proxy.clear()

        // Then
        proxy.getValue mustBe null
        proxy.getValues mustBe emptyList()
        assertFailsWith<NullPointerException> { proxy.get }
        assertFailsWith<NullPointerException> { proxy.set }

        proxy.calls mustBe 0
        assertFailsWith<MockError.MissingCall> { proxy.getArgumentsForCall(0) }
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
