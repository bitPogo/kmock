@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.reflect.KClass
import multi.template.commonGeneric.Generic1
import multi.template.commonGeneric.GenericCommonContract
import multi.template.commonGeneric.nested.Generic2
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = false,
): Mock

internal expect inline fun <reified Mock : SpyOn, reified SpyOn, KMockTypeParameter0 : Any,
    KMockTypeParameter1, KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4,
    KMockTypeParameter5> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = false,
    templateType0: KClass<Generic1<*, *>>,
    templateType1: KClass<Generic2<*, *>>,
    templateType2: KClass<GenericCommonContract.Generic3<*, *>>,
): Mock where SpyOn : Generic1<KMockTypeParameter0, KMockTypeParameter1>, SpyOn :
Generic2<KMockTypeParameter2, KMockTypeParameter3>, SpyOn :
              GenericCommonContract.Generic3<KMockTypeParameter4, KMockTypeParameter5>, KMockTypeParameter1 :
              Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter3 : Any,
              KMockTypeParameter3 : Comparable<KMockTypeParameter3>
