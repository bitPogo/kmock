package generatorTest

import factory.template.spy.Shared1
import factory.template.spy.Shared2
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal expect inline fun <reified Mock : Shared1<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER") templateType: kotlin.reflect.KClass<factory.template.spy.Shared1<*,
        *>>
): Mock where L : Any, L : Comparable<L>

internal expect inline fun <reified Mock : Shared2<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER") templateType: kotlin.reflect.KClass<factory.template.spy.Shared2<*,
        *>>
): Mock where L : Any, L : Comparable<L>
