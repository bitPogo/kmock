/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryBuildType
import com.android.build.api.dsl.LibraryExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.gradle.test.invokeGradleAction
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.util.test.fulfils

class AndroidSourceBinderSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils AndroidSourceBinder`() {
        AndroidSourceBinder fulfils KMockPluginContract.AndroidSourceBinder::class
    }

    @Test
    fun `Given bind is called with a project it adds the source directories for an AndroidApplication`() {
        // Given
        val project: Project = mockk()
        val buildDirPath: String = fixture.fixture()
        val extensions: ExtensionContainer = mockk()
        val androidExtension: ApplicationExtension = mockk()
        val sourceSets: NamedDomainObjectContainer<AndroidSourceSet> = mockk()

        val release: ApplicationBuildType = mockk()
        val debug: ApplicationBuildType = mockk()
        val buildTypes: NamedDomainObjectContainer<ApplicationBuildType> = mockk()

        val debugTest: AndroidSourceSet = mockk()
        val debugAndroidTest: AndroidSourceSet = mockk()

        val releaseTest: AndroidSourceSet = mockk()
        val releaseAndroidTest: AndroidSourceSet = mockk()

        every { project.plugins.findPlugin("com.android.application") } returns mockk()
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns buildDirPath

        invokeGradleAction(
            { probe -> extensions.configure(ApplicationExtension::class.java, probe) },
            androidExtension,
        )

        every { androidExtension.buildTypes } returns buildTypes
        every { androidExtension.sourceSets } returns sourceSets
        every { buildTypes.size } returns 2
        every { buildTypes.iterator() } returns mutableListOf(debug, release).iterator()

        every { debug.name } returns "debug"
        every { release.name } returns "release"

        every { sourceSets.getByName("testDebug") } returns debugTest
        every { sourceSets.getByName("androidTestDebug") } returns debugAndroidTest

        every { sourceSets.getByName("testRelease") } returns releaseTest
        every { sourceSets.getByName("androidTestRelease") } returns releaseAndroidTest

        every { debugTest.java.srcDir(any()) } returns mockk()
        every { debugAndroidTest.java.srcDir(any()) } returns mockk()

        every { releaseTest.java.srcDir(any()) } returns mockk()
        every { releaseAndroidTest.java.srcDir(any()) } returns mockk()
        // When
        AndroidSourceBinder.bind(project)

        // Then
        verify(exactly = 1) {
            debugTest.java.srcDir("$buildDirPath/generated/ksp/debugUnitTest")
        }
        verify(exactly = 1) {
            debugAndroidTest.java.srcDir("$buildDirPath/generated/ksp/debugAndroidTest")
        }

        verify(exactly = 1) {
            releaseTest.java.srcDir("$buildDirPath/generated/ksp/releaseUnitTest")
        }
        verify(exactly = 1) {
            releaseAndroidTest.java.srcDir("$buildDirPath/generated/ksp/releaseAndroidTest")
        }
    }

    @Test
    fun `Given bind is called with a project it adds the source directories for an AndroidLibrary`() {
        // Given
        val project: Project = mockk()
        val buildDirPath: String = fixture.fixture()
        val extensions: ExtensionContainer = mockk()
        val androidExtension: LibraryExtension = mockk()
        val sourceSets: NamedDomainObjectContainer<AndroidSourceSet> = mockk()

        val release: LibraryBuildType = mockk()
        val debug: LibraryBuildType = mockk()
        val buildTypes: NamedDomainObjectContainer<LibraryBuildType> = mockk()

        val debugTest: AndroidSourceSet = mockk()
        val debugAndroidTest: AndroidSourceSet = mockk()

        val releaseTest: AndroidSourceSet = mockk()
        val releaseAndroidTest: AndroidSourceSet = mockk()

        every { project.plugins.findPlugin("com.android.application") } returns null
        every { project.extensions } returns extensions
        every { project.buildDir.absolutePath } returns buildDirPath

        invokeGradleAction(
            { probe -> extensions.configure(LibraryExtension::class.java, probe) },
            androidExtension,
        )

        every { androidExtension.buildTypes } returns buildTypes
        every { androidExtension.sourceSets } returns sourceSets
        every { buildTypes.size } returns 2
        every { buildTypes.iterator() } returns mutableListOf(debug, release).iterator()

        every { debug.name } returns "debug"
        every { release.name } returns "release"

        every { sourceSets.getByName("testDebug") } returns debugTest
        every { sourceSets.getByName("androidTestDebug") } returns debugAndroidTest

        every { sourceSets.getByName("testRelease") } returns releaseTest
        every { sourceSets.getByName("androidTestRelease") } returns releaseAndroidTest

        every { debugTest.java.srcDir(any()) } returns mockk()
        every { debugAndroidTest.java.srcDir(any()) } returns mockk()

        every { releaseTest.java.srcDir(any()) } returns mockk()
        every { releaseAndroidTest.java.srcDir(any()) } returns mockk()
        // When
        AndroidSourceBinder.bind(project)

        // Then
        verify(exactly = 1) {
            debugTest.java.srcDir("$buildDirPath/generated/ksp/debugUnitTest")
        }
        verify(exactly = 1) {
            debugAndroidTest.java.srcDir("$buildDirPath/generated/ksp/debugAndroidTest")
        }

        verify(exactly = 1) {
            releaseTest.java.srcDir("$buildDirPath/generated/ksp/releaseUnitTest")
        }
        verify(exactly = 1) {
            releaseAndroidTest.java.srcDir("$buildDirPath/generated/ksp/releaseAndroidTest")
        }
    }
}
