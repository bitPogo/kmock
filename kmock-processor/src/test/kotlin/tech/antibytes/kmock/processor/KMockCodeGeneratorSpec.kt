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
import java.io.File
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.qualifier.qualifiedBy
import tech.antibytes.kmock.fixture.StringAlphaGenerator
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class KMockCodeGeneratorSpec {
    @TempDir
    lateinit var kspDir: File

    private val fixture = kotlinFixture {
        addGenerator(
            String::class,
            StringAlphaGenerator,
            qualifiedBy("alpha"),
        )
    }

    @Test
    fun `It fulfils CodeGenerator`() {
        KMockCodeGenerator(fixture.fixture(), mockk()) fulfils CodeGenerator::class
    }

    @Test
    fun `It fulfils KmpCodeGenerator`() {
        KMockCodeGenerator(fixture.fixture(), mockk()) fulfils ProcessorContract.KmpCodeGenerator::class
    }

    @Test
    fun `Given createNewFile is called it delegates the call to the given kspGenerator`() {
        // Given
        val ksp = kspDir.absolutePath
        val packageName: String = fixture.fixture(qualifiedBy("alpha"))
        val fileName: String = fixture.fixture(qualifiedBy("alpha"))
        val extensionName: String = fixture.fixture(qualifiedBy("alpha"))
        val kspGenerator: CodeGenerator = mockk(relaxed = true)

        val dependencies: Dependencies = mockk()

        // When
        KMockCodeGenerator(
            kspDir = ksp,
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
                extensionName,
            )
        }
    }

    @Test
    fun `Given createNewFile is called it writes a File to a given SourceSet if told in the given KSP Dir`() {
        // Given
        val ksp = kspDir.absolutePath
        val packageName: String = fixture.fixture(qualifiedBy("alpha"))
        val fileName: String = fixture.fixture(qualifiedBy("alpha"))
        val extensionName: String = fixture.fixture(qualifiedBy("alpha"))
        val kspGenerator: CodeGenerator = mockk(relaxed = true)

        val dependencies: Dependencies = mockk()

        // When
        val generator = KMockCodeGenerator(
            kspDir = ksp,
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
                extensionName,
            )
        }

        actual mustBe true
    }

    @Test
    fun `Given createNewFile is called it files while writing a File to a given SourceSet if told in the given KSP Dir, if the file already exists`() {
        // Given
        val ksp = kspDir.absolutePath
        val packageName: String = fixture.fixture(qualifiedBy("alpha"))
        val fileName: String = fixture.fixture(qualifiedBy("alpha"))
        val extensionName: String = fixture.fixture(qualifiedBy("alpha"))
        val kspGenerator: CodeGenerator = mockk(relaxed = true)

        val dependencies: Dependencies = mockk()

        // When
        val generator = KMockCodeGenerator(
            kspDir = ksp,
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
                extensionName,
            )
        }
    }

    @Test
    fun `createNewFileByPath is not implemented`() {
        assertFailsWith<NotImplementedError> {
            KMockCodeGenerator(fixture.fixture(), mockk()).createNewFileByPath(
                mockk(),
                fixture.fixture(),
                fixture.fixture(),
            )
        }
    }

    @Test
    fun `associate is not implemented`() {
        assertFailsWith<NotImplementedError> {
            KMockCodeGenerator(fixture.fixture(), mockk()).associate(
                listOf(),
                fixture.fixture(),
                fixture.fixture(),
            )
        }
    }

    @Test
    fun `associateWithClasses is not implemented`() {
        assertFailsWith<NotImplementedError> {
            KMockCodeGenerator(fixture.fixture(), mockk()).associateWithClasses(
                listOf(),
                fixture.fixture(),
                fixture.fixture(),
            )
        }
    }

    @Test
    fun `associateByPath is not implemented`() {
        assertFailsWith<NotImplementedError> {
            KMockCodeGenerator(fixture.fixture(), mockk()).associateByPath(
                listOf(),
                fixture.fixture(),
                fixture.fixture(),
            )
        }
    }
}
