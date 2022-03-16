/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.compiler.config.MainConfig
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import java.util.concurrent.Callable

class KMockCompilerPluginSpec {
    @Test
    fun `It fulfils KotlinCompilerPluginSupportPlugin`() {
        KMockCompilerPlugin() fulfils KotlinCompilerPluginSupportPlugin::class
    }

    @Test
    fun `Given isApplicable is called with regular compile source it returns false`() {
        // Given
        val sourceSet: KotlinCompilation<*> = mockk()

        every { sourceSet.compileKotlinTaskName } returns "compileKotlinMetadata"

        // When
        val actual = KMockCompilerPlugin().isApplicable(sourceSet)

        // Then
        actual mustBe false
    }

    @Test
    fun `Given isApplicable is called with regular compile test source it returns true`() {
        // Given
        val sourceSet: KotlinCompilation<*> = mockk()

        every { sourceSet.compileKotlinTaskName } returns "compileTestKotlinLinuxX64"

        // When
        val actual = KMockCompilerPlugin().isApplicable(sourceSet)

        // Then
        actual mustBe true
    }

    @Test
    fun `Given getCompilerPluginId it returns its PluginId`() {
        // When
        val actual = KMockCompilerPlugin().getCompilerPluginId()

        // Then
        actual mustBe "tech.antibytes.kmock.kmock-compiler"
    }

    @Test
    fun `Given getPluginArtifact it returns its coordinates`() {
        // When
        val actual = KMockCompilerPlugin().getPluginArtifact()

        // Then
        actual.groupId mustBe "tech.antibytes.kmock"
        actual.artifactId mustBe "kmock-compiler"
        actual.version mustBe MainConfig.version
    }

    @Test
    fun `Given getPluginArtifactForNative it returns its coordinates`() {
        // When
        val actual = KMockCompilerPlugin().getPluginArtifactForNative()

        // Then
        actual.groupId mustBe "tech.antibytes.kmock"
        actual.artifactId mustBe "kmock-compiler"
        actual.version mustBe MainConfig.version
    }

    @Test
    fun `Given applyToCompilation is called it builds an provider which propagates the compiler options`() {
        // Given
        val project: Project = mockk()
        val extension: KMockCompilerExtension = mockk()
        val sourceSet: KotlinCompilation<*> = mockk()
        val provider: Provider<List<SubpluginOption>> = mockk()

        val providerBuilder = slot<Callable<List<SubpluginOption>>>()

        every { sourceSet.target.project } returns project
        every {
            project.extensions.getByType<KMockCompilerExtension>(KMockCompilerExtension::class.java)
        } returns extension
        every { extension.useExperimentalCompilerPlugin.get() } returns false
        every { project.provider(capture(providerBuilder)) } returns provider

        // When
        val actual = KMockCompilerPlugin().applyToCompilation(sourceSet)
        val options = providerBuilder.captured.call()

        // Then
        actual sameAs provider
        options[0].key mustBe "enableOpenClasses"
        options[0].value mustBe "false"
    }

    @Test
    fun `Given apply is called it registers its plugin`() {
        // Given
        val project: Project = mockk()

        every {
            project.extensions.create(any(), KMockCompilerExtension::class.java)
        } returns mockk()

        // When
        KMockCompilerPlugin().apply(project)

        // Then
        verify(exactly = 1) {
            project.extensions.create("kmockCompilation", KMockCompilerExtension::class.java)
        }
    }
}
