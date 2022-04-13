/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.util.test.fulfils

class NoopFactoryGeneratorSpec {
    @Test
    fun `It fulfils MockFactoryGenerator`() {
        NoopFactoryGenerator fulfils ProcessorContract.MockFactoryGenerator::class
    }

    @Test
    fun `It fulfils MockFactoryEntryPointGenerator`() {
        NoopFactoryGenerator fulfils ProcessorContract.MockFactoryEntryPointGenerator::class
    }
}
