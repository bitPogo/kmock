@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    factory.template.relaxed.CommonMock::class -> factory.template.relaxed.CommonMock(verifier =
    verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.relaxed.Common?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = getMockInstance(
    spyOn = null,
    verifier = verifier,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)
