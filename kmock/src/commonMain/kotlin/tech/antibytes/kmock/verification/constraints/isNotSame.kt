/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName")

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract.VerificationConstraint

/**
 * VerificationConstraint matches if the actual and expected value are not identical.
 * @param illegal value which should match.
 * @see VerificationConstraint
 * @author Matthias Geisler
 */
class isNotSame(
    private val illegal: Any?
) : VerificationConstraint {
    override fun matches(actual: Any?): Boolean = illegal !== actual
}
