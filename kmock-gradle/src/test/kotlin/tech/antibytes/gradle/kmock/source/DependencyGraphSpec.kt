/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.junit.jupiter.api.Test
import tech.antibytes.gradle.kmock.KMockPluginContract
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class DependencyGraphSpec {
    @Test
    fun `It fulfils DependencyGraphBuilder`() {
        DependencyGraph fulfils KMockPluginContract.DependencyGraph::class
    }

    @Test
    fun `Given resolveAncestors is called with a flat mapping it returns a Mapping of a meta Source set to all its ancestors`() {
        // Given
        val platformSources = mapOf(
            "concurrentTest" to setOf("jvm"),
        )
        val metaSources = mapOf(
            "commonTest" to setOf("concurrentTest"),
        )

        // When
        val actual = DependencyGraph.resolveAncestors(platformSources, metaSources)

        // Then
        actual mustBe mapOf(
            "commonTest" to emptySet(),
            "concurrentTest" to setOf("commonTest"),
        )
    }

    @Test
    fun `Given resolveAncestors is called with a transitive mapping it returns a Mapping of a meta Source set to all its ancestors`() {
        // Given
        val platformSources = mapOf(
            "iosTest" to setOf("iosX64", "iosArm32"),
        )
        val metaSources = mapOf(
            "commonTest" to setOf("concurrentTest", "nativeTest", "iosTest"),
            "concurrentTest" to setOf("nativeTest"),
            "nativeTest" to setOf("iosTest"),
        )

        // When
        val actual = DependencyGraph.resolveAncestors(platformSources, metaSources)

        // Then
        actual mustBe mapOf(
            "commonTest" to emptySet(),
            "concurrentTest" to setOf("commonTest"),
            "nativeTest" to setOf("commonTest", "concurrentTest"),
            "iosTest" to setOf("commonTest", "nativeTest", "concurrentTest"),
        )
    }

    @Test
    fun `Given resolveAncestors is called with a misconfigured mapping it returns a Mapping of a meta Source set to all its ancestors`() {
        // Given
        val platformSources = mapOf(
            "iosTest" to setOf("iosX64", "iosArm32"),
        )
        val metaSources = mapOf(
            "commonTest" to setOf("concurrentTest"),
            "concurrentTest" to setOf("nativeTest"),
            "nativeTest" to setOf("iosTest"),
        )

        // When
        val actual = DependencyGraph.resolveAncestors(platformSources, metaSources)

        // Then
        actual mustBe mapOf(
            "commonTest" to emptySet(),
            "concurrentTest" to setOf("commonTest"),
            "nativeTest" to setOf("commonTest", "concurrentTest"),
            "iosTest" to setOf("commonTest", "nativeTest", "concurrentTest"),
        )
    }

    @Test
    fun `Given resolveAncestors is called with a mixed it returns a Mapping of a meta Source set to all its ancestors`() {
        // Given
        val platformSources = mapOf(
            "iosTest" to setOf("iosX64", "iosArm32"),
            "commonTest" to setOf("iosX64", "iosArm32", "linuxX64", "jvm"),
            "otherTest" to setOf("linuxX64"),
            "nativeTest" to setOf("linuxX64"),
            "concurrentTest" to setOf("jvm"),
        )
        val metaSources = mapOf(
            "metaTest" to setOf("concurrentTest"),
            "nativeTest" to setOf("otherTest", "iosTest"),
            "commonTest" to setOf("otherTest", "nativeTest", "iosTest", "metaTest"),
            "concurrentTest" to setOf("nativeTest"),
        )

        // When
        val actual = DependencyGraph.resolveAncestors(platformSources, metaSources)

        // Then
        actual mustBe mapOf(
            "commonTest" to emptySet(),
            "metaTest" to setOf("commonTest"),
            "concurrentTest" to setOf("commonTest", "metaTest"),
            "nativeTest" to setOf("commonTest", "concurrentTest", "metaTest"),
            "otherTest" to setOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
            "iosTest" to setOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
        )
    }

    @Test
    fun `Given resolveAncestors is called with a transitive mapping it returns a Mapping while dealing with unconnected sources`() {
        // Given
        val platformSources = emptyMap<String, Set<String>>()
        val metaSources = mapOf(
            "commonTest" to setOf("concurrentTest", "nativeTest", "iosTest"),
            "concurrentTest" to setOf("nativeTest"),
            "nativeTest" to emptySet(),
        )

        // When
        val actual = DependencyGraph.resolveAncestors(platformSources, metaSources)

        // Then
        actual mustBe mapOf(
            "commonTest" to emptySet(),
            "concurrentTest" to setOf("commonTest"),
            "nativeTest" to setOf("commonTest", "concurrentTest"),
        )
    }
}
