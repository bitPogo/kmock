/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.Reference
import tech.antibytes.kmock.KMockContract.SyncFunProxy

/**
 * @suppress
 */
abstract class AsserterBase(
    private val coverAllInvocations: Boolean
) : KMockContract.Asserter, KMockContract.Collector {
    protected abstract val _references: MutableList<Reference>

    override val references: List<Reference>
        get() = _references.toList()

    private fun ignoreReference(referredProxy: Proxy<*, *>): Boolean {
        return when {
            coverAllInvocations -> false
            referredProxy !is SyncFunProxy<*, *> -> false
            else -> referredProxy.ignorableForVerification
        }
    }

    override fun addReference(referredProxy: Proxy<*, *>, referredCall: Int) {
        if (!ignoreReference(referredProxy)) {
            _references.add(Reference(referredProxy, referredCall))
        }
    }

    override fun clear() {
        _references.clear()
    }
}
