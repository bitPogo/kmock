/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class KSClassDeclarationSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `Given deriveSimpleName is called it fails if the qualified name is empty`() {
        // Given
        val qualified = null
        val declaration: KSClassDeclaration = mockk()

        every { declaration.qualifiedName?.asString() } returns qualified

        // When
        val error = assertFailsWith<IllegalStateException> {
            declaration.deriveSimpleName(fixture.fixture())
        }

        // Then
        error.message mustBe "Expected non null class name!"
    }

    @Test
    fun `Given deriveSimpleName is called it fails if the package name is not part of the qualified name`() {
        // Given
        val qualified: String = fixture.fixture()
        val declaration: KSClassDeclaration = mockk()

        every { declaration.qualifiedName?.asString() } returns qualified

        // When
        val error = assertFailsWith<IllegalStateException> {
            declaration.deriveSimpleName(fixture.fixture())
        }

        // Then
        error.message mustBe "Malformed class name!"
    }

    @Test
    fun `Given deriveSimpleName is called it fails if the package name is the qualified name`() {
        // Given
        val qualified: String = fixture.fixture()
        val declaration: KSClassDeclaration = mockk()

        every { declaration.qualifiedName?.asString() } returns qualified

        // When
        val error = assertFailsWith<IllegalStateException> {
            declaration.deriveSimpleName(qualified)
        }

        // Then
        error.message mustBe "Malformed class name!"
    }

    @Test
    fun `Given deriveSimpleName is called it returns the derived SimpleName`() {
        // Given
        val simpleName: String = fixture.fixture()
        val packageName: String = fixture.fixture()
        val declaration: KSClassDeclaration = mockk()

        every { declaration.qualifiedName?.asString() } returns "$packageName.$simpleName"

        // When
        val actual = declaration.deriveSimpleName(packageName)

        // Then
        actual mustBe simpleName
    }
}
