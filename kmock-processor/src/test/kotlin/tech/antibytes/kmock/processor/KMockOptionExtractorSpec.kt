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

class KMockOptionExtractorSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils KSPDelegationExtractor`() {
        KMockOptionExtractor fulfils ProcessorContract.OptionExtractor::class
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.isKmp mustBe isKmp
        actual.rootPackage mustBe rootPackage
        actual.kspDir mustBe kspDir
    }

    @Test
    fun `Given convertOptions it returns a empty map if no dependencies was delegated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.dependencies mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of declared dependencies`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val keys: List<String> = fixture.listFixture(size = 3)
        val ancestors0: List<String> = fixture.listFixture(size = 3)
        val ancestors1: List<String> = fixture.listFixture(size = 3)
        val ancestors2: List<String> = fixture.listFixture(size = 3)

        delegateKSP["kmock_dependencies_${keys[0]}#0"] = ancestors0[0]
        delegateKSP["kmock_dependencies_${keys[0]}#1"] = ancestors0[1]
        delegateKSP["kmock_dependencies_${keys[0]}#2"] = ancestors0[2]

        delegateKSP["kmock_dependencies_${keys[1]}#0"] = ancestors1[0]
        delegateKSP["kmock_dependencies_${keys[1]}#1"] = ancestors1[1]
        delegateKSP["kmock_dependencies_${keys[1]}#2"] = ancestors1[2]

        delegateKSP["kmock_dependencies_${keys[2]}#0"] = ancestors2[0]
        delegateKSP["kmock_dependencies_${keys[2]}#1"] = ancestors2[1]
        delegateKSP["kmock_dependencies_${keys[2]}#2"] = ancestors2[2]

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.dependencies mustBe mapOf(
            keys[0] to ancestors0.toSortedSet(),
            keys[1] to ancestors1.toSortedSet(),
            keys[2] to ancestors2.toSortedSet()
        )
    }

    @Test
    fun `Given convertOptions it returns a empty set of sourceSets if no dependencies were given to derive them from`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.knownSharedSourceSets mustBe emptySet()
    }

    @Test
    fun `Given convertOptions it returns a set of derived sourceSets, which are extracted from dependenciess`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString()
        )

        val keys: List<String> = fixture.listFixture(size = 3)
        val ancestors0: List<String> = fixture.listFixture(size = 3)
        val ancestors1: List<String> = fixture.listFixture(size = 3)
        val ancestors2: List<String> = fixture.listFixture(size = 3)

        delegateKSP["kmock_dependencies_${keys[0]}#0"] = ancestors0[0]
        delegateKSP["kmock_dependencies_${keys[0]}#1"] = ancestors0[1]
        delegateKSP["kmock_dependencies_${keys[0]}#2"] = ancestors0[2]

        delegateKSP["kmock_dependencies_${keys[1]}#0"] = ancestors1[0]
        delegateKSP["kmock_dependencies_${keys[1]}#1"] = ancestors1[1]
        delegateKSP["kmock_dependencies_${keys[1]}#2"] = ancestors1[2]

        delegateKSP["kmock_dependencies_${keys[2]}#0"] = ancestors2[0]
        delegateKSP["kmock_dependencies_${keys[2]}#1"] = ancestors2[1]
        delegateKSP["kmock_dependencies_${keys[2]}#2"] = ancestors2[2]

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.knownSharedSourceSets mustBe keys.toSet()
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

        val keys: List<String> = fixture.listFixture(size = 3)
        val ancestors0: List<String> = fixture.listFixture(size = 3)
        val ancestors1: List<String> = fixture.listFixture(size = 3)
        val ancestors2: List<String> = fixture.listFixture(size = 3)

        delegateKSP["kmock_dependencies_${keys[0]}#0"] = ancestors0[0]
        delegateKSP["kmock_dependencies_${keys[0]}#1"] = ancestors0[1]
        delegateKSP["kmock_dependencies_${keys[0]}#2"] = ancestors0[2]

        delegateKSP["kmock_dependencies_${keys[1]}#0"] = ancestors1[0]
        delegateKSP["kmock_dependencies_${keys[1]}#1"] = ancestors1[1]
        delegateKSP["kmock_dependencies_${keys[1]}#2"] = ancestors1[2]

        delegateKSP["kmock_dependencies_${keys[2]}#0"] = ancestors2[0]
        delegateKSP["kmock_dependencies_${keys[2]}#1"] = ancestors2[1]
        delegateKSP["kmock_dependencies_${keys[2]}#2"] = ancestors2[2]

        delegateKSP["kmock_dependencies_commonTest#0"] = "23"

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.knownSharedSourceSets mustBe keys.toSet()
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.aliases mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of aliases dependencies`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.aliases mustBe expected
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.spyOn mustBe expected
    }

    @Test
    fun `Given convertOptions it returns true for freezeOnDefault if no value was propagated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.freezeOnDefault mustBe true
    }

    @Test
    fun `Given convertOptions it returns the propagated value for freezeOnDefault`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected = false

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_freeze" to expected.toString()
        )

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.freezeOnDefault mustBe expected
    }

    @Test
    fun `Given convertOptions it returns false for allowInterfaces if no value was propagated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowInterfaces mustBe false
    }

    @Test
    fun `Given convertOptions it returns the propagated value for allowInterfaces`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected = true

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_allowInterfaces" to expected.toString()
        )

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowInterfaces mustBe expected
    }

    @Test
    fun `Given convertOptions it returns false for spiesOnly if no value was propagated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.spiesOnly mustBe false
    }

    @Test
    fun `Given convertOptions it returns the propagated value for spiesOnly`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected = true

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_spiesOnly" to expected.toString()
        )

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.spiesOnly mustBe expected
    }

    @Test
    fun `Given convertOptions it returns false for spyAll if no value was propagated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.spyAll mustBe false
    }

    @Test
    fun `Given convertOptions it returns the propagated value for spyAll`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected = true

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_spyAll" to expected.toString()
        )

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.spyAll mustBe expected
    }

    @Test
    fun `Given convertOptions it returns false for disableFactories if no value was propagated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.disableFactories mustBe false
    }

    @Test
    fun `Given convertOptions it returns the propagated value for disableFactories`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected = true

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_disable_factories" to expected.toString()
        )

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.disableFactories mustBe expected
    }

    @Test
    fun `Given convertOptions it returns false for enableNewOverloadingNames if no value was propagated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.enableNewOverloadingNames mustBe true
    }

    @Test
    fun `Given convertOptions it returns the propagated value for enableNewOverloadingNames`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected = false

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_useNewOverloadedNames" to expected.toString()
        )

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.enableNewOverloadingNames mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a empty map if no TypePrefixes were delegated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.useTypePrefixFor mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of declared TypePrefixes`() {
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
            delegateKSP["kmock_namePrefix_$key"] = value
        }

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.useTypePrefixFor mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a empty map if no CustomMethodNames were delegated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.customMethodNames mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of declared CustomMethodNames`() {
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
            delegateKSP["kmock_customMethodName_$key"] = value
        }

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.customMethodNames mustBe expected
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
            delegateKSP["kmock_oldNamePrefix_$idx"] = value
        }

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.uselessPrefixes mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a empty map if no CustomAnnotations were delegated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.customAnnotations mustBe emptyMap()
    }

    @Test
    fun `Given convertOptions it returns a map of declared CustomAnnotations`() {
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
            delegateKSP["kmock_customAnnotation_$key"] = value
        }

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.customAnnotations mustBe expected
    }

    @Test
    fun `Given convertOptions it returns false for allowExperimentalProxyAccess if no value was propagated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowExperimentalProxyAccess mustBe false
    }

    @Test
    fun `Given convertOptions it returns the propagated value for allowExperimentalProxyAccess`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected = false

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_alternativeProxyAccess" to expected.toString()
        )

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.allowExperimentalProxyAccess mustBe expected
    }

    @Test
    fun `Given convertOptions it returns false for enableFineGrainedNames if no value was propagated`() {
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
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.enableFineGrainedNames mustBe false
    }

    @Test
    fun `Given convertOptions it returns the propagated value for enableFineGrainedNames`() {
        // Given
        val rootPackage: String = fixture.fixture()
        val isKmp: Boolean = fixture.fixture()
        val kspDir: String = fixture.fixture()
        val expected = false

        val delegateKSP = mutableMapOf(
            "kmock_kspDir" to kspDir,
            "kmock_rootPackage" to rootPackage,
            "kmock_isKmp" to isKmp.toString(),
            "kmock_enableFineGrainedNames" to expected.toString()
        )

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.enableFineGrainedNames mustBe expected
    }

    @Test
    fun `Given convertOptions it returns a set of target names what to purge`() {
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
            delegateKSP["kmock_purgeFiles_$idx"] = value
        }

        // When
        val actual = KMockOptionExtractor.convertOptions(delegateKSP)

        // Then
        actual.purgeFiles mustBe expected
    }
}
