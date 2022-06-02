/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import org.gradle.api.Project
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.fixture.StringAlphaGenerator
import tech.antibytes.gradle.kmock.source.KmpSourceSetsConfigurator
import tech.antibytes.gradle.kmock.source.SingleSourceSetConfigurator
import tech.antibytes.gradle.test.createExtension
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

@OptIn(KMockGradleExperimental::class)
class ExtensionSpec {
    private val fixture = kotlinFixture {
        it.addGenerator(
            String::class,
            StringAlphaGenerator,
            qualifier = named("stringAlpha")
        )
    }

    @BeforeEach
    fun setUp() {
        mockkObject(KSPBridge)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(KSPBridge)
    }

    @Test
    fun `It fulfils Extension`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)
        extension fulfils KMockPluginContract.Extension::class
    }

    @Test
    fun `It initializes a KSPBridge`() {
        // Given
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        // When
        createExtension<KMockExtension>(project)

        // Then
        verify(exactly = 1) {
            KSPBridge.getInstance(
                project = project,
                singleSourceSetConfigurator = SingleSourceSetConfigurator,
                kmpSourceSetConfigurator = KmpSourceSetsConfigurator,
            )
        }
    }

    @Test
    fun `Its default rootPackage is an empty string`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.rootPackage mustBe ""
    }

    @Test
    fun `Its propagates rootPackage changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: String = fixture.fixture()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.rootPackage = expected

        extension.rootPackage mustBe expected
        verify(exactly = 1) { kspBridge.propagateValue("kmock_rootPackage", expected) }
    }

    @Test
    fun `Its default aliasNameMapping is a empty map`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.aliasNameMapping mustBe emptyMap()
    }

    @Test
    fun `It propagates aliasNameMapping changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Map<String, String> = fixture.mapFixture(
            valueQualifier = named("stringAlpha"),
            size = 3
        )

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.aliasNameMapping = expected

        // Then
        extension.aliasNameMapping mustBe expected

        verify(exactly = 1) {
            kspBridge.propagateMapping("kmock_alias_", expected, any())
        }
    }

    @Test
    fun `It fails if the aliasNameMapping contains internal names`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk()
        val action = slot<(String, String) -> Unit>()

        val internalNames = listOf(
            "kmock_rootPackage",
            "kmock_isKmp",
            "kmock_kspDir"
        )

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateMapping(any(), any(), capture(action)) } just Runs

        internalNames.forEach { name ->
            val extension = createExtension<KMockExtension>(project)
            // When
            extension.aliasNameMapping = mapOf(
                name to fixture.fixture(named("stringAlpha"))
            )
            // Then
            val error = assertFailsWith<IllegalArgumentException> {
                action.captured.invoke(name, fixture.fixture(named("stringAlpha")))
            }

            error.message mustBe "$name is not allowed!"
        }
    }

    @Test
    fun `It fails if the aliasNameMapping contains special chars in the value`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val action = slot<(String, String) -> Unit>()
        val illegal = "some.thing"

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateMapping(any(), any(), capture(action)) } just Runs

        val extension = createExtension<KMockExtension>(project)
        // When
        extension.aliasNameMapping = mapOf(
            fixture.fixture<String>(named("stringAlpha")) to illegal
        )
        // Then
        val error = assertFailsWith<IllegalArgumentException> {
            action.captured.invoke(fixture.fixture(named("stringAlpha")), illegal)
        }

        error.message mustBe "$illegal is not applicable!"
    }

    @Test
    fun `Its default allowBuildInProxies is a empty set`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.useBuildInProxiesOn mustBe emptySet()
    }

    @Test
    fun `It propagates allowBuildInProxies changes to Ksp while transforming them to string`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val action = slot<(String) -> String>()
        val kspBridge: KMockPluginContract.KSPBridge = mockk()
        val expected: Set<String> = fixture.listFixture<String>(size = 3).toSet()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateIterable(any(), any(), capture(action)) } just Runs

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.useBuildInProxiesOn = expected

        // Then
        extension.useBuildInProxiesOn mustBe expected
        verify(exactly = 1) {
            kspBridge.propagateIterable("kmock_buildIn_", expected, action.captured)
        }

        val given: String = fixture.fixture()
        action.captured.invoke(given) mustBe given
    }

    @Test
    fun `Its useTypePrefixFor has no values`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.useTypePrefixFor mustBe emptyMap()
    }

    @Test
    fun `It propagates useTypePrefixFor changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Map<String, String> = fixture.mapFixture(
            size = 3,
            valueQualifier = named("stringAlpha")
        )

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.useTypePrefixFor = expected

        // Then
        extension.useTypePrefixFor mustBe expected
        verify(exactly = 1) {
            kspBridge.propagateMapping("kmock_namePrefix_", expected, any())
        }
    }

    @Test
    fun `It fails if the useTypePrefixFor contains internal names`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk()
        val action = slot<(String, String) -> Unit>()

        val internalNames = listOf(
            "kmock_rootPackage",
            "kmock_isKmp",
            "kmock_kspDir"
        )

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateMapping(any(), any(), capture(action)) } just Runs

        internalNames.forEach { name ->
            val extension = createExtension<KMockExtension>(project)
            // When
            extension.useTypePrefixFor = mapOf(
                name to fixture.fixture(named("stringAlpha"))
            )
            // Then
            val error = assertFailsWith<IllegalArgumentException> {
                action.captured.invoke(name, fixture.fixture(named("stringAlpha")))
            }

            error.message mustBe "$name is not allowed!"
        }
    }

    @Test
    fun `It fails if the useTypePrefixFor contains special chars in the value`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val action = slot<(String, String) -> Unit>()
        val illegal = "some.thing"

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateMapping(any(), any(), capture(action)) } just Runs

        val extension = createExtension<KMockExtension>(project)
        // When
        extension.useTypePrefixFor = mapOf(
            fixture.fixture<String>(named("stringAlpha")) to illegal
        )

        // Then
        val error = assertFailsWith<IllegalArgumentException> {
            action.captured.invoke(fixture.fixture(named("stringAlpha")), illegal)
        }

        error.message mustBe "$illegal is not applicable!"
    }

    @Test
    fun `Its customMethodNames has no values`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.customMethodNames mustBe emptyMap()
    }

    @Test
    fun `It propagates customMethodNames changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Map<String, String> = fixture.mapFixture(
            size = 3,
            valueQualifier = named("stringAlpha")
        )

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.customMethodNames = expected

        // Then
        extension.customMethodNames mustBe expected
        verify(exactly = 1) {
            kspBridge.propagateMapping("kmock_customMethodName_", expected, any())
        }
    }

    @Test
    fun `It fails if the customMethodNames contains internal names`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk()
        val action = slot<(String, String) -> Unit>()

        val internalNames = listOf(
            "kmock_rootPackage",
            "kmock_isKmp",
            "kmock_kspDir"
        )

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateMapping(any(), any(), capture(action)) } just Runs

        internalNames.forEach { name ->
            val extension = createExtension<KMockExtension>(project)
            // When
            extension.customMethodNames = mapOf(
                name to fixture.fixture(named("stringAlpha"))
            )
            // Then
            val error = assertFailsWith<IllegalArgumentException> {
                action.captured.invoke(name, fixture.fixture(named("stringAlpha")))
            }

            error.message mustBe "$name is not allowed!"
        }
    }

    @Test
    fun `It fails if the customMethodNames contains special chars in the value`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val action = slot<(String, String) -> Unit>()
        val illegal = "some.thing"

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateMapping(any(), any(), capture(action)) } just Runs

        val extension = createExtension<KMockExtension>(project)
        // When
        extension.customMethodNames = mapOf(
            fixture.fixture<String>(named("stringAlpha")) to illegal
        )
        // Then
        val error = assertFailsWith<IllegalArgumentException> {
            action.captured.invoke(fixture.fixture(named("stringAlpha")), illegal)
        }

        error.message mustBe "$illegal is not applicable!"
    }

    @Test
    fun `Its freezeOnDefault is true by default`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.freezeOnDefault mustBe true
    }

    @Test
    fun `It propagates freezeOnDefault changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.freezeOnDefault = expected

        extension.freezeOnDefault mustBe expected
        verify(exactly = 1) { kspBridge.propagateValue("kmock_freeze", expected.toString()) }
    }

    @Test
    fun `Its allowInterfaces is false by default`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.allowInterfaces mustBe false
    }

    @Test
    fun `It propagates allowInterfaces changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.allowInterfaces = expected

        extension.allowInterfaces mustBe expected
        verify(exactly = 1) { kspBridge.propagateValue("kmock_allowInterfaces", expected.toString()) }
    }

    @Test
    fun `Its default spyOn is a empty set`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.spyOn mustBe emptySet()
    }

    @Test
    fun `It propagates spyOn changes to Ksp while transforming them to string`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val action = slot<(String) -> String>()
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Set<String> = fixture.listFixture<String>(size = 3).toSet()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateIterable(any(), any(), capture(action)) } just Runs

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.spyOn = expected

        verify(exactly = 1) {
            kspBridge.propagateIterable("kmock_spyOn_", expected, action.captured)
        }

        val given: String = fixture.fixture()
        action.captured.invoke(given) mustBe given
    }

    @Test
    fun `Its spiesOnly is false by default`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.spiesOnly mustBe false
    }

    @Test
    fun `It propagates spiesOnly changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.spiesOnly = expected

        extension.spiesOnly mustBe expected
        verify(exactly = 1) { kspBridge.propagateValue("kmock_spiesOnly", expected.toString()) }
    }

    @Test
    fun `Its spyAll is false by default`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.spyAll mustBe false
    }

    @Test
    fun `It propagates spyAll changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.spyAll = expected

        extension.spyAll mustBe expected
        verify(exactly = 1) { kspBridge.propagateValue("kmock_spyAll", expected.toString()) }
    }

    @Test
    fun `Its disableFactories is false by default`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.disableFactories mustBe false
    }

    @Test
    fun `It propagates disableFactories changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.disableFactories = expected

        extension.disableFactories mustBe expected
        verify(exactly = 1) { kspBridge.propagateValue("kmock_disable_factories", expected.toString()) }
    }

    @Test
    fun `Its customSharedAnnotations has no values`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.customAnnotationsForMeta mustBe emptyMap()
    }

    @Test
    fun `It propagates customSharedAnnotations changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Map<String, String> = fixture.mapFixture(
            size = 3,
            valueQualifier = named("stringAlpha")
        )

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.customAnnotationsForMeta = expected

        extension.customAnnotationsForMeta mustBe expected
        verify(exactly = 1) {
            kspBridge.propagateMapping("kmock_customAnnotation_", expected, any())
        }
    }

    @Test
    fun `It fails if the customAnnotationsForMeta contains internal names`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk()
        val action = slot<(String, String) -> Unit>()

        val internalNames = listOf(
            "kmock_rootPackage",
            "kmock_isKmp",
            "kmock_kspDir"
        )

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateMapping(any(), any(), capture(action)) } just Runs

        internalNames.forEach { name ->
            val extension = createExtension<KMockExtension>(project)
            // When
            extension.customAnnotationsForMeta = mapOf(
                name to fixture.fixture(named("stringAlpha"))
            )
            // Then
            val error = assertFailsWith<IllegalArgumentException> {
                action.captured.invoke(name, fixture.fixture(named("stringAlpha")))
            }

            error.message mustBe "$name is not allowed!"
        }
    }

    @Test
    fun `It fails if the customAnnotationsForMeta contains special chars in the value`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val action = slot<(String, String) -> Unit>()
        val illegal = "some.thing"

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge
        every { kspBridge.propagateMapping(any(), any(), capture(action)) } just Runs

        val extension = createExtension<KMockExtension>(project)
        // When
        extension.customAnnotationsForMeta = mapOf(
            fixture.fixture<String>(named("stringAlpha")) to illegal
        )
        // Then
        val error = assertFailsWith<IllegalArgumentException> {
            action.captured.invoke(fixture.fixture(named("stringAlpha")), illegal)
        }

        error.message mustBe "$illegal is not applicable!"
    }

    @Test
    fun `Its allowExperimentalProxyAccess is false by default`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.allowExperimentalProxyAccess mustBe false
    }

    @Test
    fun `It propagates allowExperimentalProxyAccess changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.allowExperimentalProxyAccess = expected

        extension.allowExperimentalProxyAccess mustBe expected
        verify(exactly = 1) { kspBridge.propagateValue("kmock_alternativeProxyAccess", expected.toString()) }
    }

    @Test
    fun `Its enableFineGrainedNames is false by default`() {
        val project: Project = mockk(relaxed = true)

        every { KSPBridge.getInstance(any(), any(), any()) } returns mockk(relaxed = true)

        val extension = createExtension<KMockExtension>(project)

        extension.enableFineGrainedNames mustBe false
    }

    @Test
    fun `It propagates enableFineGrainedNames changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspBridge: KMockPluginContract.KSPBridge = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { KSPBridge.getInstance(any(), any(), any()) } returns kspBridge

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.enableFineGrainedNames = expected

        extension.enableFineGrainedNames mustBe expected
        verify(exactly = 1) { kspBridge.propagateValue("kmock_enableFineGrainedProxyNames", expected.toString()) }
    }
}
