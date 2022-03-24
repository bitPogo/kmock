package generatorTest

import factory.template.interfaze.Platform1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.interfaze.Platform2::class -> factory.template.interfaze.Platform2Mock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.interfaze.Platform2Mock::class ->
        factory.template.interfaze.Platform2Mock(verifier = verifier, relaxUnitFun = relaxUnitFun,
            freeze = freeze) as Mock
    factory.template.interfaze.Platform3::class -> factory.template.interfaze.Platform3Mock(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.interfaze.Platform3Mock::class ->
        factory.template.interfaze.Platform3Mock(verifier = verifier, relaxUnitFun = relaxUnitFun,
            freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true
): Mock = when (Mock::class) {
    factory.template.interfaze.Platform2::class -> factory.template.interfaze.Platform2Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.interfaze.Platform2) as Mock
    factory.template.interfaze.Platform2Mock::class ->
        factory.template.interfaze.Platform2Mock(verifier = verifier, freeze = freeze, spyOn = spyOn
            as factory.template.interfaze.Platform2) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : Platform1<K, L>, K : Any, L> kmock(
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    @Suppress("UNUSED_PARAMETER") relaxed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.interfaze.Platform1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.interfaze.Platform1::class -> factory.template.interfaze.Platform1Mock<K,
        L>(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    factory.template.interfaze.Platform1Mock::class -> factory.template.interfaze.Platform1Mock<K,
        L>(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn : Platform1<K, L>, K : Any, L> kspy(
    @Suppress("UNUSED_PARAMETER") spyOn: SpyOn,
    verifier: KMockContract.Collector = Collector { _, _ -> Unit },
    freeze: Boolean = true,
    @Suppress("UNUSED_PARAMETER")
    templateType: kotlin.reflect.KClass<factory.template.interfaze.Platform1<*, *>>
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.interfaze.Platform1::class -> factory.template.interfaze.Platform1Mock(verifier =
    verifier, freeze = freeze, spyOn = spyOn as factory.template.interfaze.Platform1<K, L>) as
        Mock
    factory.template.interfaze.Platform1Mock::class ->
        factory.template.interfaze.Platform1Mock(verifier = verifier, freeze = freeze, spyOn = spyOn
            as factory.template.interfaze.Platform1<K, L>) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}
