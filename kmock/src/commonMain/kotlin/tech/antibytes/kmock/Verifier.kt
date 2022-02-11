/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import co.touchlab.stately.isolate.IsolateState
import tech.antibytes.kmock.KMockContract.Mockery
import tech.antibytes.kmock.KMockContract.Reference

class Verifier : KMockContract.Verifier, KMockContract.Collector {
    private val _references: IsolateState<MutableList<Reference>> = IsolateState { mutableListOf() }

    override val references: List<Reference>
        get() = _references.access { it.toList() }

    override fun addReference(referredMock: Mockery<*, *>, referredCall: Int) {
        _references.access { references ->
            references.add(Reference(referredMock, referredCall))
        }
    }
}
