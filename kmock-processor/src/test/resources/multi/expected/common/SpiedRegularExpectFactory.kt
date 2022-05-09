@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Boolean
import kotlin.Suppress
import multi.template.common.CommonContractRegular
import multi.template.common.Regular1
import multi.template.common.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
): Mock

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
    templateType0: kotlin.reflect.KClass<multi.template.common.Regular1>,
    templateType1: kotlin.reflect.KClass<multi.template.common.CommonContractRegular.Regular2>,
    templateType2: kotlin.reflect.KClass<multi.template.common.nested.Regular3>,
): Mock where SpyOn : Regular1, SpyOn : CommonContractRegular.Regular2, SpyOn : Regular3
