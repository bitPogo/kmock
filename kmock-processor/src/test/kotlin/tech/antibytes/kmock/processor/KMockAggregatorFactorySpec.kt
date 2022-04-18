/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.KSPLogger
import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract.Aggregator
import tech.antibytes.kmock.processor.ProcessorContract.AggregatorFactory
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fixture.listFixture
import tech.antibytes.util.test.fixture.mapFixture
import tech.antibytes.util.test.fulfils

class KMockAggregatorFactorySpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils AggregatorFactory`() {
        KMockAggregator fulfils AggregatorFactory::class
    }

    @Test
    fun `Given getInstance is called it returns a Aggregator`() {
        // Given
        val logger: KSPLogger = mockk()
        val knownSources: Set<String> = fixture.listFixture<String>().toSet()
        val generics: GenericResolver = mockk()
        val aliases: Map<String, String> = fixture.mapFixture()

        // When
        val actual = KMockAggregator.getInstance(
            logger = logger,
            knownSourceSets = knownSources,
            generics = generics,
            aliases = aliases
        )

        // Then
        actual fulfils Aggregator::class
    }
}
