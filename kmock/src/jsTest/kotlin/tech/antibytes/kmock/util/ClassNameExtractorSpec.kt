/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ktlint:standard:filename")

package tech.antibytes.kmock.util

import kotlin.test.Test
import tech.antibytes.util.test.mustBe

actual class ClassNameExtractorSpec {
    @Test
    @JsName("fn0")
    actual fun `Given extractKClassName is called it returns the of the class name`() {
        extractKClassName(Any::class) mustBe Any::class.toString()
    }
}
