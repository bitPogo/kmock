/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.tasks.StopExecutionException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import java.io.File
import kotlin.test.assertFailsWith

class FactoryGeneratorSpec {
    @TempDir
    lateinit var buildDir: File
    private val fixtureRoot = "/generatorTest"

    private fun loadResource(path: String): String {
        return FactoryGeneratorSpec::class.java.getResource(fixtureRoot + path).readText()
    }

    private fun String.normalizeSource(): String {
        return this.replace(Regex("[\t ]+"), "")
    }

    @Test
    fun `It fulfils FactoryGenerator`() {
        FactoryGenerator fulfils KMockPluginContract.FactoryGenerator::class
    }

    @Test
    fun `Given generate is called, it fails if the rootPackage is empty`() {
        // Given
        val rootPackage = ""

        // Then
        val error = assertFailsWith<StopExecutionException> {
            FactoryGenerator.generate(buildDir, rootPackage)
        }

        error.message mustBe "Missing package definition!"
    }

    @Test
    fun `Given generate is called, it writes factories for Common`() {
        // Given
        val rootPackage = "test"
        val expected = loadResource("/MockFactory.kt")

        // When
        FactoryGenerator.generate(buildDir, rootPackage)

        // Then
        var actual: String? = null
        buildDir.walkBottomUp().toList().forEach { file ->
            if (file.isFile) {
                actual = file.readText()
            }
        }

        actual!!.normalizeSource() mustBe expected.normalizeSource()
    }
}
