@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Boolean
import kotlin.Suppress
import kotlin.reflect.KClass
import multi.template.platform.PlatformContractRegular
import multi.template.platform.Regular1
import multi.template.platform.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    multi.PlatformMultiMock::class -> multi.PlatformMultiMock<multi.PlatformMultiMock<*>>(collector =
    collector, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = false,
): Mock = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector,
    freeze: Boolean,
    templateType0: KClass<Regular1>,
    templateType1: KClass<PlatformContractRegular.Regular2>,
    templateType2: KClass<Regular3>,
): Mock where SpyOn : Regular1, SpyOn : PlatformContractRegular.Regular2, SpyOn : Regular3 =
    multi.PlatformMultiMock(collector = collector, freeze = freeze, spyOn = spyOn as SpyOn?) as Mock
