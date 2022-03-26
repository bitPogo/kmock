@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.interfaze.Common1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock> kmock(
    verifier:KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier:KMockContract.Collector = NoopCollector,
    freeze: Boolean = true
): Mock

internal expect inline fun <reified Mock : Common1<K, L>, K : Any, L> kmock(
    verifier:KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.interfaze.Common1<*, *>>
): Mock where L : Any, L : Comparable<L>

internal expect inline fun <reified Mock : SpyOn, reified SpyOn : Common1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier:KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.interfaze.Common1<*, *>>
): Mock where L : Any, L : Comparable<L>
