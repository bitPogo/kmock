/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.util.test.mustBe

class GenericDeclarationExtensionSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `Given a GenericDeclarationExtension has exact 1 type stored it returns it`() {
        // Given
        val type = ByteArray::class.asTypeName()

        // When
        val actual = GenericDeclaration(
            types = listOf(type),
            isRecursive = fixture.fixture(),
            isNullable = false,
            doCastReturnType = fixture.fixture(),
        ).resolveGeneric()

        // Then
        actual mustBe type
    }

    @Test
    fun `Given a GenericDeclarationExtension has exact 1 type stored it returns it, while aligning its nullability`() {
        // Given
        val type = ByteArray::class.asTypeName()
        val nullable: Boolean = fixture.fixture()

        // When
        val actual = GenericDeclaration(
            types = listOf(type),
            isRecursive = fixture.fixture(),
            isNullable = nullable,
            doCastReturnType = fixture.fixture(),
        ).resolveGeneric()

        // Then
        actual mustBe type.copy(nullable = nullable)
    }

    @Test
    fun `Given a GenericDeclarationExtension has more then 1 type stored it returns ann any`() {
        // When
        val actual = GenericDeclaration(
            types = listOf(mockk(), mockk()),
            isRecursive = fixture.fixture(),
            isNullable = false,
            doCastReturnType = fixture.fixture(),
        ).resolveGeneric()

        // Then
        actual mustBe Any::class.asClassName()
    }

    @Test
    fun `Given a GenericDeclarationExtension has more then 1 type stored it returns it, while aligning its nullability`() {
        // Given
        val nullable: Boolean = fixture.fixture()

        // When
        val actual = GenericDeclaration(
            types = listOf(mockk(), mockk()),
            isRecursive = fixture.fixture(),
            isNullable = nullable,
            doCastReturnType = fixture.fixture(),
        ).resolveGeneric()

        // Then
        actual.isNullable mustBe nullable
    }
}
