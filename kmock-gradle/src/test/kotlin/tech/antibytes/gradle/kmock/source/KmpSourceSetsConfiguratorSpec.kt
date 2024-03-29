/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import com.google.devtools.ksp.gradle.KspExtension
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.config.MainConfig
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.util.test.fulfils

class KmpSourceSetsConfiguratorSpec {
    private val fixture = kotlinFixture()

    @BeforeEach
    fun setUp() {
        mockkObject(DependencyGraph)
        mockkObject(KmpCleanup)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(DependencyGraph)
        unmockkObject(KmpCleanup)
    }

    @Test
    fun `It fulfils SourceSetConfigurator`() {
        KmpSourceSetsConfigurator fulfils KMockPluginContract.SourceSetConfigurator::class
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
            source2,
        )

        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path

        invokeGradleAction(kotlin) { probe ->
            extensions.configure<KotlinMultiplatformExtension>("kotlin", probe)
        }

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 0) { dependencies.add(any(), any()) }
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

        val kspExtension: KspExtension = mockk()

        val source1: KotlinSourceSet = mockk()
        val source1Dependencies: KotlinSourceSet = mockk()
        val source1DependenciesName: String = fixture.fixture()

        val source2: KotlinSourceSet = mockk()
        val source2Dependencies: KotlinSourceSet = mockk()
        val source2DependenciesName: String = fixture.fixture()

        val sourceSets = mutableListOf(
            source1,
            source2,
        )

        val dependencyGraph = mapOf(
            "commonTest" to emptySet<String>(),
        )

        every { project.findProperty(any()) } returns "false"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(kotlin) { probe ->
            extensions.configure("kotlin", probe)
        }

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        every { dependencies.add(any(), any()) } returns mockk()

        every { source1.name } returns "jvmTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf(source1Dependencies)
        every { source1Dependencies.name } returns source1DependenciesName

        every { source2.name } returns "jsTest"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source2Dependencies)
        every { source2Dependencies.name } returns source2DependenciesName

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        every { DependencyGraph.resolveAncestors(any(), any()) } returns dependencyGraph
        every { KmpCleanup.cleanup(any(), any()) } just Runs

        every { project.tasks } returns mockk(relaxed = true)

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            dependencies.add(
                "kspJvmTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspJsTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest/kotlin")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/js/jsTest/kotlin")
        }

        verify(exactly = 1) {
            DependencyGraph.resolveAncestors(
                sourceDependencies = mapOf(
                    source1DependenciesName to setOf("jvm"),
                    source2DependenciesName to setOf("js"),
                ),
                metaDependencies = emptyMap(),
            )
        }

        verify(exactly = 1) {
            KmpCleanup.cleanup(
                project,
                listOf("jvm", "js"),
            )
        }

        verify(exactly = 0) { kspExtension.arg(any(), any()) }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources, which contain a Android Source v1`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()

        val kspExtension: KspExtension = mockk()

        val source1: KotlinSourceSet = mockk()
        val source1Dependencies: KotlinSourceSet = mockk()
        val source1DependenciesName: String = fixture.fixture()

        val source2: KotlinSourceSet = mockk()
        val source2Dependencies: KotlinSourceSet = mockk()
        val source2DependenciesName: String = fixture.fixture()

        val source3: KotlinSourceSet = mockk()
        val source3Dependencies: KotlinSourceSet = mockk()
        val source3DependenciesName: String = fixture.fixture()

        val source4: KotlinSourceSet = mockk()
        val source4Dependencies: KotlinSourceSet = mockk()
        val source4DependenciesName: String = fixture.fixture()

        val source5: KotlinSourceSet = mockk()
        val source5Dependencies: KotlinSourceSet = mockk()
        val source5DependenciesName: String = fixture.fixture()

        val sourceSets = mutableListOf(
            source1,
            source2,
            source3,
            source4,
            source5,
        )

        val dependencyGraph = mapOf(
            "commonTest" to emptySet<String>(),
        )

        every { project.findProperty(any()) } returns "false"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(kotlin) { probe ->
            extensions.configure("kotlin", probe)
        }

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        every { dependencies.add(any(), any()) } returns mockk()

        every { source1.name } returns "jvmTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf(source1Dependencies)
        every { source1Dependencies.name } returns source1DependenciesName

        every { source2.name } returns "androidTest"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source2Dependencies)
        every { source2Dependencies.name } returns source2DependenciesName

        every { source3.name } returns "androidAndroidTest"
        every { source3.kotlin.srcDir(any()) } returns mockk()
        every { source3.dependsOn } returns setOf(source3Dependencies)
        every { source3Dependencies.name } returns source3DependenciesName

        every { source4.name } returns "androidAndroidTestDebug"
        every { source4.kotlin.srcDir(any()) } returns mockk()
        every { source4.dependsOn } returns setOf(source4Dependencies)
        every { source4Dependencies.name } returns source4DependenciesName

        every { source5.name } returns "androidAndroidTestRelease"
        every { source5.kotlin.srcDir(any()) } returns mockk()
        every { source5.dependsOn } returns setOf(source5Dependencies)
        every { source5Dependencies.name } returns source5DependenciesName

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        every { DependencyGraph.resolveAncestors(any(), any()) } returns dependencyGraph
        every { KmpCleanup.cleanup(any(), any()) } just Runs

        every { project.tasks } returns mockk(relaxed = true)

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            dependencies.add(
                "kspJvmTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspAndroidTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspAndroidAndroidTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest/kotlin")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/android/androidTest/kotlin")
        }

        verify(exactly = 0) {
            source3.kotlin.srcDir(any())
        }

        verify(exactly = 1) {
            source4.kotlin.srcDir("$path/generated/ksp/android/androidDebugAndroidTest/kotlin")
        }

        verify(exactly = 1) {
            source5.kotlin.srcDir("$path/generated/ksp/android/androidReleaseAndroidTest/kotlin")
        }

        verify(exactly = 1) {
            DependencyGraph.resolveAncestors(
                sourceDependencies = mapOf(
                    source1DependenciesName to setOf("jvm"),
                    source2DependenciesName to setOf("android"),
                    source3DependenciesName to setOf("androidAndroid"),
                ),
                metaDependencies = emptyMap(),
            )
        }

        verify(exactly = 1) {
            KmpCleanup.cleanup(
                project,
                listOf("jvm", "android", "androidAndroid", "androidAndroid", "androidAndroid"),
            )
        }

        verify(exactly = 0) { kspExtension.arg(any(), any()) }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources, which contain a Android Source v2`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()

        val kspExtension: KspExtension = mockk()

        val source1: KotlinSourceSet = mockk()
        val source1Dependencies: KotlinSourceSet = mockk()
        val source1DependenciesName: String = fixture.fixture()

        val source2: KotlinSourceSet = mockk()
        val source2Dependencies: KotlinSourceSet = mockk()
        val source2DependenciesName: String = fixture.fixture()

        val source3: KotlinSourceSet = mockk()
        val source3Dependencies: KotlinSourceSet = mockk()
        val source3DependenciesName: String = fixture.fixture()

        val sourceSets = mutableListOf(
            source1,
            source2,
            source3,
        )

        val dependencyGraph = mapOf(
            "commonTest" to emptySet<String>(),
        )

        every { project.findProperty(any()) } returns "false"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(kotlin) { probe ->
            extensions.configure("kotlin", probe)
        }

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        every { dependencies.add(any(), any()) } returns mockk()

        every { source1.name } returns "jvmTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf(source1Dependencies)
        every { source1Dependencies.name } returns source1DependenciesName

        every { source2.name } returns "androidUnitTest"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source2Dependencies)
        every { source2Dependencies.name } returns source2DependenciesName

        every { source3.name } returns "androidInstrumentedTest"
        every { source3.kotlin.srcDir(any()) } returns mockk()
        every { source3.dependsOn } returns setOf(source3Dependencies)
        every { source3Dependencies.name } returns source3DependenciesName

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        every { DependencyGraph.resolveAncestors(any(), any()) } returns dependencyGraph
        every { KmpCleanup.cleanup(any(), any()) } just Runs

        every { project.tasks } returns mockk(relaxed = true)

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            dependencies.add(
                "kspJvmTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspAndroidTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspAndroidAndroidTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest/kotlin")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/android/androidUnitTest/kotlin")
        }

        verify(exactly = 1) {
            source3.kotlin.srcDir("$path/generated/ksp/android/androidInstrumentedTest/kotlin")
        }

        verify(exactly = 1) {
            DependencyGraph.resolveAncestors(
                sourceDependencies = mapOf(
                    source1DependenciesName to setOf("jvm"),
                    source2DependenciesName to setOf("androidUnit"),
                    source3DependenciesName to setOf("androidInstrumented"),
                ),
                metaDependencies = emptyMap(),
            )
        }

        verify(exactly = 1) {
            KmpCleanup.cleanup(
                project,
                listOf("jvm", "androidUnit", "androidInstrumented"),
            )
        }

        verify(exactly = 0) { kspExtension.arg(any(), any()) }
    }

    @Test
    fun `Given configure is called it resolves shared source sets`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()

        val kspExtension: KspExtension = mockk()

        val source0: KotlinSourceSet = mockk()
        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()

        val sourceSets = mutableListOf(
            source0,
            source1,
            source2,
        )

        val dependencyGraph = mapOf(
            "commonTest" to emptySet<String>(),
            "nativeTest" to setOf("commonTest"),
        )

        every { project.findProperty(any()) } returns "false"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(kotlin) { probe ->
            extensions.configure("kotlin", probe)
        }

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        every { source0.name } returns "commonTest"
        every { source0.kotlin.srcDir(any()) } returns mockk()
        every { source0.dependsOn } returns setOf()

        every { source1.name } returns "nativeTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf(source0)

        every { source2.name } returns "iosX64Test"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source1)

        every { dependencies.add(any(), any()) } throws RuntimeException()
        every { dependencies.add("kspIosX64Test", any()) } returns mockk()

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        every { DependencyGraph.resolveAncestors(any(), any()) } returns dependencyGraph
        every { KmpCleanup.cleanup(any(), any()) } just Runs

        every { project.tasks } returns mockk(relaxed = true)

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(atLeast = 1) {
            dependencies.add(
                "kspNativeTest",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspIosX64Test",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/native/nativeTest/kotlin")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/iosX64/iosX64Test/kotlin")
        }

        verify(exactly = 1) {
            DependencyGraph.resolveAncestors(
                sourceDependencies = mapOf(
                    "nativeTest" to setOf("iosX64"),
                ),
                metaDependencies = mapOf(
                    "commonTest" to setOf("nativeTest"),
                ),
            )
        }

        verify(exactly = 1) {
            KmpCleanup.cleanup(
                project,
                listOf("common", "native", "iosX64"),
            )
        }

        verify(exactly = 1) {
            kspExtension.arg(
                "kmock_dependencies_nativeTest#0",
                "commonTest",
            )
        }
    }

    @Test
    fun `Given configure is called it resolves deep shared source sets`() {
        // Given
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()

        val kspExtension: KspExtension = mockk()

        val source0: KotlinSourceSet = mockk()
        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()
        val source3: KotlinSourceSet = mockk()
        val source4: KotlinSourceSet = mockk()
        val source5: KotlinSourceSet = mockk()
        val source6: KotlinSourceSet = mockk()
        val source7: KotlinSourceSet = mockk()
        val source8: KotlinSourceSet = mockk()
        val source9: KotlinSourceSet = mockk()

        val sourceSets = mutableListOf(
            source1,
            source3,
            source2,
            source0,
            source4,
            source5,
            source6,
            source7,
            source8,
            source9,
        )

        val dependencyGraph = mapOf(
            "commonTest" to emptySet(),
            "metaTest" to setOf("commonTest"),
            "concurrentTest" to setOf("commonTest", "metaTest"),
            "nativeTest" to setOf("commonTest", "concurrentTest", "metaTest"),
            "otherTest" to setOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
            "iosTest" to setOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
        )

        every { project.findProperty(any()) } returns "false"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(kotlin) { probe ->
            extensions.configure("kotlin", probe)
        }

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        every { source0.name } returns "commonTest"
        every { source0.kotlin.srcDir(any()) } returns mockk()
        every { source0.dependsOn } returns setOf()

        every { source1.name } returns "concurrentTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf(source8)

        every { source2.name } returns "nativeTest"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source1, source0)

        every { source3.name } returns "iosTest"
        every { source3.kotlin.srcDir(any()) } returns mockk()
        every { source3.dependsOn } returns setOf(source2, source0)

        every { source4.name } returns "iosX64Test"
        every { source4.kotlin.srcDir(any()) } returns mockk()
        every { source4.dependsOn } returns setOf(source3, source0)

        every { source5.name } returns "iosArm32Test"
        every { source5.kotlin.srcDir(any()) } returns mockk()
        every { source5.dependsOn } returns setOf(source3, source0)

        every { source6.name } returns "linuxX64Test"
        every { source6.kotlin.srcDir(any()) } returns mockk()
        every { source6.dependsOn } returns setOf(source9, source2, source0)

        every { source7.name } returns "jvmTest"
        every { source7.kotlin.srcDir(any()) } returns mockk()
        every { source7.dependsOn } returns setOf(source1, source0)

        every { source8.name } returns "metaTest"
        every { source8.kotlin.srcDir(any()) } returns mockk()
        every { source8.dependsOn } returns setOf(source0)

        every { source9.name } returns "otherTest"
        every { source9.kotlin.srcDir(any()) } returns mockk()
        every { source9.dependsOn } returns setOf(source2, source0)

        every { dependencies.add(any(), any()) } throws RuntimeException()
        every { dependencies.add("kspIosX64Test", any()) } returns mockk()
        every { dependencies.add("kspIosArm32Test", any()) } returns mockk()
        every { dependencies.add("kspLinuxX64Test", any()) } returns mockk()
        every { dependencies.add("kspJvmTest", any()) } returns mockk()

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        every { DependencyGraph.resolveAncestors(any(), any()) } returns dependencyGraph
        every { KmpCleanup.cleanup(any(), any()) } just Runs

        every { project.tasks } returns mockk(relaxed = true)

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(atLeast = 1) {
            dependencies.add(
                "kspCommonTest",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspMetaTest",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspConcurrentTest",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspNativeTest",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspIosTest",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspIosX64Test",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspIosArm32Test",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspLinuxX64Test",
                MainConfig.processor,
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspJvmTest",
                MainConfig.processor,
            )
        }

        verify(exactly = 1) {
            source0.kotlin.srcDir("$path/generated/ksp/common/commonTest/kotlin")
        }

        verify(exactly = 1) {
            source8.kotlin.srcDir("$path/generated/ksp/meta/metaTest/kotlin")
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/concurrent/concurrentTest/kotlin")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/native/nativeTest/kotlin")
        }

        verify(exactly = 1) {
            source3.kotlin.srcDir("$path/generated/ksp/ios/iosTest/kotlin")
        }

        verify(exactly = 1) {
            source4.kotlin.srcDir("$path/generated/ksp/iosX64/iosX64Test/kotlin")
        }

        verify(exactly = 1) {
            source5.kotlin.srcDir("$path/generated/ksp/iosArm32/iosArm32Test/kotlin")
        }

        verify(exactly = 1) {
            source6.kotlin.srcDir("$path/generated/ksp/linuxX64/linuxX64Test/kotlin")
        }

        verify(exactly = 1) {
            source7.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest/kotlin")
        }

        verify(exactly = 1) {
            source9.kotlin.srcDir("$path/generated/ksp/other/otherTest/kotlin")
        }

        verify(exactly = 1) {
            DependencyGraph.resolveAncestors(
                sourceDependencies = mapOf(
                    "iosTest" to setOf("iosX64", "iosArm32"),
                    "commonTest" to setOf("iosX64", "iosArm32", "linuxX64", "jvm"),
                    "otherTest" to setOf("linuxX64"),
                    "nativeTest" to setOf("linuxX64"),
                    "concurrentTest" to setOf("jvm"),
                ),
                metaDependencies = mapOf(
                    "metaTest" to setOf("concurrentTest"),
                    "nativeTest" to setOf("otherTest", "iosTest"),
                    "commonTest" to setOf("otherTest", "nativeTest", "iosTest", "metaTest"),
                    "concurrentTest" to setOf("nativeTest"),
                ),
            )
        }

        verify(exactly = 1) {
            KmpCleanup.cleanup(
                project,
                listOf(
                    "concurrent",
                    "ios",
                    "native",
                    "common",
                    "iosX64",
                    "iosArm32",
                    "linuxX64",
                    "jvm",
                    "meta",
                    "other",
                ),
            )
        }

        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_iosTest#0", "nativeTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_iosTest#1", "commonTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_iosTest#2", "concurrentTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_iosTest#3", "metaTest") }

        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_otherTest#0", "nativeTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_otherTest#1", "commonTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_otherTest#2", "concurrentTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_otherTest#3", "metaTest") }

        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_nativeTest#0", "commonTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_nativeTest#1", "concurrentTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_nativeTest#2", "metaTest") }

        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_concurrentTest#0", "commonTest") }
        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_concurrentTest#1", "metaTest") }

        verify(exactly = 1) { kspExtension.arg("kmock_dependencies_metaTest#0", "commonTest") }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources while chaining the tests`() {
        // Given
        mockkObject(KmpTestTaskChain)
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()

        val kspExtension: KspExtension = mockk()

        val source0: KotlinSourceSet = mockk()

        val source1: KotlinSourceSet = mockk()
        val source1Dependencies: KotlinSourceSet = mockk()
        val source1DependenciesName: String = fixture.fixture()

        val source2: KotlinSourceSet = mockk()
        val source2Dependencies: KotlinSourceSet = mockk()
        val source2DependenciesName: String = fixture.fixture()

        val sourceSets = mutableListOf(
            source0,
            source1,
            source2,
        )

        val dependencyGraph = mapOf(
            "commonTest" to emptySet<String>(),
        )

        every { project.findProperty(any()) } returns "true"
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(kotlin) { probe ->
            extensions.configure("kotlin", probe)
        }

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        every { dependencies.add(any(), any()) } throws RuntimeException()
        every { dependencies.add("kspJvmTest", any()) } returns mockk()
        every { dependencies.add("kspJsTest", any()) } returns mockk()

        every { source0.name } returns "commonTest"
        every { source0.kotlin.srcDir(any()) } returns mockk()
        every { source0.dependsOn } returns setOf()

        every { source1.name } returns "jvmTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf(source1Dependencies)
        every { source1Dependencies.name } returns source1DependenciesName

        every { source2.name } returns "jsTest"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source2Dependencies)
        every { source2Dependencies.name } returns source2DependenciesName

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        every { DependencyGraph.resolveAncestors(any(), any()) } returns dependencyGraph
        every { KmpCleanup.cleanup(any(), any()) } just Runs

        every { project.tasks } returns mockk(relaxed = true)
        every { KmpTestTaskChain.chainTasks(any(), any()) } just Runs

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            KmpTestTaskChain.chainTasks(
                project,
                mapOf(
                    "jvm" to "kspTestKotlinJvm",
                    "js" to "kspTestKotlinJs",
                ),
            )
        }

        unmockkObject(KmpTestTaskChain)
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources while using its default chaining tests`() {
        // Given
        mockkObject(KmpTestTaskChain)
        val project: Project = mockk()
        val extensions: ExtensionContainer = mockk()
        val dependencies: DependencyHandler = mockk()
        val kotlin: KotlinMultiplatformExtension = mockk()
        val sources: NamedDomainObjectContainer<KotlinSourceSet> = mockk()
        val path: String = fixture.fixture()

        val kspExtension: KspExtension = mockk()

        val source0: KotlinSourceSet = mockk()

        val source1: KotlinSourceSet = mockk()
        val source1Dependencies: KotlinSourceSet = mockk()
        val source1DependenciesName: String = fixture.fixture()

        val source2: KotlinSourceSet = mockk()
        val source2Dependencies: KotlinSourceSet = mockk()
        val source2DependenciesName: String = fixture.fixture()

        val sourceSets = mutableListOf(
            source0,
            source1,
            source2,
        )

        val dependencyGraph = mapOf(
            "commonTest" to emptySet<String>(),
        )

        every { project.findProperty(any()) } returns null
        every { project.dependencies } returns dependencies
        every { project.extensions } returns extensions
        every { project.layout.buildDirectory.get().asFile.absolutePath } returns path
        every { project.plugins.hasPlugin(any<String>()) } returns false

        invokeGradleAction(kotlin) { probe ->
            extensions.configure("kotlin", probe)
        }

        every { kotlin.sourceSets } returns sources
        every { sources.iterator() } returns sourceSets.listIterator()

        every { dependencies.add(any(), any()) } throws RuntimeException()
        every { dependencies.add("kspJvmTest", any()) } returns mockk()
        every { dependencies.add("kspJsTest", any()) } returns mockk()

        every { source0.name } returns "commonTest"
        every { source0.kotlin.srcDir(any()) } returns mockk()
        every { source0.dependsOn } returns setOf()

        every { source1.name } returns "jvmTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf(source1Dependencies)
        every { source1Dependencies.name } returns source1DependenciesName

        every { source2.name } returns "jsTest"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source2Dependencies)
        every { source2Dependencies.name } returns source2DependenciesName

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        every { DependencyGraph.resolveAncestors(any(), any()) } returns dependencyGraph
        every { KmpCleanup.cleanup(any(), any()) } just Runs

        every { project.tasks } returns mockk(relaxed = true)
        every { KmpTestTaskChain.chainTasks(any(), any()) } just Runs

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            KmpTestTaskChain.chainTasks(
                project,
                mapOf(
                    "jvm" to "kspTestKotlinJvm",
                    "js" to "kspTestKotlinJs",
                ),
            )
        }

        unmockkObject(KmpTestTaskChain)
    }
}
