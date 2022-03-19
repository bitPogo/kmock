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
    factory.template.alias.Platform::class -> factory.template.alias.AliasPlatformMock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
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
    factory.template.alias.Platform::class -> factory.template.alias.AliasPlatformMock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.alias.Platform) as Mock
    factory.template.alias.AliasPlatformMock::class ->
        factory.template.alias.AliasPlatformMock(verifier = verifier, freeze = freeze, spyOn = spyOn
            as factory.template.alias.Platform) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}