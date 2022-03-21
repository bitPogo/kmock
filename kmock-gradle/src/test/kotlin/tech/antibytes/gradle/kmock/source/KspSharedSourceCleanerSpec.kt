/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import java.io.File

class KspSharedSourceCleanerSpec {
    @TempDir
    private lateinit var buildDir: File
    private lateinit var file: File
    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
        project.buildDir = buildDir
    }

    private fun createStubs(): File {
        val generated = File(buildDir, "generated")
        generated.mkdir()

        val ksp = File(generated, "ksp")
        ksp.mkdir()

        val targetPlatform = File(ksp, "platform")
        targetPlatform.mkdir()

        val target = File(targetPlatform, "target")
        target.mkdir()

        val sub = File(target, "sub")
        sub.mkdir()

        val file1 = File(target, "Something.kt")
        file1.createNewFile()

        val file2 = File(sub, "SomethingElse.kt")
        file2.createNewFile()

        file1.writeText("content")
        file2.writeText("content")

        return targetPlatform
    }

    @Test
    fun `It fulfils KmpSharedSourceCleaner`() {
        KspSharedSourceCleaner fulfils KMockPluginContract.KspSharedSourceCleaner::class
    }

    @Test
    fun `Given cleanMetaSources is called, it removes the given SharedSources from the generated KSP output`() {
        // Given
        val platform = createStubs()

        // When
        KspSharedSourceCleaner.cleanKspSources(project, setOf("${platform.name}Test"))

        // Then
        platform.exists() mustBe false
    }
}
