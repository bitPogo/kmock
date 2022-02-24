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
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.KMockCleanTask
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.SharedSourceCopist
import tech.antibytes.gradle.kmock.config.MainConfig
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils

class KMPSourceSetsConfiguratorSpec {
    private val fixture = kotlinFixture()

    @BeforeEach
    fun setUp() {
        mockkObject(MainConfig)
        mockkObject(SharedSourceCopist)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(MainConfig)
        unmockkObject(SharedSourceCopist)
    }

    @Test
    fun `It fulfils SourceSetConfigurator`() {
        KMPSourceSetsConfigurator fulfils KMockPluginContract.SourceSetConfigurator::class
    }

    @Test
    fun `Given configure is called it ignores non Test Sources`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()
        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()

        every { source1.name } returns "main"
        every { source2.name } returns "nativeMain"

        val sourceSets = mutableListOf(
            source1,
            source2
        )

        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path

        invokeGradleAction(
            { probe -> extensions.configure<KotlinMultiplatformExtension>("kotlin", probe) },
            kotlin
        )

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        // When
        KMPSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 0) { dependencies.add(any(), any()) }
    }

    @Test
    fun `Given configure is called it ignores SharedTest Sources, while extending there source dirs anyways`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()
        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()
        val version: String = fixture.fixture()

        every { source1.name } returns "nativeTest"
        every { source2.name } returns "appleTest"

        val sourceSets = mutableListOf(
            source1,
            source2
        )

        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(
            { probe -> extensions.configure<KotlinMultiplatformExtension>("kotlin", probe) },
            kotlin
        )

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()
        every { MainConfig.version } returns version

        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source2.kotlin.srcDir(any()) } returns mockk()

        every { dependencies.add(any(), any()) } throws RuntimeException()

        // When
        KMPSourceSetsConfigurator.configure(project)

        // Then
        verify(atLeast = 1) {
            dependencies.add(
                "kspNativeTest",
                "tech.antibytes.kmock:kmock-processor:$version"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspAppleTest",
                "tech.antibytes.kmock:kmock-processor:$version"
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/native/nativeTest")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/apple/appleTest")
        }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()
        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()
        val version: String = fixture.fixture()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
        )

        val sourceSets = mutableListOf(
            source1,
            source2
        )

        every { source1.name } returns "jvmTest"
        every { source2.name } returns "jsTest"

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicator.set(any<String>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]

        every { cleanUpTasks[1].group = any() } just Runs
        every { cleanUpTasks[1].description = any() } just Runs
        every { cleanUpTasks[1].target.set(any<String>()) } just Runs
        every { cleanUpTasks[1].indicator.set(any<String>()) } just Runs
        every { cleanUpTasks[1].dependsOn(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(any()) } returns cleanUpTasks[1]

        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(
            { probe -> extensions.configure<KotlinMultiplatformExtension>("kotlin", probe) },
            kotlin
        )

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()
        every { MainConfig.version } returns version

        every { dependencies.add(any(), any()) } returns mockk()

        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source2.kotlin.srcDir(any()) } returns mockk()

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJs") } returns kspTask

        every { compileTask.dependsOn(any(), any()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        // When
        KMPSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            dependencies.add(
                "kspJvmTest",
                "tech.antibytes.kmock:kmock-processor:$version"
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspJsTest",
                "tech.antibytes.kmock:kmock-processor:$version"
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/js/jsTest")
        }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJvmTest", KMockCleanTask::class.java)
        }

        verify(atLeast = 1) {
            project.tasks.create("cleanDuplicatesJsTest", KMockCleanTask::class.java)
        }

        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, any(), "commonTest", "COMMON SOURCE")
        }

        verify(exactly = 1) { cleanUpTasks[0].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[0].indicator.set("COMMON SOURCE") }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspTestKotlinJvm") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[1].target.set("jsTest") }
        verify(exactly = 1) { cleanUpTasks[1].indicator.set("COMMON SOURCE") }
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
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()
        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()
        val version: String = fixture.fixture()

        val copyTask: Copy = mockk(relaxed = true)
        val compileTask: Task = mockk(relaxed = true)
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
        )

        val sourceSets = mutableListOf(
            source1,
            source2
        )

        every { source1.name } returns "jvmTest"
        every { source2.name } returns "jsTest"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(
            { probe -> extensions.configure<KotlinMultiplatformExtension>("kotlin", probe) },
            kotlin
        )

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()
        every { MainConfig.version } returns version

        every { dependencies.add(any(), any()) } returns mockk()

        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source2.kotlin.srcDir(any()) } returns mockk()

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask

        // When
        KMPSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, "jvmTest", "commonTest", "COMMON SOURCE")
        }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources, it selects Js over Any other source`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()
        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()
        val version: String = fixture.fixture()

        val copyTask: Copy = mockk(relaxed = true)
        val compileTask: Task = mockk(relaxed = true)
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
        )

        val sourceSets = mutableListOf(
            source1,
            source2
        )

        every { source1.name } returns "nativeTest"
        every { source2.name } returns "jsTest"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(
            { probe -> extensions.configure<KotlinMultiplatformExtension>("kotlin", probe) },
            kotlin
        )

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()
        every { MainConfig.version } returns version

        every { dependencies.add(any(), any()) } returns mockk()

        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source2.kotlin.srcDir(any()) } returns mockk()

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask

        // When
        KMPSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, "jsTest", "commonTest", "COMMON SOURCE")
        }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources it selects Any other source if no precedence matches`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()
        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()
        val version: String = fixture.fixture()

        val copyTask: Copy = mockk(relaxed = true)
        val compileTask: Task = mockk(relaxed = true)
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(relaxed = true),
            mockk(relaxed = true),
        )

        val sourceSets = mutableListOf(
            source1,
            source2
        )

        every { source1.name } returns "native1Test"
        every { source2.name } returns "native2Test"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(
            { probe -> extensions.configure<KotlinMultiplatformExtension>("kotlin", probe) },
            kotlin
        )

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()
        every { MainConfig.version } returns version

        every { dependencies.add(any(), any()) } returns mockk()

        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source2.kotlin.srcDir(any()) } returns mockk()

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask

        // When
        KMPSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, "native1Test", "commonTest", "COMMON SOURCE")
        }
    }

    @Test
    fun `Given configure is called it configures AndroidSources and uses them over Jvm`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()
        val jvm: KotlinSourceSet = mockk()
        val androidTest: KotlinSourceSet = mockk()
        val version: String = fixture.fixture()

        val copyTask: Copy = mockk()
        val compileTask: Task = mockk()
        val kspTask: Task = mockk()
        val cleanUpTasks: List<KMockCleanTask> = listOf(
            mockk(),
            mockk(),
            mockk(),
        )

        val sourceSets = mutableListOf(
            jvm,
            androidTest,
        )

        every { jvm.name } returns "jvmTest"
        every { androidTest.name } returns "androidTest"

        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns true

        invokeGradleAction(
            { probe -> extensions.configure<KotlinMultiplatformExtension>("kotlin", probe) },
            kotlin
        )

        invokeGradleAction(
            { probe -> project.afterEvaluate(probe) },
            project
        )

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()
        every { MainConfig.version } returns version

        every { cleanUpTasks[0].group = any() } just Runs
        every { cleanUpTasks[0].description = any() } just Runs
        every { cleanUpTasks[0].target.set(any<String>()) } just Runs
        every { cleanUpTasks[0].indicator.set(any<String>()) } just Runs
        every { cleanUpTasks[0].dependsOn(any()) } returns cleanUpTasks[0]
        every { cleanUpTasks[0].mustRunAfter(any()) } returns cleanUpTasks[0]

        every { cleanUpTasks[1].group = any() } just Runs
        every { cleanUpTasks[1].description = any() } just Runs
        every { cleanUpTasks[1].target.set(any<String>()) } just Runs
        every { cleanUpTasks[1].indicator.set(any<String>()) } just Runs
        every { cleanUpTasks[1].dependsOn(any()) } returns cleanUpTasks[1]
        every { cleanUpTasks[1].mustRunAfter(any()) } returns cleanUpTasks[1]

        every { cleanUpTasks[2].group = any() } just Runs
        every { cleanUpTasks[2].description = any() } just Runs
        every { cleanUpTasks[2].target.set(any<String>()) } just Runs
        every { cleanUpTasks[2].indicator.set(any<String>()) } just Runs
        every { cleanUpTasks[2].dependsOn(any()) } returns cleanUpTasks[2]
        every { cleanUpTasks[2].mustRunAfter(any()) } returns cleanUpTasks[2]

        every { dependencies.add(any(), any()) } returns mockk()

        every { jvm.kotlin.srcDir(any()) } returns mockk()
        every { androidTest.kotlin.srcDir(any()) } returns mockk()

        every { project.tasks.create(any<String>(), KMockCleanTask::class.java) } returnsMany cleanUpTasks

        every { SharedSourceCopist.copySharedSource(any(), any(), any(), any()) } returns copyTask

        every { project.tasks.getByName(any<String>()) } returns compileTask
        every { project.tasks.getByName("kspTestKotlinJvm") } returns kspTask
        every { project.tasks.getByName("kspReleaseUnitTestKotlinAndroid") } returns kspTask

        every { compileTask.dependsOn(any(), any()) } returns compileTask
        every { compileTask.mustRunAfter(any()) } returns compileTask

        every { copyTask.dependsOn(any<String>()) } returns copyTask
        every { copyTask.mustRunAfter(any<String>()) } returns copyTask

        every { kspTask.mustRunAfter(any<String>()) } returns kspTask

        // When
        KMPSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            SharedSourceCopist.copySharedSource(project, "androidDebugUnitTest", "commonTest", "COMMON SOURCE")
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspJvmTest",
                "tech.antibytes.kmock:kmock-processor:$version"
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspAndroidTest",
                "tech.antibytes.kmock:kmock-processor:$version"
            )
        }

        verify(exactly = 1) {
            jvm.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest")
        }

        verify(exactly = 1) {
            androidTest.kotlin.srcDir("$path/generated/ksp/android/androidTest")
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
                "androidDebugUnitTest",
                "commonTest",
                "COMMON SOURCE"
            )
        }

        verify(exactly = 1) { copyTask.dependsOn("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { copyTask.mustRunAfter("kspDebugUnitTestKotlinAndroid") }

        verify(exactly = 1) { cleanUpTasks[0].target.set("androidDebugUnitTest") }
        verify(exactly = 1) { cleanUpTasks[0].indicator.set("COMMON SOURCE") }
        verify(exactly = 1) { cleanUpTasks[0].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[0].mustRunAfter("kspDebugUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[0].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[0].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[1].target.set("androidReleaseUnitTest") }
        verify(exactly = 1) { cleanUpTasks[1].indicator.set("COMMON SOURCE") }
        verify(exactly = 1) { cleanUpTasks[1].dependsOn(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter(copyTask) }
        verify(atLeast = 1) { cleanUpTasks[1].mustRunAfter("kspReleaseUnitTestKotlinAndroid") }
        verify(exactly = 1) { cleanUpTasks[1].description = "Removes Contradicting Sources" }
        verify(exactly = 1) { cleanUpTasks[1].group = "Code Generation" }

        verify(exactly = 1) { cleanUpTasks[2].target.set("jvmTest") }
        verify(exactly = 1) { cleanUpTasks[2].indicator.set("COMMON SOURCE") }
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
}
