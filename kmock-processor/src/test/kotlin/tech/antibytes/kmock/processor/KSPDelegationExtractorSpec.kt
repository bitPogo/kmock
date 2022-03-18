/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class KSPDelegationExtractorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils KSPDelegationExtractor`() {
        KSPDelegationExtractor fulfils ProcessorContract.KSPDelegationExtractor::class
    }

    @Test
    fun `Given convertOptions it returns the required Options`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()

        val delegateKSP = mapOf(
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        // When
        val actual = KSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.isKmp mustBe isKmp
        actual.rootPackage mustBe rootPackage
    }

    @Test
    fun `Given convertOptions it returns a empty map if no precedence was delegated`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()

        val delegateKSP = mapOf(
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        // When
        val actual = KSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.precedences mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of declared precedence`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        val expected: Map<String, Int> = fixture.mapFixture(size = 3)

        expected.forEach { (key, value) ->
            delegateKSP["precedence_$key"] = value.toString()
        }

        // When
        val actual = KSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.precedences mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a empty map if no aliases were delegated`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()

        val delegateKSP = mapOf(
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        // When
        val actual = KSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.precedences mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of aliases precedence`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "rootPackage" to rootPackage,
            "isKmp" to isKmp.toString()
        )

        val expected: Map<String, String> = fixture.mapFixture(size = 3)

        expected.forEach { (key, value) ->
            delegateKSP["alias_$key"] = value
        }

        // When
        val actual = KSPDelegationExtractor.convertOptions(delegateKSP)

        // Then
        actual.aliases mustBe expected
    }
}
