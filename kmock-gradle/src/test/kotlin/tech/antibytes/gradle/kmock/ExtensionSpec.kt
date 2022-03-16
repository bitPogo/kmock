/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.compiler.KMockCompilerExtension
import tech.antibytes.gradle.test.createExtension
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class ExtensionSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils Extension`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()
        val compilerExtension: KMockCompilerExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { project.extensions.getByType(KMockCompilerExtension::class.java) } returns compilerExtension

        val extension = createExtension<KMockExtension>(project)
        extension fulfils KMockPluginContract.Extension::class
    }

    @Test
    fun `Its default rootPackage is an empty string`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()
        val compilerExtension: KMockCompilerExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { project.extensions.getByType(KMockCompilerExtension::class.java) } returns compilerExtension

        val extension = createExtension<KMockExtension>(project)

        extension.rootPackage mustBe ""
    }

    @Test
    fun `It propagates rootPackage changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val compilerExtension: KMockCompilerExtension = mockk()
        val expected: String = fixture.fixture()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { project.extensions.getByType(KMockCompilerExtension::class.java) } returns compilerExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.rootPackage = expected

        extension.rootPackage mustBe expected
        verify(exactly = 1) { kspExtension.arg("rootPackage", expected) }
    }

    @Test
    fun `Its default useExperimentalCompilerPlugin flag is false`() {
        // Given
        val project: Project = mockk()
        val kspExtension: KspExtension = mockk()
        val compilerExtension: KMockCompilerExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { project.extensions.getByType(KMockCompilerExtension::class.java) } returns compilerExtension

        // When
        val extension = createExtension<KMockExtension>(project)

        // Then
        extension.useExperimentalCompilerPlugin mustBe false
    }

    @Test
    fun `It propagates useExperimentalCompilerPlugin changes to the CompilerPlugin`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()
        val compilerExtension: KMockCompilerExtension = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { project.extensions.getByType(KMockCompilerExtension::class.java) } returns compilerExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.useExperimentalCompilerPlugin = expected

        extension.useExperimentalCompilerPlugin mustBe expected
        verify(exactly = 1) { compilerExtension.useExperimentalCompilerPlugin.set(expected) }
    }
}
