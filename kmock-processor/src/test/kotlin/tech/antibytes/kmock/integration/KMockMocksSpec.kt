/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.symbolProcessorProviders
import java.io.File
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.kmock.processor.KMockProcessorProvider
import tech.antibytes.kmock.processor.ProcessorContract.Companion.DEPENDENCIES
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_PREFIX
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMP_FLAG
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSP_DIR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ROOT_PACKAGE
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe

@OptIn(ExperimentalCompilerApi::class)
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
        filePath: List<String> = emptyList(),
        kspArguments: Map<String, String> = emptyMap(),
    ): JvmCompilationResult {
        val args = mutableMapOf(
            KSP_DIR to "${buildDir.absolutePath.trimEnd('/')}/ksp/sources/kotlin",
            ROOT_PACKAGE to "generatorTest",
            KMP_FLAG to isKmp.toString(),
        ).also {
            it.putAll(kspArguments)
        }.also {
            it["${KMOCK_PREFIX}oldNamePrefix_0"] = "kotlin.collections"
            it["${KMOCK_PREFIX}oldNamePrefix_1"] = "kotlin"
            it["${DEPENDENCIES}sharedTest#0"] = "otherTest"
            it["${DEPENDENCIES}sharedTest#1"] = "commonTest"
            it["${DEPENDENCIES}otherTest#0"] = "commonTest"
        }

        if (filePath.isNotEmpty()) {
            var dir: File = buildDir
            filePath.forEach { part ->
                dir = File(dir, part)
                dir.mkdir()
            }
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

    private fun resolveGenerated(expected: String): File? {
        return findGeneratedSource { file -> file.absolutePath.endsWith(expected) }
            .getOrNull(0)
    }

    private fun String.normalizeSource(): String = this
        .replace(Regex("[ \t\r\n]+\n"), "\n")
        .replace(Regex("\n[ \t\r]+"), "\n")
        .replace(Regex("[\t\r]"), " ")
        .replace(Regex("[ ]+"), " ")
        .trim()

    @Test
    fun `Given a annotated Source for Properties for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/property/Platform.kt"),
        )
        val expected = loadResource("/expected/property/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Properties for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/property/Shared.kt"),
        )
        val expected = loadResource("/expected/property/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/property/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Properties for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/property/Common.kt"),
        )
        val expected = loadResource("/expected/property/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/property/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Sync for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/sync/Platform.kt"),
        )
        val expected = loadResource("/expected/sync/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Sync for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/sync/Shared.kt"),
        )
        val expected = loadResource("/expected/sync/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/sync/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Sync for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/sync/Common.kt"),
        )
        val expected = loadResource("/expected/sync/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/sync/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Async for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/async/Platform.kt"),
        )
        val expected = loadResource("/expected/async/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Async for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/async/Shared.kt"),
        )
        val expected = loadResource("/expected/async/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/async/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Async for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/async/Common.kt"),
        )
        val expected = loadResource("/expected/async/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/async/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/generic/Platform.kt"),
        )
        val expected = loadResource("/expected/generic/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics for a Platform with a supertype is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "SuperTyped.kt",
            loadResource("/template/generic/SuperTyped.kt"),
        )
        val expected = loadResource("/expected/generic/SuperTyped.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("SuperTypedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/generic/Shared.kt"),
        )
        val expected = loadResource("/expected/generic/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/generic/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Generics for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/generic/Common.kt"),
        )
        val expected = loadResource("/expected/generic/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/generic/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Relaxation for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/relaxed/Platform.kt"),
        )
        val expected = loadResource("/expected/relaxed/Platform.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = false)
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Relaxation for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/relaxed/Shared.kt"),
        )
        val expected = loadResource("/expected/relaxed/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/relaxed/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Relaxation for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/relaxed/Common.kt"),
        )
        val expected = loadResource("/expected/relaxed/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/relaxed/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/overloaded/Platform.kt"),
        )
        val expected = loadResource("/expected/overloaded/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for a Platform is processed, which contains collisions, it uses the user induces mapping, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Collision.kt",
            loadResource("/template/overloaded/Collision.kt"),
        )
        val expected = loadResource("/expected/overloaded/Collision.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_namePrefix_mock.template.overloaded.Scope.Abc" to "Scoped",
            ),
        )
        val actual = resolveGenerated("CollisionMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source with extended Names for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Refined.kt",
            loadResource("/template/overloaded/Refined.kt"),
        )
        val expected = loadResource("/expected/overloaded/Refined.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_enableFineGrainedProxyNames" to "true",
            ),
        )
        val actual = resolveGenerated("RefinedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/overloaded/Shared.kt"),
        )
        val expected = loadResource("/expected/overloaded/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/overloaded/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/overloaded/Common.kt"),
        )
        val expected = loadResource("/expected/overloaded/Common.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/overloaded/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Alias names for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/alias/Platform.kt"),
        )
        val expected = loadResource("/expected/alias/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_alias_mock.template.alias.Platform" to "AliasPlatform",
            ),
        )
        val actual = resolveGenerated("AliasPlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Alias names for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/alias/Shared.kt"),
        )
        val expected = loadResource("/expected/alias/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}alias_mock.template.alias.Shared" to "AliasShared",
            ),
        )
        val actual = resolveGenerated("AliasSharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/alias/AliasSharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Alias names for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/alias/Common.kt"),
        )
        val expected = loadResource("/expected/alias/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}alias_mock.template.alias.Common" to "AliasCommon",
            ),
        )
        val actual = resolveGenerated("AliasCommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/alias/AliasCommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with BuildIns for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/buildIn/Platform.kt"),
        )
        val expected = loadResource("/expected/buildIn/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.buildIn.Platform",
            ),
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with BuildIns like methods for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "NoBuildIns.kt",
            loadResource("/template/buildIn/NoBuildIns.kt"),
        )
        val expected = loadResource("/expected/buildIn/NoBuildIns.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("NoBuildInsMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which contains a colliding name with BuildIns for a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Collision.kt",
            loadResource("/template/buildIn/Collision.kt"),
        )
        val expected = loadResource("/expected/buildIn/Collision.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.buildIn.Collision",
            ),
        )
        val actual = resolveGenerated("CollisionMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with BuildIns for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/buildIn/Shared.kt"),
        )
        val expected = loadResource("/expected/buildIn/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.buildIn.Shared",
            ),
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/buildIn/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with BuildIns for Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/buildIn/Common.kt"),
        )
        val expected = loadResource("/expected/buildIn/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.buildIn.Common",
            ),
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/buildIn/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies on a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/spy/Platform.kt"),
        )
        val expected = loadResource("/expected/spy/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Platform",
            ),
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies under an Alias on a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/spy/Platform.kt"),
        )
        val expected = loadResource("/expected/spy/Alias.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.AliasPlatform",
                "${KMOCK_PREFIX}alias_mock.template.spy.Platform" to "AliasPlatform",
            ),
        )
        val actual = resolveGenerated("AliasPlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies relaxed on a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Collision.kt",
            loadResource("/template/spy/Relaxed.kt"),
        )
        val expected = loadResource("/expected/spy/Relaxed.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Relaxed",
            ),
        )
        val actual = resolveGenerated("RelaxedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies on Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/spy/Shared.kt"),
        )
        val expected = loadResource("/expected/spy/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Shared",
            ),
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/spy/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies on Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/spy/Common.kt"),
        )
        val expected = loadResource("/expected/spy/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Common",
            ),
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/spy/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which contains TypeAliases is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/typealiaz/Platform.kt"),
        )
        val expected = loadResource("/expected/typealiaz/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which contains TypeAliases and is relaxed is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Relaxed.kt",
            loadResource("/template/typealiaz/Relaxed.kt"),
        )
        val expected = loadResource("/expected/typealiaz/Relaxed.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("RelaxedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which contains TypeAliases and Receiver is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Receiver.kt",
            loadResource("/template/typealiaz/Receiver.kt"),
        )
        val expected = loadResource("/expected/typealiaz/Receiver.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("ReceiverMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which contains TypeAliases and is inherited is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "SuperType.kt",
            loadResource("/template/typealiaz/SuperType.kt"),
        )
        val expected = loadResource("/expected/typealiaz/SuperType.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("InheritedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform and has AccessMethods is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "AccessMethods.kt",
            loadResource("/template/typealiaz/AccessMethods.kt"),
        )
        val expected = loadResource("/expected/typealiaz/Access.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_alternativeProxyAccess" to "true",
            ),
        )
        val actual = resolveGenerated("AccessMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform and has AccessMethods is processed and prevents Aliases from resolving, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "PreventResolving.kt",
            loadResource("/template/typealiaz/PreventResolving.kt"),
        )
        val expected = loadResource("/expected/typealiaz/PreventResolving.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_alternativeProxyAccess" to "true",
                "kmock_preventAliasResolving_0" to "mock.template.typealiaz.Alias921",
                "kmock_preventAliasResolving_1" to "mock.template.typealiaz.Alias923",
                "kmock_preventAliasResolving_2" to "mock.template.typealiaz.Alias977",
                "kmock_preventAliasResolving_3" to "mock.template.typealiaz.Alias973",
                "kmock_preventAliasResolving_4" to "mock.template.typealiaz.Alias955",
                "kmock_preventAliasResolving_5" to "mock.template.typealiaz.Alias999",
                "kmock_preventAliasResolving_6" to "mock.template.typealiaz.Alias1000",
            ),
        )
        val actual = resolveGenerated("PreventResolvingMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared which contains TypeAliases is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/typealiaz/Shared.kt"),
        )
        val expected = loadResource("/expected/typealiaz/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/typealiaz/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common which contains TypeAliases is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/typealiaz/Common.kt"),
        )
        val expected = loadResource("/expected/typealiaz/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/typealiaz/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which contains Receiver for properties is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/propertyreceiver/Platform.kt"),
        )
        val expected = loadResource("/expected/propertyreceiver/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform while relaxed which contains Receiver for properties is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Relaxed.kt",
            loadResource("/template/propertyreceiver/Relaxed.kt"),
        )
        val expected = loadResource("/expected/propertyreceiver/Relaxed.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("RelaxedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform while inherited which contains Receiver for properties is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Inherited.kt",
            loadResource("/template/propertyreceiver/SuperType.kt"),
        )
        val expected = loadResource("/expected/propertyreceiver/SuperType.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("InheritedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform while spied which contains Receiver for properties is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Spied.kt",
            loadResource("/template/propertyreceiver/Spied.kt"),
        )
        val expected = loadResource("/expected/propertyreceiver/Spied.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.propertyreceiver.Spied",
            ),
        )
        val actual = resolveGenerated("SpiedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common which contains Receiver for properties is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/propertyreceiver/Common.kt"),
        )
        val expected = loadResource("/expected/propertyreceiver/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/propertyreceiver/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which contains Receiver for methods is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/methodreceiver/Platform.kt"),
        )
        val expected = loadResource("/expected/methodreceiver/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform while relaxed which contains Receiver for methods is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Relaxed.kt",
            loadResource("/template/methodreceiver/Relaxed.kt"),
        )
        val expected = loadResource("/expected/methodreceiver/Relaxed.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("RelaxedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform while inherited which contains Receiver for methods is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Inherited.kt",
            loadResource("/template/methodreceiver/SuperType.kt"),
        )
        val expected = loadResource("/expected/methodreceiver/SuperType.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
        )
        val actual = resolveGenerated("InheritedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform while spied which contains Receiver for methods is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Spied.kt",
            loadResource("/template/methodreceiver/Spied.kt"),
        )
        val expected = loadResource("/expected/methodreceiver/Spied.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.methodreceiver.Spied",
            ),
        )
        val actual = resolveGenerated("SpiedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common which contains Receiver for methods is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/methodreceiver/Common.kt"),
        )
        val expected = loadResource("/expected/methodreceiver/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/methodreceiver/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform is processed, it allows renaming its method names, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/renamed/Platform.kt"),
        )
        val expected = loadResource("/expected/renamed/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.renamed.Platform",
                "kmock_customMethodName_mock.template.renamed.PlatformMock#_buzz" to "customName",
                "kmock_customMethodName_mock.template.renamed.PlatformMock#_hashCode" to "noHash",
            ),
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared is processed, it allows renaming its method names, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/renamed/Shared.kt"),
        )
        val expected = loadResource("/expected/renamed/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.renamed.Shared",
                "kmock_customMethodName_mock.template.renamed.SharedMock#_buzz" to "customName",
                "kmock_customMethodName_mock.template.renamed.SharedMock#_hashCode" to "noHash",
            ),
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/renamed/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common is processed, it allows renaming its method names, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/renamed/Common.kt"),
        )
        val expected = loadResource("/expected/renamed/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.renamed.Common",
                "kmock_customMethodName_mock.template.renamed.CommonMock#_buzz" to "customName",
                "kmock_customMethodName_mock.template.renamed.CommonMock#_hashCode" to "noHash",
            ),
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/renamed/CommonMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared is processed while using a custom annotation, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/customshared/Shared.kt"),
        )
        val expected = loadResource("/expected/customshared/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "kmock_customAnnotation_mock.template.customshared.CustomShared" to "sharedTest",
            ),
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/customshared/SharedMock.kt",
        ) mustBe true
        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a Source with multiple Annotations is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Sample.kt",
            loadResource("/template/mixedannotation/Sample.kt"),
        )
        val expectedPlatform = loadResource("/expected/mixedannotation/Platform.kt")
        val expectedShared = loadResource("/expected/mixedannotation/Shared.kt")
        val expectedCommon = loadResource("/expected/mixedannotation/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
        )
        val actualPlatform = resolveGenerated("PlatformMock.kt")
        val actualShared = resolveGenerated("SharedMock.kt")
        val actualCommon = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK

        actualShared?.absolutePath?.toString()?.endsWith(
            "shared/sharedTest/kotlin/mock/template/mixedannotation/SharedMock.kt",
        ) mustBe true
        actualCommon?.absolutePath?.toString()?.endsWith(
            "common/commonTest/kotlin/mock/template/mixedannotation/CommonMock.kt",
        ) mustBe true

        actualPlatform!!.readText().normalizeSource() mustBe expectedPlatform.normalizeSource()
        actualShared?.readText()?.normalizeSource() mustBe expectedShared.normalizeSource()
        actualCommon?.readText()?.normalizeSource() mustBe expectedCommon.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Properties for a Platform is processed while alternative Access is enabled, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Property.kt",
            loadResource("/template/access/Property.kt"),
        )
        val expected = loadResource("/expected/access/Property.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_alternativeProxyAccess" to "true",
            ),
        )
        val actual = resolveGenerated("PropertyMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for SyncFunctions for a Platform is processed while alternative Access is enabled, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "SyncFun.kt",
            loadResource("/template/access/SyncFun.kt"),
        )
        val expected = loadResource("/expected/access/SyncFun.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_alternativeProxyAccess" to "true",
            ),
        )
        val actual = resolveGenerated("SyncFunMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for AsyncFunctions for a Platform is processed while alternative Access is enabled, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "AsyncFun.kt",
            loadResource("/template/access/AsyncFun.kt"),
        )
        val expected = loadResource("/expected/access/AsyncFun.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_alternativeProxyAccess" to "true",
            ),
        )
        val actual = resolveGenerated("AsyncFunMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with overload for a Platform is processed while alternative Access is enabled, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Overloaded.kt",
            loadResource("/template/access/Overloaded.kt"),
        )
        val expected = loadResource("/expected/access/Overloaded.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_alternativeProxyAccess" to "true",
            ),
        )
        val actual = resolveGenerated("OverloadedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with BuildIns for a Platform is processed while alternative Access is enabled, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "BuildIn.kt",
            loadResource("/template/access/BuildIn.kt"),
        )
        val expected = loadResource("/expected/access/BuildIn.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_alternativeProxyAccess" to "true",
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.access.BuildIn",
            ),
        )
        val actual = resolveGenerated("BuildInMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which is with KMock annotated, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "src/jvmTest/Platform.kt",
            loadResource("/template/kmock/Platform.kt/"),
        )
        val expected = loadResource("/expected/kmock/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            filePath = listOf("sources", "src", "jvmTest"),
            isKmp = false,
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.endsWith("ksp/sources/kotlin/mock/template/kmock/PlatformMock.kt") mustBe true

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared which is with KMock annotated, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "src/sharedTest/Shared.kt",
            loadResource("/template/kmock/Shared.kt/"),
        )
        val expected = loadResource("/expected/kmock/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            filePath = listOf("sources", "src", "sharedTest"),
            isKmp = true,
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.endsWith(
            "ksp/sources/kotlin/shared/sharedTest/kotlin/mock/template/kmock/SharedMock.kt",
        ) mustBe true

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common which is with KMock annotated, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "src/commonTest/Common.kt",
            loadResource("/template/kmock/Common.kt/"),
        )
        val expected = loadResource("/expected/kmock/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            filePath = listOf("sources", "src", "commonTest"),
            isKmp = true,
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual?.absolutePath?.endsWith(
            "ksp/sources/kotlin/common/commonTest/kotlin/mock/template/kmock/CommonMock.kt",
        ) mustBe true

        actual?.readText()?.normalizeSource() mustBe expected.normalizeSource()
    }
}
