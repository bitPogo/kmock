@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.generic.Shared1
import factory.template.generic.Shared2
import factory.template.generic.Shared3
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")

internal actual inline fun <reified Mock> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

private inline fun <reified Mock : SpyOn, reified SpyOn : Shared1<K, L>, K : Any, L>
    getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared1<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.Shared1Mock::class -> factory.template.generic.Shared1Mock<K,
        L>(collector = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.generic.Shared1<K, L>?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Shared1<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared1<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType = templateType,
)

private inline fun <reified Mock : SpyOn, reified SpyOn : Shared2<K, L>, K : Any, L>
    getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared2<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.Shared2Mock::class -> factory.template.generic.Shared2Mock<K,
        L>(collector = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.generic.Shared2<K, L>?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Shared2<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared2<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType = templateType,
)

private inline fun <reified Mock : SpyOn, reified SpyOn : Shared3<K, L>, K : Any, L>
    getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared3<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.Shared3Mock::class -> factory.template.generic.Shared3Mock<K,
        L>(collector = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.generic.Shared3<K, L>?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Shared3<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared3<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType = templateType,
)
