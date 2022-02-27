/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import VerificationChainBuilderStub
import tech.antibytes.kmock.KMockContract.VerificationHandle
import tech.antibytes.mock.FunMockeryStub
import tech.antibytes.mock.PropertyMockeryStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class VerificationHandleFactorySpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `Given hasBeenCalledWith is called with a FunMockery it returns a VerficationHandle which contains no matches if there were no calls`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 0)

        // When
        val actual = mock.hasBeenCalled()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
    }

    @Test
    @JsName("fn1")
    fun `Given hasBeenCalledWith is called with a FunMockery it returns a VerficationHandle which contains no matches if there were calls`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = mock.hasBeenCalled()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }
    
    @Test
    @JsName("fn2")
    fun `Given hasBeenCalledWith is called with a FunMockery it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 0)

        // When
        val actual = mock.hasBeenCalledWith()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
    }

    @Test
    @JsName("fn3")
    fun `Given hasBeenCalledWith is called with a FunMockery it returns a VerficationHandle which contains no matches if nothing matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = mock.hasBeenCalledWith(fixture.fixture<String>())

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn4")
    fun `Given hasBeenCalledWith is called with a FunMockery it returns a VerficationHandle which contains matches if something matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)
        val values = fixture.listFixture<String>().toTypedArray()

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = mock.hasBeenCalledWith(*(values.sorted()).toTypedArray())

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn5")
    fun `Given hasBeenCalledWith is called with a FunMockery it propagtes its handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<VerificationHandle> = mutableListOf()
        val values = fixture.listFixture<String>().toTypedArray()

        val builder = VerificationChainBuilderStub(captured)
        val mock = FunMockeryStub(name, 1, verificationBuilderReference = builder)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        mock.hasBeenCalledWith(*(values.sorted()).toTypedArray())
        val actual = captured.first()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn6")
    fun `Given hasBeenStrictlyCalledWith is called with a FunMockery it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 0)

        // When
        val actual = mock.hasBeenStrictlyCalledWith()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
    }

    @Test
    @JsName("fn7")
    fun `Given hasBeenStrictlyCalledWith is called with a FunMockery it returns a VerficationHandle which contains no matches if nothing matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = mock.hasBeenStrictlyCalledWith(fixture.fixture<String>())

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn8")
    fun `Given hasBeenStrictlyCalledWith is called with a FunMockery it returns a VerficationHandle which contains matches if something matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)
        val values = fixture.listFixture<String>().toTypedArray()

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = mock.hasBeenStrictlyCalledWith(*values)

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn9")
    fun `Given hasBeenStrictlyCalledWith is called with a FunMockery it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<VerificationHandle> = mutableListOf()
        val values = fixture.listFixture<String>().toTypedArray()

        val builder = VerificationChainBuilderStub(captured)
        val mock = FunMockeryStub(name, 1, verificationBuilderReference = builder)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        mock.hasBeenStrictlyCalledWith(*values)

        val actual = captured.first()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn10")
    fun `Given hasBeenCalledWithout is called with a FunMockery it returns a VerficationHandle which contains no matches if nothing matches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 0)

        // When
        val actual = mock.hasBeenCalledWithout()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
    }

    @Test
    @JsName("fn11")
    fun `Given hasBeenCalledWithout is called with a FunMockery it returns a VerficationHandle which contains no matches if nothing matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)
        val values = fixture.listFixture<String>().toTypedArray()

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            values
        }

        // When
        val actual = mock.hasBeenCalledWithout(*values)

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn12")
    fun `Given hasBeenCalledWithout is called with a FunMockery it returns a VerficationHandle which contains matches if something matches while delegating the captured values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = FunMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        val actual = mock.hasBeenCalledWithout(fixture.fixture<String>())

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn13")
    fun `Given hasBeenCalledWithout is called with a FunMockery it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<VerificationHandle> = mutableListOf()

        val builder = VerificationChainBuilderStub(captured)
        val mock = FunMockeryStub(name, 1, verificationBuilderReference = builder)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            fixture.listFixture<String>().toTypedArray()
        }

        // When
        mock.hasBeenCalledWithout(fixture.fixture<String>())
        val actual = captured.first()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn14")
    fun `Given wasGotten is called with a PropMockery it returns a VerficationHandle while filtering mismatches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = PropertyMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(null)
        }

        // When
        val actual = mock.wasGotten()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn15")
    fun `Given wasGotten is called with a PropMockery it returns a VerficationHandle which contains matches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = PropertyMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Get
        }

        // When
        val actual = mock.wasGotten()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn16")
    fun `Given wasGotten is called with a PropMockery it it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<VerificationHandle> = mutableListOf()

        val builder = VerificationChainBuilderStub(captured)
        val mock = PropertyMockeryStub(name, 1, verificationBuilderReference = builder)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Get
        }

        // When
        mock.wasGotten()
        val actual = captured.first()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn17")
    fun `Given wasSet is called with a PropMockery it returns a VerficationHandle while filtering mismatches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = PropertyMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Get
        }

        // When
        val actual = mock.wasSet()

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn18")
    fun `Given wasSet is called with a PropMockery it returns a VerficationHandle which contains matches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = PropertyMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(null)
        }

        // When
        val actual = mock.wasSet()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn19")
    fun `Given wasSet is called with a PropMockery it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val captured: MutableList<VerificationHandle> = mutableListOf()

        val builder = VerificationChainBuilderStub(captured)
        val mock = PropertyMockeryStub(name, 1, verificationBuilderReference = builder)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(null)
        }

        // When
        mock.wasSet()
        val actual = captured.first()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn20")
    fun `Given wasSetTo is called with a PropMockery it returns a VerficationHandle while filtering mismatches`() {
        // Given
        val name: String = fixture.fixture()
        val mock = PropertyMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Get
        }

        // When
        val actual = mock.wasSetTo(fixture.fixture())

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn21")
    fun `Given wasSetTo is called with a PropMockery it returns a VerficationHandle while filtering mismatching Values`() {
        // Given
        val name: String = fixture.fixture()
        val mock = PropertyMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(fixture.fixture())
        }

        // When
        val actual = mock.wasSetTo(fixture.fixture())

        // Then
        actual mustBe VerificationHandle(name, emptyList())
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn22")
    fun `Given wasSetTo is called with a PropMockery it returns a VerficationHandle which contains matches`() {
        // Given
        val name: String = fixture.fixture()
        val value: Any = fixture.fixture()
        val mock = PropertyMockeryStub(name, 1)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(value)
        }

        // When
        val actual = mock.wasSetTo(value)

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }

    @Test
    @JsName("fn23")
    fun `Given wasSetTo is called with a PropMockery it propagates its Handle`() {
        // Given
        val name: String = fixture.fixture()
        val value: Any = fixture.fixture()

        val captured: MutableList<VerificationHandle> = mutableListOf()

        val builder = VerificationChainBuilderStub(captured)
        val mock = PropertyMockeryStub(name, 1, verificationBuilderReference = builder)

        var capturedIndex: Int? = null
        mock.getArgumentsForCall = { givenIndex ->
            capturedIndex = givenIndex

            KMockContract.GetOrSet.Set(value)
        }

        // When
        mock.wasSetTo(value)
        val actual = captured.first()

        // Then
        actual mustBe VerificationHandle(name, listOf(0))
        capturedIndex mustBe 0
    }
}
