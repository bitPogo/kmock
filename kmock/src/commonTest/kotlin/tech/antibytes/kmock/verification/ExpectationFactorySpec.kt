/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Expectation
import tech.antibytes.kmock.fixture.funProxyFixture
import tech.antibytes.mock.PropertyProxyStub
import tech.antibytes.mock.AssertionChainStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class ExpectationFactorySpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given hasBeenCalled is called with a FunProxy it returns a VerficationHandle which contains no matches if there were no calls`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 0)

        // When
        val actual = proxy.hasBeenCalled()

        // Then
        actual mustBe Expectation(proxy, emptyList())
    }

    @Test
    @JsName("fn1")
    fun `Given hasBeenCalled is called with a FunProxy it returns a VerficationHandle which contains no matches if there were calls`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = proxy.hasBeenCalled()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn2")
    fun `Given hasBeenCalledWithVoid is called with a FunProxy it returns a VerficationHandle which contains matches if there were no calls`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 0)

        // When
        val actual = proxy.hasBeenCalledWithVoid()

        // Then
        actual mustBe Expectation(proxy, emptyList())
    }

    @Test
    @JsName("fn3")
    fun `Given hasBeenCalledWithVoid is called with a FunProxy it returns a VerficationHandle which contains no matches if no call matches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = proxy.hasBeenCalledWithVoid()

        // Then
        actual mustBe Expectation(proxy, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn4")
    fun `Given hasBeenCalledWithVoid is called with a FunProxy it returns a VerficationHandle which contains matches if there were calls`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            arrayOf()
        }

        // When
        val actual = proxy.hasBeenCalled()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn5")
    fun `Given hasBeenCalledWith is called with a FunProxy it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 0)

        // When
        val actual = proxy.hasBeenCalledWith()

        // Then
        actual mustBe Expectation(proxy, emptyList())
    }

    @Test
    @JsName("fn6")
    fun `Given hasBeenCalledWith is called with a FunProxy it returns a VerficationHandle which contains no matches if nothing matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = proxy.hasBeenCalledWith(fixture.fixture<String>())

        // Then
        actual mustBe Expectation(proxy, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn7")
    fun `Given hasBeenCalledWith is called with a FunProxy it returns a VerficationHandle which contains matches if something matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)
        val values = fixture.listFixture<String>().toTypedArray().sortedArray()

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = proxy.hasBeenCalledWith(*values)

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn8")
    fun `Given hasBeenCalledWith is called with a FunProxy it propagtes its handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<Expectation> = mutableListOf()
        val values = fixture.listFixture<String>().toTypedArray().sortedArray()

        val builder = AssertionChainStub { handle ->
            captured.add(handle)
        }
        val proxy = fixture.funProxyFixture(name, 1).also { it.assertionChain = builder }

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        proxy.hasBeenCalledWith(*values)
        val actual = captured.first()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn9")
    fun `Given hasBeenStrictlyCalledWith is called with a FunProxy it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 0)

        // When
        val actual = proxy.hasBeenStrictlyCalledWith()

        // Then
        actual mustBe Expectation(proxy, emptyList())
    }

    @Test
    @JsName("fn10")
    fun `Given hasBeenStrictlyCalledWith is called with a FunProxy it returns a VerficationHandle which contains no matches if nothing matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = proxy.hasBeenStrictlyCalledWith(fixture.fixture<String>())

        // Then
        actual mustBe Expectation(proxy, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn11")
    fun `Given hasBeenStrictlyCalledWith is called with a FunProxy it returns a VerficationHandle which contains matches if something matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)
        val values = fixture.listFixture<String>().toTypedArray()

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = proxy.hasBeenStrictlyCalledWith(*values)

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn12")
    fun `Given hasBeenStrictlyCalledWith is called with a FunProxy it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<Expectation> = mutableListOf()
        val values = fixture.listFixture<String>().toTypedArray()

        val builder = AssertionChainStub { handle ->
            captured.add(handle)
        }
        val proxy = fixture.funProxyFixture(name, 1).also { it.assertionChain = builder }

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        proxy.hasBeenStrictlyCalledWith(*values)

        val actual = captured.first()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn13")
    fun `Given hasBeenCalledWithout is called with a FunProxy it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 0)

        // When
        val actual = proxy.hasBeenCalledWithout()

        // Then
        actual mustBe Expectation(proxy, emptyList())
    }

    @Test
    @JsName("fn14")
    fun `Given hasBeenCalledWithout is called with a FunProxy it returns a VerficationHandle which contains no matches if nothing matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)
        val values = fixture.listFixture<String>().toTypedArray()

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = proxy.hasBeenCalledWithout(*values)

        // Then
        actual mustBe Expectation(proxy, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn15")
    fun `Given hasBeenCalledWithout is called with a FunProxy it returns a VerficationHandle which contains matches if something matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = fixture.funProxyFixture(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = proxy.hasBeenCalledWithout(fixture.fixture<String>())

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn16")
    fun `Given hasBeenCalledWithout is called with a FunProxy it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<Expectation> = mutableListOf()

        val builder = AssertionChainStub { handle ->
            captured.add(handle)
        }
        val proxy = fixture.funProxyFixture(name, 1).also { it.assertionChain = builder }

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        proxy.hasBeenCalledWithout(fixture.fixture<String>())
        val actual = captured.first()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn17")
    fun `Given wasGotten is called with a PropProxy it returns a VerficationHandle while filtering mismatches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = PropertyProxyStub(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(null)
        }

        // When
        val actual = proxy.wasGotten()

        // Then
        actual mustBe Expectation(proxy, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn18")
    fun `Given wasGotten is called with a PropProxy it returns a VerficationHandle which contains matches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = PropertyProxyStub(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Get
        }

        // When
        val actual = proxy.wasGotten()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn19")
    fun `Given wasGotten is called with a PropProxy it it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<Expectation> = mutableListOf()

        val builder = AssertionChainStub { handle ->
            captured.add(handle)
        }
        val proxy = PropertyProxyStub(name, 1).also { it.assertionChain = builder }

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Get
        }

        // When
        proxy.wasGotten()
        val actual = captured.first()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn20")
    fun `Given wasSet is called with a PropProxy it returns a VerficationHandle while filtering mismatches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = PropertyProxyStub(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Get
        }

        // When
        val actual = proxy.wasSet()

        // Then
        actual mustBe Expectation(proxy, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn21")
    fun `Given wasSet is called with a PropProxy it returns a VerficationHandle which contains matches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = PropertyProxyStub(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(null)
        }

        // When
        val actual = proxy.wasSet()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn22")
    fun `Given wasSet is called with a PropProxy it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<Expectation> = mutableListOf()

        val builder = AssertionChainStub { handle ->
            captured.add(handle)
        }
        val proxy = PropertyProxyStub(name, 1).also { it.assertionChain = builder }

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(null)
        }

        // When
        proxy.wasSet()
        val actual = captured.first()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn23")
    fun `Given wasSetTo is called with a PropProxy it returns a VerficationHandle while filtering mismatches`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = PropertyProxyStub(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Get
        }

        // When
        val actual = proxy.wasSetTo(fixture.fixture())

        // Then
        actual mustBe Expectation(proxy, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn24")
    fun `Given wasSetTo is called with a PropProxy it returns a VerficationHandle while filtering mismatching Values`() {
        // Given
        val name: String = fixture.fixture()
        val proxy = PropertyProxyStub(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(fixture.fixture())
        }

        // When
        val actual = proxy.wasSetTo(fixture.fixture())

        // Then
        actual mustBe Expectation(proxy, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn25")
    fun `Given wasSetTo is called with a PropProxy it returns a VerficationHandle which contains matches`() {
        // Given
        val name: String = fixture.fixture()
        val value: Any = fixture.fixture()
        val proxy = PropertyProxyStub(name, 1)

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(value)
        }

        // When
        val actual = proxy.wasSetTo(value)

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn26")
    fun `Given wasSetTo is called with a PropProxy it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val value: Any = fixture.fixture()

        val captured: MutableList<Expectation> = mutableListOf()

        val builder = AssertionChainStub { handle ->
            captured.add(handle)
        }
        val proxy = PropertyProxyStub(name, 1).also { it.assertionChain = builder }

        var capturedIndex: Int? = null
        proxy.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(value)
        }

        // When
        proxy.wasSetTo(value)
        val actual = captured.first()

        // Then
        actual mustBe Expectation(proxy, listOf(0))
        capturedIndex mustBe 0
    }
}
