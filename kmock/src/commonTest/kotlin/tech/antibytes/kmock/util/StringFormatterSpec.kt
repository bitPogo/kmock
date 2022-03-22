/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.util

import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

class StringFormatterSpec {
    @Test
    @JsName("fn0")
    fun `Given format is called without presetted placeholders and arguments it returns the given String`() {
        // Given
        val template = "Something"

        // When
        val actual = template.format()

        // Then
        actual mustBe template
    }

    @Test
    @JsName("fn1")
    fun `Given format is called presetted placeholders and arguments it uses the arguments according to their index`() {
        // Given
        val template = "Something"
        val arg0 = 123
        val arg1 = 12.3

        // When
        val actual = "$template %0 %1 %0".format(arg0, arg1)

        // Then
        actual mustBe "$template $arg0 $arg1 $arg0"
    }

    @Test
    @JsName("fn3")
    fun `Given format is called presetted placeholders and arguments which contains a Array it uses the arguments according to their index`() {
        // Given
        val template = "Something"
        val arg0 = 123
        val arg1 = arrayOf(12.3, 123)

        // When
        val actual = "$template %0 %1 %0".format(arg0, arg1)

        // Then
        actual mustBe "$template $arg0 [${arg1.joinToString(", ")}] $arg0"
    }
}
