package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal expect inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock
