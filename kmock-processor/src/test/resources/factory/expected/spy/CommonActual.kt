package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal actual inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean
): Mock = when (Mock::class) {
    factory.template.spy.Common::class -> factory.template.spy.CommonMock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.spy.CommonMock::class -> factory.template.spy.CommonMock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
