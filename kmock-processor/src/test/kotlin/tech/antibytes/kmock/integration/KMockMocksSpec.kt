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
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_PREFIX
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMP_FLAG
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSP_DIR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PRECEDENCE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ROOT_PACKAGE
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
            KSP_DIR to "${buildDir.absolutePath.trimEnd('/')}/ksp/sources/kotlin",
            ROOT_PACKAGE to "generatorTest",
            KMP_FLAG to isKmp.toString(),
        ).also {
            it.putAll(kspArguments)
        }.also {
            it["${KMOCK_PREFIX}oldNamePrefix_0"] = "kotlin.collections"
            it["${KMOCK_PREFIX}oldNamePrefix_1"] = "kotlin"
            it["${PRECEDENCE}sharedTest"] = "0"
            it["${PRECEDENCE}otherTest"] = "1"
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
        .replace(Regex("[\t\r]"), " ")
        .replace(Regex("[ ]+"), " ")
        .trim()

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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Properties for Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/property/Shared.kt")
        )
        val expected = loadResource("/expected/property/Shared.kt")

        // When
        val compilerResult = compile(provider, source, isKmp = true)
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/property/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/property/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/sync/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/sync/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/async/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/async/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/generic/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/generic/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/relaxed/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/relaxed/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for a Platform is processed, it writes a mock`() {
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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for a Platform is processed, which contains collisions, it uses the user induces mapping, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Collision.kt",
            loadResource("/template/overloaded/Collision.kt")
        )
        val expected = loadResource("/expected/overloaded/Collision.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_namePrefix_mock.template.overloaded.Scope.Abc" to "Scoped"
            )
        )
        val actual = resolveGenerated("CollisionMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for Shared is processed, it writes a mock`() {
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

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/overloaded/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for Common is processed, it writes a mock`() {
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

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/overloaded/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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
                "${KMOCK_PREFIX}alias_mock.template.alias.Platform" to "AliasPlatform",
            )
        )
        val actual = resolveGenerated("AliasPlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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
                "${KMOCK_PREFIX}alias_mock.template.alias.Shared" to "AliasShared",
            )
        )
        val actual = resolveGenerated("AliasSharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/alias/AliasSharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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
                "${KMOCK_PREFIX}alias_mock.template.alias.Common" to "AliasCommon",
            )
        )
        val actual = resolveGenerated("AliasCommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/alias/AliasCommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.buildIn.Platform",
            )
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.buildIn.Collision",
            )
        )
        val actual = resolveGenerated("CollisionMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.buildIn.Shared",
            )
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/buildIn/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
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
                "${KMOCK_PREFIX}buildIn_0" to "mock.template.buildIn.Common",
            )
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/buildIn/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies on a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/spy/Platform.kt")
        )
        val expected = loadResource("/expected/spy/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Platform",
            )
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies under an Alias on a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/spy/Platform.kt")
        )
        val expected = loadResource("/expected/spy/Alias.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Platform",
                "${KMOCK_PREFIX}alias_mock.template.spy.Platform" to "AliasPlatform",
            )
        )
        val actual = resolveGenerated("AliasPlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies relaxed on a Platform is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Collision.kt",
            loadResource("/template/spy/Relaxed.kt")
        )
        val expected = loadResource("/expected/spy/Relaxed.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Relaxed",
            )
        )
        val actual = resolveGenerated("RelaxedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies on Shared is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/spy/Shared.kt")
        )
        val expected = loadResource("/expected/spy/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Shared",
            )
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/spy/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source which spies on Common is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/spy/Common.kt")
        )
        val expected = loadResource("/expected/spy/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "mock.template.spy.Common",
            )
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/spy/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which contains TypeAliases is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/typealiaz/Platform.kt")
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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared which contains TypeAliases is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/typealiaz/Shared.kt")
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

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/typealiaz/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common which contains TypeAliases is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/typealiaz/Common.kt")
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

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/typealiaz/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform which contains scoped Extensions is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/scoped/Platform.kt")
        )
        val expected = loadResource("/expected/scoped/Platform.kt")

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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared which contains scoped Extensions is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/scoped/Shared.kt")
        )
        val expected = loadResource("/expected/scoped/Shared.kt")

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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common which contains scoped Extensions is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/scoped/Common.kt")
        )
        val expected = loadResource("/expected/scoped/Common.kt")

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

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform is processed, it allows renaming its method names, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/renamed/Platform.kt")
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
                "kmock_customMethodName_mock.template.renamed.PlatformMock#_hashCode" to "noHash"
            )
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared is processed, it allows renaming its method names, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/renamed/Shared.kt")
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
                "kmock_customMethodName_mock.template.renamed.SharedMock#_hashCode" to "noHash"
            )
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/renamed/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common is processed, it allows renaming its method names, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/renamed/Common.kt")
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
                "kmock_customMethodName_mock.template.renamed.CommonMock#_hashCode" to "noHash"
            )
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/renamed/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for a Platform is processed while keeping the old name schema, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/compatibility/Platform.kt")
        )
        val expected = loadResource("/expected/compatibility/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = false,
            kspArguments = mapOf(
                "kmock_useNewOverloadedNames" to "false"
            )
        )
        val actual = resolveGenerated("PlatformMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated overloaded Source for Common, which contains also generic Parameter is processed while keeping the old name schema, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/compatibility/Common.kt")
        )
        val expected = loadResource("/expected/compatibility/Common.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "kmock_useNewOverloadedNames" to "false"
            )
        )
        val actual = resolveGenerated("CommonMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/compatibility/CommonMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared is processed while using a custom annotation, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/customshared/Shared.kt")
        )
        val expected = loadResource("/expected/customshared/Shared.kt")

        // When
        val compilerResult = compile(
            provider,
            source,
            isKmp = true,
            kspArguments = mapOf(
                "kmock_customAnnotation_mock.template.customshared.CustomShared" to "sharedTest"
            )
        )
        val actual = resolveGenerated("SharedMock.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/customshared/SharedMock.kt"
        ) mustBe true
        actual.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a Source with multiple Annotations is processed, it writes a mock`() {
        // Given
        val source = SourceFile.kotlin(
            "Sample.kt",
            loadResource("/template/mixedannotation/Sample.kt")
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

        actualShared!!.absolutePath.toString().endsWith(
            "shared/sharedTest/kotlin/mock/template/mixedannotation/SharedMock.kt"
        ) mustBe true
        actualCommon!!.absolutePath.toString().endsWith(
            "common/commonTest/kotlin/mock/template/mixedannotation/CommonMock.kt"
        ) mustBe true

        actualPlatform!!.readText().normalizeSource() mustBe expectedPlatform.normalizeSource()
        actualShared.readText().normalizeSource() mustBe expectedShared.normalizeSource()
        actualCommon.readText().normalizeSource() mustBe expectedCommon.normalizeSource()
    }
}
