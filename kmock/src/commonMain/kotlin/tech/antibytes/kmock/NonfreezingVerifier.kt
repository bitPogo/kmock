/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.Mockery
import tech.antibytes.kmock.KMockContract.Reference

class NonfreezingVerifier : KMockContract.Verifier, KMockContract.Collector {
    private val _references: MutableList<Reference> = mutableListOf()

    override val references: List<Reference>
        get() = _references.toList()

    override fun addReference(referredMock: Mockery<*, *>, referredCall: Int) {
        _references.add(Reference(referredMock, referredCall))
    }

    override fun clear() {
        _references.clear()
    }
}
