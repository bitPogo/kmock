@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Boolean
import kotlin.Suppress
import kotlin.reflect.KClass
import multi.template.common.CommonContractRegular
import multi.template.common.Regular1
import multi.template.common.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = false,
): Mock

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = false,
    templateType0: KClass<Regular1>,
    templateType1: KClass<CommonContractRegular.Regular2>,
    templateType2: KClass<Regular3>,
): Mock where SpyOn : Regular1, SpyOn : CommonContractRegular.Regular2, SpyOn : Regular3
