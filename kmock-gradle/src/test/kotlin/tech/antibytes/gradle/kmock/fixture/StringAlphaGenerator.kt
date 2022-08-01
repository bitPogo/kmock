/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.fixture

import kotlin.random.Random
import tech.antibytes.kfixture.PublicApi

internal class StringAlphaGenerator(
    private val random: Random,
) : PublicApi.Generator<String> {
    override fun generate(): String {
        val length = random.nextInt(1, 10)
        val chars = ByteArray(length)

        for (idx in 0 until length) {
            val char = random.nextInt(65, 91)
            chars[idx] = if (random.nextBoolean()) {
                (char + 32).toByte()
            } else {
                char.toByte()
            }
        }

        return chars.decodeToString()
    }

    companion object : PublicApi.GeneratorFactory<String> {
        override fun getInstance(random: Random): PublicApi.Generator<String> {
            return StringAlphaGenerator(random)
        }
    }
}
