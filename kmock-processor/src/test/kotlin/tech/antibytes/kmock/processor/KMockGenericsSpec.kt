/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils

class KMockGenericsSpec {
    @Test
    fun `It fulfils FunctionUtils`() {
        KMockGenerics fulfils ProcessorContract.GenericResolver::class
    }
}