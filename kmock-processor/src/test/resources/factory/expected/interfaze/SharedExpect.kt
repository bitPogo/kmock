@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.interfaze.Shared
import factory.template.interfaze.Shared1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock : Shared1<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.interfaze.Shared1<*, *>>,
): Mock where L : Any, L : Comparable<L>

internal expect inline fun <reified Mock : SpyOn, reified SpyOn : Shared1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.interfaze.Shared1<*, *>>,
): Mock where L : Any, L : Comparable<L>

internal expect inline fun <reified Mock : Shared.Shared4<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.interfaze.Shared.Shared4<*, *>>,
): Mock where L : Any, L : Comparable<L>
