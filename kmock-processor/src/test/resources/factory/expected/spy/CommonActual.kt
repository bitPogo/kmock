package generatorTest

import factory.template.spy.Common1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal actual inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean
): Mock = when (Mock::class) {
    factory.template.spy.Common2Mock::class -> factory.template.spy.Common2Mock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean
): Mock = when (Mock::class) {
    factory.template.spy.Common2Mock::class -> factory.template.spy.Common2Mock(verifier = verifier,
        freeze = freeze, spyOn = spyOn as factory.template.spy.Common2) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Common1<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER") templateType: kotlin.reflect.KClass<factory.template.spy.Common1<*,
        *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spy.Common1Mock::class -> factory.template.spy.Common1Mock<K, L>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Common1<K, L>, K : Any, L> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER") templateType: kotlin.reflect.KClass<factory.template.spy.Common1<*,
        *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spy.Common1Mock::class -> factory.template.spy.Common1Mock(verifier = verifier,
        freeze = freeze, spyOn = spyOn as factory.template.spy.Common1<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
