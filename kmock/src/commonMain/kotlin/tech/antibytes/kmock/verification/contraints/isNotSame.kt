/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName")

package tech.antibytes.kmock.verification.contraints

import tech.antibytes.kmock.KMockContract

class isNotSame(
    private val illegal: Any?
) : KMockContract.MatcherConstraint {
    override fun matches(value: Any?): Boolean = illegal !== value
}
