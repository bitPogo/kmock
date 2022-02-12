/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Plugin
import org.junit.Test
import tech.antibytes.util.test.fulfils

class KMockSpec {
    @Test
    fun `It fulfils Plugin`() {
        KMock() fulfils Plugin::class
    }
}
