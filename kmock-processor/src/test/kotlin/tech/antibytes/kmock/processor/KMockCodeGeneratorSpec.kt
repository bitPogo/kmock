/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils

class KMockCodeGeneratorSpec {
    @Test
    fun `It fulfils CodeGenerator`() {
        KMockCodeGenerator("abc", mockk()) fulfils CodeGenerator::class
    }

    @Test
    fun `It fulfils KmpCodeGenerator`() {
        KMockCodeGenerator("abc", mockk()) fulfils ProcessorContract.KmpCodeGenerator::class
    }
}
