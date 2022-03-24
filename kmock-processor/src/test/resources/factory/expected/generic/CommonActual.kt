package generatorTest

import factory.template.generic.Common
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal actual inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean
): Mock = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean
): Mock = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Common<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generic.Common<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.Common::class -> factory.template.generic.CommonMock<K, L>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.generic.CommonMock::class -> factory.template.generic.CommonMock<K, L>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Common<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generic.Common<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
