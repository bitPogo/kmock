@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.interfaze.Shared1
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
    factory.template.interfaze.Shared2Mock::class -> factory.template.interfaze.Shared2Mock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean
): Mock = when (Mock::class) {
    factory.template.interfaze.Shared2Mock::class -> factory.template.interfaze.Shared2Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.interfaze.Shared2) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Shared1<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
templateType: kotlin.reflect.KClass<factory.template.interfaze.Shared1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.interfaze.Shared1Mock::class -> factory.template.interfaze.Shared1Mock<K,
        L>(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Shared1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
templateType: kotlin.reflect.KClass<factory.template.interfaze.Shared1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.interfaze.Shared1Mock::class -> factory.template.interfaze.Shared1Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.interfaze.Shared1<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
