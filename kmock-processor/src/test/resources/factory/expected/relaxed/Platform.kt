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
    factory.template.relaxed.Platform::class -> factory.template.relaxed.PlatformMock(verifier =
    verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.relaxed.PlatformMock::class -> factory.template.relaxed.PlatformMock(verifier =
    verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.relaxed.Platform::class -> factory.template.relaxed.PlatformMock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.relaxed.Platform) as Mock
    factory.template.relaxed.PlatformMock::class -> factory.template.relaxed.PlatformMock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.relaxed.Platform) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
