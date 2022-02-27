/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example

import tech.antibytes.kmock.KMockContract

internal expect inline fun <reified T> kmock(
    verifier: KMockContract.Collector = KMockContract.Collector { _, _ -> Unit },
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): T

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = KMockContract.Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock
