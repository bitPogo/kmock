// COMMONTEST
package generatorTest

import factory.template.generics.Common
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal expect inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock

internal expect inline fun <reified Mock : Common<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generics.Common<*, *>>
): Mock where L : Any, L : Comparable<L>

internal expect inline fun <reified Mock : SpyOn, reified SpyOn : Common<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generics.Common<*, *>>
): Mock where L : Any, L : Comparable<L>
