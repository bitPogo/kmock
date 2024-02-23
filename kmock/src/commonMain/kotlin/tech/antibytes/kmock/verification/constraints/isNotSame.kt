/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
@file:Suppress("ClassName", "ktlint:standard:filename")

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract.ArgumentConstraint

/**
 * VerificationConstraint matches if the actual and expected value are not identical.
 * @param illegal value which should not match.
 * @property illegal value which should not match.
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
public fun isNotSame(
    illegal: Any?,
): ArgumentConstraint = not(isSame(illegal))
