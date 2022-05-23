/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.util.test.mustBe
import kotlin.test.Test

class HintSpec {
    @Test
    fun `Given hint is called with no Parameter it creates a Hint0`() {
        hint() mustBe Hint0()
    }

    @Test
    fun `Given hint is called with a Parameter it creates a Hint1`() {
        hint<Any>() mustBe Hint1(Any::class)
    }

    @Test
    fun `Given hint is called with 2 Parameter it creates a Hint2`() {
        hint<Any, Int>() mustBe Hint2(
            Any::class,
            Int::class,
        )
    }

    @Test
    fun `Given hint is called with 3 Parameter it creates a Hint3`() {
        hint<Any, Int, Boolean>() mustBe Hint3(
            Any::class,
            Int::class,
            Boolean::class,
        )
    }

    @Test
    fun `Given hint is called with 4 Parameter it creates a Hint4`() {
        hint<Any, Int, Boolean, String>() mustBe Hint4(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
        )
    }

    @Test
    fun `Given hint is called with 5 Parameter it creates a Hint5`() {
        hint<Any, Int, Boolean, String, CharSequence>() mustBe Hint5(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
            CharSequence::class,
        )
    }

    @Test
    fun `Given hint is called with 6 Parameter it creates a Hint6`() {
        hint<Any, Int, Boolean, String, CharSequence, CharArray>() mustBe Hint6(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
            CharSequence::class,
            CharArray::class,
        )
    }

    @Test
    fun `Given hint is called with 7 Parameter it creates a Hint7`() {
        hint<Any, Int, Boolean, String, CharSequence, CharArray, Long>() mustBe Hint7(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
            CharSequence::class,
            CharArray::class,
            Long::class,
        )
    }

    @Test
    fun `Given hint is called with 8 Parameter it creates a Hint8`() {
        hint<Any, Int, Boolean, String, CharSequence, CharArray, Long, IntArray>() mustBe Hint8(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
            CharSequence::class,
            CharArray::class,
            Long::class,
            IntArray::class,
        )
    }

    @Test
    fun `Given hint is called with 9 Parameter it creates a Hint9`() {
        hint<Any, Int, Boolean, String, CharSequence, CharArray, Long, IntArray, ByteArray>() mustBe Hint9(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
            CharSequence::class,
            CharArray::class,
            Long::class,
            IntArray::class,
            ByteArray::class,
        )
    }

    @Test
    fun `Given hint is called with 10 Parameter it creates a Hint10`() {
        hint<Any, Int, Boolean, String, CharSequence, CharArray, Long, IntArray, ByteArray, Char>() mustBe Hint10(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
            CharSequence::class,
            CharArray::class,
            Long::class,
            IntArray::class,
            ByteArray::class,
            Char::class
        )
    }

    @Test
    fun `Given hint is called with 11 Parameter it creates a Hint11`() {
        hint<Any, Int, Boolean, String, CharSequence, CharArray, Long, IntArray, ByteArray, Char, Appendable>() mustBe Hint11(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
            CharSequence::class,
            CharArray::class,
            Long::class,
            IntArray::class,
            ByteArray::class,
            Char::class,
            Appendable::class
        )
    }

    @Test
    fun `Given hint is called with 12 Parameter it creates a Hint12`() {
        hint<Any, Int, Boolean, String, CharSequence, CharArray, Long, IntArray, ByteArray, Char, Appendable, Error>() mustBe Hint12(
            Any::class,
            Int::class,
            Boolean::class,
            String::class,
            CharSequence::class,
            CharArray::class,
            Long::class,
            IntArray::class,
            ByteArray::class,
            Char::class,
            Appendable::class,
            Error::class,
        )
    }
}
