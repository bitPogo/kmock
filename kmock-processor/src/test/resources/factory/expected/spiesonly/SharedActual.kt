@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.spiesonly.Shared1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.reflect.KClass
import tech.antibytes.kmock.KMockContract

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    factory.template.spiesonly.Shared2Mock::class -> factory.template.spiesonly.Shared2Mock(collector
    = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.spiesonly.Shared2?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector,
    freeze: Boolean,
): Mock = getMockInstance(
    spyOn = spyOn,
    collector = collector,
    relaxed = false,
    relaxUnitFun = false,
    freeze = freeze,
)

private inline fun <reified Mock : SpyOn, reified SpyOn : Shared1<K, L>, K : Any, L>
    getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: KClass<Shared1<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.spiesonly.Shared1Mock::class -> factory.template.spiesonly.Shared1Mock<K,
        L>(collector = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.spiesonly.Shared1<K, L>?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Shared1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector,
    freeze: Boolean,
    templateType: KClass<Shared1<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = spyOn,
    collector = collector,
    relaxed = false,
    relaxUnitFun = false,
    freeze = freeze,
    templateType = templateType,
)
