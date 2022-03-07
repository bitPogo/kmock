/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName")

package tech.antibytes.kmock.verification.contraints

import tech.antibytes.kmock.KMockContract

class isSame(
    private val expected: Any?
) : KMockContract.VerificationConstraint {
    override fun matches(actual: Any?): Boolean = expected === actual
}
