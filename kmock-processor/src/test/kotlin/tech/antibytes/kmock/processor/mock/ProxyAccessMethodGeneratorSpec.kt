/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import io.mockk.mockk
import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGeneratorFactory
import tech.antibytes.util.test.fulfils

class ProxyAccessMethodGeneratorSpec {
    @Test
    fun `It fulfils ProxyAccessMethodGeneratorFactory`() {
        KMockProxyAccessMethodGenerator fulfils ProxyAccessMethodGeneratorFactory::class
    }

    @Test
    fun `Given getInstance is called with a Boolean it creates a ProxyAccessMethodGenerator`() {
        KMockProxyAccessMethodGenerator.getInstance(
            false,
            mockk(),
            mockk(),
            mockk(),
        ) fulfils ProxyAccessMethodGenerator::class
    }
}
