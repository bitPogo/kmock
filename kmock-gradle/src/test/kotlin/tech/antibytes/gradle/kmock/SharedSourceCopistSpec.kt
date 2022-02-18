/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import io.mockk.Runs
import tech.antibytes.util.test.fixture.kotlinFixture
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.file.FileTreeElement
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopExecutionException
import org.gradle.kotlin.dsl.invoke
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import java.io.File
import java.util.Locale
import kotlin.test.assertFailsWith

class SharedSourceCopistSpec {
    private val project: Project = mockk()
    private val fixture = kotlinFixture()

    @TempDir
    private lateinit var buildDir: File
    private lateinit var file: File

    @BeforeEach
    fun setUp() {
        clearMocks(project)
    }

    private fun prepareFile(value: String): File {
        val file = File(buildDir, "test")
        file.createNewFile()
        file.writeText(value)

        return file
    }

    @Test
    fun `It fulfils SharedSourceCopist`() {
        SharedSourceCopist fulfils KMockPluginContract.SharedSourceCopist::class
    }

    @Test
    fun `Given copySharedSource is called it fails if Source is empty`() {
        val error = assertFailsWith<StopExecutionException> {
            SharedSourceCopist.copySharedSource(project, "", "", "")
        }

        error.message mustBe "Cannot copy form invalid SourceDefinition!"
    }

    @Test
    fun `Given copySharedSource is called it fails if Target is empty`() {
        val error = assertFailsWith<StopExecutionException> {
            SharedSourceCopist.copySharedSource(project, "test", "", "")
        }

        error.message mustBe "Cannot copy to invalid SourceDefinition!"
    }

    @Test
    fun `Given copySharedSource is called it fails if Indicator is empty`() {
        val error = assertFailsWith<StopExecutionException> {
            SharedSourceCopist.copySharedSource(project, "test", "test", "")
        }

        error.message mustBe "Cannot copy with invalid Indicator!"
    }

    @Test
    fun `Given copySharedSource is called it creates a CopyTask`() {
        // Given
        val indicator = "Common"
        val source = "sourceTest"
        val target = "targetTest"
        val buildDir: String = fixture.fixture()
        val copyTask: Copy = mockk()

        every { project.buildDir.absolutePath } returns buildDir
        every { project.tasks.create(any<String>(), Copy::class.java) } returns copyTask

        every { copyTask.description = any<String>() } just Runs
        every { copyTask.group = any<String>() } just Runs
        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns copyTask
        every { copyTask.from(any<String>()) } returns copyTask
        every { copyTask.into(any<String>()) } returns copyTask
        every { copyTask.include(any<String>()) } returns copyTask
        every { copyTask.exclude(any<Spec<FileTreeElement>>()) } returns copyTask

        // When
        val task = SharedSourceCopist.copySharedSource(project, source, target, indicator)

        // Then
        task sameAs copyTask
        verify(exactly = 1) {
            project.tasks.create("moveTo${target.capitalize(Locale.ROOT)}", Copy::class.java)
        }

        verify(exactly = 1) { copyTask.description = "Extract Target Sources" }
        verify(exactly = 1) { copyTask.group = "Code Generation" }
        verify(exactly = 1) { copyTask.dependsOn("kspTestKotlinSource") }
        verify(exactly = 1) { copyTask.mustRunAfter("kspTestKotlinSource") }
        verify(exactly = 1) { copyTask.from("$buildDir/generated/ksp/$source") }
        verify(exactly = 1) { copyTask.into("$buildDir/generated/ksp/$target") }
        verify(exactly = 1) { copyTask.include("**/*.kt") }
        verify(exactly = 1) { copyTask.exclude(any<Spec<FileTreeElement>>()) }
    }

    @Test
    fun `Given copySharedSource is called it creates a CopyTask, which ignores non files`() {
        // Given
        val indicator = "Common"
        val source = "sourceTest"
        val target = "targetTest"
        val buildDir: String = fixture.fixture()
        val copyTask: Copy = mockk(relaxUnitFun = true)
        val fileTreeElement: FileTreeElement = mockk()

        val filter = slot<Spec<FileTreeElement>>()
        val file: File = mockk()

        every { project.buildDir.absolutePath } returns buildDir
        every { project.tasks.create(any<String>(), Copy::class.java) } returns copyTask


        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns copyTask
        every { copyTask.from(any<String>()) } returns copyTask
        every { copyTask.into(any<String>()) } returns copyTask
        every { copyTask.include(any<String>()) } returns copyTask
        every { copyTask.exclude(capture(filter)) } returns copyTask

        SharedSourceCopist.copySharedSource(project, source, target, indicator)

        every { fileTreeElement.file } returns file
        every { file.isFile } returns false

        // When
        val actual = filter.captured.invoke(fileTreeElement)

        // Then
        actual mustBe false
    }

    @Test
    fun `Given copySharedSource is called it creates a CopyTask, which ignores files, which do not contain the indicator`() {
        // Given
        val indicator = "Common"
        val source = "sourceTest"
        val target = "targetTest"
        val buildDir: String = fixture.fixture()
        val copyTask: Copy = mockk(relaxUnitFun = true)
        val fileTreeElement: FileTreeElement = mockk()

        val filter = slot<Spec<FileTreeElement>>()
        val file = prepareFile("NOT AN INDICATOR")

        every { project.buildDir.absolutePath } returns buildDir
        every { project.tasks.create(any<String>(), Copy::class.java) } returns copyTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns copyTask
        every { copyTask.from(any<String>()) } returns copyTask
        every { copyTask.into(any<String>()) } returns copyTask
        every { copyTask.include(any<String>()) } returns copyTask
        every { copyTask.exclude(capture(filter)) } returns copyTask


        SharedSourceCopist.copySharedSource(project, source, target, indicator)

        every { fileTreeElement.file } returns file

        // When
        val actual = filter.captured.invoke(fileTreeElement)

        // Then
        actual mustBe true
    }

    @Test
    fun `Given copySharedSource is called it creates a CopyTask, which copies files, which contain the indicator`() {
        // Given
        val indicator = "Common"
        val source = "sourceTest"
        val target = "targetTest"
        val buildDir: String = fixture.fixture()
        val copyTask: Copy = mockk(relaxUnitFun = true)
        val fileTreeElement: FileTreeElement = mockk()

        val filter = slot<Spec<FileTreeElement>>()
        val file = prepareFile("// $indicator")

        every { project.buildDir.absolutePath } returns buildDir
        every { project.tasks.create(any<String>(), Copy::class.java) } returns copyTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns copyTask
        every { copyTask.from(any<String>()) } returns copyTask
        every { copyTask.into(any<String>()) } returns copyTask
        every { copyTask.include(any<String>()) } returns copyTask
        every { copyTask.exclude(capture(filter)) } returns copyTask


        SharedSourceCopist.copySharedSource(project, source, target, indicator)

        every { fileTreeElement.file } returns file

        // When
        val actual = filter.captured.invoke(fileTreeElement)

        // Then
        actual mustBe false
    }
}
