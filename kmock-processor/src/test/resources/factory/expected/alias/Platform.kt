@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.alias.AliasPlatformMock::class ->
        factory.template.alias.AliasPlatformMock(verifier = verifier, relaxUnitFun = relaxUnitFun,
            freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
