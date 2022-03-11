/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.Proxy

/**
 * Placeholder Collector for Proxies.
 * @see Collector
 * @author Matthias Geisler
 */
internal object NoopCollector : Collector {
    /**
     * Performs a noop on invocation
     * @param referredProxy the proxy it is referring to.
     * @param referredCall the invocation index of the Proxy it refers to.
     */
    override fun addReference(
        referredProxy: Proxy<*, *>,
        referredCall: Int
    ) = Unit
}
