@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.spiesonly.Platform1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.spiesonly.Platform2Mock::class ->
        factory.template.spiesonly.Platform2Mock(verifier = verifier, freeze = freeze, spyOn = spyOn
            as factory.template.spiesonly.Platform2) as Mock
    factory.template.spiesonly.Platform3Mock::class ->
        factory.template.spiesonly.Platform3Mock(verifier = verifier, freeze = freeze, spyOn = spyOn
            as factory.template.spiesonly.Platform3) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn : Platform1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.spiesonly.Platform1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spiesonly.Platform1Mock::class ->
        factory.template.spiesonly.Platform1Mock(verifier = verifier, freeze = freeze, spyOn = spyOn
            as factory.template.spiesonly.Platform1<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
