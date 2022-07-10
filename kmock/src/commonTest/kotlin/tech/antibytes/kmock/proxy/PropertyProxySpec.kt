/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.error.MockError
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.resolveMultiBlockCalls
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs

class PropertyProxySpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils PropProxy`() {
        PropertyProxy<Unit>(fixture.fixture()) fulfils KMockContract.PropertyProxy::class
    }

    @Test
    @JsName("fn1")
    fun `It is frozen by default`() {
        PropertyProxy<Unit>(fixture.fixture()).frozen mustBe true
    }

    @Test
    @JsName("fn2")
    fun `Given a getValue is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValue = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.getValue mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2a")
    fun `Given a getValue is set with nullable value it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any?>(
            fixture.fixture(),
        )
        val value: Any? = null

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValue = value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.getValue mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2b")
    fun `returns is an alias setter of getValue`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy returns value
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.getValue mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn3")
    fun `Given a getValues is set with an emptyList it fails`() {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())

        // Then
        val error = assertFailsWith<MockError.MissingStub> {
            proxy.getValues = emptyList()
        }

        error.message mustBe "Empty Lists are not valid as value provider."
    }

    @Test
    @JsName("fn4")
    fun `Given a getValues is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValues = values
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.getValues mustBe values
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn4a")
    fun `returnMany is an alias setter of getValues`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy returnsMany values
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.getValues mustBe values
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn4b")
    fun `getMany is an alias setter of getValues`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getMany = values
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.getValues mustBe values
            proxy.getMany mustBe values
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn5")
    fun `Given a get is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val sideEffect = { fixture.fixture<Any>() }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.get = sideEffect
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.get mustBe sideEffect
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn5a")
    fun `runOnGet is an alias setter of get`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val sideEffect = { fixture.fixture<Any>() }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy runOnGet sideEffect
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.get mustBe sideEffect
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn6")
    fun `Given a set is set it is threadsafe retrievable`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val effect: (Any) -> Unit = { }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.set = effect
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.set sameAs effect
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn6a")
    fun `runOnSet is an alias setter of set`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val effect: (Any) -> Unit = { }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy runOnSet effect
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.set sameAs effect
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn7")
    fun `Given executeOnGet is called it fails if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val proxy = PropertyProxy<Any>(name)

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // Then
            val error = assertFailsWith<MockError.MissingStub> {
                // When
                proxy.executeOnGet()
            }

            error.message mustBe "Missing stub value for $name"
        }
    }

    @Test
    @JsName("fn7a")
    fun `Given executeOnGet is called it uses the given Relaxer if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val value = AtomicReference(fixture.fixture<Any>())
        val capturedId = AtomicReference<String?>(null)
        val proxy = PropertyProxy<Any>(name)

        return runBlockingTestInContext(testScope1.coroutineContext) {
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
    }

    @Test
    @JsName("fn7b")
    fun `Given executeOnGet is called it uses the given Spy if no ReturnValue Provider is set`(): AsyncTestReturnValue {
        // Given
        val name: String = fixture.fixture()
        val value: Any = fixture.fixture()

        val implementation = Implementation<Any>()
        val proxy = PropertyProxy<Any>(name)

        implementation.fooProp = value

        return runBlockingTestInContext(testScope1.coroutineContext) {
            // When
            val actual = proxy.executeOnGet { useSpyIf(implementation) { implementation.foo } }

            // Then
            actual mustBe value
        }
    }

    @Test
    @JsName("fn8")
    fun `Given executeOnGet is called it returns the GetValue threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: String = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValue = value
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.executeOnGet()

            // Then
            actual mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn9")
    fun `Given executeOnGet is called it returns the GetValues Value threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val values: List<String> = fixture.listFixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            values.forEach { value ->
                val actual = proxy.executeOnGet()

                // Then
                actual mustBe value
            }
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn10")
    fun `Given executeOnGet is called it returns the last GetValues Value if the given List is down to one value threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 1)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            repeat(10) {
                val actual = proxy.executeOnGet()

                // Then
                actual mustBe values.first()
            }
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn11")
    fun `Given executeOnGet is called it returns the SideEffect of Get threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value = fixture.fixture<Any>()
        val sideEffect = { value }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.get = sideEffect
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.executeOnGet()

            // Then
            actual mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn12")
    fun `Given executeOnGet is called it uses GetValues over a GetValue`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValues = values
            proxy.getValue = value
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.executeOnGet()

            // Then
            actual mustBe values.first()
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn13")
    fun `Given executeOnGet is called it uses Get over GetValues`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: Any = fixture.fixture()
        val values: List<Any> = fixture.listFixture(size = 2)
        val sideEffect = { value }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.get = sideEffect
            proxy.getValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.executeOnGet()

            // Then
            actual mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn14")
    fun `Given executeOnGet is called it uses OnGetSpy over Get`(): AsyncTestReturnValue {
        // Given
        val value: Any = fixture.fixture()
        val implementation = Implementation(fooProp = value)
        val proxy = PropertyProxy<Any>(
            fixture.fixture(),
        )
        val sideEffect = { fixture.fixture<Any>() }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.get = sideEffect
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.executeOnGet { useSpyIf(implementation) { implementation.foo } }

            // Then
            actual mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn15")
    fun `Given executeOnSet is called it calls the given SideEffect and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val newValue: Any = fixture.fixture()

        val actualNew = Channel<Any>()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.set = { givenNew ->
                testScope1.launch {
                    actualNew.send(givenNew)
                }
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.executeOnSet(newValue)

            // Then
            actual mustBe Unit
            actualNew.receive() mustBe newValue
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn16")
    fun `Given executeOnGet is called it captures Arguments threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.executeOnGet()
        }

        runBlockingTest {
            val actual = proxy.getArgumentsForCall(0)

            actual fulfils KMockContract.GetOrSet.Get::class
            actual.value mustBe null
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn16a")
    fun `Given executeOnGet is called it captures Arguments which are accessable ArrayStyle`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValues = values.toList()
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.executeOnGet()
        }

        runBlockingTest {
            val actual = proxy[0]

            actual fulfils KMockContract.GetOrSet.Get::class
            actual.value mustBe null
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn17")
    fun `Given executeOnSet is called it sets an Arguments to capture the call threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.executeOnSet(value)
        }

        runBlockingTest {
            val actual = proxy.getArgumentsForCall(0)

            actual fulfils KMockContract.GetOrSet.Set::class
            actual.value mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn17a")
    fun `Given executeOnSet is called it captures Arguments which are accessable ArrayStyle`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.executeOnSet(value)
        }

        runBlockingTest {
            val actual = proxy[0]

            actual fulfils KMockContract.GetOrSet.Set::class
            actual.value mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn18")
    fun `Given executeOnSet is called it uses the given Spy`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.executeOnSet(value) { useSpyIf(implementation) { implementation.bar = value } }
        }

        runBlockingTest {
            implementation.barProp.value mustBe value
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn19")
    fun `It reflects the given id`() {
        // Given
        val name: String = fixture.fixture()

        // When
        val actual = PropertyProxy<Any>(name).id

        // Then
        actual mustBe name
    }

    @Test
    @JsName("fn20")
    fun `Its default call count is 0`() {
        PropertyProxy<Any>(fixture.fixture()).calls mustBe 0
    }

    @Test
    @JsName("fn21")
    fun `Given executeOnGet is called it increments the call counter threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val values: List<Any> = fixture.listFixture(size = 5)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.executeOnGet()
        }

        runBlockingTest {
            proxy.calls mustBe 1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn22")
    fun `Given executeOnSet is called it increments the call counter threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = PropertyProxy<Any>(fixture.fixture())
        val value: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.executeOnSet(value)
        }

        runBlockingTest {
            proxy.calls mustBe 1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn23")
    fun `Given the Proxy has a Collector and executeOnGet is called it calls the Collect`(): AsyncTestReturnValue {
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

        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.getValues = values
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.executeOnGet()
        }

        runBlockingTest {
            capturedMock.get()?.id mustBe proxy.id
            capturedCalledIdx.get() mustBe 0
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn24")
    fun `Given the Proxy has a Collector and executeOnSet is called it calls the Collect`(): AsyncTestReturnValue {
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

        runBlockingTestInContext(testScope2.coroutineContext) {
            proxy.executeOnSet(value)
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
        val proxy = PropertyProxy<Any>(fixture.fixture())
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
        var barProp: AtomicReference<T?> = AtomicReference(null),
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
