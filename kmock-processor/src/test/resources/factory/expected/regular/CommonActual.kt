package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal actual inline fun <Mock> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean
): Mock = when (Mock::class) {
    factory.template.regular.Common::class -> factory.template.regular.CommonMock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.regular.CommonMock::class -> factory.template.regular.CommonMock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean
): Mock = when (Mock::class) {
    factory.template.regular.Common::class -> factory.template.regular.CommonMock(verifier = verifier,
        freeze = freeze, spyOn = spyOn as factory.template.regular.Common) as Mock
    factory.template.regular.CommonMock::class -> factory.template.regular.CommonMock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.regular.Common) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
