/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.kmock.KMockContract.ParameterizedRelaxer

object ProxyFactory : KMockContract.ProxyFactory {
    override fun <ReturnValue, SideEffect : Function<ReturnValue>> createSyncFunProxy(
        id: String,
        collector: Collector,
        ignorableForVerification: Boolean,
        relaxer: Relaxer<ReturnValue>?,
        unitFunRelaxer: Relaxer<ReturnValue?>?,
        buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>?,
        freeze: Boolean,
        spyOn: SideEffect?
    ): SyncFunProxy<ReturnValue, SideEffect> = SyncFunProxy(
        id = id,
        collector = collector,
        ignorableForVerification = ignorableForVerification,
        relaxer = relaxer,
        unitFunRelaxer = unitFunRelaxer,
        buildInRelaxer = buildInRelaxer,
        freeze = freeze,
        spyOn = spyOn
    )

    override fun <ReturnValue, SideEffect : Function<ReturnValue>> createAsyncFunProxy(
        id: String,
        collector: Collector,
        ignorableForVerification: Boolean,
        relaxer: Relaxer<ReturnValue>?,
        unitFunRelaxer: Relaxer<ReturnValue?>?,
        buildInRelaxer: ParameterizedRelaxer<Any?, ReturnValue>?,
        freeze: Boolean,
        spyOn: SideEffect?
    ): AsyncFunProxy<ReturnValue, SideEffect> = AsyncFunProxy(
        id = id,
        collector = collector,
        relaxer = relaxer,
        unitFunRelaxer = unitFunRelaxer,
        buildInRelaxer = buildInRelaxer,
        freeze = freeze,
        spyOn = spyOn
    )

    override fun <Value> createPropertyProxy(
        id: String,
        collector: Collector,
        relaxer: Relaxer<Value>?,
        freeze: Boolean,
        spyOnGet: (() -> Value)?,
        spyOnSet: ((Value) -> Unit)?,
    ): KMockContract.PropertyProxy<Value> = PropertyProxy(
        id = id,
        collector = collector,
        relaxer = relaxer,
        freeze = freeze,
        spyOnGet = spyOnGet,
        spyOnSet = spyOnSet,
    )
}
