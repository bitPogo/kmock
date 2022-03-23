package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.spy.Platform::class -> factory.template.spy.PlatformMock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.spy.PlatformMock::class -> factory.template.spy.PlatformMock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
