package generatorTest

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <reified T> kmock(verifier: KMockContract.Collector = Collector { _, _ -> Unit
}, relaxed: Boolean = false): T = when (T::class) {
    generatorTest.PropertyPlatform::class -> generatorTest.PropertyPlatformMock(verifier = verifier)
        as T
    generatorTest.PropertyPlatformMock::class -> generatorTest.PropertyPlatformMock(verifier =
    verifier) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}

internal inline fun <reified T> kspy(verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    spyOn: T): T = when (T::class) {
    generatorTest.PropertyPlatform::class -> generatorTest.PropertyPlatformMock(verifier = verifier,
        spyOn = spyOn as generatorTest.PropertyPlatform) as T
    else -> throw RuntimeException("Unknown Interface ${T::class.simpleName}.")
}
