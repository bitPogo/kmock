package generatorTest

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <reified T> kmock(verifier: KMockContract.Collector = Collector { _, _ -> Unit
}, relaxed: Boolean = false): T = when (T::class) {
    generatorTest.Relaxed::class -> generatorTest.RelaxedMock(verifier = verifier, relaxed = relaxed)
        as T
    generatorTest.RelaxedMock::class -> generatorTest.RelaxedMock(verifier = verifier, relaxed =
    relaxed) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}

internal inline fun <reified T> kspy(verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: T): T = when (T::class) {
    generatorTest.Relaxed::class -> generatorTest.RelaxedMock(verifier = verifier, spyOn = spyOn as
        generatorTest.Relaxed) as T
    generatorTest.RelaxedMock::class -> generatorTest.RelaxedMock(verifier = verifier, spyOn = spyOn
        as generatorTest.Relaxed) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}
