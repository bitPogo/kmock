/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Proxy

/**
 * Handle with the aggregated information of a Proxy invocation.
 * Meant for internal usage only!
 * @author Matthias Geisler
 */
internal data class Expectation(
    override val proxy: Proxy<*, *>,
    override val callIndices: List<Int>,
) : KMockContract.Expectation
