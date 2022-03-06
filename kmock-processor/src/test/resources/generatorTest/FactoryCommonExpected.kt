package generatorTest

import kotlin.Boolean
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal actual inline fun <reified T> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean
): T = when (T::class) {
    generatorTest.PropertyCommon::class -> generatorTest.PropertyCommonMock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as T
    generatorTest.PropertyCommonMock::class -> generatorTest.PropertyCommonMock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean
): Mock = when (Mock::class) {
    generatorTest.PropertyCommon::class -> generatorTest.PropertyCommonMock(verifier = verifier,
        freeze = freeze, spyOn = spyOn as generatorTest.PropertyCommon) as Mock
    generatorTest.PropertyCommonMock::class -> generatorTest.PropertyCommonMock(verifier = verifier,
        freeze = freeze, spyOn = spyOn as generatorTest.PropertyCommon) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
