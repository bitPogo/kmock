/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.integration

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.gradle.kmock.compiler.KMockCLIProcessor
import tech.antibytes.gradle.kmock.compiler.KMockCompilerPluginContract.Companion.ENABLE_COMPILER_PLUGIN_FIELD
import tech.antibytes.gradle.kmock.compiler.KMockRegistration
import tech.antibytes.gradle.kmock.compiler.config.MainConfig
import tech.antibytes.util.test.mustBe
import java.io.File

class KMockCompilerOpenPluginSpec {
    @TempDir
    lateinit var buildDir: File
    private val pluginCli = KMockCLIProcessor()
    private val pluginRegistration = KMockRegistration()
    private val fixtureRoot = "/pluginTest"

    private fun loadResource(path: String): String {
        return KMockCompilerOpenPluginSpec::class.java.getResource(fixtureRoot + path).readText()
    }

    private fun compile(
        source: SourceFile,
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = listOf(source)
            workingDir = buildDir
            inheritClassPath = true
            verbose = false
            commandLineProcessors = listOf(pluginCli)
            compilerPlugins = listOf(pluginRegistration)
            pluginOptions = listOf(
                PluginOption(
                    MainConfig.pluginId,
                    ENABLE_COMPILER_PLUGIN_FIELD,
                    true.toString()
                )
            )
            useIR = true
        }.compile()
    }

    @Test
    fun `It opens standard classes`() {
        // Given
        val source = SourceFile.kotlin(
            "StdClass.kt",
            loadResource("/StdClass.kt")
        )

        // When
        val compilerResult = compile(source)

        // Then
        compilerResult.exitCode mustBe KotlinCompilation.ExitCode.OK
    }
}
