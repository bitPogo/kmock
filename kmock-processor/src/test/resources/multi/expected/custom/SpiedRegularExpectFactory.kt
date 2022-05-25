@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Boolean
import kotlin.Suppress
import multi.template.custom.Regular1
import multi.template.custom.SharedContractRegular
import multi.template.custom.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
    templateType0: kotlin.reflect.KClass<multi.template.custom.Regular1>,
    templateType1: kotlin.reflect.KClass<multi.template.custom.SharedContractRegular.Regular2>,
    templateType2: kotlin.reflect.KClass<multi.template.custom.nested.Regular3>,
): Mock where SpyOn : Regular1, SpyOn : SharedContractRegular.Regular2, SpyOn : Regular3
