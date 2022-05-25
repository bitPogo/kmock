@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import multi.template.sharedGeneric.Generic1
import multi.template.sharedGeneric.GenericSharedContract
import multi.template.sharedGeneric.nested.Generic2
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock, KMockTypeParameter0 : Any, KMockTypeParameter1,
    KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
    templateType0: kotlin.reflect.KClass<multi.template.sharedGeneric.Generic1<KMockTypeParameter0,
        KMockTypeParameter1>>,
    templateType1: kotlin.reflect.KClass<multi.template.sharedGeneric.nested.Generic2<KMockTypeParameter2,
        KMockTypeParameter3>>,
    templateType2: kotlin.reflect.KClass<multi.template.sharedGeneric.GenericSharedContract.Generic3<KMockTypeParameter4,
        KMockTypeParameter5>>,
): Mock where Mock : Generic1<KMockTypeParameter0, KMockTypeParameter1>, Mock :
Generic2<KMockTypeParameter2, KMockTypeParameter3>, Mock :
              GenericSharedContract.Generic3<KMockTypeParameter4, KMockTypeParameter5>, KMockTypeParameter1 :
              Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter3 : Any,
              KMockTypeParameter3 : Comparable<KMockTypeParameter3>
