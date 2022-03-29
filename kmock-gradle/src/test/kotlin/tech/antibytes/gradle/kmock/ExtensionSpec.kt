/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.fixture.StringAlphaGenerator
import tech.antibytes.gradle.test.createExtension
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fixture.qualifier.named
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.assertFailsWith

class ExtensionSpec {
    private val fixture = kotlinFixture {
        it.addGenerator(
            String::class,
            StringAlphaGenerator,
            qualifier = named("stringAlpha")
        )
    }

    @Test
    fun `It fulfils Extension`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)
        extension fulfils KMockPluginContract.Extension::class
    }

    @Test
    fun `Its default rootPackage is an empty string`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.rootPackage mustBe ""
    }

    @Test
    fun `Its propagates rootPackage changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: String = fixture.fixture()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.rootPackage = expected

        extension.rootPackage mustBe expected
        verify(exactly = 1) { kspExtension.arg("kmock_rootPackage", expected) }
    }

    @Test
    fun `Its default mockNameMapping is a empty map`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.aliasNameMapping mustBe emptyMap()
    }

    @Test
    fun `It propagates mockNameMapping changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: Map<String, String> = fixture.mapFixture(
            valueQualifier = named("stringAlpha"),
            size = 3
        )

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.aliasNameMapping = expected

        extension.aliasNameMapping mustBe expected
        verify(exactly = 1) {
            kspExtension.arg("kmock_alias_${expected.keys.toList()[0]}", expected.values.toList()[0])
        }
        verify(exactly = 1) {
            kspExtension.arg("kmock_alias_${expected.keys.toList()[1]}", expected.values.toList()[1])
        }
        verify(exactly = 1) {
            kspExtension.arg("kmock_alias_${expected.keys.toList()[2]}", expected.values.toList()[2])
        }
    }

    @Test
    fun `It fails if the mockNameMapping contains internal names`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)

        val internalNames = listOf(
            "kmock_rootPackage",
            "kmock_isKmp",
            "kmock_kspDir"
        )

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        internalNames.forEach { name ->
            val extension = createExtension<KMockExtension>(project)
            // Then
            val error = assertFailsWith<IllegalArgumentException> {
                // When
                extension.aliasNameMapping = mapOf(
                    name to fixture.fixture(named("stringAlpha"))
                )
            }

            error.message mustBe "$name is not allowed!"
        }
    }

    @Test
    fun `It fails if the mockNameMapping contains special chars in the value`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val illegal = "some.thing"

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)
        // Then
        val error = assertFailsWith<IllegalArgumentException> {
            // When
            extension.aliasNameMapping = mapOf(
                fixture.fixture<String>(named("stringAlpha")) to "some.thing"
            )
        }

        error.message mustBe "$illegal is not a valid alias!"
    }

    @Test
    fun `Its default allowedRecursiveTypes is a empty set`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.allowedRecursiveTypes mustBe emptySet()
    }

    @Test
    fun `It propagates allowedRecursiveTypes changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: List<String> = fixture.listFixture(size = 3)

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.allowedRecursiveTypes = expected.toSet()

        extension.allowedRecursiveTypes mustBe expected.toSet()
        verify(exactly = 1) { kspExtension.arg("kmock_recursive_0", expected[0]) }
        verify(exactly = 1) { kspExtension.arg("kmock_recursive_1", expected[1]) }
        verify(exactly = 1) { kspExtension.arg("kmock_recursive_2", expected[2]) }
    }

    @Test
    fun `Its default allowBuildInProxies is a empty set`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.useBuildInProxiesOn mustBe emptySet()
    }

    @Test
    fun `It propagates allowBuildInProxies changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: List<String> = fixture.listFixture(size = 3)

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.useBuildInProxiesOn = expected.toSet()

        extension.useBuildInProxiesOn mustBe expected.toSet()
        verify(exactly = 1) { kspExtension.arg("kmock_buildIn_0", expected[0]) }
        verify(exactly = 1) { kspExtension.arg("kmock_buildIn_1", expected[1]) }
        verify(exactly = 1) { kspExtension.arg("kmock_buildIn_2", expected[2]) }
    }

    @Test
    fun `Its uselessPrefixes has default values`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.uselessPrefixes mustBe setOf(
            "kotlin.collections",
            "kotlin",
        )
    }

    @Test
    fun `It propagates uselessPrefixes changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: List<String> = fixture.listFixture(size = 3)

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.uselessPrefixes = expected.toSet()

        extension.uselessPrefixes mustBe expected.toSet()
        verify(exactly = 1) { kspExtension.arg("kmock_namePrefix_0", expected[0]) }
        verify(exactly = 1) { kspExtension.arg("kmock_namePrefix_1", expected[1]) }
        verify(exactly = 1) { kspExtension.arg("kmock_namePrefix_2", expected[2]) }
    }

    @Test
    fun `Its default spyOn is a empty set`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.spyOn mustBe emptySet()
    }

    @Test
    fun `It propagates spyOn changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: List<String> = fixture.listFixture(size = 3)

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.spyOn = expected.toSet()

        extension.spyOn mustBe expected.toSet()
        verify(exactly = 1) { kspExtension.arg("kmock_spyOn_0", expected[0]) }
        verify(exactly = 1) { kspExtension.arg("kmock_spyOn_1", expected[1]) }
        verify(exactly = 1) { kspExtension.arg("kmock_spyOn_2", expected[2]) }
    }

    @Test
    fun `Its allowInterfacesOnKmock is false by default`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.allowInterfacesOnKmock mustBe false
    }

    @Test
    fun `It propagates allowInterfacesOnKmock changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.allowInterfacesOnKmock = expected

        extension.allowInterfacesOnKmock mustBe expected
        verify(exactly = 1) { kspExtension.arg("kmock_allowInterfacesOnKmock", expected.toString()) }
    }

    @Test
    fun `Its allowInterfacesOnKspy is false by default`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.allowInterfacesOnKspy mustBe false
    }

    @Test
    fun `It propagates allowInterfacesOnKspy changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.allowInterfacesOnKspy = expected

        extension.allowInterfacesOnKspy mustBe expected
        verify(exactly = 1) { kspExtension.arg("kmock_allowInterfacesOnKspy", expected.toString()) }
    }

    @Test
    fun `Its spiesOnly is false by default`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.spiesOnly mustBe false
    }

    @Test
    fun `It propagates spiesOnly changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected: Boolean = fixture.fixture()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.spiesOnly = expected

        extension.spiesOnly mustBe expected
        verify(exactly = 1) { kspExtension.arg("kmock_spiesOnly", expected.toString()) }
    }
}
