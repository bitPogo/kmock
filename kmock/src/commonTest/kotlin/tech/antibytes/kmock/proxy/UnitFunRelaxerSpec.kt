/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class UnitFunRelaxerSpec {
    @Test
    @JsName("fn0")
    fun `Given relaxVoidFunction is called it returns Unit if the refered type is Unit`() {
        relaxVoidFunction<Unit>() mustBe Unit
    }

    @Test
    @JsName("fn1")
    fun `Given relaxVoidFunction is called it returns Null if the refered type is not Unit`() {
        relaxVoidFunction<Int>() mustBe null
    }
}
