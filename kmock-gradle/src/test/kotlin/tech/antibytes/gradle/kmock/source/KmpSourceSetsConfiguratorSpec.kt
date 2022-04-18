/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import com.google.devtools.ksp.gradle.KspExtension
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.kmock.config.MainConfig
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils

class KmpSourceSetsConfiguratorSpec {
    private val fixture = kotlinFixture()

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

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            dependencies.add(
                "kspJvmTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspJsTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/js/jsTest")
        }

        verify(exactly = 0) { kspExtension.arg(any(), any()) }
    }

    @Test
    fun `Given configure is called it configures PlatformTest Sources, which contain a Android Source`() {
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

        every { dependencies.add(any(), any()) } returns mockk()

        every { source1.name } returns "jvmTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf(source1Dependencies)
        every { source1Dependencies.name } returns source1DependenciesName

        every { source2.name } returns "androidTest"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source2Dependencies)
        every { source2Dependencies.name } returns source2DependenciesName

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(exactly = 1) {
            dependencies.add(
                "kspJvmTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(exactly = 1) {
            dependencies.add(
                "kspAndroidTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/android/androidTest")
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

        val source1: KotlinSourceSet = mockk()
        val source2: KotlinSourceSet = mockk()

        val sourceSets = mutableListOf(
            source1,
            source2,
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

        every { source1.name } returns "nativeTest"
        every { source1.kotlin.srcDir(any()) } returns mockk()
        every { source1.dependsOn } returns setOf()

        every { source2.name } returns "iosX64Test"
        every { source2.kotlin.srcDir(any()) } returns mockk()
        every { source2.dependsOn } returns setOf(source1)

        every { dependencies.add(any(), any()) } throws RuntimeException()
        every { dependencies.add("kspIosX64Test", any()) } returns mockk()

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(atLeast = 1) {
            dependencies.add(
                "kspNativeTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspIosX64Test",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/native/nativeTest")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/iosX64/iosX64Test")
        }

        verify(exactly = 0) { kspExtension.arg(any(), any()) }
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
        every { source6.dependsOn } returns setOf(source2, source0)

        every { source7.name } returns "jvmTest"
        every { source7.kotlin.srcDir(any()) } returns mockk()
        every { source7.dependsOn } returns setOf(source1, source0)

        every { source8.name } returns "metaTest"
        every { source8.kotlin.srcDir(any()) } returns mockk()
        every { source8.dependsOn } returns setOf(source0)

        every { dependencies.add(any(), any()) } throws RuntimeException()
        every { dependencies.add("kspIosX64Test", any()) } returns mockk()
        every { dependencies.add("kspIosArm32Test", any()) } returns mockk()
        every { dependencies.add("kspLinuxX64Test", any()) } returns mockk()
        every { dependencies.add("kspJvmTest", any()) } returns mockk()

        every { extensions.getByType(KspExtension::class.java) } returns kspExtension
        every { kspExtension.arg(any(), any()) } just Runs

        // When
        KmpSourceSetsConfigurator.configure(project)

        // Then
        verify(atLeast = 1) {
            dependencies.add(
                "kspCommonTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspMetaTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspConcurrentTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspNativeTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspIosTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspIosX64Test",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspIosArm32Test",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspLinuxX64Test",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(atLeast = 1) {
            dependencies.add(
                "kspJvmTest",
                "tech.antibytes.kmock:kmock-processor:${MainConfig.version}"
            )
        }

        verify(exactly = 1) {
            source0.kotlin.srcDir("$path/generated/ksp/common/commonTest")
        }

        verify(exactly = 1) {
            source8.kotlin.srcDir("$path/generated/ksp/meta/metaTest")
        }

        verify(exactly = 1) {
            source1.kotlin.srcDir("$path/generated/ksp/concurrent/concurrentTest")
        }

        verify(exactly = 1) {
            source2.kotlin.srcDir("$path/generated/ksp/native/nativeTest")
        }

        verify(exactly = 1) {
            source3.kotlin.srcDir("$path/generated/ksp/ios/iosTest")
        }

        verify(exactly = 1) {
            source4.kotlin.srcDir("$path/generated/ksp/iosX64/iosX64Test")
        }

        verify(exactly = 1) {
            source5.kotlin.srcDir("$path/generated/ksp/iosArm32/iosArm32Test")
        }

        verify(exactly = 1) {
            source6.kotlin.srcDir("$path/generated/ksp/linuxX64/linuxX64Test")
        }

        verify(exactly = 1) {
            source7.kotlin.srcDir("$path/generated/ksp/jvm/jvmTest")
        }

        verify(exactly = 1) { kspExtension.arg("kmock_precedence_metaTest", "0") }
        verify(exactly = 1) { kspExtension.arg("kmock_precedence_concurrentTest", "-1") }
        verify(exactly = 1) { kspExtension.arg("kmock_precedence_nativeTest", "-3") }
        verify(exactly = 1) { kspExtension.arg("kmock_precedence_iosTest", "-5") }
    }
}
