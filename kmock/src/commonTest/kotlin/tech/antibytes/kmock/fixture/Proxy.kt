/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.fixture

import tech.antibytes.mock.PropertyProxyStub
import tech.antibytes.mock.SyncFunProxyStub
import tech.antibytes.util.test.fixture.PublicApi
import tech.antibytes.util.test.fixture.fixture

internal fun PublicApi.Fixture.funProxyFixture(
    id: String? = null,
    calls: Int? = null
): SyncFunProxyStub {
    return SyncFunProxyStub(
        id ?: this.fixture(),
        calls ?: this.fixture()
    )
}

internal fun PublicApi.Fixture.propertyProxyFixture(
    id: String? = null,
    calls: Int? = null
): PropertyProxyStub {
    return PropertyProxyStub(
        id ?: this.fixture(),
        calls ?: this.fixture()
    )
}
