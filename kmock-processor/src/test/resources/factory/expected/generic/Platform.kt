@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.generic.Platform
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")

internal inline fun <reified Mock> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
): Mock = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

private inline fun <reified Mock : SpyOn, reified SpyOn : Platform<K, L>, K : Any, L>
    getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Platform<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.PlatformMock::class -> factory.template.generic.PlatformMock<K,
        L>(collector = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.generic.Platform<K, L>?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : Platform<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.generic.Platform<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType = templateType,
)
