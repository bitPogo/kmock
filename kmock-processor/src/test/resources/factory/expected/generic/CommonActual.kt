@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.generic.Common
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")

internal actual inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = getMockInstance(
    spyOn = null,
    verifier = verifier,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

private inline fun <reified Mock : SpyOn, reified SpyOn : Common<K, L>, K : Any, L> getMockInstance(
    spyOn: SpyOn?,
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Common<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.CommonMock::class -> factory.template.generic.CommonMock<K, L>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.generic.Common<K, L>?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Common<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: kotlin.reflect.KClass<factory.template.generic.Common<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = null,
    verifier = verifier,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType = templateType,
)
