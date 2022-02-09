/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

data class VerificationHandle(
    override val id: String,
    override val callIndices: List<Int>
) : KMockContract.VerificationHandle
