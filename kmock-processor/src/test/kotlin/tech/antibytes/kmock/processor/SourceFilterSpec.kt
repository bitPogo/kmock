/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class SourceFilterSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils SourceFilterSpec`() {
        SourceFilter() fulfils ProcessorContract.SourceFilter::class
    }

    @Test
    fun `Given filter is called with 2 Lists of Sources it filters the first by the 2nd according to the qualified name`() {
        // Given
        val source0_0: KSClassDeclaration = mockk()
        val source1_1: KSClassDeclaration = mockk()

        val source1_0: KSClassDeclaration = mockk()
        val source0_1: KSClassDeclaration = mockk()

        val sources0 = listOf(
            ProcessorContract.InterfaceSource("", source0_0),
            ProcessorContract.InterfaceSource("", source0_1)
        )

        val sources1 = listOf(
            ProcessorContract.InterfaceSource("", source1_0),
            ProcessorContract.InterfaceSource("", source1_1)
        )

        val sameSource: String = fixture.fixture()

        every { source0_0.qualifiedName!!.asString() } returns fixture.fixture()
        every { source0_1.qualifiedName!!.asString() } returns sameSource

        every { source1_0.qualifiedName!!.asString() } returns fixture.fixture()
        every { source1_1.qualifiedName!!.asString() } returns sameSource

        // When
        val actual = SourceFilter().filter(sources0, sources1)

        // Then
        actual mustBe listOf(
            sources0.first()
        )
    }
}
