/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.util.test.mustBe
import kotlin.test.Test

class ExampleClassSpec {
    @Test
    fun test() {
        ExampleClass().doSomething() mustBe "something"
    }
}
