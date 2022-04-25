/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.util

import kotlin.js.JsName

expect class ClassNameExtractorSpec {
    @JsName("fn0")
    fun `Given extractKClassName is called it returns the of the class name`()
}
