/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.junit.jupiter.api.Test
import tech.antibytes.gradle.test.createExtension
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class ExtensionSpec {
    @Test
    fun `It fulfils Extension`() {
        val extension = createExtension<KMockExtension>()
        extension fulfils KMockPluginContract.Extension::class
    }

    @Test
    fun `Its default rootPackage is an empty string`() {
        val extension = createExtension<KMockExtension>()

        extension.rootPackage mustBe ""
    }
}
