/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.NonIntrusiveFunConfiguration
import tech.antibytes.kmock.KMockContract.NonIntrusiveFunConfigurator
import tech.antibytes.kmock.KMockContract.Relaxer
import tech.antibytes.kmock.KMockContract.SyncFunProxy

object ProxyFactory : KMockContract.ProxyFactory {
    private fun <ReturnValue, SideEffect : Function<ReturnValue>> resolveFunProxyRelaxers(
        relaxationConfiguration: NonIntrusiveFunConfigurator<ReturnValue, SideEffect>.() -> Unit
    ): NonIntrusiveFunConfiguration<ReturnValue, SideEffect> {
        val relaxationConfigurator = NonIntrusiveFunConfigurator<ReturnValue, SideEffect>()

        relaxationConfiguration(relaxationConfigurator)

        return relaxationConfigurator.getConfiguration()
    }

    override fun <ReturnValue, SideEffect : Function<ReturnValue>> createSyncFunProxy(
        id: String,
        collector: Collector,
        ignorableForVerification: Boolean,
        freeze: Boolean,
        relaxationConfiguration: NonIntrusiveFunConfigurator<ReturnValue, SideEffect>.() -> Unit
    ): SyncFunProxy<ReturnValue, SideEffect> {
        val (unitFunRelaxer, buildInRelaxer, relaxer, spyOn) = resolveFunProxyRelaxers(relaxationConfiguration)

        return SyncFunProxy(
            id = id,
            collector = collector,
            ignorableForVerification = ignorableForVerification,
            relaxer = relaxer,
            unitFunRelaxer = unitFunRelaxer,
            buildInRelaxer = buildInRelaxer,
            freeze = freeze,
            spyOn = spyOn
        )
    }

    override fun <ReturnValue, SideEffect : Function<ReturnValue>> createAsyncFunProxy(
        id: String,
        collector: Collector,
        ignorableForVerification: Boolean,
        freeze: Boolean,
        relaxationConfiguration: NonIntrusiveFunConfigurator<ReturnValue, SideEffect>.() -> Unit
    ): AsyncFunProxy<ReturnValue, SideEffect> {
        val (unitFunRelaxer, buildInRelaxer, relaxer, spyOn) = resolveFunProxyRelaxers(relaxationConfiguration)

        return AsyncFunProxy(
            id = id,
            collector = collector,
            relaxer = relaxer,
            unitFunRelaxer = unitFunRelaxer,
            buildInRelaxer = buildInRelaxer,
            freeze = freeze,
            spyOn = spyOn
        )
    }

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
