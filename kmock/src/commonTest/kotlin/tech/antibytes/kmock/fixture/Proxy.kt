/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.fixture

import tech.antibytes.kfixture.PublicApi
import tech.antibytes.kfixture.fixture
import tech.antibytes.mock.PropertyProxyStub
import tech.antibytes.mock.SyncFunProxyStub

internal fun PublicApi.Fixture.funProxyFixture(
    id: String? = null,
    calls: Int? = null,
    freeze: Boolean? = null,
): SyncFunProxyStub {
    return SyncFunProxyStub(
        id ?: this.fixture(),
        calls ?: this.fixture(),
        frozen = freeze ?: this.fixture(),
    )
}

internal fun PublicApi.Fixture.propertyProxyFixture(
    id: String? = null,
    calls: Int? = null,
    freeze: Boolean? = null,
): PropertyProxyStub {
    return PropertyProxyStub(
        id ?: this.fixture(),
        calls ?: this.fixture(),
        frozen = freeze ?: this.fixture(),
    )
}
