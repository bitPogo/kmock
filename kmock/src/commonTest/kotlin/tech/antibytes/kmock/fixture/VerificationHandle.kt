/* ktlint-disable filename */
/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.fixture

import tech.antibytes.kfixture.PublicApi
import tech.antibytes.kfixture.listFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.verification.Expectation

internal fun PublicApi.Fixture.fixtureVerificationHandle(
    proxy: KMockContract.Proxy<*, *>? = null,
    calls: Int? = null,
    callIndices: List<Int>? = null,
): Expectation {
    return Expectation(
        proxy ?: this.funProxyFixture(),
        callIndices ?: this.listFixture(size = calls ?: 23),
    )
}
