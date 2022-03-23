/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.fixture

import co.touchlab.stately.isolate.IsolateState
import tech.antibytes.util.test.fixture.PublicApi
import kotlin.random.Random

internal class StringAlphaGenerator(
    private val random: IsolateState<Random>
) : PublicApi.Generator<String> {
    override fun generate(): String {
        val length = random.access { it.nextInt(1, 10) }
        val chars = ByteArray(length)

        for (idx in 0 until length) {
            val char = random.access { it.nextInt(65, 91) }
            chars[idx] = if (random.access { it.nextBoolean() }) {
                (char + 32).toByte()
            } else {
                char.toByte()
            }
        }

        return chars.decodeToString()
    }

    companion object : PublicApi.GeneratorFactory<String> {
        override fun getInstance(random: IsolateState<Random>): PublicApi.Generator<String> {
            return StringAlphaGenerator(random)
        }
    }
}
