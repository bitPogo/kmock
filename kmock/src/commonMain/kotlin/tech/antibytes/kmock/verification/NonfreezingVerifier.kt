/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference

/**
 * Container to which collects and holds actual references of proxy calls in a non freezing manner.
 * The references are ordered by their invocation.
 * @see Verifier
 * @author Matthias Geisler
 */
class NonfreezingVerifier : KMockContract.Verifier, KMockContract.Collector {
    private val _references: MutableList<Reference> = mutableListOf()

    override val references: List<Reference>
        get() = _references.toList()

    override fun addReference(referredProxy: Proxy<*, *>, referredCall: Int) {
        _references.add(Reference(referredProxy, referredCall))
    }

    override fun clear() {
        _references.clear()
    }
}
