package generatorTest

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <reified T> kmockCommon(verifier: KMockContract.Collector = Collector { _, _ ->
    Unit }, relaxed: Boolean = false): T = when (T::class) {
    generatorTest.PropertyCommon::class -> generatorTest.PropertyCommonMock(verifier = verifier) as T
    generatorTest.PropertyCommonMock::class -> generatorTest.PropertyCommonMock(verifier = verifier)
        as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}

internal inline fun <reified T> kspyCommon(verifier: KMockContract.Collector = Collector { _, _ ->
    Unit }, spyOn: T): T = when (T::class) {
    generatorTest.PropertyCommon::class -> generatorTest.PropertyCommonMock(verifier = verifier, spyOn
    = spyOn as generatorTest.PropertyCommon) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}
