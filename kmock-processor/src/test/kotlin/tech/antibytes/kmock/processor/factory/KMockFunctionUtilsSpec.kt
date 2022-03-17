/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import org.junit.jupiter.api.Test
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.util.test.fulfils

class KMockFunctionUtilsSpec {
    @Test
    fun `It fulfils MockFactoryGeneratorUtil`() {
        KMockFactoryGeneratorUtil fulfils ProcessorContract.MockFactoryGeneratorUtil::class
    }
}
