/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils

class KMockProcessorProviderSpec {
    @Test
    fun `It fulfils SymbolProcessorProvider`() {
        KMockProcessorProvider() fulfils SymbolProcessorProvider::class
    }

    @Test
    fun `Given create is called it returns a SymbolProcessor`() {
        // Given
        val environment: SymbolProcessorEnvironment = mockk()
        val options = mapOf(
            "kmock_kspDir" to "dir",
            "kmock_isKmp" to "false",
            "kmock_rootPackage" to "somewhere",
        )

        every { environment.logger } returns mockk()
        every { environment.codeGenerator } returns mockk()
        every { environment.options } returns options

        // When
        val processor = KMockProcessorProvider().create(environment)

        // Then
        processor fulfils SymbolProcessor::class

        verify(exactly = 1) { environment.logger }
        verify(exactly = 1) { environment.codeGenerator }
        verify(exactly = 1) { environment.options }
    }
}
