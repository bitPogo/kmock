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
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.util.test.isNot
import tech.antibytes.util.test.mustBe
import java.io.File

class KMockFactoriesSpec {
    @TempDir
    lateinit var buildDir: File
    private val provider = KMockProcessorProvider()
    private val root = "/factory"
    private val rootPackage = "generatorTest"

    private fun loadResource(path: String): String {
        return KMockMocksSpec::class.java.getResource(root + path).readText()
    }

    private fun compile(
        processorProvider: SymbolProcessorProvider,
        isKmp: Boolean,
        aliases: Map<String, String> = emptyMap(),
        vararg sourceFiles: SourceFile,
    ): KotlinCompilation.Result {
        val args = mutableMapOf(
            ProcessorContract.KSP_DIR to "${buildDir.absolutePath.trimEnd('/')}/ksp/sources/kotlin",
            ProcessorContract.ROOT_PACKAGE to rootPackage,
            ProcessorContract.KMP_FLAG to isKmp.toString()
        ).also {
            it.putAll(aliases)
        }

        return KotlinCompilation().apply {
            sources = sourceFiles.toList()
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
        return findGeneratedSource { file -> file.absolutePath.endsWith(expected) }.getOrNull(0)
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
        val compilerResult = compile(
            provider,
            isKmp = false,
            emptyMap(),
            source
        )
        val actual = resolveGenerated("MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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
        val compilerResult = compile(
            provider,
            isKmp = true,
            emptyMap(),
            source
        )
        val actualActual = resolveGenerated("kotlin/${rootPackage}/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/${rootPackage}/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
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
        val compilerResult = compile(
            provider,
            isKmp = false,
            emptyMap(),
            source
        )
        val actual = resolveGenerated("MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
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
        val compilerResult = compile(
            provider,
            isKmp = true,
            emptyMap(),
            source
        )
        val actualActual = resolveGenerated("kotlin/${rootPackage}/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/${rootPackage}/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
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
        val compilerResult = compile(
            provider,
            isKmp = false,
            emptyMap(),
            source
        )
        val actual = resolveGenerated("MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with Generics for Shared is processed, it writes a mock factory`() {
        // Given
        val source1 = SourceFile.kotlin(
            "Shared1.kt",
            loadResource("/template/generic/Shared1.kt")
        )
        val source2 = SourceFile.kotlin(
            "Shared2.kt",
            loadResource("/template/generic/Shared2.kt")
        )
        val source3 = SourceFile.kotlin(
            "Shared3.kt",
            loadResource("/template/generic/Shared3.kt")
        )
        val expectedExpect1 = loadResource("/expected/generic/Shared1Expect.kt")
        val expectedExpect2 = loadResource("/expected/generic/Shared2Expect.kt")
        // Note: Shared2Expect is actually Shared3, since Shared2 is eaten by Shared1, which is totally correct!

        val expectedActual = loadResource("/expected/generic/SharedActual.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            emptyMap(),
            source1,
            source2,
            source3
        )

        val actualActual = resolveGenerated("kotlin/${rootPackage}/MockFactory.kt")
        val actualExpect1 = resolveGenerated("kotlin/other/otherTest/${rootPackage}/MockFactory.kt")
        val actualExpect2 = resolveGenerated("kotlin/shared/sharedTest/${rootPackage}/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualExpect1 isNot null
        actualExpect2 isNot null
        actualActual isNot null

        actualExpect1!!.readText().normalizeSource() mustBe expectedExpect1.normalizeSource()
        actualExpect2!!.readText().normalizeSource() mustBe expectedExpect2.normalizeSource()
        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
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
        val compilerResult = compile(
            provider,
            isKmp = true,
            emptyMap(),
            source
        )
        val actualActual = resolveGenerated("kotlin/${rootPackage}/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/${rootPackage}/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with an Alias for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/alias/Platform.kt")
        )
        val expected = loadResource("/expected/alias/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = false,
            aliases = mapOf(
                "alias_factory.template.alias.Platform" to "AliasPlatform",
            ),
            source
        )
        val actual = resolveGenerated("MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with an Alias and Generics for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Generic.kt",
            loadResource("/template/alias/Generic.kt")
        )
        val expected = loadResource("/expected/alias/Generic.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = false,
            aliases = mapOf(
                "alias_factory.template.alias.Generic" to "AliasGeneric",
            ),
            source
        )
        val actual = resolveGenerated("MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actual isNot null

        actual!!.readText().normalizeSource() mustBe expected.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with an Alias for Common is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/alias/Common.kt")
        )
        val expectedActual = loadResource("/expected/alias/CommonActual.kt")
        val expectedExpect = loadResource("/expected/alias/CommonExpect.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            aliases = mapOf(
                "alias_factory.template.alias.Common" to "AliasCommon",
            ),
            source
        )
        val actualActual = resolveGenerated("kotlin/${rootPackage}/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/${rootPackage}/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }
}
