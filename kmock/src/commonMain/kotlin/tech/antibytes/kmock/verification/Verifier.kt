/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import co.touchlab.stately.collections.IsoMutableList
import co.touchlab.stately.collections.sharedMutableListOf
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference

/**
 * Container which collects and holds actual references of proxy calls in a freezing manner.
 * The references are ordered by their invocation.
 * This is intended as default mode for Verification.
 * @author Matthias Geisler
 */
class Verifier : KMockContract.Verifier, KMockContract.Collector {
    private val _references: IsoMutableList<Reference> = sharedMutableListOf()

    override val references: List<Reference>
        get() = _references.toList()

    override fun addReference(referredProxy: Proxy<*, *>, referredCall: Int) {
        _references.add(Reference(referredProxy, referredCall))
    }

    override fun clear() {
        _references.clear()
    }
}
