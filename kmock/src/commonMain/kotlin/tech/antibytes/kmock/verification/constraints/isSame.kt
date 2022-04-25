/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName")

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract.ArgumentConstraint

/**
 * VerificationConstraint matches if the actual and expected value are identical.
 * @param expected the expected value which should match.
 * @property expected the expected value which should match.
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
class isSame(
    private val expected: Any?
) : ArgumentConstraint {
    override fun matches(actual: Any?): Boolean = expected === actual

    override fun toString(): String = "same($expected)"
}
