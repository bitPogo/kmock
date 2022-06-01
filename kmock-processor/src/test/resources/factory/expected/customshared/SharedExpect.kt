@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.customshared.Shared1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.reflect.KClass
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock : Shared1<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType: KClass<Shared1<*, *>>,
): Mock where L : Any, L : Comparable<L>
