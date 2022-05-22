/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGeneratorFactory

class ProxyAccessMethodGeneratorSpec {
    @Test
    fun `It fulfils ProxyAccessMethodGeneratorFactory`() {
        KMockProxyAccessMethodGenerator fulfils ProxyAccessMethodGeneratorFactory::class
    }

    @Test
    fun `Given getInstance is called with a Boolean it creates a ProxyAccessMethodGenerator`() {
        KMockProxyAccessMethodGenerator.getInstance(false) fulfils ProxyAccessMethodGenerator::class
    }
}
