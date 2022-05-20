/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessorProviders
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
import java.io.File

class KMockMultiInterfaceMocksSpec {
    @TempDir
    lateinit var buildDir: File
    private val root = "/multi"
    private val rootPackage = "multi"

    private fun loadResource(path: String): String {
        return KMockMultiInterfaceMocksSpec::class.java.getResource(root + path).readText()
    }

    // Workaround for https://github.com/tschuchortdev/kotlin-compile-testing/issues/263
    class SymbolProcessorProviderSpy : SymbolProcessorProvider {
        lateinit var lastProcessor: SymbolProcessor

        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
            val processor = KMockProcessorProvider(true).create(environment)
            lastProcessor = processor

            return processor
        }
    }

    class ShallowSymbolProcessorProvider(
        private val processor: SymbolProcessor
    ) : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = processor
    }

    private fun compile(
        processorProvider: SymbolProcessorProvider,
        isKmp: Boolean,
        kspArguments: Map<String, String> = emptyMap(),
        vararg sourceFiles: SourceFile,
    ): KotlinCompilation.Result {
        val args = mutableMapOf(
            KSP_DIR to "${buildDir.absolutePath.trimEnd('/')}/ksp/sources/kotlin",
            ROOT_PACKAGE to "multi",
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

        return KotlinCompilation().apply {
            sources = sourceFiles.toList()
            symbolProcessorProviders = listOf(processorProvider)
            workingDir = buildDir
            inheritClassPath = true
            verbose = false
            kspIncremental = true
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
    fun `Given a annotated Source for Common with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/common/Regular1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/common/nested/Regular3.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/common/Regular2.kt")
        )
        val expectedInterface = loadResource("/expected/common/RegularInterface.kt")
        val expectedActualFactory = loadResource("/expected/common/RegularActualFactory.kt")
        val expectedExpectFactory = loadResource("/expected/common/RegularExpectFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true"
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectMockFactory!!.readText().normalizeSource() mustBe expectedExpectFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/common/RegularMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("CommonMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/common/commonTest/kotlin/$rootPackage/CommonMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Common, while spied, with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/common/Regular1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/common/nested/Regular3.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/common/Regular2.kt")
        )
        val expectedInterface = loadResource("/expected/common/RegularInterface.kt")
        val expectedActualFactory = loadResource("/expected/common/SpiedRegularActualFactory.kt")
        val expectedExpectFactory = loadResource("/expected/common/SpiedRegularExpectFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.CommonMulti",
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectMockFactory!!.readText().normalizeSource() mustBe expectedExpectFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/common/SpiedRegularMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.CommonMulti",
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("CommonMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/common/commonTest/kotlin/$rootPackage/CommonMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for generic Common with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Generic1.kt",
            loadResource("/template/commonGeneric/Generic1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Generic2.kt",
            loadResource("/template/commonGeneric/nested/Generic2.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Generic3.kt",
            loadResource("/template/commonGeneric/Generic3.kt")
        )
        val expectedInterface = loadResource("/expected/commonGeneric/GenericInterface.kt")
        val expectedActualFactory = loadResource("/expected/commonGeneric/GenericActualFactory.kt")
        val expectedExpectFactory = loadResource("/expected/commonGeneric/GenericExpectFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true"
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectMockFactory!!.readText().normalizeSource() mustBe expectedExpectFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/commonGeneric/GenericMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("CommonGenericMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/common/commonTest/kotlin/$rootPackage/CommonGenericMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for generic Common while spied with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Generic1.kt",
            loadResource("/template/commonGeneric/Generic1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Generic2.kt",
            loadResource("/template/commonGeneric/nested/Generic2.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Generic3.kt",
            loadResource("/template/commonGeneric/Generic3.kt")
        )
        val expectedInterface = loadResource("/expected/commonGeneric/SpiedGenericInterface.kt")
        val expectedActualFactory = loadResource("/expected/commonGeneric/SpiedGenericActualFactory.kt")
        val expectedExpectFactory = loadResource("/expected/commonGeneric/SpiedGenericExpectFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
                "${KMOCK_PREFIX}spiesOnly" to "true",
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/common/commonTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectMockFactory!!.readText().normalizeSource() mustBe expectedExpectFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/commonGeneric/SpiedGenericMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
                "${KMOCK_PREFIX}spiesOnly" to "true",
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("CommonGenericMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/common/commonTest/kotlin/$rootPackage/CommonGenericMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Shared Source with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/shared/Regular1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/shared/nested/Regular3.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/shared/Regular2.kt")
        )
        val expectedInterface = loadResource("/expected/shared/RegularInterface.kt")
        val expectedActualFactory = loadResource("/expected/shared/RegularActualFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true"
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory mustBe null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/shared/RegularMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("SharedMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/shared/sharedTest/kotlin/$rootPackage/SharedMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared Sources, while spied, with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/shared/Regular1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/shared/nested/Regular3.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/shared/Regular2.kt")
        )
        val expectedInterface = loadResource("/expected/shared/RegularInterface.kt")
        val expectedActualFactory = loadResource("/expected/shared/SpiedRegularActualFactory.kt")
        val expectedExpectFactory = loadResource("/expected/shared/SpiedRegularExpectFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.SharedMulti",
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectMockFactory!!.readText().normalizeSource() mustBe expectedExpectFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/shared/SpiedRegularMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.SharedMulti",
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("SharedMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/shared/sharedTest/kotlin/$rootPackage/SharedMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Shared Source with multiple Interface it writes a mock while using custom annotations`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/custom/Regular1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/custom/nested/Regular3.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/custom/Regular2.kt")
        )
        val expectedInterface = loadResource("/expected/custom/RegularInterface.kt")
        val expectedActualFactory = loadResource("/expected/custom/RegularActualFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
                "kmock_customAnnotation_multi.template.custom.CustomShared" to "sharedTest"
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory mustBe null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/custom/RegularMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "kmock_customAnnotation_multi.template.custom.CustomShared" to "sharedTest"
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("SharedMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/shared/sharedTest/kotlin/$rootPackage/SharedMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for Shared Sources, while spied, with multiple Interface it writes a mock while using custom annotations`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/custom/Regular1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/custom/nested/Regular3.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/custom/Regular2.kt")
        )
        val expectedInterface = loadResource("/expected/custom/RegularInterface.kt")
        val expectedActualFactory = loadResource("/expected/custom/SpiedRegularActualFactory.kt")
        val expectedExpectFactory = loadResource("/expected/custom/SpiedRegularExpectFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.SharedMulti",
                "kmock_customAnnotation_multi.template.custom.CustomShared" to "sharedTest",
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectMockFactory!!.readText().normalizeSource() mustBe expectedExpectFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/custom/SpiedRegularMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.SharedMulti",
                "kmock_customAnnotation_multi.template.custom.CustomShared" to "sharedTest",
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("SharedMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/shared/sharedTest/kotlin/$rootPackage/SharedMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a generic Source with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Generic1.kt",
            loadResource("/template/sharedGeneric/Generic1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Generic2.kt",
            loadResource("/template/sharedGeneric/nested/Generic2.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Generic3.kt",
            loadResource("/template/sharedGeneric/Generic3.kt")
        )
        val expectedInterface = loadResource("/expected/sharedGeneric/GenericInterface.kt")
        val expectedActualFactory = loadResource("/expected/sharedGeneric/GenericActualFactory.kt")
        val expectedExpectFactory = loadResource("/expected/sharedGeneric/GenericExpectFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true"
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectMockFactory!!.readText().normalizeSource() mustBe expectedExpectFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/sharedGeneric/GenericMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("SharedGenericMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/shared/sharedTest/kotlin/$rootPackage/SharedGenericMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for generic Shared Source while spied with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Generic1.kt",
            loadResource("/template/sharedGeneric/Generic1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Generic2.kt",
            loadResource("/template/sharedGeneric/nested/Generic2.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Generic3.kt",
            loadResource("/template/sharedGeneric/Generic3.kt")
        )
        val expectedInterface = loadResource("/expected/sharedGeneric/SpiedGenericInterface.kt")
        val expectedActualFactory = loadResource("/expected/sharedGeneric/SpiedGenericActualFactory.kt")
        val expectedExpectFactory = loadResource("/expected/sharedGeneric/SpiedGenericExpectFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
                "${KMOCK_PREFIX}spiesOnly" to "true",
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectMockFactory = resolveGenerated("kotlin/shared/sharedTest/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectMockFactory!!.readText().normalizeSource() mustBe expectedExpectFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/sharedGeneric/SpiedGenericMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
                "${KMOCK_PREFIX}spiesOnly" to "true",
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("SharedGenericMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/shared/sharedTest/kotlin/$rootPackage/SharedGenericMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source a Platform with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/platform/Regular1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/platform/nested/Regular3.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/platform/Regular2.kt")
        )
        val expectedInterface = loadResource("/expected/platform/RegularInterface.kt")
        val expectedActualFactory = loadResource("/expected/platform/RegularActualFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true"
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/platform/RegularMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("PlatformMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/PlatformMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source a Platform, while spied, with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/platform/Regular1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/platform/nested/Regular3.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/platform/Regular2.kt")
        )
        val expectedInterface = loadResource("/expected/platform/RegularInterface.kt")
        val expectedActualFactory = loadResource("/expected/platform/SpiedRegularActualFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.PlatformMulti",
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/platform/SpiedRegularMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.PlatformMulti",
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("PlatformMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/PlatformMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for generic Platform Source with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Generic1.kt",
            loadResource("/template/platformGeneric/Generic1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Generic2.kt",
            loadResource("/template/platformGeneric/nested/Generic2.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Generic3.kt",
            loadResource("/template/platformGeneric/Generic3.kt")
        )
        val expectedInterface = loadResource("/expected/platformGeneric/GenericInterface.kt")
        val expectedActualFactory = loadResource("/expected/platformGeneric/GenericActualFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true"
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/platformGeneric/GenericMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = false,
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("PlatformGenericMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/PlatformGenericMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for generic Platform while spied with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Generic1.kt",
            loadResource("/template/platformGeneric/Generic1.kt")
        )
        val nestedInterface = SourceFile.kotlin(
            "Generic2.kt",
            loadResource("/template/platformGeneric/nested/Generic2.kt")
        )
        val scopedInterface = SourceFile.kotlin(
            "Generic3.kt",
            loadResource("/template/platformGeneric/Generic3.kt")
        )
        val expectedInterface = loadResource("/expected/platformGeneric/SpiedGenericInterface.kt")
        val expectedActualFactory = loadResource("/expected/platformGeneric/SpiedGenericActualFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = false,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
                "${KMOCK_PREFIX}spiesOnly" to "true",
            ),
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces.readText()
        )
        val expectedMock = loadResource("/expected/platformGeneric/SpiedGenericMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
                "${KMOCK_PREFIX}spiesOnly" to "true",
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, nestedInterface, scopedInterface)
        )
        val actualMock = resolveGenerated("PlatformGenericMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/PlatformGenericMultiMock.kt"
        ) mustBe true
        actualMock.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for mixed Sources while spied with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Generic1.kt",
            loadResource("/template/mixed/Generic1.kt")
        )
        val platformNestedInterface = SourceFile.kotlin(
            "Generic2.kt",
            loadResource("/template/mixed/nested/Generic2.kt")
        )
        val platformScopedInterface = SourceFile.kotlin(
            "Generic3.kt",
            loadResource("/template/mixed/Generic3.kt")
        )
        val commonNonNestesdInterface = SourceFile.kotlin(
            "Regular1.kt",
            loadResource("/template/mixed/Regular1.kt")
        )
        val commonNestedInterface = SourceFile.kotlin(
            "Regular3.kt",
            loadResource("/template/mixed/nested/Regular3.kt")
        )
        val commonScopedInterface = SourceFile.kotlin(
            "Regular2.kt",
            loadResource("/template/mixed/Regular2.kt")
        )

        val expectedInterface = loadResource("/expected/mixed/Interface.kt")
        val expectedActualFactory = loadResource("/expected/mixed/ActualFactory.kt")
        val expectedExpectedFactory = loadResource("/expected/mixed/ExpectedFactory.kt")

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
            ),
            sourceFiles = arrayOf(
                rootInterface,
                platformNestedInterface,
                platformScopedInterface,
                commonNonNestesdInterface,
                commonNestedInterface,
                commonScopedInterface,
            )
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")
        val actualActualMockFactory = resolveGenerated("ksp/sources/kotlin/$rootPackage/MockFactory.kt")
        val actualExpectedMockFactory = resolveGenerated("ksp/sources/kotlin/common/commonTest/kotlin/multi/MockFactory.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null
        actualActualMockFactory isNot null
        actualExpectedMockFactory isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/$rootPackage/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
        actualActualMockFactory!!.readText().normalizeSource() mustBe expectedActualFactory.normalizeSource()
        actualExpectedMockFactory!!.readText().normalizeSource() mustBe expectedExpectedFactory.normalizeSource()
    }

    @Test
    fun `Given a annotated Source for a Platform with receivers while spied with multiple Interface it writes a mock`() {
        // Round1
        // Given
        val spyProvider = SymbolProcessorProviderSpy()
        val rootInterface = SourceFile.kotlin(
            "Properties.kt",
            loadResource("/template/receiver/Properties.kt")
        )
        val methodInterface = SourceFile.kotlin(
            "Methods.kt",
            loadResource("/template/receiver/Methods.kt")
        )

        // When
        val compilerResultRound1 = compile(
            spyProvider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}allowInterfaces" to "true",
                "${KMOCK_PREFIX}spyOn_0" to "multi.ReceiverMulti",
            ),
            sourceFiles = arrayOf(
                rootInterface,
                methodInterface,
            )
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")

        // Then
        compilerResultRound1.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null

        // Round2
        // Given
        val provider = ShallowSymbolProcessorProvider(spyProvider.lastProcessor)
        val multiInterfaceInterface = SourceFile.kotlin(
            "KMockMultiInterfaceArtifacts.kt",
            actualIntermediateInterfaces!!.readText()
        )
        val expectedMock = loadResource("/expected/receiver/SpiedReceiverMock.kt")

        // When
        val compilerResultRound2 = compile(
            provider,
            isKmp = true,
            kspArguments = mapOf(
                "${KMOCK_PREFIX}spyOn_0" to "multi.ReceiverMulti",
            ),
            sourceFiles = arrayOf(multiInterfaceInterface, rootInterface, methodInterface)
        )
        val actualMock = resolveGenerated("ReceiverMultiMock.kt")

        // Then
        compilerResultRound2.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualMock isNot null

        actualMock!!.readText().normalizeSource() mustBe expectedMock.normalizeSource()
    }
}
