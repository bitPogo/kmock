/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.getByName
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.FactoryGenerator
import tech.antibytes.gradle.kmock.KMockCleanTask
import tech.antibytes.gradle.kmock.KMockExtension
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.SharedSourceCopist
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import java.io.File

class KmpSetupConfiguratorSpec {
    private val fixture = kotlinFixture()

    @BeforeEach
    fun setUp() {
        mockkObject(SharedSourceCopist)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(SharedSourceCopist)
    }

    @Test
    fun `It fulfils KmpSetupConfigurator`() {
        KmpSetupConfigurator fulfils KMockPluginContract.KmpSetupConfigurator::class
    }

    @Test
    fun `Given wireSharedSourceTasks is called it configures PlatformTest Sources`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "jvm" to "kspTestKotlinJvm",
            "js" to "kspTestKotlinJs"
        )

        val extensions: ExtensionContainer = mockk()
        val path: String = fixture.fixture()

        val kMockExtension: KMockExtension = mockk()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
        )

        every { project.extensions } returns extensions
        every { extensions.getByType(KMockExtension::class.java) } returns kMockExtension

        every { kMockExtension.sharedSources.orNull } returns null

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].targetPlatform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.add(any<String>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]

        every { cleanUpTasks[1].group = any() } just Runs
        every { cleanUpTasks[1].description = any() } just Runs
        every { cleanUpTasks[1].target.set(any<String>()) } just Runs
        every { cleanUpTasks[1].targetPlatform.set(any<String>()) } just Runs
        every { cleanUpTasks[1].indicators.add(any<String>()) } just Runs
        every { cleanUpTasks[1].dependsOn(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(any()) } returns cleanUpTasks[1]

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any(), any()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(project, sources)

        // Then
        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJvmTest", KMockCleanTask::class.java)
        }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJsTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, any(), any(), "commonTest", "COMMON SOURCE")
        }

        verify(exactly = 1) { copyTask.dependsOn("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTask.mustRunAfter("kspTestKotlinJvm") }

        verify(exactly = 1) { cleanUpTasks[0].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[0].targetPlatform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.add("COMMON SOURCE") }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[1].target.set("jsTest") }
        verify(exactly = 1) { cleanUpTasks[1].targetPlatform.set("js") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.add("COMMON SOURCE") }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter("kspTestKotlinJs") }
        verify(exactly = 1) { cleanUpTasks[1].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[1].group = "Code Generation" }

        verify(atLeast = 1) {
            project.tasks.getByName("compileTestKotlinJvm")
        }

        verify(atLeast = 1) {
            project.tasks.getByName("compileTestKotlinJs")
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[0],
                copyTask,
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[1],
                copyTask,
            )
        }

        verify(atLeast = 1) {
            compileTask.mustRunAfter(copyTask)
        }

        verify(exactly = 1) { kspTask.mustRunAfter(copyTask) }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources, it selects Jvm over Js`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "jvm" to "kspTestKotlinJvm",
            "js" to "kspTestKotlinJs"
        )

        val extensions: ExtensionContainer = mockk()
        val path: String = fixture.fixture()

        val kMockExtension: KMockExtension = mockk()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
        )

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { project.extensions } returns extensions
        every { extensions.getByType(KMockExtension::class.java) } returns kMockExtension

        every { kMockExtension.sharedSources.orNull } returns mapOf<String, String>()

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any(), any()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(project, sources)

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, "jvm", "jvmTest", "commonTest", "COMMON SOURCE")
        }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources, it selects Js over Any other source`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "native" to "kspTestKotlinNative",
            "js" to "kspTestKotlinJs"
        )

        val extensions: ExtensionContainer = mockk()
        val path: String = fixture.fixture()

        val kMockExtension: KMockExtension = mockk()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
        )

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        every { project.extensions } returns extensions
        every { extensions.getByType(KMockExtension::class.java) } returns kMockExtension

        every { kMockExtension.sharedSources.orNull } returns mapOf<String, String>()

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any(), any()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(project, sources)

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, "js", "jsTest", "commonTest", "COMMON SOURCE")
        }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources it selects Any other source if no precedence matches`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "native1" to "kspTestKotlinNative1",
            "native2" to "kspTestKotlinNative2"
        )

        val extensions: ExtensionContainer = mockk()
        val path: String = fixture.fixture()

        val kMockExtension: KMockExtension = mockk()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
        )

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        every { project.extensions } returns extensions
        every { extensions.getByType(KMockExtension::class.java) } returns kMockExtension

        every { kMockExtension.sharedSources.orNull } returns mapOf<String, String>()

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any(), any()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(project, sources)

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, "native1", "native1Test", "commonTest", "COMMON SOURCE")
        }
    }

    @Test
    fun `Given configure is called it configures AndroidSources and uses them over Jvm`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "android" to "kspTestKotlinAndroid",
            "jvm" to "kspTestKotlinJvm",
        )

        val extensions: ExtensionContainer = mockk()
        val path: String = fixture.fixture()

        val kMockExtension: KMockExtension = mockk()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
            mockk(),
        )

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns true

        every { project.extensions } returns extensions
        every { extensions.getByType(KMockExtension::class.java) } returns kMockExtension

        every { kMockExtension.sharedSources.orNull } returns mapOf<String, String>()

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].targetPlatform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.add(any<String>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]

        every { cleanUpTasks[1].group = any() } just Runs
        every { cleanUpTasks[1].description = any() } just Runs
        every { cleanUpTasks[1].target.set(any<String>()) } just Runs
        every { cleanUpTasks[1].targetPlatform.set(any<String>()) } just Runs
        every { cleanUpTasks[1].indicators.add(any<String>()) } just Runs
        every { cleanUpTasks[1].dependsOn(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(any()) } returns cleanUpTasks[1]

        every { cleanUpTasks[2].group = any() } just Runs
        every { cleanUpTasks[2].description = any() } just Runs
        every { cleanUpTasks[2].target.set(any<String>()) } just Runs
        every { cleanUpTasks[2].targetPlatform.set(any<String>()) } just Runs
        every { cleanUpTasks[2].indicators.add(any<String>()) } just Runs
        every { cleanUpTasks[2].dependsOn(any()) } returns cleanUpTasks[2]
        every { cleanUpTasks[2].mustRunAfter(any()) } returns cleanUpTasks[2]

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJvm") } returns kspTask
        every { project.tasks.getByName("kspReleaseUnitTestKotlinAndroid") } returns kspTask

        every { compileTask.dependsOn(any(), any()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask

        every { copyTask.dependsOn(any<String>()) } returns copyTask
        every { copyTask.mustRunAfter(any<String>()) } returns copyTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(project, sources)

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "android",
                "androidDebugUnitTest",
                "commonTest",
                "COMMON SOURCE"
            )
        }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJvmTest", KMockCleanTask::class.java)
        }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesAndroidDebugUnitTest", KMockCleanTask::class.java)
        }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesAndroidReleaseUnitTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "android",
                "androidDebugUnitTest",
                "commonTest",
                "COMMON SOURCE"
            )
        }

        verify(exactly = 1) { copyTask.dependsOn("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { copyTask.mustRunAfter("kspDebugUnitTestKotlinAndroid") }

        verify(exactly = 1) { cleanUpTasks[0].target.set("androidDebugUnitTest") }
        verify(exactly = 1) { cleanUpTasks[0].targetPlatform.set("android") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.add("COMMON SOURCE") }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[1].target.set("androidReleaseUnitTest") }
        verify(exactly = 1) { cleanUpTasks[1].targetPlatform.set("android") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.add("COMMON SOURCE") }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter("kspReleaseUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[1].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[1].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[2].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[2].targetPlatform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[2].indicators.add("COMMON SOURCE") }
        verify(exactly = 1) { cleanUpTasks[2].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[2].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[2].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { cleanUpTasks[2].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[2].group = "Code Generation" }

        verify(atLeast = 1) {
            project.tasks.getByName("compileTestKotlinJvm")
        }

        verify(atLeast = 1) {
            project.tasks.getByName("compileDebugUnitTestKotlinAndroid")
        }

        verify(atLeast = 1) {
            project.tasks.getByName("compileReleaseUnitTestKotlinAndroid")
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[0],
                copyTask,
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[1],
                copyTask,
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[2],
                copyTask,
            )
        }

        verify(atLeast = 2) { compileTask.mustRunAfter(copyTask) }

        verify(exactly = 2) { kspTask.mustRunAfter(copyTask) }
    }

    @Test
    fun `Given configure is called it configures sets up the Entrypoint for the Factory method`() {
        mockkObject(FactoryGenerator)

        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "jvm" to "kspTestKotlinJvm",
        )

        val extensions: ExtensionContainer = mockk()
        val path: String = fixture.fixture()

        val kMockExtension: KMockExtension = mockk()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
        )

        val delegatedProject: Project = mockk()
        val delegatedCopyTask: Copy = mockk()
        val kmockExtension: KMockExtension = mockk()
        val targetFile: File = mockk()
        val factoryGenerator = slot<Action<Copy>>()
        val rootPackage: String = fixture.fixture()

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].targetPlatform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.add(any<String>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        every { project.extensions } returns extensions
        every { extensions.getByType(KMockExtension::class.java) } returns kMockExtension

        every { kMockExtension.sharedSources.orNull } returns mapOf<String, String>()

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask

        every { compileTask.dependsOn(any(), any()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(capture(factoryGenerator) as Action<in Task>) } returns mockk()

        every { delegatedCopyTask.project } returns delegatedProject

        every { delegatedProject.file(any<String>()) } returns targetFile
        every { delegatedProject.extensions.getByType(KMockExtension::class.java) } returns kmockExtension
        every { delegatedProject.buildDir.absolutePath } returns path

        every { kmockExtension.rootPackage } returns rootPackage

        every { FactoryGenerator.generate(any(), any()) } just Runs

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(project, sources)

        factoryGenerator.captured.execute(delegatedCopyTask)

        // Then
        verify(exactly = 1) { delegatedProject.file("$path/generated/ksp/common/commonTest/kotlin") }
        verify(exactly = 1) { FactoryGenerator.generate(targetFile, rootPackage) }

        unmockkObject(FactoryGenerator)
    }
}
