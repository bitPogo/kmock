/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName")

package tech.antibytes.kmock.verification.contraints

import tech.antibytes.kmock.KMockContract
import kotlin.reflect.KClass

class any(
    private val expected: KClass<*>? = null
) : KMockContract.MatcherConstraint {
    override fun matches(actual: Any?): Boolean {
        return when {
            expected == null -> true
            actual == null -> false
            else -> actual::class == expected
        }
    }
}