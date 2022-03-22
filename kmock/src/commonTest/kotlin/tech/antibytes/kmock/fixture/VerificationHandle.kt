/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.fixture

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.verification.Expectation
import tech.antibytes.util.test.fixture.PublicApi
import tech.antibytes.util.test.fixture.listFixture

internal fun PublicApi.Fixture.fixtureVerificationHandle(
    proxy: KMockContract.Proxy<*, *>? = null,
    calls: Int? = null,
    callIndices: List<Int>? = null
): Expectation {
    return Expectation(
        proxy ?: this.funProxyFixture(),
        callIndices ?: this.listFixture(size = calls ?: 23)
    )
}
