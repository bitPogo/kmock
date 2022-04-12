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
        kspArguments: Map<String, String> = emptyMap(),
        vararg sourceFiles: SourceFile,
    ): KotlinCompilation.Result {
        val args = mutableMapOf(
            KSP_DIR to "${buildDir.absolutePath.trimEnd('/')}/ksp/sources/kotlin",
            ROOT_PACKAGE to rootPackage,
            KMP_FLAG to isKmp.toString()
        ).also {
            it.putAll(kspArguments)
        }.also {
            it["${PRECEDENCE}sharedTest"] = "0"
            it["${PRECEDENCE}otherTest"] = "1"
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

    private fun String.normalizeSource(): String = this
        .replace(Regex("[ \t\r\n]+\n"), "\n")
        .replace(Regex("[\t\r]"), " ")
        .replace(Regex("[ ]+"), " ")
        .trim()

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
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

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
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

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

        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect1 = resolveGenerated("kotlin/other/otherTest/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect2 = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

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
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

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
            kspArguments = mapOf(
                "${KMOCK_PREFIX}alias_factory.template.alias.Platform" to "AliasPlatform",
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
            kspArguments = mapOf(
                "${KMOCK_PREFIX}alias_factory.template.alias.Generic" to "AliasGeneric",
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
            kspArguments = mapOf(
                "${KMOCK_PREFIX}alias_factory.template.alias.Common" to "AliasCommon",
            ),
            source
        )
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Spies for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/spy/Platform.kt")
        )
        val expected = loadResource("/expected/spy/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.spy.Platform1",
                "${KMOCK_PREFIX}spyOn_1" to "factory.template.spy.Platform2",
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
    fun `Given a annotated Source for Spies for Shared is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/spy/Shared.kt")
        )
        val expectedActual = loadResource("/expected/spy/SharedActual.kt")
        val expectedExpect = loadResource("/expected/spy/SharedExpect.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.spy.Shared1",
                "${KMOCK_PREFIX}spyOn_1" to "factory.template.spy.Shared2",
            ),
            source
        )
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Spies for Common is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/spy/Common.kt")
        )
        val expectedActual = loadResource("/expected/spy/CommonActual.kt")
        val expectedExpect = loadResource("/expected/spy/CommonExpect.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.spy.Common1",
                "${KMOCK_PREFIX}spyOn_1" to "factory.template.spy.Common2",
            ),
            source
        )
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Interface enabled for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/interfaze/Platform.kt")
        )
        val expected = loadResource("/expected/interfaze/NoSpy.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfacesOnKmock" to "true",
                "${KMOCK_PREFIX}allowInterfacesOnKspy" to "true",
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
    fun `Given a annotated Source for Spies and Interface enabled for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/interfaze/Platform.kt")
        )
        val expected = loadResource("/expected/interfaze/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.interfaze.Platform1",
                "${KMOCK_PREFIX}spyOn_1" to "factory.template.interfaze.Platform2",
                "${KMOCK_PREFIX}allowInterfacesOnKmock" to "true",
                "${KMOCK_PREFIX}allowInterfacesOnKspy" to "true",
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
    fun `Given a annotated Source for Spies and Interface and aliased enabled for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/interfaze/Platform.kt")
        )
        val expected = loadResource("/expected/interfaze/Alias.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.interfaze.Platform1",
                "${KMOCK_PREFIX}spyOn_1" to "factory.template.interfaze.Platform2",
                "${KMOCK_PREFIX}alias_factory.template.interfaze.Platform1" to "AliasPlatform",
                "${KMOCK_PREFIX}allowInterfacesOnKmock" to "true",
                "${KMOCK_PREFIX}allowInterfacesOnKspy" to "true",
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
    fun `Given a annotated Source for Spies and enabled Interfaces for Shared is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/interfaze/Shared.kt")
        )
        val expectedActual = loadResource("/expected/interfaze/SharedActual.kt")
        val expectedExpect = loadResource("/expected/interfaze/SharedExpect.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.interfaze.Shared1",
                "${KMOCK_PREFIX}spyOn_1" to "factory.template.interfaze.Shared2",
            ),
            source
        )
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Spies with enabled Interfazes for Common is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/interfaze/Common.kt")
        )
        val expectedActual = loadResource("/expected/interfaze/CommonActual.kt")
        val expectedExpect = loadResource("/expected/interfaze/CommonExpect.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.interfaze.Common1",
                "${KMOCK_PREFIX}spyOn_1" to "factory.template.interfaze.Common2",
            ),
            source
        )
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for only Spies for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/spiesonly/Platform.kt")
        )
        val expected = loadResource("/expected/spiesonly/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spiesOnly" to "true",
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
    fun `Given a annotated Source for only Spies for Shared is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Shared.kt",
            loadResource("/template/spiesonly/Shared.kt")
        )
        val expectedActual = loadResource("/expected/spiesonly/SharedActual.kt")
        val expectedExpect = loadResource("/expected/spiesonly/SharedExpect.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spiesOnly" to "true",
            ),
            source
        )
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for only Spies for Common is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/spiesonly/Common.kt")
        )
        val expectedActual = loadResource("/expected/spiesonly/CommonActual.kt")
        val expectedExpect = loadResource("/expected/spiesonly/CommonExpect.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spiesOnly" to "true",
            ),
            source
        )
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }

    @Test
    fun `Given a annotated Source with a changed freeze flag for a Platform is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Platform.kt",
            loadResource("/template/freeze/Platform.kt")
        )
        val expected = loadResource("/expected/freeze/Platform.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}freeze" to "false",
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.interfaze.Platform",
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
    fun `Given a annotated Source with a changed freeze flag for Common is processed, it writes a mock factory`() {
        // Given
        val source = SourceFile.kotlin(
            "Common.kt",
            loadResource("/template/freeze/Common.kt")
        )
        val expectedActual = loadResource("/expected/freeze/CommonActual.kt")
        val expectedExpect = loadResource("/expected/freeze/CommonExpect.kt")

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            mapOf(
                "${KMOCK_PREFIX}freeze" to "false",
                "${KMOCK_PREFIX}spyOn_0" to "factory.template.interfaze.Common",
            ),
            source
        )
        val actualActual = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpect = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualActual isNot null
        actualExpect isNot null

        actualActual!!.readText().normalizeSource() mustBe expectedActual.normalizeSource()
        actualExpect!!.readText().normalizeSource() mustBe expectedExpect.normalizeSource()
    }
}
