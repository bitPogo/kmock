@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Boolean
import kotlin.Suppress
import kotlin.reflect.KClass
import multi.template.common.CommonContractRegular
import multi.template.common.Regular1
import multi.template.common.nested.Regular3
import tech.antibytes.kmock.KMockContract

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    multi.CommonMultiMock::class -> multi.CommonMultiMock<multi.CommonMultiMock<*>>(collector =
    collector, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector,
    freeze: Boolean,
    templateType0: KClass<Regular1>,
    templateType1: KClass<CommonContractRegular.Regular2>,
    templateType2: KClass<Regular3>,
): Mock where SpyOn : Regular1, SpyOn : CommonContractRegular.Regular2, SpyOn : Regular3 =
    multi.CommonMultiMock(collector = collector, freeze = freeze, spyOn = spyOn as SpyOn?) as Mock
