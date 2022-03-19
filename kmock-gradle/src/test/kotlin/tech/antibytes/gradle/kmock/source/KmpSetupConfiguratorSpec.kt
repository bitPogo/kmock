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

        val copyTasks: List<Copy> = listOf(
            mockk(),
            mockk()
        )
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
        )
        val compileTasks: List<Task> = listOf(
            mockk(),
            mockk()
        )

        every { copyTasks[0].dependsOn(any()) } returns copyTasks[0]
        every { copyTasks[0].mustRunAfter(any()) } returns copyTasks[0]

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].dependsOn(*anyVararg()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(*anyVararg()) } returns cleanUpTasks[0]

        every { copyTasks[1].dependsOn(any()) } returns copyTasks[1]
        every { copyTasks[1].mustRunAfter(any()) } returns copyTasks[1]

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

        every { project.tasks.create(any(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every {
            SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any())
        } returnsMany copyTasks

        every { project.tasks.getByName(any()) } returnsMany compileTasks

        every { compileTasks[0].dependsOn(any()) } returns compileTasks[0]
        every { compileTasks[0].dependsOn(*anyVararg()) } returns compileTasks[0]
        every { compileTasks[0].mustRunAfter(any()) } returns compileTasks[0]
        every { compileTasks[0].mustRunAfter(*anyVararg()) } returns compileTasks[0]

        every { compileTasks[1].dependsOn(any()) } returns compileTasks[1]
        every { compileTasks[1].dependsOn(*anyVararg()) } returns compileTasks[1]
        every { compileTasks[1].mustRunAfter(any()) } returns compileTasks[1]
        every { compileTasks[1].mustRunAfter(*anyVararg()) } returns compileTasks[1]

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

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "js",
                "jsTest",
                "commonTest",
                "COMMONTEST",
            )
        }
        verify(exactly = 1) { copyTasks[0].dependsOn("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTasks[0].mustRunAfter("kspTestKotlinJvm") }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJvmTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) { cleanUpTasks[0].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[0].platform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.addAll(listOf("COMMONTEST")) }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(copyTasks[0]) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(copyTasks[0]) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources for jvm" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { copyTasks[1].dependsOn("kspTestKotlinJs") }
        verify(exactly = 1) { copyTasks[1].mustRunAfter("kspTestKotlinJs") }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJsTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) { cleanUpTasks[1].target.set("jsTest") }
        verify(exactly = 1) { cleanUpTasks[1].platform.set("js") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.addAll(listOf("COMMONTEST")) }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(copyTasks[1]) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(copyTasks[1]) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter("kspTestKotlinJs") }
        verify(exactly = 1) { cleanUpTasks[1].description = "Removes Contradicting Sources for js" }
        verify(exactly = 1) { cleanUpTasks[1].group = "Code Generation" }

        verify(atLeast = 1) {
            project.tasks.getByName("compileTestKotlinJvm")
        }

        verify(atLeast = 1) {
            project.tasks.getByName("compileTestKotlinJs")
        }

        verify(atLeast = 1) {
            compileTasks[0].dependsOn(
                cleanUpTasks[0],
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].mustRunAfter(
                cleanUpTasks[0],
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].dependsOn(
                *arrayOf(copyTasks[0]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].mustRunAfter(
                *arrayOf(copyTasks[0]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].dependsOn(
                cleanUpTasks[1]
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].mustRunAfter(
                cleanUpTasks[1],
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].dependsOn(
                *arrayOf(copyTasks[1]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].mustRunAfter(
                *arrayOf(copyTasks[1]),
            )
        }
    }

    @Test
    fun `Given wireSharedSourceTasks is called it wires Android Sources`() {
        // Given
        val project: Project = mockk()
        val sources = mapOf(
            "jvm" to "kspTestKotlinJvm",
            "android" to "kspTestKotlinAndroid"
        )

        val path: String = fixture.fixture()

        val copyTasks: List<Copy> = listOf(
            mockk(),
            mockk(),
            mockk(),
        )
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
            mockk()
        )
        val compileTasks: List<Task> = listOf(
            mockk(),
            mockk(),
            mockk()
        )

        every { copyTasks[0].dependsOn(any()) } returns copyTasks[0]
        every { copyTasks[0].mustRunAfter(any()) } returns copyTasks[0]
        every { copyTasks[1].dependsOn(any()) } returns copyTasks[1]
        every { copyTasks[1].mustRunAfter(any()) } returns copyTasks[1]
        every { copyTasks[2].dependsOn(any()) } returns copyTasks[2]
        every { copyTasks[2].mustRunAfter(any()) } returns copyTasks[2]

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

        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        every { project.tasks.create(any(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every {
            SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any())
        } returnsMany copyTasks

        every { project.tasks.getByName(any()) } returnsMany compileTasks

        every { compileTasks[0].dependsOn(any()) } returns compileTasks[0]
        every { compileTasks[0].dependsOn(*anyVararg()) } returns compileTasks[0]
        every { compileTasks[0].mustRunAfter(any()) } returns compileTasks[0]
        every { compileTasks[0].mustRunAfter(*anyVararg()) } returns compileTasks[0]

        every { compileTasks[1].dependsOn(any()) } returns compileTasks[1]
        every { compileTasks[1].dependsOn(*anyVararg()) } returns compileTasks[1]
        every { compileTasks[1].mustRunAfter(any()) } returns compileTasks[1]
        every { compileTasks[1].mustRunAfter(*anyVararg()) } returns compileTasks[1]

        every { compileTasks[2].dependsOn(any()) } returns compileTasks[2]
        every { compileTasks[2].dependsOn(*anyVararg()) } returns compileTasks[2]
        every { compileTasks[2].mustRunAfter(any()) } returns compileTasks[2]
        every { compileTasks[2].mustRunAfter(*anyVararg()) } returns compileTasks[2]

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("jvm", "android"),
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

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "androidDebugUnit",
                "androidDebugUnitTest",
                "commonTest",
                "COMMONTEST",
            )
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "androidReleaseUnit",
                "androidReleaseUnitTest",
                "commonTest",
                "COMMONTEST",
            )
        }

        verify(exactly = 1) { copyTasks[0].dependsOn("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTasks[0].mustRunAfter("kspTestKotlinJvm") }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJvmTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) { cleanUpTasks[0].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[0].platform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.addAll(listOf("COMMONTEST")) }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(*arrayOf(copyTasks[0])) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(*arrayOf(copyTasks[0])) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources for jvm" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { copyTasks[1].dependsOn("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { copyTasks[1].mustRunAfter("kspDebugUnitTestKotlinAndroid") }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesAndroidDebugUnitTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) { cleanUpTasks[1].target.set("androidDebugUnitTest") }
        verify(exactly = 1) { cleanUpTasks[1].platform.set("androidDebugUnit") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.addAll(listOf("COMMONTEST")) }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(*arrayOf(copyTasks[1])) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(*arrayOf(copyTasks[1])) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[1].description = "Removes Contradicting Sources for androidDebugUnit" }
        verify(exactly = 1) { cleanUpTasks[1].group = "Code Generation" }

        verify(exactly = 1) { copyTasks[2].dependsOn("kspReleaseUnitTestKotlinAndroid") }
        verify(exactly = 1) { copyTasks[2].mustRunAfter("kspReleaseUnitTestKotlinAndroid") }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesAndroidReleaseUnitTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) { cleanUpTasks[2].target.set("androidReleaseUnitTest") }
        verify(exactly = 1) { cleanUpTasks[2].platform.set("androidReleaseUnit") }
        verify(exactly = 1) { cleanUpTasks[2].indicators.addAll(listOf("COMMONTEST")) }
        verify(exactly = 1) { cleanUpTasks[2].dependsOn(*arrayOf(copyTasks[2])) }
        verify(atLeast = 1) { cleanUpTasks[2].mustRunAfter(*arrayOf(copyTasks[2])) }
        verify(atLeast = 1) { cleanUpTasks[2].mustRunAfter("kspReleaseUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[2].description = "Removes Contradicting Sources for androidReleaseUnit" }
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
            compileTasks[0].dependsOn(
                cleanUpTasks[0],
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].mustRunAfter(
                cleanUpTasks[0],
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].dependsOn(
                *arrayOf(copyTasks[0]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].mustRunAfter(
                *arrayOf(copyTasks[0]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].dependsOn(
                cleanUpTasks[1]
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].mustRunAfter(
                cleanUpTasks[1],
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].dependsOn(
                *arrayOf(copyTasks[1]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].mustRunAfter(
                *arrayOf(copyTasks[1]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[2].dependsOn(
                cleanUpTasks[2]
            )
        }

        verify(atLeast = 1) {
            compileTasks[2].mustRunAfter(
                cleanUpTasks[2],
            )
        }

        verify(atLeast = 1) {
            compileTasks[2].dependsOn(
                *arrayOf(copyTasks[2]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[2].mustRunAfter(
                *arrayOf(copyTasks[2]),
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

        val copyTasks: List<Copy> = listOf(
            mockk(),
            mockk(),
            mockk(),
            mockk()
        )
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
        )
        val compileTasks: List<Task> = listOf(
            mockk(),
            mockk()
        )

        every { copyTasks[0].dependsOn(any()) } returns copyTasks[0]
        every { copyTasks[0].mustRunAfter(any()) } returns copyTasks[0]
        every { copyTasks[1].dependsOn(any()) } returns copyTasks[1]
        every { copyTasks[1].mustRunAfter(any()) } returns copyTasks[1]

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].platform.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicators.addAll(any<Iterable<String>>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].dependsOn(*anyVararg()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(*anyVararg()) } returns cleanUpTasks[0]

        every { copyTasks[2].dependsOn(any()) } returns copyTasks[2]
        every { copyTasks[2].mustRunAfter(any()) } returns copyTasks[2]
        every { copyTasks[3].dependsOn(any()) } returns copyTasks[3]
        every { copyTasks[3].mustRunAfter(any()) } returns copyTasks[3]

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

        every { project.tasks.create(any(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every {
            SharedSourceCopist.copySharedSource(any(), any(), any(), any(), any())
        } returnsMany copyTasks

        every { project.tasks.getByName(any()) } returnsMany compileTasks

        every { compileTasks[0].dependsOn(any()) } returns compileTasks[0]
        every { compileTasks[0].dependsOn(*anyVararg()) } returns compileTasks[0]
        every { compileTasks[0].mustRunAfter(any()) } returns compileTasks[0]
        every { compileTasks[0].mustRunAfter(*anyVararg()) } returns compileTasks[0]

        every { compileTasks[1].dependsOn(any()) } returns compileTasks[1]
        every { compileTasks[1].dependsOn(*anyVararg()) } returns compileTasks[1]
        every { compileTasks[1].mustRunAfter(any()) } returns compileTasks[1]
        every { compileTasks[1].mustRunAfter(*anyVararg()) } returns compileTasks[1]

        // When
        KmpSetupConfigurator.wireSharedSourceTasks(
            project,
            sources,
            mapOf(
                "commonTest" to setOf("jvm", "js"),
                "otherTest" to setOf("jvm", "js")
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

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "js",
                "jsTest",
                "commonTest",
                "COMMONTEST",
            )
        }
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "jvm",
                "jvmTest",
                "otherTest",
                "OTHERTEST",
            )
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(
                project,
                "js",
                "jsTest",
                "otherTest",
                "OTHERTEST",
            )
        }

        verify(exactly = 1) { copyTasks[0].dependsOn("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTasks[0].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTasks[2].dependsOn("kspTestKotlinJvm") }
        verify(exactly = 1) { copyTasks[2].mustRunAfter("kspTestKotlinJvm") }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJvmTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) { cleanUpTasks[0].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[0].platform.set("jvm") }
        verify(exactly = 1) { cleanUpTasks[0].indicators.addAll(listOf("COMMONTEST", "OTHERTEST")) }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(*arrayOf(copyTasks[0], copyTasks[2])) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(*arrayOf(copyTasks[0], copyTasks[2])) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources for jvm" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { copyTasks[1].dependsOn("kspTestKotlinJs") }
        verify(exactly = 1) { copyTasks[1].mustRunAfter("kspTestKotlinJs") }
        verify(exactly = 1) { copyTasks[3].dependsOn("kspTestKotlinJs") }
        verify(exactly = 1) { copyTasks[3].mustRunAfter("kspTestKotlinJs") }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJsTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) { cleanUpTasks[1].target.set("jsTest") }
        verify(exactly = 1) { cleanUpTasks[1].platform.set("js") }
        verify(exactly = 1) { cleanUpTasks[1].indicators.addAll(listOf("COMMONTEST", "OTHERTEST")) }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(*arrayOf(copyTasks[1], copyTasks[3])) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(*arrayOf(copyTasks[1], copyTasks[3])) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter("kspTestKotlinJs") }
        verify(exactly = 1) { cleanUpTasks[1].description = "Removes Contradicting Sources for js" }
        verify(exactly = 1) { cleanUpTasks[1].group = "Code Generation" }

        verify(atLeast = 1) {
            project.tasks.getByName("compileTestKotlinJvm")
        }

        verify(atLeast = 1) {
            project.tasks.getByName("compileTestKotlinJs")
        }

        verify(atLeast = 1) {
            compileTasks[0].dependsOn(
                cleanUpTasks[0],
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].mustRunAfter(
                cleanUpTasks[0],
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].dependsOn(
                *arrayOf(copyTasks[0], copyTasks[2]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[0].mustRunAfter(
                *arrayOf(copyTasks[0], copyTasks[2]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].dependsOn(
                cleanUpTasks[1]
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].mustRunAfter(
                cleanUpTasks[1],
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].dependsOn(
                *arrayOf(copyTasks[1], copyTasks[3]),
            )
        }

        verify(atLeast = 1) {
            compileTasks[1].mustRunAfter(
                *arrayOf(copyTasks[1], copyTasks[3]),
            )
        }
    }
}
