/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.KMockPluginContract.CacheController
import tech.antibytes.gradle.kmock.KMockPluginContract.SourceSetConfigurator
import tech.antibytes.gradle.kmock.fixture.StringAlphaGenerator
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kfixture.mapFixture
import tech.antibytes.kfixture.qualifier.qualifiedBy
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class KSPBridgeSpec {
    private val fixture = kotlinFixture {
        addGenerator(
            String::class,
            StringAlphaGenerator,
            qualifiedBy("stringAlpha"),
        )
    }

    @Test
    fun `It fulfils KSPBridgeFactory`() {
        KSPBridge fulfils KMockPluginContract.KSPBridgeFactory::class
    }

    @Test
    fun `Given getInstance is called with a Project and a SingleSourceSetConfigurator and a KMPSourceSetConfigurator it returns a KSPBridgeFactory`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val singleSource: SourceSetConfigurator = mockk(relaxed = true)
        val kmpSource: SourceSetConfigurator = mockk(relaxed = true)

        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)
        val extensions: ExtensionContainer = mockk()

        every { project.extensions } returns extensions
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        // When
        val actual: Any = KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        )

        // Then
        actual fulfils KMockPluginContract.KSPBridge::class
    }

    @Test
    fun `Given propagateValue is called with a key and value it propagates the source set config for single sources sets first`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { singleSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateValue(fixture.fixture(), fixture.fixture())

        // Then
        verify(exactly = 1) { singleSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "false") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateValue is called with a key and value it propagates the source set config for single sources sets first only once`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { singleSource.configure(any()) } just Runs

        // When
        val bridge = KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        )
        bridge.propagateValue(fixture.fixture(), fixture.fixture())
        bridge.propagateValue(fixture.fixture(), fixture.fixture())

        // Then
        verify(exactly = 1) { singleSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "false") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateValue is called with a key and value it propagates the source set config for kmp sources sets first`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateValue(fixture.fixture(), fixture.fixture())

        // Then
        verify(exactly = 1) { kmpSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "true") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateValue is called with a key and value it propagates the source set config for kmp sources sets first only once`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        val bridge = KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        )
        bridge.propagateValue(fixture.fixture(), fixture.fixture())
        bridge.propagateValue(fixture.fixture(), fixture.fixture())

        // Then
        verify(exactly = 1) { kmpSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "true") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateValue is called with a key and value it propagates the given key value pair to ksp`() {
        // Given
        val key: String = fixture.fixture()
        val value: String = fixture.fixture()

        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateValue(key, value)

        // Then
        verify(exactly = 1) { ksp.arg(key, value) }
    }

    @Test
    fun `Given propagateMapping is called with a root key and mapping it propagates the source set config for single sources sets first`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { singleSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateMapping(fixture.fixture(), fixture.mapFixture())

        // Then
        verify(exactly = 1) { singleSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "false") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateMapping is called with a root key and mapping it propagates the source set config for single sources sets first only once`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { singleSource.configure(any()) } just Runs

        // When
        val bridge = KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        )
        bridge.propagateMapping(fixture.fixture(), fixture.mapFixture())
        bridge.propagateMapping(fixture.fixture(), fixture.mapFixture())

        // Then
        verify(exactly = 1) { singleSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "false") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateMapping is called with a root key and mapping it propagates the source set config for kmp sources sets first`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateMapping(fixture.fixture(), fixture.mapFixture())

        // Then
        verify(exactly = 1) { kmpSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "true") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateMapping is called with a root key and mapping it propagates the source set config for kmp sources sets first only once`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        val bridge = KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        )
        bridge.propagateMapping(fixture.fixture(), fixture.mapFixture())
        bridge.propagateMapping(fixture.fixture(), fixture.mapFixture())

        // Then
        verify(exactly = 1) { kmpSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "true") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateMapping is called with a root key and mapping it propagates the given key value pairs to ksp while delegating to a given action as well`() {
        // Given
        val rootKey: String = fixture.fixture()
        val pairs: Map<String, String> = fixture.mapFixture(size = 3)

        val captured: MutableMap<String, String> = mutableMapOf()
        val action: (String, String) -> Unit = { givenKey, givenValue ->
            captured[givenKey] = givenValue
        }

        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateMapping(
            rootKey = rootKey,
            mapping = pairs,
            onPropagation = action,
        )

        // Then
        pairs.forEach { (key, value) ->
            verify(exactly = 1) { ksp.arg("$rootKey$key", value) }
        }
        captured mustBe pairs
    }

    @Test
    fun `Given propagateIterable is called with a rootKey and values it propagates the source set config for single sources sets first`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { singleSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateIterable(fixture.fixture(), fixture.listFixture<String>())

        // Then
        verify(exactly = 1) { singleSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "false") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateIterable is called with a rootKey and values it propagates the source set config for single sources sets first only once`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns false
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { singleSource.configure(any()) } just Runs

        // When
        val bridge = KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        )
        bridge.propagateIterable(fixture.fixture(), fixture.listFixture<String>())
        bridge.propagateIterable(fixture.fixture(), fixture.listFixture<String>())

        // Then
        verify(exactly = 1) { singleSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "false") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateIterable is called with a rootKey and values it propagates the source set config for kmp sources sets first`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateIterable(fixture.fixture(), fixture.listFixture<String>())

        // Then
        verify(exactly = 1) { kmpSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "true") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateIterable is called with a rootKey and values it propagates the source set config for kmp sources sets first only once`() {
        // Given
        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        val bridge = KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        )
        bridge.propagateIterable(fixture.fixture(), fixture.listFixture<String>())
        bridge.propagateIterable(fixture.fixture(), fixture.listFixture<String>())

        // Then
        verify(exactly = 1) { kmpSource.configure(project) }
        verify(exactly = 1) { cacheController.configure(project) }
        verify(exactly = 1) { ksp.arg("kmock_isKmp", "true") }
        verify(exactly = 1) { ksp.arg("kmock_kspDir", "${buildDir.absolutePath}/generated/ksp") }
    }

    @Test
    fun `Given propagateIterable is called with a rootKey and values it propagates the given values to ksp while transforing it with the given action`() {
        // Given
        val rootKey: String = fixture.fixture()
        val values: List<String> = fixture.listFixture(size = 3)

        val captured: MutableList<String> = mutableListOf()
        val action: (String) -> String = { givenValue ->
            captured.add(givenValue)

            "${givenValue}X"
        }

        val project: Project = mockk()
        val singleSource: SourceSetConfigurator = mockk()
        val kmpSource: SourceSetConfigurator = mockk()
        val ksp: KspExtension = mockk()
        val cacheController: CacheController = mockk(relaxUnitFun = true)

        val plugins: PluginContainer = mockk()
        val extensions: ExtensionContainer = mockk()
        val buildDir = File(fixture.fixture<String>(qualifiedBy("stringAlpha")))

        every { project.plugins } returns plugins
        every { project.extensions } returns extensions
        every { project.buildDir } returns buildDir

        every { plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") } returns true
        every { extensions.getByType(KspExtension::class.java) } returns ksp

        every { ksp.arg(any(), any()) } just Runs
        every { kmpSource.configure(any()) } just Runs

        // When
        KSPBridge.getInstance(
            project = project,
            cacheController = cacheController,
            singleSourceSetConfigurator = singleSource,
            kmpSourceSetConfigurator = kmpSource,
        ).propagateIterable(
            rootKey = rootKey,
            values = values,
            action = action,
        )

        // Then
        values.forEachIndexed { idx, value ->
            verify(exactly = 1) { ksp.arg("$rootKey$idx", "${value}X") }
        }
        captured mustBe values
    }
}
