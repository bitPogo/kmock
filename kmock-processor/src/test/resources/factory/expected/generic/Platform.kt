package generatorTest

import factory.template.generics.Platform
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : Platform<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER") ignoreMe0: K? = null,
    @Suppress("UNUSED_PARAMETER") ignoreMe1: L? = null
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generics.Platform::class -> factory.template.generics.PlatformMock<K, L>(verifier
    = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.generics.PlatformMock::class -> factory.template.generics.PlatformMock<K,
        L>(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn : Platform<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER") ignoreMe0: K? = null,
    @Suppress("UNUSED_PARAMETER") ignoreMe1: L? = null
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generics.Platform::class -> factory.template.generics.PlatformMock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.generics.Platform<K, L>) as Mock
    factory.template.generics.PlatformMock::class -> factory.template.generics.PlatformMock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.generics.Platform<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
