@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

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
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean
): Mock = when (Mock::class) {
    factory.template.spy.Common2Mock::class -> factory.template.spy.Common2Mock(verifier = verifier,
        relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean
): Mock = when (Mock::class) {
    factory.template.spy.Common2Mock::class -> factory.template.spy.Common2Mock(verifier = verifier,
        freeze = freeze, spyOn = spyOn as factory.template.spy.Common2) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Common1<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.spy.Common1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spy.Common1Mock::class -> factory.template.spy.Common1Mock<K, L>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Common1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.spy.Common1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spy.Common1Mock::class -> factory.template.spy.Common1Mock(verifier = verifier,
        freeze = freeze, spyOn = spyOn as factory.template.spy.Common1<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
