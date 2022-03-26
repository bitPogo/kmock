@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal expect inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock
