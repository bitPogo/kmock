/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import java.io.File
import kotlin.test.assertFailsWith

class KMockCodeGeneratorSpec {
    @TempDir
    lateinit var kspDir: File

    private val fixture = kotlinFixture { config ->
        config.addGenerator(
            String::class,
            StringAlphaGenerator,
            named("alpha")
        )
    }

    @Test
    fun `It fulfils CodeGenerator`() {
        KMockCodeGenerator(fixture.fixture(), mockk(), mockk()) fulfils CodeGenerator::class
    }

    @Test
    fun `It fulfils KmpCodeGenerator`() {
        KMockCodeGenerator(fixture.fixture(), mockk(), mockk()) fulfils ProcessorContract.KmpCodeGenerator::class
    }

    @Test
    fun `Given createNewFile is called it delegates the call to the given kspGenerator`() {
        // Given
        val ksp = kspDir.absolutePath
        val packageName: String = fixture.fixture(named("alpha"))
        val fileName: String = fixture.fixture(named("alpha"))
        val extensionName: String = fixture.fixture(named("alpha"))
        val kspGenerator: CodeGenerator = mockk(relaxed = true)

        val dependencies: Dependencies = mockk()

        // When
        KMockCodeGenerator(
            kspDir = ksp,
            purgeFiles = emptySet(),
            kspGenerator = kspGenerator,
        ).createNewFile(
            dependencies = dependencies,
            packageName = packageName,
            fileName = fileName,
            extensionName = extensionName,
        )

        // Then
        verify(exactly = 1) {
            kspGenerator.createNewFile(
                dependencies,
                packageName,
                fileName,
                extensionName
            )
        }
    }

    @Test
    fun `Given createNewFile is called it writes a File to a given SourceSet if told in the given KSP Dir`() {
        // Given
        val ksp = kspDir.absolutePath
        val packageName: String = fixture.fixture(named("alpha"))
        val fileName: String = fixture.fixture(named("alpha"))
        val extensionName: String = fixture.fixture(named("alpha"))
        val kspGenerator: CodeGenerator = mockk(relaxed = true)

        val dependencies: Dependencies = mockk()

        // When
        val generator = KMockCodeGenerator(
            kspDir = ksp,
            purgeFiles = emptySet(),
            kspGenerator = kspGenerator,
        )
        generator.setOneTimeSourceSet("commonTest")

        generator.createNewFile(
            dependencies = dependencies,
            packageName = packageName,
            fileName = fileName,
            extensionName = extensionName,
        )

        val actual = kspDir.walkBottomUp().toList().any { file ->
            file.absolutePath.endsWith("common/commonTest/kotlin/$packageName/$fileName.kt")
        }

        // Then
        verify(exactly = 0) {
            kspGenerator.createNewFile(
                dependencies,
                packageName,
                fileName,
                extensionName
            )
        }

        actual mustBe true
    }

    @Test
    fun `Given createNewFile is called it files while writing a File to a given SourceSet if told in the given KSP Dir, if the file already exists`() {
        // Given
        val ksp = kspDir.absolutePath
        val packageName: String = fixture.fixture(named("alpha"))
        val fileName: String = fixture.fixture(named("alpha"))
        val extensionName: String = fixture.fixture(named("alpha"))
        val kspGenerator: CodeGenerator = mockk(relaxed = true)

        val dependencies: Dependencies = mockk()

        // When
        val generator = KMockCodeGenerator(
            kspDir = ksp,
            purgeFiles = emptySet(),
            kspGenerator = kspGenerator,
        )
        generator.setOneTimeSourceSet("commonTest")
        generator.createNewFile(
            dependencies = dependencies,
            packageName = packageName,
            fileName = fileName,
            extensionName = extensionName,
        )

        generator.setOneTimeSourceSet("commonTest")
        assertFailsWith<FileAlreadyExistsException> {
            generator.createNewFile(
                dependencies = dependencies,
                packageName = packageName,
                fileName = fileName,
                extensionName = extensionName,
            )
        }

        // Then
        verify(exactly = 0) {
            kspGenerator.createNewFile(
                dependencies,
                packageName,
                fileName,
                extensionName
            )
        }
    }

    @Test
    fun `Given createNewFile is called it files writes a File to a given SourceSet if told in the given KSP Dir, while removing a existing version of it if it part of the purgeFiles`() {
        // Given
        val ksp = kspDir.absolutePath
        val packageName: String = fixture.fixture(named("alpha"))
        val fileName: String = fixture.fixture(named("alpha"))
        val extensionName: String = fixture.fixture(named("alpha"))
        val kspGenerator: CodeGenerator = mockk(relaxed = true)

        val dependencies: Dependencies = mockk()

        // When
        val generator = KMockCodeGenerator(
            kspDir = ksp,
            purgeFiles = setOf(
                "$ksp/common/commonTest/kotlin/$packageName/$fileName.kt"
            ),
            kspGenerator = kspGenerator,
        )
        generator.setOneTimeSourceSet("commonTest")
        generator.createNewFile(
            dependencies = dependencies,
            packageName = packageName,
            fileName = fileName,
            extensionName = extensionName,
        )

        generator.setOneTimeSourceSet("commonTest")
        generator.createNewFile(
            dependencies = dependencies,
            packageName = packageName,
            fileName = fileName,
            extensionName = extensionName,
        )

        val actual = kspDir.walkBottomUp().toList().any { file ->
            file.absolutePath.endsWith("common/commonTest/kotlin/$packageName/$fileName.kt")
        }

        // Then
        verify(exactly = 0) {
            kspGenerator.createNewFile(
                dependencies,
                packageName,
                fileName,
                extensionName
            )
        }

        actual mustBe true
    }

    @Test
    fun `associate is not implemented`() {
        assertFailsWith<NotImplementedError> {
            KMockCodeGenerator(fixture.fixture(), mockk(), mockk()).associate(
                listOf(),
                fixture.fixture(),
                fixture.fixture()
            )
        }
    }

    @Test
    fun `associateWithClasses is not implemented`() {
        assertFailsWith<NotImplementedError> {
            KMockCodeGenerator(fixture.fixture(), mockk(), mockk()).associateWithClasses(
                listOf(),
                fixture.fixture(),
                fixture.fixture()
            )
        }
    }
}
