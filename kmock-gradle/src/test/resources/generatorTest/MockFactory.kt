// COMMON SOURCE
package test

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal expect inline fun <reified T> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): T

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock
