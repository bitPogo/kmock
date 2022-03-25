package generatorTest

import factory.template.spy.Platform1
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
    factory.template.spy.Platform2Mock::class -> factory.template.spy.Platform2Mock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.spy.Platform3Mock::class -> factory.template.spy.Platform3Mock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.spy.Platform2Mock::class -> factory.template.spy.Platform2Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.spy.Platform2) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : Platform1<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.spy.Platform1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spy.Platform1Mock::class -> factory.template.spy.Platform1Mock<K, L>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn : Platform1<K, L>, K : Any, L> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.spy.Platform1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spy.Platform1Mock::class -> factory.template.spy.Platform1Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.spy.Platform1<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}