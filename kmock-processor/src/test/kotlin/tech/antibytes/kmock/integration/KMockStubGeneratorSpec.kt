/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.kmock.processor.KMockProcessorProvider
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import java.io.File

class KMockStubGeneratorSpec {
    @TempDir
    lateinit var buildDir: File
    private val provider = KMockProcessorProvider()
    private val fixtureRoot = "/generatorTest"

    private fun loadResource(path: String): String {
        return KMockStubGeneratorSpec::class.java.getResource(fixtureRoot + path).readText()
    }

    private fun compile(
        processorProvider: SymbolProcessorProvider,
        source: SourceFile
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = listOf(source)
            symbolProcessorProviders = listOf(processorProvider)
            workingDir = buildDir
            inheritClassPath = true
            verbose = false
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

    private fun String.normalizeSource(): String {
        return this.replace(Regex("[\t ]+"), "")
    }

    @Test
    fun `Given a annotated PlatformSource is processed, it writes a stub for abstract Properties`() {
        // Given
        val source = SourceFile.kotlin(
            "PropertyPlatformSource.kt",
            loadResource("/PropertyPlatformSource.kt")
        )
        val expected = loadResource("/PropertyPlatformExpected.kt")

        // When
        val compilerResult = compile(provider, source)
        val actual = resolveGenerated("PropertyPlatformStub.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated CommonSource is processed, it writes a stub for abstract Properties`() {
        // Given
        val source = SourceFile.kotlin(
            "PropertyCommonSource.kt",
            loadResource("/PropertyCommonSource.kt")
        )
        val expected = loadResource("/PropertyCommonExpected.kt")

        // When
        val compilerResult = compile(provider, source)
        val actual = resolveGenerated("PropertyCommonStub.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated PlatformSource is processed, it writes a stub for abstract Functions`() {
        // Given
        val source = SourceFile.kotlin(
            "SyncFunctionPlatformSource.kt",
            loadResource("/SyncFunctionPlatformSource.kt")
        )
        val expected = loadResource("/SyncFunctionPlatformExpected.kt")

        // When
        val compilerResult = compile(provider, source)
        val actual = resolveGenerated("SyncFunctionPlatformStub.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated CommonSource is processed, it writes a stub for abstract Functions`() {
        // Given
        val source = SourceFile.kotlin(
            "SyncFunctionCommonSource.kt",
            loadResource("/SyncFunctionCommonSource.kt")
        )
        val expected = loadResource("/SyncFunctionCommonExpected.kt")

        // When
        val compilerResult = compile(provider, source)
        val actual = resolveGenerated("SyncFunctionCommonStub.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated PlatformSource is processed, it writes a stub for abstract Suspending Functions`() {
        // Given
        val source = SourceFile.kotlin(
            "AsyncFunctionPlatformSource.kt",
            loadResource("/AsyncFunctionPlatformSource.kt")
        )
        val expected = loadResource("/AsyncFunctionPlatformExpected.kt")

        // When
        val compilerResult = compile(provider, source)
        val actual = resolveGenerated("AsyncFunctionPlatformStub.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source is processed, it writes a stub for abstract Functions while respecting overload`() {
        // Given
        val source = SourceFile.kotlin(
            "FunctionOverloadSource.kt",
            loadResource("/FunctionOverloadSource.kt")
        )
        val expected = loadResource("/FunctionOverloadExpected.kt")

        // When
        val compilerResult = compile(provider, source)
        val actual = resolveGenerated("FunctionOverloadStub.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source is processed, it writes a stub for abstract Functions while respecting generic types`() {
        // Given
        val source = SourceFile.kotlin(
            "GenericsSource.kt",
            loadResource("/GenericsSource.kt")
        )
        val expected = loadResource("/GenericsExpected.kt")

        // When
        val compilerResult = compile(provider, source)
        val actual = resolveGenerated("GenericsStub.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source is processed, it writes a stub while using the Relaxer`() {
        // Given
        val source = SourceFile.kotlin(
            "RelaxedSource.kt",
            loadResource("/RelaxedSource.kt")
        )
        val expected = loadResource("/RelaxedExpected.kt")

        // When
        val compilerResult = compile(provider, source)
        val actual = resolveGenerated("RelaxedStub.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source is processed, it fails if function parameters are ill named`() {
        // Given
        val source = SourceFile.kotlin(
            "IllNamedSource.kt",
            loadResource("/IllNamedSource.kt")
        )

        // When
        val compilerResult = compile(provider, source)

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.COMPILATION_ERROR
    }
}
