/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName", "ktlint:standard:filename")

package tech.antibytes.kmock.verification.constraints
import tech.antibytes.kmock.KMockContract.ArgumentConstraint

public class not(
    private val constraint: ArgumentConstraint,
) : ArgumentConstraint {
    override fun matches(actual: Any?): Boolean = !constraint.matches(actual)

    override fun toString(): String = "not($constraint)"
}
