@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.spiesonly.Common1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
): Mock = when (Mock::class) {
    factory.template.spiesonly.Common2Mock::class -> factory.template.spiesonly.Common2Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.spiesonly.Common2) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Common1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.spiesonly.Common1<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spiesonly.Common1Mock::class -> factory.template.spiesonly.Common1Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.spiesonly.Common1<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
