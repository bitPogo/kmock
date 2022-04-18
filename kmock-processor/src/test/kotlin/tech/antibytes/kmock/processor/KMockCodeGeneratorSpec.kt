/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import kotlin.test.assertFailsWith

class KMockCodeGeneratorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils CodeGenerator`() {
        KMockCodeGenerator(fixture.fixture(), mockk()) fulfils CodeGenerator::class
    }

    @Test
    fun `It fulfils KmpCodeGenerator`() {
        KMockCodeGenerator(fixture.fixture(), mockk()) fulfils ProcessorContract.KmpCodeGenerator::class
    }

    @Test
    fun `associate is not implemented`() {
        assertFailsWith<NotImplementedError> {
            KMockCodeGenerator(fixture.fixture(), mockk()).associate(
                listOf(),
                fixture.fixture(),
                fixture.fixture()
            )
        }
    }

    @Test
    fun `associateWithClasses is not implemented`() {
        assertFailsWith<NotImplementedError> {
            KMockCodeGenerator(fixture.fixture(), mockk()).associateWithClasses(
                listOf(),
                fixture.fixture(),
                fixture.fixture()
            )
        }
    }
}
