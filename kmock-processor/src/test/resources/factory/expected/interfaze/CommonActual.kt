@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.interfaze.Common1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.reflect.KClass
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    factory.template.interfaze.Common2Mock::class -> factory.template.interfaze.Common2Mock(collector
    = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.interfaze.Common2?) as Mock
    factory.template.interfaze.Common3Mock::class -> factory.template.interfaze.Common3Mock(collector
    = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.interfaze.Common3?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

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

private inline fun <reified Mock : SpyOn, reified SpyOn : Common1<K, L>, K : Any, L>
    getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: KClass<Common1<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.interfaze.Common1Mock::class -> factory.template.interfaze.Common1Mock<K,
        L>(collector = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.interfaze.Common1<K, L>?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Common1<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: KClass<Common1<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType = templateType,
)

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Common1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector,
    freeze: Boolean,
    templateType: KClass<Common1<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = spyOn,
    collector = collector,
    relaxed = false,
    relaxUnitFun = false,
    freeze = freeze,
    templateType = templateType,
)
