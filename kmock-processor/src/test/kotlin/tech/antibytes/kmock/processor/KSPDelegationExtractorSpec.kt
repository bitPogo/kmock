/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class KSPDelegationExtractorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils KSPDelegationExtractor`() {
        KMockKSPDelegationExtractor fulfils ProcessorContract.KSPDelegationExtractor::class
    }

    @Test
    fun `Given convertOptions it returns the required Options`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.isKmp mustBe isKmp
        actual.rootPackage mustBe rootPackage
        actual.kspDir mustBe kspDir
    }

    @Test
    fun `Given convertOptions it returns a empty map if no precedence was delegated`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.precedences mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of declared precedence`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected: Map<String, Int> = fixture.mapFixture(size = 3)

        expected.forEach { (key, value) ->
            delegateKSP["kmock_precedence_$key"] = value.toString()
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.precedences mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a empty set of sourceSets if no precedences were given to derive them from`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.knownSourceSets mustBe emptySet()
    }

    @Test
    fun `Given convertOptions it returns a set of derived sourceSets, which are extracted from precedences`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected: Map<String, Int> = fixture.mapFixture(size = 3)

        expected.forEach { (key, value) ->
            delegateKSP["kmock_precedence_$key"] = value.toString()
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.knownSourceSets mustBe expected.keys
    }

    @Test
    fun `Given convertOptions it returns a set of derived sourceSets, while filtering commonTest`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected: Map<String, Int> = fixture.mapFixture(size = 3)

        expected.forEach { (key, value) ->
            delegateKSP["kmock_precedence_$key"] = value.toString()
        }

        delegateKSP["kmock_precedence_commonTest"] = "23"

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.knownSourceSets mustBe expected.keys
    }

    @Test
    fun `Given convertOptions it returns a empty map if no aliases were delegated`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.precedences mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of aliases precedence`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected: Map<String, String> = fixture.mapFixture(size = 3)

        expected.forEach { (key, value) ->
            delegateKSP["kmock_alias_$key"] = value
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.aliases mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a set of allowed recursive types`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected = fixture.listFixture<String>(size = 3).toSet()

        expected.forEachIndexed { idx, value ->
            delegateKSP["kmock_recursive_$idx"] = value
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowedRecursiveTypes mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a set of target names where to use buildIn proxies`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected = fixture.listFixture<String>(size = 3).toSet()

        expected.forEachIndexed { idx, value ->
            delegateKSP["kmock_buildIn_$idx"] = value
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.useBuildInProxiesOn mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a set of target names where to use buildIn proxies while utilizing delegated spies`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected = fixture.listFixture<String>(size = 3).toSet()

        expected.forEachIndexed { idx, value ->
            delegateKSP["kmock_spyOn_$idx"] = value
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.useBuildInProxiesOn mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a set of target names where to use spies`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected = fixture.listFixture<String>(size = 3).toSet()

        expected.forEachIndexed { idx, value ->
            delegateKSP["kmock_spyOn_$idx"] = value
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.spyOn mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a set of target names where to use uselessPrefixes proxies`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val expected = fixture.listFixture<String>(size = 3).toSet()

        expected.forEachIndexed { idx, value ->
            delegateKSP["kmock_namePrefix_$idx"] = value
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.uselessPrefixes mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a false for allowInterfacesOnKmock if no value was propagated`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowInterfacesOnKmock mustBe false
    }

    @Test
    fun `Given convertOptions it returns the propagated value for allowInterfacesOnKmock`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected: Boolean = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_allowInterfacesOnKmock" to expected.toString()
        )

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowInterfacesOnKmock mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a false for allowInterfacesOnKspy if no value was propagated`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowInterfacesOnKspy mustBe false
    }

    @Test
    fun `Given convertOptions it returns the propagated value for allowInterfacesOnKspy`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected: Boolean = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_allowInterfacesOnKspy" to expected.toString()
        )

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowInterfacesOnKspy mustBe expected
    }
}
