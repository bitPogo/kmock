@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.generic.Shared3
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal expect inline fun <reified Mock : Shared3<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared3<*, *>>
): Mock where L : Any, L : Comparable<L>

internal expect inline fun <reified Mock : SpyOn, reified SpyOn : Shared3<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true,
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared3<*, *>>
): Mock where L : Any, L : Comparable<L>
