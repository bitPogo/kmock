package generatorTest

import kotlin.Boolean
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <reified T> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    relaxed: Boolean = false,
    freeze: Boolean = true
): T = when (T::class) {
    generatorTest.Relaxed::class -> generatorTest.RelaxedMock(verifier = verifier, relaxed = relaxed,
        freeze = freeze) as T
    generatorTest.RelaxedMock::class -> generatorTest.RelaxedMock(verifier = verifier, relaxed =
    relaxed, freeze = freeze) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}

internal inline fun <reified T> kspy(
    spyOn: T,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): T = when (T::class) {
    generatorTest.Relaxed::class -> generatorTest.RelaxedMock(verifier = verifier, spyOn = spyOn as
        generatorTest.Relaxed, freeze = freeze) as T
    generatorTest.RelaxedMock::class -> generatorTest.RelaxedMock(verifier = verifier, spyOn = spyOn
        as generatorTest.Relaxed, freeze = freeze) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}
