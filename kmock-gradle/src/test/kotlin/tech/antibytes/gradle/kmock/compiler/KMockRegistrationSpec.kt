/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.compiler.KMockCompilerPluginContract.Companion.ARG_ENABLE_COMPILER_PLUGIN_FIELD
import tech.antibytes.util.test.fulfils

class KMockRegistrationSpec {
    @Test
    fun `It fulfils ComponentRegistrar`() {
        KMockRegistration() fulfils ComponentRegistrar::class
    }

    @Test
    fun `Given registerProjectComponents is called,  it does nothing if enableOpenClasses is false or by default`() {
        // Given
        val project: MockProject = mockk()
        val config: CompilerConfiguration = mockk()

        every { config.get(ARG_ENABLE_COMPILER_PLUGIN_FIELD, false) } returns false

        // When
        KMockRegistration().registerProjectComponents(project, config)

        verify(exactly = 1) { config.get(ARG_ENABLE_COMPILER_PLUGIN_FIELD, false) }
    }
}
