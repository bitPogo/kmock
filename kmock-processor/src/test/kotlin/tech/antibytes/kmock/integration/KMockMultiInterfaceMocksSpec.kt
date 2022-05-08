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

class KMockMultiInterfaceMocksSpec {
    @TempDir
    lateinit var buildDir: File
    private val provider = KMockProcessorProvider()
    private val root = "/multi"

    private fun loadResource(path: String): String {
        return KMockMultiInterfaceMocksSpec::class.java.getResource(root + path).readText()
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
            .onEach { println(it) }
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
    fun `Given a annotated Source with multiple Interface it writes a mock`() {
        // Given
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

        // When
        val compilerResult = compile(
            provider,
            isKmp = true,
            sourceFiles = arrayOf(rootInterface, nestedInterface, scopedInterface)
        )
        val actualIntermediateInterfaces = resolveGenerated("KMockMultiInterfaceArtifacts.kt")

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
        actualIntermediateInterfaces isNot null

        actualIntermediateInterfaces!!.absolutePath.toString().endsWith(
            "ksp/sources/kotlin/multi/KMockMultiInterfaceArtifacts.kt"
        ) mustBe true
        actualIntermediateInterfaces.readText().normalizeSource() mustBe expectedInterface.normalizeSource()
    }
}
