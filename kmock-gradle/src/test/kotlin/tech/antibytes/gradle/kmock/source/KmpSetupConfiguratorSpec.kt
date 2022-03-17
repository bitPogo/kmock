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
import io.mockk.unmockkObject
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.getByName
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.KMockCleanTask
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.SharedSourceCopist
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils

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

        val path: String = fixture.fixture()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
        )

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].dependsOn(*anyVararg()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(*anyVararg()) } returns cleanUpTasks[0]

        every { cleanUpTasks[1].group = any() } just Runs
        every { cleanUpTasks[1].description = any() } just Runs
        every { cleanUpTasks[1].target.set(any<String>()) } just Runs
        every { cleanUpTasks[1].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[1].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[1].dependsOn(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].dependsOn(*anyVararg()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(*anyVararg()) } returns cleanUpTasks[1]

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any()) } returns compileTask
        every { compileTask.dependsOn(*anyVararg()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask
        every { compileTask.mustRunAfter(*anyVararg()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("jvm", "js")
            )
        )

        // Then
        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJvmTest", KMockCleanTask::class.java)
        }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJsTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                any(),
                any(),
                "commonTest",
                "COMMONTEST",
            )
        }

        verify(exactly = 1) { copyTask.dependsOn("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTask.mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTask.mustRunAfter("kspTestKotlinJs") }

        verify(exactly = 1) { cleanUpTasks[0].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[0].platform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.addAll(listOf("COMMONTEST")) }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[1].target.set("jsTest") }
        verify(exactly = 1) { cleanUpTasks[1].platform.set("js") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.addAll(listOf("COMMONTEST")) }
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
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                *arrayOf(copyTask),
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[1]
            )
        }

        verify(atLeast = 1) {
            compileTask.mustRunAfter(copyTask)
        }
    }

    @Test
    fun `Given configure is called it filters unrecognized SharedSources`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "jvm" to "kspTestKotlinJvm",
            "js" to "kspTestKotlinJs"
        )

        val path: String = fixture.fixture()

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

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any()) } returns compileTask
        every { compileTask.dependsOn(*anyVararg()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask
        every { compileTask.mustRunAfter(*anyVararg()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("jvm", "js"),
                "otherTest" to setOf("jvm", "js"),
            )
        )

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "jvm",
                "jvmTest",
                "commonTest",
                "COMMONTEST",
            )
        }
    }

    @Test
    fun `Given wireSharedSourceTasks is allows arbitrary SharedSources`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "jvm" to "kspTestKotlinJvm",
            "js" to "kspTestKotlinJs"
        )

        val path: String = fixture.fixture()

        val copyTask0: Copy = mockk()
        val copyTask1: Copy = mockk()

        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
        )

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].dependsOn(*anyVararg()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(*anyVararg()) } returns cleanUpTasks[0]

        every { cleanUpTasks[1].group = any() } just Runs
        every { cleanUpTasks[1].description = any() } just Runs
        every { cleanUpTasks[1].target.set(any<String>()) } just Runs
        every { cleanUpTasks[1].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[1].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[1].dependsOn(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].dependsOn(*anyVararg()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(*anyVararg()) } returns cleanUpTasks[1]

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), "commonTest", any()) } returns copyTask0
        every { SharedSourceCopist.copySharedSource(any(), any(), any(), "otherTest", any()) } returns copyTask1

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any()) } returns compileTask
        every { compileTask.dependsOn(*anyVararg()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask
        every { compileTask.mustRunAfter(*anyVararg()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask0.dependsOn(any()) } returns copyTask0
        every { copyTask0.mustRunAfter(any()) } returns mockk()
        every { copyTask0.doLast(any<Action<in Task>>()) } returns mockk()

        every { copyTask1.dependsOn(any()) } returns copyTask1
        every { copyTask1.mustRunAfter(any()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("jvm", "js"),
                "otherTest" to setOf("jvm"),
            )
        )

        // Then
        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJvmTest", KMockCleanTask::class.java)
        }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJsTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                any(),
                any(),
                "commonTest",
                "COMMONTEST",
            )
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                any(),
                any(),
                "otherTest",
                "OTHERTEST",
            )
        }

        verify(exactly = 1) { copyTask0.dependsOn("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTask0.mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTask0.mustRunAfter("kspTestKotlinJs") }

        verify(exactly = 1) { copyTask1.dependsOn("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTask1.mustRunAfter("kspTestKotlinJvm") }

        verify(exactly = 1) { cleanUpTasks[0].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[0].platform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.addAll(listOf("COMMONTEST", "OTHERTEST")) }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(*arrayOf(copyTask0, copyTask1)) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(*arrayOf(copyTask0, copyTask1)) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[1].target.set("jsTest") }
        verify(exactly = 1) { cleanUpTasks[1].platform.set("js") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.addAll(listOf("COMMONTEST", "OTHERTEST")) }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(*arrayOf(copyTask0)) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(*arrayOf(copyTask0)) }
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
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                *arrayOf(copyTask0, copyTask1),
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[1]
            )
        }

        verify(atLeast = 1) {
            compileTask.mustRunAfter(*arrayOf(copyTask0))
        }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources, it selects Jvm over Js`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "jvm" to "kspTestKotlinJvm",
            "js" to "kspTestKotlinJs"
        )

        val path: String = fixture.fixture()

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

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any()) } returns compileTask
        every { compileTask.dependsOn(*anyVararg()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask
        every { compileTask.mustRunAfter(*anyVararg()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("jvm", "js")
            )
        )

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "jvm",
                "jvmTest",
                "commonTest",
                "COMMONTEST",
            )
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

        val path: String = fixture.fixture()

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

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any()) } returns compileTask
        every { compileTask.dependsOn(*anyVararg()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask
        every { compileTask.mustRunAfter(*anyVararg()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("native", "js")
            )
        )

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "js",
                "jsTest",
                "commonTest",
                "COMMONTEST",
            )
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

        val path: String = fixture.fixture()

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

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any()) } returns compileTask
        every { compileTask.dependsOn(*anyVararg()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask
        every { compileTask.mustRunAfter(*anyVararg()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.dependsOn(any()) } returns copyTask
        every { copyTask.mustRunAfter(any()) } returns mockk()
        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("native1", "native2")
            )
        )

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "native1",
                "native1Test",
                "commonTest",
                "COMMONTEST",
            )
        }

        verify(exactly = 1) { copyTask.dependsOn("kspTestKotlinNative1") }
    }

    @Test
    fun `Given configure is called it configures AndroidSources and uses them over Jvm`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "android" to "kspTestKotlinAndroid",
            "jvm" to "kspTestKotlinJvm",
        )

        val path: String = fixture.fixture()

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

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].dependsOn(*anyVararg()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(*anyVararg()) } returns cleanUpTasks[0]

        every { cleanUpTasks[1].group = any() } just Runs
        every { cleanUpTasks[1].description = any() } just Runs
        every { cleanUpTasks[1].target.set(any<String>()) } just Runs
        every { cleanUpTasks[1].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[1].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[1].dependsOn(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].dependsOn(*anyVararg()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(*anyVararg()) } returns cleanUpTasks[1]

        every { cleanUpTasks[2].group = any() } just Runs
        every { cleanUpTasks[2].description = any() } just Runs
        every { cleanUpTasks[2].target.set(any<String>()) } just Runs
        every { cleanUpTasks[2].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[2].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[2].dependsOn(any()) } returns cleanUpTasks[2]
        every { cleanUpTasks[2].dependsOn(*anyVararg()) } returns cleanUpTasks[2]
        every { cleanUpTasks[2].mustRunAfter(any()) } returns cleanUpTasks[2]
        every { cleanUpTasks[2].mustRunAfter(*anyVararg()) } returns cleanUpTasks[2]

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJvm") } returns kspTask
        every { project.tasks.getByName("kspReleaseUnitTestKotlinAndroid") } returns kspTask

        every { compileTask.dependsOn(any()) } returns compileTask
        every { compileTask.dependsOn(*anyVararg()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask
        every { compileTask.mustRunAfter(*anyVararg()) } returns compileTask

        every { copyTask.dependsOn(any<String>()) } returns copyTask
        every { copyTask.mustRunAfter(any<String>()) } returns copyTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        every { copyTask.doLast(any<Action<in Task>>()) } returns mockk()

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("jvm", "android")
            )
        )

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "android",
                "androidDebugUnitTest",
                "commonTest",
                "COMMONTEST",
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
                "COMMONTEST",
            )
        }

        verify(exactly = 1) { copyTask.dependsOn("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { copyTask.mustRunAfter("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { copyTask.mustRunAfter("kspTestKotlinJvm") }

        verify(exactly = 1) { cleanUpTasks[0].target.set("androidDebugUnitTest") }
        verify(exactly = 1) { cleanUpTasks[0].platform.set("android") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.addAll(listOf("COMMONTEST")) }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[1].target.set("androidReleaseUnitTest") }
        verify(exactly = 1) { cleanUpTasks[1].platform.set("android") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.addAll(listOf("COMMONTEST")) }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter("kspReleaseUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[1].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[1].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[2].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[2].platform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[2].indicators.addAll(listOf("COMMONTEST")) }
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
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[1],
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[2],
            )
        }

        verify(atLeast = 1) { compileTask.mustRunAfter(*arrayOf(copyTask)) }
    }

    @Test
    fun `Given configure is called it allows Arbitrary Sources for Android`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "android" to "kspTestKotlinAndroid",
            "jvm" to "kspTestKotlinJvm",
        )

        val path: String = fixture.fixture()

        val copyTask0: Copy = mockk()
        val copyTask1: Copy = mockk()

        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
            mockk(),
        )

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns true

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].dependsOn(*anyVararg()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(*anyVararg()) } returns cleanUpTasks[0]

        every { cleanUpTasks[1].group = any() } just Runs
        every { cleanUpTasks[1].description = any() } just Runs
        every { cleanUpTasks[1].target.set(any<String>()) } just Runs
        every { cleanUpTasks[1].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[1].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[1].dependsOn(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].dependsOn(*anyVararg()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(*anyVararg()) } returns cleanUpTasks[1]

        every { cleanUpTasks[2].group = any() } just Runs
        every { cleanUpTasks[2].description = any() } just Runs
        every { cleanUpTasks[2].target.set(any<String>()) } just Runs
        every { cleanUpTasks[2].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[2].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[2].dependsOn(any()) } returns cleanUpTasks[2]
        every { cleanUpTasks[2].dependsOn(*anyVararg()) } returns cleanUpTasks[2]
        every { cleanUpTasks[2].mustRunAfter(any()) } returns cleanUpTasks[2]
        every { cleanUpTasks[2].mustRunAfter(*anyVararg()) } returns cleanUpTasks[2]

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), "commonTest", any()) } returns copyTask0
        every { SharedSourceCopist.copySharedSource(any(), any(), any(), "otherTest", any()) } returns copyTask1

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJvm") } returns kspTask
        every { project.tasks.getByName("kspReleaseUnitTestKotlinAndroid") } returns kspTask

        every { compileTask.dependsOn(any()) } returns compileTask
        every { compileTask.dependsOn(*anyVararg()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask
        every { compileTask.mustRunAfter(*anyVararg()) } returns compileTask

        every { copyTask0.dependsOn(any<String>()) } returns copyTask0
        every { copyTask0.mustRunAfter(any<String>()) } returns copyTask0
        every { copyTask0.doLast(any<Action<in Task>>()) } returns mockk()

        every { copyTask1.dependsOn(any<String>()) } returns copyTask1
        every { copyTask1.mustRunAfter(any<String>()) } returns copyTask1

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("jvm", "android"),
                "otherTest" to setOf("android")
            )
        )

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "android",
                "androidDebugUnitTest",
                "commonTest",
                "COMMONTEST",
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
                "COMMONTEST",
            )
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "android",
                "androidDebugUnitTest",
                "otherTest",
                "OTHERTEST",
            )
        }

        verify(exactly = 1) { copyTask0.dependsOn("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { copyTask0.mustRunAfter("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { copyTask0.mustRunAfter("kspTestKotlinJvm") }

        verify(exactly = 1) { cleanUpTasks[0].target.set("androidDebugUnitTest") }
        verify(exactly = 1) { cleanUpTasks[0].platform.set("android") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.addAll(listOf("COMMONTEST", "OTHERTEST")) }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(*arrayOf(copyTask0, copyTask1)) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(*arrayOf(copyTask0, copyTask1)) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[1].target.set("androidReleaseUnitTest") }
        verify(exactly = 1) { cleanUpTasks[1].platform.set("android") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.addAll(listOf("COMMONTEST", "OTHERTEST")) }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(*arrayOf(copyTask0, copyTask1)) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(*arrayOf(copyTask0, copyTask1)) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter("kspReleaseUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[1].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[1].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[2].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[2].platform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[2].indicators.addAll(listOf("COMMONTEST", "OTHERTEST")) }
        verify(exactly = 1) { cleanUpTasks[2].dependsOn(copyTask0) }
        verify(atLeast = 1) { cleanUpTasks[2].mustRunAfter(copyTask0) }
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
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[1],
            )
        }

        verify(atLeast = 1) {
            compileTask.dependsOn(
                cleanUpTasks[2],
            )
        }

        verify(atLeast = 1) { compileTask.mustRunAfter(*arrayOf(copyTask0)) }
    }
}
