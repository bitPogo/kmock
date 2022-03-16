/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.kmock.processor.KMockProcessorProvider
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import java.io.File

class KMockFactoriesSpec {
    @TempDir
    lateinit var buildDir: File
    private val provider = KMockProcessorProvider()
    private val root = "/factory"

    private fun loadResource(path: String): String {
        return KMockMocksSpec::class.java.getResource(root + path).readText()
    }

    private fun compile(
        processorProvider: SymbolProcessorProvider,
        source: SourceFile,
        isKmp: Boolean = false,
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = listOf(source)
            symbolProcessorProviders = listOf(processorProvider)
            workingDir = buildDir
            inheritClassPath = true
            verbose = false
            kspArgs = mutableMapOf(
                "rootPackage" to "generatorTest",
                "isKmp" to isKmp.toString()
            )
        }.compile()
    }

    private fun findGeneratedSource(filter: (File) -> Boolean): List<File> {
        return buildDir.walkBottomUp()
            .toList()
            .filter(filter)
    }

    private fun resolveGenerated(expected: String): String? {
        return findGeneratedSource { file -> file.absolutePath.endsWith(expected) }
            .getOrNull(0)?.readText()
    }

    private fun String.normalizeSource(): String = this.replace(Regex("[\t ]+"), "")

    @Test
    fun `Given a annotated Source for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/regular/Platform.kt")
        )
        val expected = loadResource("/expected/regular/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("MockFactory.kt")

        println(actual)

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/regular/Common.kt")
        )
        val expectedActual = loadResource("/expected/regular/CommonActual.kt")
        val expectedExpect = loadResource("/expected/regular/CommonExpect.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actualActual = resolveGenerated("MockFactory.kt")
        val actualExpect = resolveGenerated("MockFactoryCommonEntry.kt")

        println(actualActual)
        println(actualExpect)

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Relaxation for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/relaxed/Platform.kt")
        )
        val expected = loadResource("/expected/relaxed/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("MockFactory.kt")

        println(actual)

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Relaxation for Common is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/relaxed/Common.kt")
        )
        val expectedActual = loadResource("/expected/relaxed/CommonActual.kt")
        val expectedExpect = loadResource("/expected/relaxed/CommonExpect.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actualActual = resolveGenerated("MockFactory.kt")
        val actualExpect = resolveGenerated("MockFactoryCommonEntry.kt")

        println(actualActual)
        println(actualExpect)

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/generic/Platform.kt")
        )
        val expected = loadResource("/expected/generic/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("MockFactory.kt")

        println(actual)

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Generics for Common is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/generic/Common.kt")
        )
        val expectedActual = loadResource("/expected/generic/CommonActual.kt")
        val expectedExpect = loadResource("/expected/generic/CommonExpect.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actualActual = resolveGenerated("MockFactory.kt")
        val actualExpect = resolveGenerated("MockFactoryCommonEntry.kt")

        println(actualActual)
        println(actualExpect)

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.normalizeSource() mustBe expectedExpect.normalizeSource()
    }
}
