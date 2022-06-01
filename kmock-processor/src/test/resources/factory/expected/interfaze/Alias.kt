@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package generatorTest

import factory.template.interfaze.Contract
import factory.template.interfaze.Platform1
import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.reflect.KClass
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    factory.template.interfaze.Platform2::class -> factory.template.interfaze.Platform2Mock(collector
    = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.interfaze.Platform2?) as Mock
    factory.template.interfaze.Platform2Mock::class ->
        factory.template.interfaze.Platform2Mock(collector = collector, relaxUnitFun = relaxUnitFun,
            freeze = freeze, spyOn = spyOn as factory.template.interfaze.Platform2?) as Mock
    factory.template.interfaze.Platform3::class -> factory.template.interfaze.Platform3Mock(collector
    = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.interfaze.Platform3?) as Mock
    factory.template.interfaze.Platform3Mock::class ->
        factory.template.interfaze.Platform3Mock(collector = collector, relaxUnitFun = relaxUnitFun,
            freeze = freeze, spyOn = spyOn as factory.template.interfaze.Platform3?) as Mock
    factory.template.interfaze.Contract.Platform4::class ->
        factory.template.interfaze.Platform4Mock(collector = collector, relaxUnitFun = relaxUnitFun,
            freeze = freeze, spyOn = spyOn as factory.template.interfaze.Contract.Platform4?) as Mock
    factory.template.interfaze.Platform4Mock::class ->
        factory.template.interfaze.Platform4Mock(collector = collector, relaxUnitFun = relaxUnitFun,
            freeze = freeze, spyOn = spyOn as factory.template.interfaze.Contract.Platform4?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
): Mock = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
): Mock = getMockInstance(
    spyOn = spyOn,
    collector = collector,
    relaxed = false,
    relaxUnitFun = false,
    freeze = freeze,
)

private inline fun <reified Mock : SpyOn, reified SpyOn : Platform1<K, L>, K : Any, L>
    getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: KClass<Platform1<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.interfaze.Platform1::class -> factory.template.interfaze.AliasPlatformMock<K,
        L>(collector = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.interfaze.Platform1<K, L>?) as Mock
    factory.template.interfaze.AliasPlatformMock::class ->
        factory.template.interfaze.AliasPlatformMock<K, L>(collector = collector, relaxUnitFun =
        relaxUnitFun, freeze = freeze, spyOn = spyOn as factory.template.interfaze.Platform1<K, L>?)
            as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : Platform1<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType: KClass<Platform1<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType = templateType,
)

internal inline fun <reified Mock : SpyOn, reified SpyOn : Platform1<K, L>, K : Any, L> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
    templateType: KClass<Platform1<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = spyOn,
    collector = collector,
    relaxed = false,
    relaxUnitFun = false,
    freeze = freeze,
    templateType = templateType,
)

private inline fun <reified Mock : SpyOn, reified SpyOn : Contract.Platform5<K, L>, K : Any, L>
    getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType: KClass<Contract.Platform5<*, *>>,
): Mock where L : Any, L : Comparable<L> = when (Mock::class) {
    factory.template.interfaze.Contract.Platform5::class ->
        factory.template.interfaze.Platform5Mock<K, L>(collector = collector, relaxUnitFun =
        relaxUnitFun, freeze = freeze, spyOn = spyOn as
            factory.template.interfaze.Contract.Platform5<K, L>?) as Mock
    factory.template.interfaze.Platform5Mock::class -> factory.template.interfaze.Platform5Mock<K,
        L>(collector = collector, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as
        factory.template.interfaze.Contract.Platform5<K, L>?) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : Contract.Platform5<K, L>, K : Any, L> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType: KClass<Contract.Platform5<*, *>>,
): Mock where L : Any, L : Comparable<L> = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType = templateType,
)
