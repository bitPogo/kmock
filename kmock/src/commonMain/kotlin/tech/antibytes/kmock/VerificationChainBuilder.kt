/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.VerificationHandle

class VerificationChainBuilder : KMockContract.VerificationHandleContainer {
    private val handles = mutableListOf<VerificationHandle>()

    override fun add(handle: VerificationHandle) {
        handles.add(handle)
    }

    override fun toList(): List<VerificationHandle> = handles.toList()
}
