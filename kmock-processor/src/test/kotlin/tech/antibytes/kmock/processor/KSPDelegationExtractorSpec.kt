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
            "kspDir" to kspDir,
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
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
            "kspDir" to kspDir,
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
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
            "kspDir" to kspDir,
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        val expected: Map<String, Int> = fixture.mapFixture(size = 3)

        expected.forEach { (key, value) ->
            delegateKSP["precedence_$key"] = value.toString()
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.precedences mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a empty map if no aliases were delegated`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mapOf(
            "kspDir" to kspDir,
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
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
            "kspDir" to kspDir,
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        val expected: Map<String, String> = fixture.mapFixture(size = 3)

        expected.forEach { (key, value) ->
            delegateKSP["alias_$key"] = value
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
            "kspDir" to kspDir,
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        val expected = fixture.listFixture<String>(size = 3).toSet()

        expected.forEachIndexed { idx, value ->
            delegateKSP["recursive_$idx"] = value
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
            "kspDir" to kspDir,
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        val expected = fixture.listFixture<String>(size = 3).toSet()

        expected.forEachIndexed { idx, value ->
            delegateKSP["buildIn_$idx"] = value
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.useBuildInProxiesOn mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a set of target names where to use uselessPrefixes proxies`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kspDir" to kspDir,
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        val expected = fixture.listFixture<String>(size = 3).toSet()

        expected.forEachIndexed { idx, value ->
            delegateKSP["namePrefix_$idx"] = value
        }

        // When
        val actual = KMockKSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.uselessPrefixes mustBe expected
    }
}
