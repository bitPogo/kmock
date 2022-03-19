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

class KMockMocksSpec {
    @TempDir
    lateinit var buildDir: File
    private val provider = KMockProcessorProvider()
    private val root = "/mock"

    private fun loadResource(path: String): String {
        return KMockMocksSpec::class.java.getResource(root + path).readText()
    }

    private fun compile(
        processorProvider: SymbolProcessorProvider,
        source: SourceFile,
        isKmp: Boolean,
        kspArguments: Map<String, String> = emptyMap(),
    ): KotlinCompilation.Result {
        val args = mutableMapOf(
            "rootPackage" to "generatorTest",
            "isKmp" to isKmp.toString()
        ).also {
            it.putAll(kspArguments)
        }

        return KotlinCompilation().apply {
            sources = listOf(source)
            symbolProcessorProviders = listOf(processorProvider)
            workingDir = buildDir
            inheritClassPath = true
            verbose = false
            kspArgs = args
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
    fun `Given a annotated Source for Properties for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/property/Platform.kt")
        )
        val expected = loadResource("/expected/property/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Properties for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/property/Shared.kt")
        )
        val expected = loadResource("/expected/property/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Properties for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/property/Common.kt")
        )
        val expected = loadResource("/expected/property/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Sync for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/sync/Platform.kt")
        )
        val expected = loadResource("/expected/sync/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Sync for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/sync/Shared.kt")
        )
        val expected = loadResource("/expected/sync/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Sync for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/sync/Common.kt")
        )
        val expected = loadResource("/expected/sync/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Async for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/async/Platform.kt")
        )
        val expected = loadResource("/expected/async/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Async for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/async/Shared.kt")
        )
        val expected = loadResource("/expected/async/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Async for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/async/Common.kt")
        )
        val expected = loadResource("/expected/async/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/generic/Platform.kt")
        )
        val expected = loadResource("/expected/generic/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/generic/Shared.kt")
        )
        val expected = loadResource("/expected/generic/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/generic/Common.kt")
        )
        val expected = loadResource("/expected/generic/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Relaxation for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/relaxed/Platform.kt")
        )
        val expected = loadResource("/expected/relaxed/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Relaxation for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/relaxed/Shared.kt")
        )
        val expected = loadResource("/expected/relaxed/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Relaxation for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/relaxed/Common.kt")
        )
        val expected = loadResource("/expected/relaxed/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Overload for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/overloaded/Platform.kt")
        )
        val expected = loadResource("/expected/overloaded/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Overload for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/overloaded/Shared.kt")
        )
        val expected = loadResource("/expected/overloaded/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Overload for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/overloaded/Common.kt")
        )
        val expected = loadResource("/expected/overloaded/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Alias names for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/alias/Platform.kt")
        )
        val expected = loadResource("/expected/alias/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "alias_mock.template.alias.Platform" to "AliasPlatform",
            )
        )
        val actual = resolveGenerated("AliasPlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Alias names for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/alias/Shared.kt")
        )
        val expected = loadResource("/expected/alias/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "alias_mock.template.alias.Shared" to "AliasShared",
            )
        )
        val actual = resolveGenerated("AliasSharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Alias names for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/alias/Common.kt")
        )
        val expected = loadResource("/expected/alias/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "alias_mock.template.alias.Common" to "AliasCommon",
            )
        )
        val actual = resolveGenerated("AliasCommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics which allows Recursive Types is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "AllowedRecursive.kt",
            loadResource("/template/generic/AllowedRecursive.kt")
        )
        val expected = loadResource("/expected/generic/AllowedRecursive.kt")

        val allowedRecursiveTypes = mapOf(
            "recursive_0" to "kotlin.Comparable"
        )

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = allowedRecursiveTypes
        )
        val actual = resolveGenerated("AllowedRecursiveMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with BuildIns for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/buildIn/Platform.kt")
        )
        val expected = loadResource("/expected/buildIn/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "buildIn_0" to "mock.template.buildIn.Platform",
            )
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which contains a colliding name with BuildIns for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Collision.kt",
            loadResource("/template/buildIn/Collision.kt")
        )
        val expected = loadResource("/expected/buildIn/Collision.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "buildIn_0" to "mock.template.buildIn.Collision",
            )
        )
        val actual = resolveGenerated("CollisionMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with BuildIns for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/buildIn/Shared.kt")
        )
        val expected = loadResource("/expected/buildIn/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "buildIn_0" to "mock.template.buildIn.Shared",
            )
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with BuildIns for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/buildIn/Common.kt")
        )
        val expected = loadResource("/expected/buildIn/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "buildIn_0" to "mock.template.buildIn.Common",
            )
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }
}
