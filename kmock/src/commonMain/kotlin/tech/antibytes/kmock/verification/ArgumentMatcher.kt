/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.GetOrSet
import tech.antibytes.kmock.verification.constraints.ArgumentConstraintWrapper.wrapNegatedValue
import tech.antibytes.kmock.verification.constraints.ArgumentConstraintWrapper.wrapValue

@Suppress("UNUSED_PARAMETER")
private fun noopClosure(argument: Any?, matcherIndex: Int): Unit = Unit

internal fun Array<out Any?>.hasBeenCalledWithVoid(): Boolean = this.isEmpty()

internal fun Array<out Any?>.hasBeenCalledWith(
    vararg constraints: Any?,
    onFail: (Any?, Int) -> Unit = ::noopClosure,
): Boolean {
    return when {
        this.isEmpty() -> constraints.isEmpty()
        constraints.isEmpty() -> true
        else -> {
            var lastMatch = 0

            constraints.forEachIndexed { constraintIdx, constraint ->
                var matched = false
                val expected = wrapValue(constraint)

                for (idx in lastMatch until this.size) {
                    val actual = this[idx]

                    if (expected.matches(actual)) {
                        matched = true
                        lastMatch = idx
                        break
                    }
                }

                if (!matched) {
                    onFail(null, constraintIdx)
                    return false
                }
            }

            return true
        }
    }
}

internal fun Array<out Any?>.hasBeenStrictlyCalledWith(
    vararg constraints: Any?,
    onFail: (Any?, Int) -> Unit = ::noopClosure,
): Boolean {
    return when {
        this.isEmpty() && constraints.isEmpty() -> true
        constraints.size != this.size -> false
        else -> {
            for (idx in this.indices) {
                val expected = wrapValue(constraints[idx])

                if (!expected.matches(this[idx])) {
                    onFail(this[idx], idx)
                    return false
                }
            }

            return true
        }
    }
}

internal fun Array<out Any?>.hasBeenCalledWithout(
    vararg constraints: Any?,
    onFail: (Any?, Int) -> Unit = ::noopClosure,
): Boolean {
    return if (this.isEmpty()) {
        constraints.isNotEmpty()
    } else {
        constraints.forEachIndexed { idx, constraint ->
            var matched = false
            val expected = wrapNegatedValue(constraint)

            for (actual in this) {
                if (!expected.matches(actual)) {
                    onFail(actual, idx)
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

internal fun GetOrSet.wasSetTo(constraint: Any?): Boolean {
    return when (this) {
        !is GetOrSet.Set -> false
        else -> {
            val expected = wrapValue(constraint)
            return expected.matches(this.value)
        }
    }
}
