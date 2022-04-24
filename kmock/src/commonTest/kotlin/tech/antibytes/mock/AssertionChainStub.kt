/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.mock

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.error.MockError

internal class AssertionChainStub(
    private val propagate: ((KMockContract.Expectation) -> Unit)? = null
) : KMockContract.AssertionChain {
    override fun propagate(expected: KMockContract.Expectation) {
        return propagate?.invoke(expected)
            ?: throw MockError.MissingStub("Missing SideEffect propagate")
    }

    override fun ensureAllReferencesAreEvaluated() {
        TODO("Not yet implemented")
    }
}
