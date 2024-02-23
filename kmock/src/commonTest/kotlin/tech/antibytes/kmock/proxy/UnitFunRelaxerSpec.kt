/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import kotlin.js.JsName
import kotlin.test.Test
import tech.antibytes.util.test.mustBe

class UnitFunRelaxerSpec {
    @Test
    @JsName("fn0")
    fun `Given kmockUnitFunRelaxer is called it returns Unit if the refered type is Unit`() {
        kmockUnitFunRelaxer("xx") mustBe Unit
    }
}
