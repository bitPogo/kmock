/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <Mock> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.regular.Platform::class -> factory.template.regular.PlatformMock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.regular.PlatformMock::class -> factory.template.regular.PlatformMock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.regular.Platform::class -> factory.template.regular.PlatformMock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.regular.Platform) as Mock
    factory.template.regular.PlatformMock::class -> factory.template.regular.PlatformMock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.regular.Platform) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
