/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.TypeName
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.mustBe

class MethodReturnTypeInfoExtensionsSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `Given resolveClassScope is called it returns null if no class scope was delegated`() {
        // Given
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = null,
            classScope = null
        )

        // When
        val actual = typeInfo.resolveClassScope()

        // Then
        actual mustBe null
    }

    @Test
    fun `Given resolveClassScope is called it returns null if no matching types were found`() {
        // Given
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = null,
            classScope = mapOf(
                "any" to mockk()
            )
        )

        // When
        val actual = typeInfo.resolveClassScope()

        // Then
        actual mustBe null
    }

    @Test
    fun `Given resolveClassScope is called it returns the resolved types`() {
        // Given
        val actualTypeName: TypeName = mockk()
        val actualTypeNameStr = "${fixture.fixture<String>()}?"
        val types: List<TypeName> = mockk()
        val classScope: Map<String, List<TypeName>> = mapOf(
            actualTypeNameStr.dropLast(1) to types
        )

        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = actualTypeName,
            generic = null,
            classScope = classScope
        )

        every { actualTypeName.toString() } returns actualTypeNameStr

        // When
        val actual = typeInfo.resolveClassScope()

        // Then
        actual mustBe types
    }

    @Test
    fun `Given needsCastAnnotation returns true if the typeName and actualTypeName differ`() {
        // Given
        val actualTypeName: TypeName = mockk()
        val typeName: TypeName = mockk()

        val typeInfo = MethodReturnTypeInfo(
            typeName = typeName,
            actualTypeName = actualTypeName,
            generic = null,
            classScope = null
        )

        every { actualTypeName.toString() } returns fixture.fixture()
        every { typeName.toString() } returns fixture.fixture()

        // When
        val actual = typeInfo.needsCastAnnotation(null)

        // Then
        actual mustBe true
    }

    @Test
    fun `Given needsCastAnnotation returns true if the relaxer is not null and it has generics types`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val typeName: TypeName = mockk()

        val typeInfo = MethodReturnTypeInfo(
            typeName = typeName,
            actualTypeName = typeName,
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk())

        // When
        val actual = typeInfo.needsCastAnnotation(
            relaxer = mockk()
        )

        // Then
        actual mustBe true
    }

    @Test
    fun `Given needsCastAnnotation returns true if the relaxer is not null and it has a resolve ClassScope`() {
        // Given
        val actualTypeName: TypeName = mockk()
        val actualTypeNameStr = "${fixture.fixture<String>()}?"
        val types: List<TypeName> = mockk()
        val classScope: Map<String, List<TypeName>> = mapOf(
            actualTypeNameStr.dropLast(1) to types
        )

        val typeInfo = MethodReturnTypeInfo(
            typeName = actualTypeName,
            actualTypeName = actualTypeName,
            generic = null,
            classScope = classScope
        )

        every { actualTypeName.toString() } returns actualTypeNameStr

        // When
        val actual = typeInfo.needsCastAnnotation(
            relaxer = mockk()
        )

        // Then
        actual mustBe true
    }

    @Test
    fun `Given needsCastForReceiverProperty returns false if it has no generics`() {
        // Given
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = null,
            classScope = null
        )

        // When
        val actual = typeInfo.needsCastForReceiverProperty()

        // Then
        actual mustBe false
    }

    @Test
    fun `Given needsCastForReceiverProperty returns false if it has generics and at less then 2 types`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk())
        every { generics.castReturnType } returns false

        // When
        val actual = typeInfo.needsCastForReceiverProperty()

        // Then
        actual mustBe false
    }

    @Test
    fun `Given needsCastForReceiverProperty returns false if it has generics and at least 2 types`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk(), mockk())

        // When
        val actual = typeInfo.needsCastForReceiverProperty()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given needsCastForReceiverProperty returns true if the cast flag is true`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns emptyList()
        every { generics.castReturnType } returns true

        // When
        val actual = typeInfo.needsCastForReceiverProperty()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given hasGenerics returns false if neither a class scope nor a generic were given`() {
        // Given
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = null,
            classScope = null
        )

        // When
        val actual = typeInfo.hasGenerics()

        // Then
        actual mustBe false
    }

    @Test
    fun `Given hasGenerics returns true if a class scope was given`() {
        // Given
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = null,
            classScope = mockk()
        )

        // When
        val actual = typeInfo.hasGenerics()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given hasGenerics returns true if a generics was given`() {
        // Given
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = mockk(),
            classScope = null
        )

        // When
        val actual = typeInfo.hasGenerics()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given resolveCastOnReturn returns a string if the typeName and actualTypeName differ`() {
        // Given
        val actualTypeName: TypeName = mockk()
        val typeName: TypeName = mockk()

        val typeInfo = MethodReturnTypeInfo(
            typeName = typeName,
            actualTypeName = actualTypeName,
            generic = null,
            classScope = null
        )

        // When
        val actual = typeInfo.resolveCastOnReturn()

        // Then
        actual mustBe " as $typeName"
    }

    @Test
    fun `Given resolveCastForRelaxer returns an empty string if the relaxer is null`() {
        // Given
        val typeName: TypeName = mockk()

        val typeInfo = MethodReturnTypeInfo(
            typeName = typeName,
            actualTypeName = typeName,
            generic = null,
            classScope = null
        )

        // When
        val actual = typeInfo.resolveCastForRelaxer(null)

        // Then
        actual mustBe ""
    }

    @Test
    fun `Given resolveCastForRelaxer returns an empty string if the relaxer is not null but it has no generics types`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val typeName: TypeName = mockk()

        val typeInfo = MethodReturnTypeInfo(
            typeName = typeName,
            actualTypeName = typeName,
            generic = null,
            classScope = null
        )

        every { generics.types } returns listOf(mockk())

        // When
        val actual = typeInfo.resolveCastForRelaxer(mockk())

        // Then
        actual mustBe ""
    }

    @Test
    fun `Given resolveCastForRelaxer returns a string if the relaxer is not null and it has generics types`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val typeName: TypeName = mockk()

        val typeInfo = MethodReturnTypeInfo(
            typeName = typeName,
            actualTypeName = typeName,
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk())

        // When
        val actual = typeInfo.resolveCastForRelaxer(mockk())

        // Then
        actual mustBe " as $typeName"
    }

    @Test
    fun `Given resolveCastForRelaxer returns a string if the relaxer is not null and it has a resolve ClassScope`() {
        // Given
        val actualTypeName: TypeName = mockk()
        val actualTypeNameStr = "${fixture.fixture<String>()}?"
        val types: List<TypeName> = mockk()
        val classScope: Map<String, List<TypeName>> = mapOf(
            actualTypeNameStr.dropLast(1) to types
        )

        val typeInfo = MethodReturnTypeInfo(
            typeName = actualTypeName,
            actualTypeName = actualTypeName,
            generic = null,
            classScope = classScope
        )

        every { actualTypeName.toString() } returns actualTypeNameStr

        // When
        val actual = typeInfo.resolveCastForRelaxer(mockk())

        // Then
        actual mustBe " as $actualTypeNameStr"
    }

    @Test
    fun `Given resolveCastForReceiverProperty returns an empty String if it has no generics`() {
        // Given
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = null,
            classScope = null
        )

        // When
        val actual = typeInfo.resolveCastForReceiverProperty()

        // Then
        actual mustBe ""
    }

    @Test
    fun `Given resolveCastForReceiverProperty returns an empty string if it has generics and at less then 2 types`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val typeInfo = MethodReturnTypeInfo(
            typeName = mockk(),
            actualTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk())
        every { generics.castReturnType } returns false

        // When
        val actual = typeInfo.resolveCastForReceiverProperty()

        // Then
        actual mustBe ""
    }

    @Test
    fun `Given resolveCastForReceiverProperty returns a String if it has generics and at least 2 types`() {
        // Given
        val typeName: TypeName = mockk()
        val generics: GenericDeclaration = mockk()
        val typeInfo = MethodReturnTypeInfo(
            typeName = typeName,
            actualTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk(), mockk())

        // When
        val actual = typeInfo.resolveCastForReceiverProperty()

        // Then
        actual mustBe " as $typeName"
    }

    @Test
    fun `Given resolveCastForReceiverProperty returns a string if the cast flag is true`() {
        // Given
        val type: TypeName = mockk()
        val generics: GenericDeclaration = mockk()
        val typeInfo = MethodReturnTypeInfo(
            typeName = type,
            actualTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns emptyList()
        every { generics.castReturnType } returns true

        // When
        val actual = typeInfo.resolveCastForReceiverProperty()

        // Then
        actual mustBe " as $type"
    }
}
