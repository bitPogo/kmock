/* ktlint-disable filename */
/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.util

import org.junit.Test
import tech.antibytes.util.test.mustBe

actual class ClassNameExtractorSpec {
    @Test
    actual fun `Given extractKClassName is called it returns the of the class name`() {
        extractKClassName(Any::class) mustBe Any::class.qualifiedName
    }
}
