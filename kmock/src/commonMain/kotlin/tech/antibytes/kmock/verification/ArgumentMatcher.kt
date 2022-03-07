/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.GetOrSet
import tech.antibytes.kmock.verification.constraints.eq

internal fun Array<out Any?>?.hasBeenCalledWithVoid(): Boolean = this == null

private fun wrapValue(value: Any?): KMockContract.VerificationConstraint {
    return if (value is KMockContract.VerificationConstraint) {
        value
    } else {
        eq(value)
    }
}

internal fun Array<out Any?>?.hasBeenCalledWith(vararg values: Any?): Boolean {
    return when {
        this == null -> values.isEmpty()
        values.isEmpty() -> true
        else -> {
            var lastMatch = 0

            for (value in values) {
                var matched = false
                val expected = wrapValue(value)

                for (idx in lastMatch until this.size) {
                    val actual = this[idx]

                    if (expected.matches(actual)) {
                        matched = true
                        lastMatch = idx
                        break
                    }
                }

                if (!matched) {
                    return false
                }
            }

            return true
        }
    }
}

internal fun Array<out Any?>?.hasBeenStrictlyCalledWith(vararg values: Any?): Boolean {
    return when {
        this == null && values.isEmpty() -> true
        this == null -> false
        values.size != this.size -> false
        else -> {

            for (idx in values.indices) {
                val expected = wrapValue(values[idx])

                if (!expected.matches(this[idx])) {
                    return false
                }
            }

            return true
        }
    }
}

internal fun Array<out Any?>?.hasBeenCalledWithout(vararg values: Any?): Boolean {
    return if (this == null) {
        values.isNotEmpty()
    } else {
        for (value in values) {
            var matched = false
            val expected = wrapValue(value)

            for (actual in this) {
                if (expected.matches(actual)) {
                    matched = true
                    break
                }
            }

            if (matched) {
                return false
            }
        }

        return true
    }
}

internal fun GetOrSet.wasGotten(): Boolean = this is GetOrSet.Get

internal fun GetOrSet.wasSet(): Boolean = this is GetOrSet.Set

internal fun GetOrSet.wasSetTo(value: Any?): Boolean {
    return when (this) {
        !is GetOrSet.Set -> false
        else -> {
            val expected = wrapValue(value)
            return expected.matches(this.value)
        }
    }
}
