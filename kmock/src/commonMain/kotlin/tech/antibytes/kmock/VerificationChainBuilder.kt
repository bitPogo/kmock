/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.VerificationHandle

class VerificationChainBuilder : KMockContract.VerificationChainBuilder {
    private val handles = mutableListOf<VerificationHandle>()
    private val ensuredMocks = mutableListOf<KMockContract.Mockery<*, *>>()

    override fun ensureVerificationOf(vararg mocks: KMockContract.Mockery<*, *>) {
        mocks.forEach { mock ->
            if (mock.verificationBuilderReference == null) {
                mock.verificationBuilderReference = this
                ensuredMocks.add(mock)
            }
        }
    }

    internal fun cleanEnsuredMocks() {
        ensuredMocks.forEach { mock ->
            mock.verificationBuilderReference = null
        }
    }

    override fun add(handle: VerificationHandle) {
        handles.add(handle)
    }

    override fun toList(): List<VerificationHandle> = handles.toList()
}
