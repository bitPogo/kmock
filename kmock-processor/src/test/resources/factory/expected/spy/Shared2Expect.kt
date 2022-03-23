package generatorTest

import factory.template.spy.Shared3
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal expect inline fun <reified Mock : Shared3<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER") templateType: kotlin.reflect.KClass<factory.template.spy.Shared3<*,
        *>>
): Mock where L : Any, L : Comparable<L>
