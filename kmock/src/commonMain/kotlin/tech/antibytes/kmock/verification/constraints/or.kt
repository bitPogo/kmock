/* ktlint-disable filename */
/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName")

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract.ArgumentConstraint

/**
 * VerificationConstraint which allows to chain multiple values or constraints together.
 * It matches if at least one value or constraints matches.
 * @param subConstraints the expected values or constraints.
 * @property subConstraints  the expected values or constraints.
 * @throws IllegalArgumentException if no value or constraint was provided.
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
public class or(
    vararg subConstraints: Any?,
) : ArgumentConstraint {
    private val subConstraints: Array<out ArgumentConstraint> = mapSubConstraints(subConstraints)

    private fun guard(
        subConstraints: Array<out Any?>,
        action: Array<out Any?>.() -> Array<out ArgumentConstraint>,
    ): Array<out ArgumentConstraint> {
        return if (subConstraints.isEmpty()) {
            throw IllegalArgumentException("or should not be empty!")
        } else {
            subConstraints.action()
        }
    }

    private fun mapSubConstraints(
        subConstraints: Array<out Any?>,
    ): Array<out ArgumentConstraint> {
        return guard(subConstraints) {
            map { value ->
                if (value is ArgumentConstraint) {
                    value
                } else {
                    eq(value)
                }
            }.toTypedArray()
        }
    }

    override fun matches(
        actual: Any?,
    ): Boolean = subConstraints.any { expected -> expected.matches(actual) }

    override fun toString(): String {
        val constraints = subConstraints.joinToString(", ")
        return "or[$constraints]"
    }
}
