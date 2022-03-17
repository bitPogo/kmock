package generatorTest

import factory.template.generics.Shared1
import factory.template.generics.Shared3
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

internal actual inline fun <reified Mock : Shared1<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER") ignoreMe0: K?,
    @Suppress("UNUSED_PARAMETER") ignoreMe1: L?
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generics.Shared1::class -> factory.template.generics.Shared1Mock<K, L>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.generics.Shared1Mock::class -> factory.template.generics.Shared1Mock<K,
        L>(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Shared1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER") ignoreMe0: K?,
    @Suppress("UNUSED_PARAMETER") ignoreMe1: L?
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generics.Shared1::class -> factory.template.generics.Shared1Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.generics.Shared1<K, L>) as Mock
    factory.template.generics.Shared1Mock::class -> factory.template.generics.Shared1Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.generics.Shared1<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : Shared3<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector,
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER") ignoreMe0: K?,
    @Suppress("UNUSED_PARAMETER") ignoreMe1: L?
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generics.Shared3::class -> factory.template.generics.Shared3Mock<K, L>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.generics.Shared3Mock::class -> factory.template.generics.Shared3Mock<K,
        L>(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock : SpyOn, reified SpyOn : Shared3<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    @Suppress("UNUSED_PARAMETER") ignoreMe0: K?,
    @Suppress("UNUSED_PARAMETER") ignoreMe1: L?
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.generics.Shared3::class -> factory.template.generics.Shared3Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.generics.Shared3<K, L>) as Mock
    factory.template.generics.Shared3Mock::class -> factory.template.generics.Shared3Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.generics.Shared3<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
