package generatorTest

import kotlin.Boolean
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal actual inline fun <reified T> kmock(
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    freeze: Boolean
): T = when (T::class) {
    generatorTest.PropertyCommon::class -> generatorTest.PropertyCommonMock(verifier = verifier,
        freeze = freeze) as T
    generatorTest.PropertyCommonMock::class -> generatorTest.PropertyCommonMock(verifier = verifier,
        freeze = freeze) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}

internal actual inline fun <reified T> kspy(
    spyOn: T,
    verifier: KMockContract.Collector,
    freeze: Boolean
): T = when (T::class) {
    generatorTest.PropertyCommon::class -> generatorTest.PropertyCommonMock(verifier = verifier, spyOn
    = spyOn as generatorTest.PropertyCommon, freeze = freeze) as T
    generatorTest.PropertyCommonMock::class -> generatorTest.PropertyCommonMock(verifier = verifier,
        spyOn = spyOn as generatorTest.PropertyCommon, freeze = freeze) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}
