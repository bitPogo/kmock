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
import tech.antibytes.kmock.processor.ProcessorContract.MemberReturnTypeInfo
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.mustBe

class MemberReturnTypeInfoExtensionsSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `Given resolveClassScope is called it returns null if no class scope was delegated`() {
        // Given
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
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
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
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
        val proxyTypeName: TypeName = mockk()
        val proxyTypeNameStr = "${fixture.fixture<String>()}?"
        val types: List<TypeName> = mockk()
        val classScope: Map<String, List<TypeName>> = mapOf(
            proxyTypeNameStr.dropLast(1) to types
        )

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = proxyTypeName,
            generic = null,
            classScope = classScope
        )

        every { proxyTypeName.toString() } returns proxyTypeNameStr

        // When
        val actual = typeInfo.resolveClassScope()

        // Then
        actual mustBe types
    }

    @Test
    fun `Given needsCastAnnotation returns true if the methodTypeName and proxyTypeName differ`() {
        // Given
        val proxyTypeName: TypeName = mockk()
        val methodTypeName: TypeName = mockk()

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = methodTypeName,
            proxyTypeName = proxyTypeName,
            generic = null,
            classScope = null
        )

        every { proxyTypeName.toString() } returns fixture.fixture()
        every { methodTypeName.toString() } returns fixture.fixture()

        // When
        val actual = typeInfo.needsCastAnnotation(null)

        // Then
        actual mustBe true
    }

    @Test
    fun `Given needsCastAnnotation returns true if the relaxer is not null and it has generics types`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val methodTypeName: TypeName = mockk()

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = methodTypeName,
            proxyTypeName = methodTypeName,
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
        val proxyTypeName: TypeName = mockk()
        val proxyTypeNameStr = "${fixture.fixture<String>()}?"
        val types: List<TypeName> = mockk()
        val classScope: Map<String, List<TypeName>> = mapOf(
            proxyTypeNameStr.dropLast(1) to types
        )

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = proxyTypeName,
            proxyTypeName = proxyTypeName,
            generic = null,
            classScope = classScope
        )

        every { proxyTypeName.toString() } returns proxyTypeNameStr

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
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
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
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk())
        every { generics.doCastReturnType } returns false

        // When
        val actual = typeInfo.needsCastForReceiverProperty()

        // Then
        actual mustBe false
    }

    @Test
    fun `Given needsCastForReceiverProperty returns false if it has generics and at least 2 types`() {
        // Given
        val generics: GenericDeclaration = mockk()
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
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
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns emptyList()
        every { generics.doCastReturnType } returns true

        // When
        val actual = typeInfo.needsCastForReceiverProperty()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given hasGenerics returns false if neither a class scope nor a generic were given`() {
        // Given
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
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
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
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
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
            generic = mockk(),
            classScope = null
        )

        // When
        val actual = typeInfo.hasGenerics()

        // Then
        actual mustBe true
    }

    @Test
    fun `Given resolveCastOnReturn returns a string if the methodTypeName and proxyTypeName differ`() {
        // Given
        val proxyTypeName: TypeName = mockk()
        val methodTypeName: TypeName = mockk()

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = methodTypeName,
            proxyTypeName = proxyTypeName,
            generic = null,
            classScope = null
        )

        // When
        val actual = typeInfo.resolveCastOnReturn()

        // Then
        actual mustBe " as $methodTypeName"
    }

    @Test
    fun `Given resolveCastForRelaxer returns an empty string if the relaxer is null`() {
        // Given
        val methodTypeName: TypeName = mockk()

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = methodTypeName,
            proxyTypeName = methodTypeName,
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
        val methodTypeName: TypeName = mockk()

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = methodTypeName,
            proxyTypeName = methodTypeName,
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
        val methodTypeName: TypeName = mockk()

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = methodTypeName,
            proxyTypeName = methodTypeName,
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk())

        // When
        val actual = typeInfo.resolveCastForRelaxer(mockk())

        // Then
        actual mustBe " as $methodTypeName"
    }

    @Test
    fun `Given resolveCastForRelaxer returns a string if the relaxer is not null and it has a resolve ClassScope`() {
        // Given
        val proxyTypeName: TypeName = mockk()
        val proxyTypeNameStr = "${fixture.fixture<String>()}?"
        val types: List<TypeName> = mockk()
        val classScope: Map<String, List<TypeName>> = mapOf(
            proxyTypeNameStr.dropLast(1) to types
        )

        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = proxyTypeName,
            proxyTypeName = proxyTypeName,
            generic = null,
            classScope = classScope
        )

        every { proxyTypeName.toString() } returns proxyTypeNameStr

        // When
        val actual = typeInfo.resolveCastForRelaxer(mockk())

        // Then
        actual mustBe " as $proxyTypeNameStr"
    }

    @Test
    fun `Given resolveCastForReceiverProperty returns an empty String if it has no generics`() {
        // Given
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
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
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = mockk(),
            proxyTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk())
        every { generics.doCastReturnType } returns false

        // When
        val actual = typeInfo.resolveCastForReceiverProperty()

        // Then
        actual mustBe ""
    }

    @Test
    fun `Given resolveCastForReceiverProperty returns a String if it has generics and at least 2 types`() {
        // Given
        val methodTypeName: TypeName = mockk()
        val generics: GenericDeclaration = mockk()
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = methodTypeName,
            proxyTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns listOf(mockk(), mockk())

        // When
        val actual = typeInfo.resolveCastForReceiverProperty()

        // Then
        actual mustBe " as $methodTypeName"
    }

    @Test
    fun `Given resolveCastForReceiverProperty returns a string if the cast flag is true`() {
        // Given
        val type: TypeName = mockk()
        val generics: GenericDeclaration = mockk()
        val typeInfo = MemberReturnTypeInfo(
            methodTypeName = type,
            proxyTypeName = mockk(),
            generic = generics,
            classScope = null
        )

        every { generics.types } returns emptyList()
        every { generics.doCastReturnType } returns true

        // When
        val actual = typeInfo.resolveCastForReceiverProperty()

        // Then
        actual mustBe " as $type"
    }
}
