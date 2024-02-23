/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.SyncFunProxy

public object ProxyFactory : KMockContract.ProxyFactory {
    override fun <ReturnValue, SideEffect : Function<ReturnValue>> createSyncFunProxy(
        id: String,
        collector: Collector,
        ignorableForVerification: Boolean,
        freeze: Boolean,
    ): SyncFunProxy<ReturnValue, SideEffect> = SyncFunProxy(
        id = id,
        collector = collector,
        ignorableForVerification = ignorableForVerification,
        freeze = freeze,
    )

    override fun <ReturnValue, SideEffect : Function<ReturnValue>> createAsyncFunProxy(
        id: String,
        collector: Collector,
        ignorableForVerification: Boolean,
        freeze: Boolean,
    ): AsyncFunProxy<ReturnValue, SideEffect> = AsyncFunProxy(
        id = id,
        collector = collector,
        freeze = freeze,
    )

    override fun <Value> createPropertyProxy(
        id: String,
        collector: Collector,
        freeze: Boolean,
    ): KMockContract.PropertyProxy<Value> = PropertyProxy(
        id = id,
        collector = collector,
        freeze = freeze,
    )
}
