/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import java.io.File
import tech.antibytes.gradle.kmock.KMockPluginContract.CleanUpTask
import kotlin.test.assertFailsWith

class CleanUpTaskSpec {
    @TempDir
    private lateinit var buildDir: File
    private lateinit var file: File
    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
        project.buildDir = buildDir
    }

    private fun createStubs(
        files: Pair<String, String>,
        indicator: String? = null
    ): File {
        val generated = File(buildDir, "generated")
        generated.mkdir()

        val ksp = File(generated, "ksp")
        ksp.mkdir()

        val target = File(ksp, "target")
        target.mkdir()

        val sub = File(target, "sub")
        sub.mkdir()

        val file1 = File(target, files.first)
        file1.createNewFile()

        val file2 = File(sub, files.second)
        file2.createNewFile()

        if (indicator != null) {
            file1.writeText("// $indicator")
            file2.writeText("// $indicator")
        }

        return target
    }

    @Test
    fun `It fulfils DefaultPlugin`() {
        val task = project.tasks.create("sut", KMockCleanTask::class.java) {}
        task fulfils DefaultTask::class
    }

    @Test
    fun `It fulfils CleanUpTask`() {
        val task = project.tasks.create("sut", KMockCleanTask::class.java) {}
        task fulfils CleanUpTask::class
    }

    @Test
    fun `Given the CleanUpTask is executed it fails if the indicator is missing`() {
        // Given
        val task: CleanUpTask = project.tasks.create("sut", KMockCleanTask::class.java) {}

        // Then
        val error = assertFailsWith<StopExecutionException> {
            // When
            task.cleanUp()
        }

        error.message mustBe "Missing CleanUp Indicator!"
    }

    @Test
    fun `Given the CleanUpTask is executed it fails if the indicator is empty`() {
        // Given
        val task: CleanUpTask = project.tasks.create("sut", KMockCleanTask::class.java) {}

        // Then
        task.indicator.set("")
        val error = assertFailsWith<StopExecutionException> {
            // When
            task.cleanUp()
        }

        error.message mustBe "Missing CleanUp Indicator!"
    }

    @Test
    fun `Given the CleanUpTask is executed it fails if the target is missing`() {
        // Given
        val task: CleanUpTask = project.tasks.create("sut", KMockCleanTask::class.java) {}

        // Then
        task.indicator.set("COMMON SOURCE")
        val error = assertFailsWith<StopExecutionException> {
            // When
            task.cleanUp()
        }

        error.message mustBe "Missing CleanUp Target!"
    }

    @Test
    fun `Given the CleanUpTask is executed it fails if the target is empty`() {
        // Given
        val task: CleanUpTask = project.tasks.create("sut", KMockCleanTask::class.java) {}

        // Then
        task.indicator.set("COMMON SOURCE")
        task.target.set("")

        val error = assertFailsWith<StopExecutionException> {
            // When
            task.cleanUp()
        }

        error.message mustBe "Missing CleanUp Target!"
    }

    @Test
    fun `Given the CleanUpTask is executed it ignores Stubs which contain no indicator`() {
        // Given
        val task: CleanUpTask = project.tasks.create("sut", KMockCleanTask::class.java) {}
        val stubs = Pair("SomeStub.kt", "SomeOtherStub.kt")
        val target = createStubs(stubs)

        // When
        task.indicator.set("Common")
        task.target.set("target")
        task.cleanUp()

        // Then
        target.walkBottomUp().forEach { file ->
            if (file.name != "target" && file.name != "sub") {
                val name = file.name.substringAfterLast("/")
                (name == stubs.first || name == stubs.second) mustBe true
            }
        }
    }

    @Test
    fun `Given the CleanUpTask is executed it deletes Stubs which contain a indicator`() {
        // Given
        val task: CleanUpTask = project.tasks.create("sut", KMockCleanTask::class.java) {}
        val stubs = Pair("SomeStub.kt", "SomeOtherStub.kt")
        val indicator = "Common"
        val target = createStubs(stubs, indicator)
        var isClean = true

        // When
        task.indicator.set(indicator)
        task.target.set("target")
        task.cleanUp()

        // Then
        target.walkBottomUp().forEach { file ->
            if (file.name != "target" && file.name != "sub") {
                val name = file.name.substringAfterLast("/")
                isClean = !(name == stubs.first || name == stubs.second)
            }
        }

        isClean mustBe true
    }
}
