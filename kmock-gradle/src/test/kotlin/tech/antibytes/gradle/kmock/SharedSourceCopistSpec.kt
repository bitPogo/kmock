/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.file.FileTreeElement
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopExecutionException
import org.gradle.kotlin.dsl.invoke
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import java.io.File
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
    fun `Given copySharedSource is called it fails if Platform is empty`() {
        val error = assertFailsWith<StopExecutionException> {
            SharedSourceCopist.copySharedSource(project, "", "", "", "")
        }

        error.message mustBe "Cannot copy from invalid Platform Definition!"
    }

    @Test
    fun `Given copySharedSource is called it fails if Source is empty`() {
        val error = assertFailsWith<StopExecutionException> {
            SharedSourceCopist.copySharedSource(project, "test", "", "", "")
        }

        error.message mustBe "Cannot copy from invalid Source Definition!"
    }

    @Test
    fun `Given copySharedSource is called it fails if Target is empty`() {
        val error = assertFailsWith<StopExecutionException> {
            SharedSourceCopist.copySharedSource(project, "test", "test", "", "")
        }

        error.message mustBe "Cannot copy to invalid Target Definition!"
    }

    @Test
    fun `Given copySharedSource is called it fails if Indicator is empty`() {
        val error = assertFailsWith<StopExecutionException> {
            SharedSourceCopist.copySharedSource(project, "test", "test", "test", "")
        }

        error.message mustBe "Cannot copy with invalid Indicator!"
    }

    @Test
    fun `Given copySharedSource is called it creates a CopyTask`() {
        // Given
        val indicator = "Common"
        val sourcePlatform = "source"
        val source = "${sourcePlatform}XTest"
        val targetPlatform = "target"
        val target = "${targetPlatform}Test"
        val buildDir: String = fixture.fixture()
        val copyTask: Copy = mockk()

        every { project.buildDir.absolutePath } returns buildDir
        every { project.tasks.create(any<String>(), Copy::class.java) } returns copyTask

        every { copyTask.description = any() } just Runs
        every { copyTask.group = any() } just Runs
        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns copyTask
        every { copyTask.from(any<String>()) } returns copyTask
        every { copyTask.into(any<String>()) } returns copyTask
        every { copyTask.include(any<String>()) } returns copyTask
        every { copyTask.exclude(any<Spec<FileTreeElement>>()) } returns copyTask
        every { copyTask.rename(any<Transformer<String, String>>()) } returns copyTask

        // When
        val task = SharedSourceCopist.copySharedSource(
            project,
            sourcePlatform,
            source,
            target,
            indicator,
        )

        // Then
        task sameAs copyTask
        verify(exactly = 1) {
            project.tasks.create("moveToTargetTestForSource", Copy::class.java)
        }

        verify(exactly = 1) { copyTask.description = "Extract Target Sources for $sourcePlatform" }
        verify(exactly = 1) { copyTask.group = "Code Generation" }
        verify(exactly = 1) { copyTask.from("$buildDir/generated/ksp/$sourcePlatform/$source") }
        verify(exactly = 1) { copyTask.into("$buildDir/generated/ksp/$targetPlatform/$target") }
        verify(exactly = 1) { copyTask.include("**/*.kt") }
        verify(exactly = 1) { copyTask.exclude(any<Spec<FileTreeElement>>()) }
    }

    @Test
    fun `Given copySharedSource is called it creates a CopyTask, which ignores non files`() {
        // Given
        val indicator = "Common"
        val sourcePlatform = "source"
        val source = "${sourcePlatform}Test"
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
        every { copyTask.rename(any<Transformer<String, String>>()) } returns copyTask

        SharedSourceCopist.copySharedSource(
            project,
            sourcePlatform,
            source,
            target,
            indicator,
        )

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
        val sourcePlatform = "source"
        val source = "${sourcePlatform}Test"
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
        every { copyTask.rename(any<Transformer<String, String>>()) } returns copyTask

        SharedSourceCopist.copySharedSource(
            project,
            sourcePlatform,
            source,
            target,
            indicator,
        )

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
        val sourcePlatform = "source"
        val source = "${sourcePlatform}Test"
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
        every { copyTask.rename(any<Transformer<String, String>>()) } returns copyTask

        SharedSourceCopist.copySharedSource(
            project,
            sourcePlatform,
            source,
            target,
            indicator,
        )

        every { fileTreeElement.file } returns file

        // When
        val actual = filter.captured.invoke(fileTreeElement)

        // Then
        actual mustBe false
    }

    @Test
    fun `Given copySharedSource is called it creates a CopyTask, which ignores files, which are not a MockFactory`() {
        // Given
        val indicator = "Common"
        val sourcePlatform = "source"
        val source = "${sourcePlatform}Test"
        val target = "targetTest"
        val buildDir: String = fixture.fixture()
        val copyTask: Copy = mockk(relaxUnitFun = true)
        val fileTreeElement: FileTreeElement = mockk()

        val rename = slot<Transformer<String, String>>()
        val file = prepareFile("// $indicator")

        every { project.buildDir.absolutePath } returns buildDir
        every { project.tasks.create(any<String>(), Copy::class.java) } returns copyTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns copyTask
        every { copyTask.from(any<String>()) } returns copyTask
        every { copyTask.into(any<String>()) } returns copyTask
        every { copyTask.include(any<String>()) } returns copyTask
        every { copyTask.exclude(any<Spec<FileTreeElement>>()) } returns copyTask
        every { copyTask.rename(capture(rename)) } returns copyTask

        SharedSourceCopist.copySharedSource(
            project,
            sourcePlatform,
            source,
            target,
            indicator,
        )

        every { fileTreeElement.file } returns file

        // When
        val actual = rename.captured.transform("$source.kt")

        // Then
        actual mustBe "$source.kt"
    }

    @Test
    fun `Given copySharedSource is called it creates a CopyTask, which renames files, which are a MockFactory`() {
        // Given
        val indicator = "Common"
        val sourcePlatform = "source"
        val source = "MockFactory${sourcePlatform}TestEntry"
        val target = "targetTest"
        val buildDir: String = fixture.fixture()
        val copyTask: Copy = mockk(relaxUnitFun = true)
        val fileTreeElement: FileTreeElement = mockk()

        val rename = slot<Transformer<String, String>>()
        val file = prepareFile("// $indicator")

        every { project.buildDir.absolutePath } returns buildDir
        every { project.tasks.create(any<String>(), Copy::class.java) } returns copyTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns copyTask
        every { copyTask.from(any<String>()) } returns copyTask
        every { copyTask.into(any<String>()) } returns copyTask
        every { copyTask.include(any<String>()) } returns copyTask
        every { copyTask.exclude(any<Spec<FileTreeElement>>()) } returns copyTask
        every { copyTask.rename(capture(rename)) } returns copyTask

        SharedSourceCopist.copySharedSource(
            project,
            sourcePlatform,
            source,
            target,
            indicator,
        )

        every { fileTreeElement.file } returns file

        // When
        val actual = rename.captured.transform("$source.kt")

        // Then
        actual mustBe "MockFactory.kt"
    }
}
