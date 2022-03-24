package generatorTest

import factory.template.generic.Shared1
import factory.template.generic.Shared2
import factory.template.generic.Shared3
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
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean
): Mock = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Shared1<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.Shared1Mock::class -> factory.template.generic.Shared1Mock<K, L>(verifier
    = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Shared1<K, L>, K : Any, L> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Shared2<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared2<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.Shared2Mock::class -> factory.template.generic.Shared2Mock<K, L>(verifier
    = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Shared2<K, L>, K : Any, L> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared2<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Shared3<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared3<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generic.Shared3Mock::class -> factory.template.generic.Shared3Mock<K, L>(verifier
    = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Shared3<K, L>, K : Any, L> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.generic.Shared3<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
