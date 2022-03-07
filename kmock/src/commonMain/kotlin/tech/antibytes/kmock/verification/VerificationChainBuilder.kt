/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import co.touchlab.stately.collections.IsoMutableList
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.VerificationHandle

internal class VerificationChainBuilder :
    KMockContract.VerificationReferenceBuilder,
    KMockContract.VerificationChainBuilder,
    KMockContract.VerificationReferenceCleaner {
    private val handles = IsoMutableList<VerificationHandle>()

    override fun ensureVerificationOf(vararg mocks: KMockContract.Mockery<*, *>) {
        mocks.forEach { mock ->
            if (mock.verificationBuilderReference != this) {
                throw IllegalStateException("The given mock ${mock.id} is not part of this VerificationChain.")
            }
        }
    }

    override fun add(handle: VerificationHandle) {
        handles.add(handle)
    }

    override fun toList(): List<VerificationHandle> = handles.toList()
}
